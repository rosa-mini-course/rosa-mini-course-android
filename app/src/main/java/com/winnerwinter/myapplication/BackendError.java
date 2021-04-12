package com.winnerwinter.myapplication;

public class BackendError {
    public String code;
    public String message;

    public BackendError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return "BackendError{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
