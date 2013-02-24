package ru.sergeyvasilenko.weheartpics.interview.exceptions;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

/**
 * User: Serg
 * Date: 23.02.13
 * Time: 16:14
 */
public class RequestException extends ApplicationException {

    private HttpRequest mRequest;
    private HttpResponse mResponse;

    public RequestException(HttpRequest request, HttpResponse response) {
        mRequest = request;
        mResponse = response;
    }

    @SuppressWarnings("unused")
    public HttpRequest getRequest() {
        return mRequest;
    }

    @SuppressWarnings("unused")
    public HttpResponse getResponse() {
        return mResponse;
    }
}
