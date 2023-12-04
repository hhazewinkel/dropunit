package net.lisanza.dropunit.server.services;

import net.lisanza.dropunit.server.rest.dto.DropUnitHeaderDto;
import net.lisanza.dropunit.server.rest.dto.DropUnitParametersDto;
import net.lisanza.dropunit.server.services.data.ReceivedRequest;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class DropUnitEndpoint implements Comparable<DropUnitEndpoint> {

    private String id;

    private String url;

    private String method;

    private EndPointParameters parameters;

    private Map<String, String> headers = new Hashtable<>();

    private int delay;

    private int count;

    private DropUnitEndpointRequest request;

    private DropUnitEndpointResponse response;

    private List<ReceivedRequest> receivedList = new ArrayList<>();

    // getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public EndPointParameters getParameters() {
        return parameters;
    }

    public void setParameters(EndPointParameters parameters) {
        this.parameters = parameters;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void incr() {
        count++;
    }

    public int getCount() {
        return count;
    }

    public DropUnitEndpointRequest getRequest() {
        return request;
    }

    public void setRequest(DropUnitEndpointRequest request) {
        this.request = request;
    }

    public DropUnitEndpointResponse getResponse() {
        return response;
    }

    public void setResponse(DropUnitEndpointResponse response) {
        this.response = response;
    }

    public int getReceivedSize() {
        return receivedList.size();
    }

    public ReceivedRequest getReceived(int i) {
        if ((0 < i) && (i <= receivedList.size())) {
            return receivedList.get(i - 1);
        }
        return null;
    }

    public void addReceived(ReceivedRequest received) {
        this.receivedList.add(received);
        incr();
    }

    // with-builders

    public DropUnitEndpoint withId(String id) {
        this.id = id;
        return this;
    }

    public DropUnitEndpoint withUrl(String url) {
        if (!isEmpty(url)) {
            int q = url.lastIndexOf('?');
            if (q != -1) {
                this.url = url.substring(0, q);
                withParameters(url.substring(q + 1));
            } else {
                this.url = url;
            }
        }
        if (!this.url.startsWith("/")) {
            this.url = "/" + this.url;
        }
        return this;
    }

    public DropUnitEndpoint withMethod(String method) {
        this.method = method;
        return this;
    }

    public DropUnitEndpoint withParameters(DropUnitParametersDto dto) {
        if (parameters == null) {
            parameters = new EndPointParameters();
        }
        if (dto != null) {
            parameters.setMatchAll(dto.isMatchAll());
            parameters.setMatchValue(dto.isMatchValue());
            for (String key : dto.getIncludedParameters().keySet()) {
                parameters.withParameter(key, dto.getIncludedParameters().get(key));
            }
            for (String key : dto.getExcludedParameters().keySet()) {
                parameters.withoutParameter(key, dto.getExcludedParameters().get(key));
            }
        }
        return this;
    }

    public DropUnitEndpoint withParameters(String queryString) {
        if (isNotEmpty(queryString)) {
            if (parameters == null) {
                parameters = new EndPointParameters();
            }
            parameters.withParameterString(queryString);
        }
        return this;
    }

    public DropUnitEndpoint withHeaders(List<DropUnitHeaderDto> headers) {
        for (DropUnitHeaderDto header : headers) {
            this.headers.put(header.getName(), header.getValue());
        }
        return this;
    }

    public DropUnitEndpoint withDelay(int delay) {
        this.delay = delay;
        return this;
    }

    public DropUnitEndpoint withRequest(DropUnitEndpointRequest request) {
        this.request = request;
        return this;
    }

    public DropUnitEndpoint withResponse(DropUnitEndpointResponse response) {
        this.response = response;
        return this;
    }

    // toString

    public String requestInfoString() {
        return "DropUnitEndpoint{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", headers=" + headers +
                ", request=" + request +
                '}';
    }

    @Override
    public String toString() {
        return "DropUnitEndpoint => id = '" + id + "'\n" +
                " url    = '" + url + "'\n" +
                " method = '" + method + "'\n" +
                " delay  = " + delay + "'\n" +
                " count  = " + count;

    }

    // hashCode

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .append(url)
                .append(method)
                .append(headers)
                .append(delay)
                .append(count)
                .append(request)
                .append(response)
                .toHashCode();
    }

    // comparator

    @Override
    public int compareTo(DropUnitEndpoint o) {
        return o.getUrl().length() - this.getUrl().length();
    }
}
