package net.lisanza.dropunit.server.config.yml;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.lisanza.dropunit.server.rest.dto.DropUnitParametersDto;
import net.lisanza.dropunit.server.services.DropUnitEndpointRequest;

import javax.validation.constraints.NotNull;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class EndpointDocument {

    /**
     * The URL path for which this endpoint.
     */
    @NotNull
    private String path;

    /**
     * The HTTP method to which this endpoint will react.
     */
    @NotNull
    private String method;

    /**
     * The headers on which this endpoint will react.
     */
    private DropUnitParametersDto parameters;

    /**
     * The headers on which this endpoint will react.
     */
    private Map<String, String> headers;

    /**
     * The delay in lillis beofre the response is returned.
     */
    private int delay;

    /**
     * Patterns that must be matched in the request.
     */
    private DropUnitEndpointRequest request;

    /**
     * The HTTP response code with which this endpoint will react.
     */
    private int responseCode;

    /**
     * The headers with which this endpoint will react in the response.
     */
    private Map<String, String> responseHeaders;

    /**
     * The content-type with which this endpoint will react in the response.
     */
    @NotNull
    private String responseContentType;

    /**
     * The response body with which this endpoint will react.
     */
    @NotNull
    private String responseBodyFileName;

    @JsonProperty
    public String getPath() {
        return path;
    }

    @JsonProperty
    public void setPath(String path) {
        this.path = path;
    }

    @JsonProperty
    public String getMethod() {
        return method;
    }

    @JsonProperty
    public void setMethod(String method) {
        this.method = method;
    }

    @JsonProperty
    public DropUnitParametersDto getParameters() {
        return parameters;
    }

    public void setParameters(DropUnitParametersDto parameters) {
        this.parameters = parameters;
    }

    @JsonProperty
    public Map<String, String> getHeaders() {
        return headers;
    }

    @JsonProperty
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    @JsonProperty
    public int getDelay() {
        return delay;
    }

    @JsonProperty
    public void setDelay(int delay) {
        this.delay = delay;
    }

    @JsonProperty
    public DropUnitEndpointRequest getRequest() {
        return request;
    }

    @JsonProperty
    public void setRequest(DropUnitEndpointRequest request) {
        this.request = request;
    }
    @JsonProperty
    public int getResponseCode() {
        return responseCode;
    }

    @JsonProperty
    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    @JsonProperty
    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    @JsonProperty
    public void setResponseHeaders(Map<String, String> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    @JsonProperty
    public String getResponseContentType() {
        return responseContentType;
    }

    @JsonProperty
    public void setResponseContentType(String responseContentType) {
        this.responseContentType = responseContentType;
    }

    @JsonProperty
    public String getResponseBodyFileName() {
        if (isEmpty(responseBodyFileName)) {
            return null;
        }
        if (isEmpty(System.getenv("PREFIX"))) {
            return responseBodyFileName;
        } else {
            return System.getenv("PREFIX") + responseBodyFileName;
        }
    }

    @JsonProperty
    public void setResponseBodyFileName(String responseBodyFileName) {
        this.responseBodyFileName = responseBodyFileName;
    }

    @Override
    public String toString() {
        return "{ path='" + path + '\'' +
                ", method='" + method + '\'' +
                ", headers=" + toStringHeaders(headers) +
                ", delay='" + delay + '\'' +
                ", responseCode=" + responseCode +
                ", responseHeaders=" + toStringHeaders(responseHeaders) +
                ", responseContentType='" + responseContentType + '\'' +
                ", responseBodyFileName='" + responseBodyFileName + '\'' +
                '}';
    }

    private String toStringHeaders(Map<String, String> hdrs) {
        if (hdrs != null) {
            StringBuffer hdrsBuffer = new StringBuffer();
            for (String hdrKey : hdrs.keySet()) {
                hdrsBuffer.append('\'')
                        .append(hdrKey).append(": ")
                        .append(hdrs.get(hdrKey))
                        .append('\'').append(',');
            }
            return hdrsBuffer.toString();
        } else {
            return "'',";
        }
    }
}