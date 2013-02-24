package ru.sergeyvasilenko.weheartpics.interview.exceptions;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

/**
 * User: Serg
 * Date: 23.02.13
 * Time: 16:20
 */
public class ResponseParseException extends ApplicationException {

    private HttpRequest mRequest;
    private HttpResponse mResponse;

    public ResponseParseException(String msg, HttpRequest request, HttpResponse response, Throwable throwable) {
        super(msg, throwable);
        mRequest = request;
        mResponse = response;
    }

    public HttpRequest getRequest() {
        return mRequest;
    }

    public HttpResponse getResponse() {
        return mResponse;
    }
}
