package com.meonghae.communityservice.Dto.CommentDto;

import com.meonghae.communityservice.Entity.Board.BoardComment;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentChildDto {
    @ApiModelProperty("자식댓글 id")
    private Long id;
    @ApiModelProperty("댓글 작성자 프로필 사진")
    private String profileUrl;
    @ApiModelProperty("댓글 작성자가 원글 작성자인지 여부")
    private Boolean isWriter;
    @ApiModelProperty("댓글 내용")
    private String comment;
    @ApiModelProperty("댓글 수정 여부")
    private Boolean update;
    @ApiModelProperty("댓글의 부모댓글 id")
    private Long parentId;

    public CommentChildDto(BoardComment child, String url, boolean isWriter) {
        this.id = child.getId();
        this.comment = child.getComment();
        this.isWriter = isWriter;
        this.profileUrl = url;
        this.update = child.getUpdated();
        this.parentId = child.getParent().getId();
    }
}
