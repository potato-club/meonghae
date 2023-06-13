package com.meonghae.communityservice.Service;

import com.meonghae.communityservice.Client.S3ServiceClient;
import com.meonghae.communityservice.Client.UserServiceClient;
import com.meonghae.communityservice.Dto.BoardDto.*;
import com.meonghae.communityservice.Dto.S3Dto.S3RequestDto;
import com.meonghae.communityservice.Dto.S3Dto.S3ResponseDto;
import com.meonghae.communityservice.Dto.S3Dto.S3UpdateDto;
import com.meonghae.communityservice.Entity.Board.Board;
import com.meonghae.communityservice.Entity.Board.QBoard;
import com.meonghae.communityservice.Enum.BoardType;
import com.meonghae.communityservice.Exception.Custom.BoardException;
import com.meonghae.communityservice.Repository.BoardRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.meonghae.communityservice.Exception.Error.ErrorCode.BAD_REQUEST;
import static com.meonghae.communityservice.Exception.Error.ErrorCode.UNAUTHORIZED;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final RedisService redisService;
    private final UserServiceClient userService;
    private final S3ServiceClient s3Service;

    @Transactional
    public Slice<BoardListDto> getBoardList(int typeKey, int page) {
        BoardType type = BoardType.findWithKey(typeKey);
        PageRequest request = PageRequest.of(page - 1, 20,
                Sort.by(Sort.Direction.DESC, "createdDate"));
        Slice<Board> list = boardRepository.findByType(type, request);
        Slice<BoardListDto> listDto = list.map(board -> {
            String nickname = redisService.getNickname(board.getEmail());
            return new BoardListDto(board, nickname);
        });
        return listDto;
    }

    @Transactional
    public BoardDetailDto getBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardException(BAD_REQUEST, "board is not exist"));
        String nickname = redisService.getNickname(board.getEmail());
        BoardDetailDto detailDto = new BoardDetailDto(board, nickname);
        if(board.getHasImage()) {
            List<S3ResponseDto> images = s3Service.getImages(new S3RequestDto(board.getId(), "BOARD"));
            detailDto.setImages(images);
        }
        return detailDto;
    }

    @Transactional
    public List<BoardMainDto> getMainBoard() {

        List<Board> mainBoardLists;

        QBoard qBoard = QBoard.board;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1).toLocalDate().atStartOfDay();

        mainBoardLists = Arrays.stream(BoardType.values()).map(type -> jpaQueryFactory.selectFrom(qBoard)
                .where(qBoard.type.eq(type), qBoard.createdDate.between(yesterday, now))
                .orderBy(qBoard.likes.desc(), qBoard.createdDate.desc())
                .limit(1)
                .fetchOne()).filter(Objects::nonNull).collect(Collectors.toList());

        return mainBoardLists.stream().map(board -> {
            String nickname = redisService.getNickname(board.getEmail());
            return new BoardMainDto(board, nickname);
        }).collect(Collectors.toList());
    }

    @Transactional
    public void createBoard(int typeKey, BoardRequestDto requestDto, String token) {
        BoardType type = BoardType.findWithKey(typeKey);
        String email = userService.getUserEmail(token);
        Board board = requestDto.toEntity(type, email);
        Board saveBoard = boardRepository.save(board);
        List<MultipartFile> images = requestDto.getImages();
        if(images != null) {
            imageCheck(saveBoard, images, 0);
            S3RequestDto s3Dto = new S3RequestDto(saveBoard.getId(), "BOARD");
            s3Service.uploadImage(images, s3Dto);
            saveBoard.setHasImage();
        }
    }

    @Transactional
    public void modifyBoard(Long id, BoardUpdateDto updateDto, String token) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardException(BAD_REQUEST, "board is not exist"));
        String email = userService.getUserEmail(token);
        if(!board.getEmail().equals(email)) {
            throw new BoardException(UNAUTHORIZED, "글 작성자만 수정 가능합니다.");
        }
        board.updateBoard(updateDto.getTitle(), updateDto.getContent());
        List<S3UpdateDto> reuseDto = updateDto.getUpdateDto()
                .stream().filter(dto -> !dto.isDeleted()).collect(Collectors.toList());
        int reuseSize = reuseDto.size();

        List<MultipartFile> images = updateDto.getImages();
        if(images != null) {
            imageCheck(board, images, reuseSize);
            s3Service.updateImage(images, updateDto.getUpdateDto());
            board.setHasImage();
        }
    }

    // S3 이미지 삭제 로직 생기면 추가
    @Transactional
    public void deleteBoard(Long id, String token) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("board is not exist"));
        String email = userService.getUserEmail(token);
        if(!board.getEmail().equals(email)) {
            throw new BoardException(UNAUTHORIZED, "글 작성자만 삭제 가능합니다.");
        }
        boardRepository.delete(board);
    }

    public void imageCheck(Board saveBoard, List<MultipartFile> images, int reuseSize) {
        if(saveBoard.getType() == BoardType.MISSING && images.size() - reuseSize > 5) {
            throw new BoardException(BAD_REQUEST, "실종 게시글 사진은 최대 5개까지 업로드 가능합니다.");
        } else if(images.size() - reuseSize > 3) {
            throw new BoardException(BAD_REQUEST, "게시글 사진은 최대 3개까지 업로드 가능합니다.");
        }
    }
}
