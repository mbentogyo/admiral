package com.ThePod.Admirals.exception;

import lombok.Getter;

public enum AdmiralsException {

    DATA_ERROR("An error occurred while receiving data!"),
    INTERNAL_ERROR("An internal error occurred during runtime."),
    INVALID_CODE("The code you have entered is invalid!"),
    UNREACHABLE_SERVER("The host is unreachable! Try again!"),

    ;

    @Getter private final String message;

    AdmiralsException(String message) {
        this.message = message;
    }
}
