package ru.sergeyvasilenko.weheartpics.interview.exceptions;

/**
 * User: Serg
 * Date: 23.02.13
 * Time: 16:12
 */
public class ApplicationException extends Exception {

    public ApplicationException() {
    }

    public ApplicationException(String detailMessage) {
        super(detailMessage);
    }

    public ApplicationException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ApplicationException(Throwable throwable) {
        super(throwable);
    }
}
