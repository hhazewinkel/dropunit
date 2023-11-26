package net.lisanza.dropunit.server.services.data;

import net.lisanza.dropunit.server.rest.dto.DropUnitHeaderDto;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class ReceivedRequest {

    private String path;

    private String queryString;

    private ArrayList<String> receivedParameterAndValues = new ArrayList<>();

    private String method;

    private Map<String, String> headers = new Hashtable<>();

    private String body;

    // getters and setters

    public String getPath() {
        return path;
    }

    public String getQueryString() {
        return queryString;
    }

    public ArrayList<String> getParameterAndValues() {
        return receivedParameterAndValues;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void addHeader(String name, String value) {
        this.headers.put(name, value);
    }

    public String getBody() {
        return body;
    }

    public String getContentType() {
        return headers.get("Content-Type");
    }

    // with-builders

    public ReceivedRequest withPath(String path) {
        this.path = path;
        return this;
    }

    public ReceivedRequest withQueryString(String queryString) {
        this.queryString = queryString;
        if (isNotEmpty(queryString)) {
            // split into parameters (key / value cobinations)
            for (String pair : queryString.split("&")) {
                receivedParameterAndValues.add(pair);
            }
        }
        return this;
    }

    public ReceivedRequest withMethod(String method) {
        this.method = method;
        return this;
    }

    public ReceivedRequest withHeaders(List<DropUnitHeaderDto> headers) {
        for (DropUnitHeaderDto header : headers) {
            this.headers.put(header.getName(), header.getValue());
        }
        return this;
    }

    public ReceivedRequest withReceived(String received) {
        this.body = received;
        return this;
    }

    // complex getters

    public String getUrl() {
        StringBuffer url = new StringBuffer();
        if (!path.startsWith("/")) {
            url.append('/');
        }
        url.append(path);
        if (isNotEmpty(queryString)) {
            url.append('?').append(queryString);
        }
        return url.toString();
    }

    // toString

    @Override
    public String toString() {
        return new StringBuilder()
                .append("ReceivedRequest { ")
                .append("path='").append(path).append('\'')
                .append(", queryString='").append(queryString).append('\'')
                .append(", method='").append(method).append('\'')
                .append(", headers=").append(headers)
                .append(", body=").append(body)
                .append('}').toString();
    }

    public static class ReceivedParameter {
        private String key;
        private String value;

        public ReceivedParameter(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
