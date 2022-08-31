package net.lisanza.dropunit.server.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class DropUnitEndpointDto {

    @JsonProperty("url")
    private String url;

    @JsonProperty("method")
    private String method;

    @JsonProperty("requestParameters")
    private DropUnitUrlParametersDto requestParameters;

    @JsonProperty("requestHeaders")
    private List<DropUnitHeaderDto> requestHeaders = new ArrayList();

    @JsonProperty("responseCode")
    private int responseCode;

    @JsonProperty("responseHeaders")
    private List<DropUnitHeaderDto> responseHeaders = new ArrayList();

    @JsonProperty("responseDelay")
    private int responseDelay;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        if (isNotEmpty(url)) {
            String[] uriParts = url.split("\\?", 2);
        }
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public DropUnitUrlParametersDto getRequestParameters() {
        return requestParameters;
    }

    public void setRequestParameters(DropUnitUrlParametersDto requestParameters) {
        this.requestParameters = requestParameters;
    }

    public List<DropUnitHeaderDto> getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(List<DropUnitHeaderDto> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public void addRequestHeader(DropUnitHeaderDto requestHeader) {
        this.requestHeaders.add(requestHeader);
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public List<DropUnitHeaderDto> getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(List<DropUnitHeaderDto> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public void addResponseHeader(DropUnitHeaderDto ResponseHeader) {
        this.responseHeaders.add(ResponseHeader);
    }

    public int getResponseDelay() {
        return responseDelay;
    }

    public void setResponseDelay(int responseDelay) {
        this.responseDelay = responseDelay;
    }

    @Override
    public String toString() {
        return "DropUnitEndpointDto{" +
                "url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", requestHeaders=" + requestHeaders +
                ", responseHeaders=" + responseHeaders +
                ", responseDelay=" + responseDelay +
                '}';
    }
}