package net.lisanza.dropunit.server.rest.dto;

import java.util.Hashtable;
import java.util.Map;

public class ReceivedRequestDto {

    private String path;

    private String queryString;

    private String method;

    private Map<String, String> headers = new Hashtable<>();

    // getters and setters

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    // with-builders

    public ReceivedRequestDto withPath(String path) {
        this.path = path;
        return this;
    }

    public ReceivedRequestDto withQueryString(String queryString) {
        this.queryString = queryString;
        return this;
    }

    public ReceivedRequestDto withMethod(String method) {
        this.method = method;
        return this;
    }

    public ReceivedRequestDto withHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    @Override
    public String toString() {
        return "{ path='" + path + '\'' +
                ", queryString='" + queryString + '\'' +
                ", method='" + method + '\'' +
                ", headers=" + headers +
                '}';
    }
}
