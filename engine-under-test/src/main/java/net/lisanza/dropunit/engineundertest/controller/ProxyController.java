package net.lisanza.dropunit.engineundertest.controller;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.HttpHeaders.COOKIE;
import static javax.ws.rs.core.HttpHeaders.LOCATION;
import static javax.ws.rs.core.HttpHeaders.SET_COOKIE;
import static javax.ws.rs.core.HttpHeaders.USER_AGENT;

@Path("/")
public class ProxyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyController.class);
    private HttpClient client;

    private String proxyUrl;

    public ProxyController(final String proxyUrl) {
        this.proxyUrl = proxyUrl;
        client = HttpClientBuilder.create()
                .disableRedirectHandling()
                .build();
    }

    @GET
    @Path("{any: .*}")
    public Response proxyGet(@Context HttpServletRequest request) {
        LOGGER.info("GET: {}", request.getRequestURI());
        return buildProxyRequest(request, new HttpGet());
    }

    @POST
    @Path("{any: .*}")
    public Response proxyPost(@Context HttpServletRequest request,
                              String content) {
        LOGGER.info("POST: {}", request.getRequestURI());
        return buildProxyRequest(request, new HttpPost(), content);
    }

    @PUT
    @Path("{any: .*}")
    public Response proxyPut(@Context HttpServletRequest request,
                             String content) {
        LOGGER.info("PUT: {}", request.getRequestURI());
        return buildProxyRequest(request, new HttpPut(), content);
    }

    @DELETE
    @Path("{any: .*}")
    public Response proxyDelete(@Context HttpServletRequest request,
                                String content) {
        LOGGER.info("DELETE: {}", request.getRequestURI());
        return buildProxyRequest(request, new HttpDelete());
    }

    @PATCH
    @Path("{any: .*}")
    public Response proxyPatch(@Context HttpServletRequest request,
                                String content) {
        LOGGER.info("PATCH: {}", request.getRequestURI());
        return buildProxyRequest(request, new HttpPatch(), content);
    }

    private Response buildProxyRequest(HttpServletRequest request,
                                       HttpEntityEnclosingRequestBase method,
                                       String content) {
        StringEntity entity = new StringEntity(content, "UTF-8");
        method.setEntity(entity);
        return buildProxyRequest(request, method);
    }

    private Response buildProxyRequest(HttpServletRequest request,
                                       HttpRequestBase method) {
        LOGGER.info("proxy uri       : '{}'", request.toString());

        try {
            method.setURI(new URI(proxyUrl + constructUrl(request)));
            LOGGER.info("proxy method     : '{}'", method.toString());
            buildProxyRequestHeaders(request, method, AUTHORIZATION);
            buildProxyRequestHeaders(request, method, USER_AGENT);
            buildProxyRequestHeaders(request, method, CONTENT_TYPE);
            buildProxyRequestHeaders(request, method, LOCATION);
            buildProxyRequestHeaders(request, method, COOKIE);
            buildProxyRequestHeaders(request, method, SET_COOKIE);

            return buildProxyResponse(client.execute(method));
        } catch (URISyntaxException e) {
            LOGGER.info(e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (IOException e) {
            LOGGER.info(e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private void buildProxyRequestHeaders(HttpServletRequest request,
                                          HttpRequestBase method,
                                          String name) {
        Enumeration<String> hdrs = request.getHeaders(name);
        String value;
        while (hdrs.hasMoreElements()) {
            value = hdrs.nextElement();
            LOGGER.info("proxy " + name + ": '{}'", value);
            method.setHeader(name, value);
        }
    }

    private String constructUrl(HttpServletRequest request) {
        if ((request.getQueryString() == null) || request.getQueryString().isEmpty()) {
            return request.getPathInfo();
        } else {
            return request.getPathInfo() + "?" + request.getQueryString();
        }
    }

    private Response buildProxyResponse(HttpResponse response) throws IOException {
        Response.ResponseBuilder responseBuilder = Response
                .status(Response.Status.fromStatusCode(response.getStatusLine().getStatusCode()));
        buildHeaders(response, responseBuilder);
        if (response.getEntity() != null) {
            String body = EntityUtils.toString(response.getEntity(), "UTF-8");
            printResponse(response, body);
            return responseBuilder.entity(body).build();
        }
        return responseBuilder.build();
    }

    private void buildHeaders(HttpResponse response,
                              Response.ResponseBuilder responseBuilder) {
        buildHeaders(response, responseBuilder, AUTHORIZATION);
        buildHeaders(response, responseBuilder, CONTENT_TYPE);
        buildHeaders(response, responseBuilder, LOCATION);
        buildHeaders(response, responseBuilder, COOKIE);
        buildHeaders(response, responseBuilder, SET_COOKIE);
    }

    private void buildHeaders(HttpResponse response,
                              Response.ResponseBuilder responseBuilder,
                              String hdrName) {
        Header[] hdrs = response.getHeaders(hdrName);
        if (0 < hdrs.length) {
            LOGGER.info(hdrName);
            for (Header hdr : hdrs) {
                LOGGER.info(hdr.getName() + ": " + hdr.getValue());
                responseBuilder.header(hdr.getName(), hdr.getValue());
                LOGGER.info(hdr.getName() + ": " + hdr.getValue());
            }
        }
    }

    private void printResponse(HttpResponse response, String body) throws IOException {
        LOGGER.info("build response: {}", response.getStatusLine());
        for (Header hdr : response.getAllHeaders()) {
            LOGGER.info(hdr.getName() + ": " + hdr.getValue());
        }
        LOGGER.info("build response: {}", body);
    }
}

