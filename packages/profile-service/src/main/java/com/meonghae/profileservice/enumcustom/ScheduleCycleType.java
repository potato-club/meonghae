package com.meonghae.profileservice.enumcustom;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ScheduleCycleType {
    Month(0),Day(1);

    private final int key;
}
