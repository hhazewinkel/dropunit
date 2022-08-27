package net.lisanza.dropunit.client;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.File;
import java.io.IOException;

import static net.lisanza.dropunit.server.utils.FileUtils.readFromFile;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;

public class BaseHttpClient {

    protected final String baseUrl;

    public BaseHttpClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public HttpResponse invokeHttpPost(String endpoint,
                                       String contentType,
                                       File requestDataFile)
            throws IOException {
        return invokeHttpPost(endpoint, contentType, requestDataFile, null);
    }

    public HttpResponse invokeHttpPost(String endpoint,
                                       String contentType,
                                       File requestDataFile,
                                       RequestConfig requestConfig)
            throws IOException {
        return invokeHttpPost(endpoint, contentType, readFromFile(requestDataFile), requestConfig);
    }

    public HttpResponse invokeHttpPost(String endpoint,
                                       String contentType,
                                       String requestData)
            throws IOException {
        return invokeHttpPost(endpoint, contentType, requestData, null);
    }

    public HttpResponse invokeHttpPost(String endpoint,
                                       String contentType,
                                       String requestData,
                                       RequestConfig requestConfig)
            throws IOException {
        org.apache.http.client.HttpClient client = getHttpClient(requestConfig);
        HttpPost httpPost = new HttpPost(baseUrl + endpoint);

        StringEntity entity = new StringEntity(requestData, "UTF-8");
        entity.setContentType(contentType);
        httpPost.setEntity(entity);

        return client.execute(httpPost);
    }

    public HttpResponse invokeHttpPut(String endpoint,
                                      String contentType,
                                      File requestDataFile)
            throws IOException {
        return invokeHttpPut(endpoint, contentType, requestDataFile, null);
    }

    public HttpResponse invokeHttpPut(String endpoint,
                                      String contentType,
                                      File requestDataFile,
                                      RequestConfig requestConfig)
            throws IOException {
        return invokeHttpPut(endpoint, contentType, readFromFile(requestDataFile), requestConfig);
    }

    public HttpResponse invokeHttpPut(String endpoint,
                                      String contentType,
                                      String requestData)
            throws IOException {
        return invokeHttpPut(endpoint, contentType, requestData, null);
    }

    public HttpResponse invokeHttpPut(String endpoint,
                                      String contentType,
                                      String requestData,
                                      RequestConfig requestConfig)
            throws IOException {
        org.apache.http.client.HttpClient client = getHttpClient(requestConfig);
        HttpPut httpPut = new HttpPut(baseUrl + endpoint);

        StringEntity entity = new StringEntity(requestData, "UTF-8");
        entity.setContentType(contentType);
        httpPut.setEntity(entity);

        return client.execute(httpPut);
    }

    public HttpResponse invokeHttpDelete(String endpoint)
            throws IOException {
        return invokeHttpDelete(endpoint, null);
    }

    public HttpResponse invokeHttpDelete(String endpoint,
                                         RequestConfig requestConfig)
            throws IOException {
        org.apache.http.client.HttpClient client = getHttpClient(requestConfig);
        HttpDelete httpDelete = new HttpDelete(baseUrl + endpoint);
        return client.execute(httpDelete);
    }

    public HttpResponse invokeHttpGet(String endpoint)
            throws IOException {
        return invokeHttpGet(endpoint, null, null);
    }

    public HttpResponse invokeHttpGet(String endpoint,
                                      Header[] headers)
            throws IOException {
        return invokeHttpGet(endpoint, headers, null);
    }

    public HttpResponse invokeHttpGet(String endpoint,
                                      RequestConfig requestConfig) throws IOException {
        return invokeHttpGet(endpoint, null, requestConfig);
    }

    public HttpResponse invokeHttpGet(String endpoint,
                                      Header[] headers,
                                      RequestConfig requestConfig) throws IOException {
        HttpClient client = getHttpClient(requestConfig);
        HttpGet request = new HttpGet(baseUrl + endpoint);
        if (isNotEmpty(headers)) {
            request.setHeaders(headers);
        }
        return client.execute(request);
    }

    protected HttpClient getHttpClient(RequestConfig requestConfig) {
        return HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .disableRedirectHandling()
                .build();
    }

    public HttpResponse invokeHttpPatch(String endpoint,
                                       String contentType,
                                       File requestDataFile)
            throws IOException {
        return invokeHttpPatch(endpoint, contentType, requestDataFile, null);
    }

    public HttpResponse invokeHttpPatch(String endpoint,
                                       String contentType,
                                       File requestDataFile,
                                       RequestConfig requestConfig)
            throws IOException {
        return invokeHttpPatch(endpoint, contentType, readFromFile(requestDataFile), requestConfig);
    }

    public HttpResponse invokeHttpPatch(String endpoint,
                                       String contentType,
                                       String requestData)
            throws IOException {
        return invokeHttpPatch(endpoint, contentType, requestData, null);
    }

    public HttpResponse invokeHttpPatch(String endpoint,
                                       String contentType,
                                       String requestData,
                                       RequestConfig requestConfig)
            throws IOException {
        org.apache.http.client.HttpClient client = getHttpClient(requestConfig);
        HttpPatch httpPatch = new HttpPatch(baseUrl + endpoint);

        StringEntity entity = new StringEntity(requestData, "UTF-8");
        entity.setContentType(contentType);
        httpPatch.setEntity(entity);

        return client.execute(httpPatch);
    }
}
