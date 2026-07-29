package com.auca.library.exception;

public class MembershipAlreadyActiveException extends RuntimeException {
    public MembershipAlreadyActiveException(String message) {
        super(message);
    }
}
