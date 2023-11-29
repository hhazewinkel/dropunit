package net.lisanza.dropunit.server.rest.controlers;

import net.lisanza.dropunit.server.rest.constants.RequestMappings;
import net.lisanza.dropunit.server.services.DropUnitCount;
import net.lisanza.dropunit.server.services.DropUnitEndpoint;
import net.lisanza.dropunit.server.services.EndpointRegistrationService;
import net.lisanza.dropunit.server.services.data.ReceivedRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.Enumeration;

@Path(RequestMappings.ROOT_SERVICE)
public class DropUnitController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DropUnitController.class);

    private final DropUnitCount dropUnitCount;
    private final EndpointRegistrationService dropUnitService;
    private final EndpointValidator validator = new EndpointValidator();

    public DropUnitController(EndpointRegistrationService dropUnitService,
                              DropUnitCount dropUnitCount) {
        this.dropUnitService = dropUnitService;
        this.dropUnitCount = dropUnitCount;
    }

    @GET
    @Path("{any: .*}")
    public Response dropUnitGet(@Context HttpServletRequest request) {
        dropUnitCount.incrHttpGet();
        return executeDropUnit(request, "GET", "");
    }

    @POST
    @Path("{any: .*}")
    public Response dropUnitPost(@Context HttpServletRequest request,
                                 String content) {
        dropUnitCount.incrHttpPost();
        return executeDropUnit(request, "POST", content);
    }

    @PUT
    @Path("{any: .*}")
    public Response dropUnitPut(@Context HttpServletRequest request,
                                String content) {
        dropUnitCount.incrHttpPut();
        return executeDropUnit(request, "PUT", content);
    }

    @DELETE
    @Path("{any: .*}")
    public Response dropUnitDelete(@Context HttpServletRequest request,
                                   String content) {
        dropUnitCount.incrHttpDelete();
        return executeDropUnit(request, "DELETE", content);
    }

    @PATCH
    @Path("{any: .*}")
    public Response dropUnitPatch(@Context HttpServletRequest request,
                                  String content) {
        dropUnitCount.incrHttpPut();
        return executeDropUnit(request, "PATCH", content);
    }

    public Response executeDropUnit(HttpServletRequest request, String method, String content) {
        ReceivedRequest receivedRequest = new ReceivedRequest()
                .withPath(request.getPathInfo())
                .withQueryString(request.getQueryString())
                .withMethod(method)
                .withReceived(content);
        Enumeration<String> headerNames = request.getHeaderNames();
        String name;
        while (headerNames.hasMoreElements()) {
            name = headerNames.nextElement();
            receivedRequest.addHeader(name, request.getHeader(name));
        }
        LOGGER.debug("received request: {}", receivedRequest);
        return processReceivedRequest(receivedRequest);
    }

    public Response processReceivedRequest(ReceivedRequest receivedRequest) {
        if (receivedRequest == null) {
            LOGGER.warn("'received request' is missing!");
            throw new BadRequestException("'received request' is missing!");
        }
        for (DropUnitEndpoint endpoint : dropUnitService.lookupEndpoints(receivedRequest.getUrl(), receivedRequest.getMethod())) {
            try {
                // validate if this is the request endpoint to be used
                validator.validate(endpoint, receivedRequest);
                // request received
                endpoint.addReceived(receivedRequest);
                LOGGER.debug(endpoint.requestInfoString());
                // Response build up
                return generateResponse(endpoint);
            } catch (ValidationException e) {
                LOGGER.debug("{}\n{}\n{}", e.getMessage(),
                        endpoint.requestInfoString(),
                        receivedRequest.getBody());
            }
        }
        LOGGER.warn("missing registration: {}", receivedRequest);
        dropUnitService.registerNotFound(receivedRequest);
        throw new NotFoundException("missing registration: " + receivedRequest.getUrl());
    }

    private Response generateResponse(DropUnitEndpoint endpoint) {
        waitToRespond(endpoint.getDelay());
        Response.ResponseBuilder responseBuilder = buildResponse(endpoint);
        addHeaders(endpoint, responseBuilder);
        if (endpoint.getResponse() != null) {
            addContentType(endpoint, responseBuilder);
            addContent(endpoint, responseBuilder);
        }
        return responseBuilder.build();
    }

    private Response.ResponseBuilder buildResponse(DropUnitEndpoint dropUnitEndpoint) {
        LOGGER.info("response code: {}", dropUnitEndpoint.getResponse().getCode());
        return Response.status(Response.Status.fromStatusCode(dropUnitEndpoint.getResponse().getCode()));
    }

    private void addHeaders(DropUnitEndpoint dropUnitEndpoint, Response.ResponseBuilder responseBuilder) {
        if ((dropUnitEndpoint.getResponse().getHeaders() != null)
                && (0 < dropUnitEndpoint.getResponse().getHeaders().size())) {
            for (String hdrKey : dropUnitEndpoint.getResponse().getHeaders().keySet()) {
                responseBuilder.header(hdrKey, dropUnitEndpoint.getResponse().getHeaders().get(hdrKey));
            }
        }
    }

    private void addContentType(DropUnitEndpoint dropUnitEndpoint, Response.ResponseBuilder responseBuilder) {
        if (dropUnitEndpoint.getResponse().getContentType() != null) {
            responseBuilder.header("Content-type", dropUnitEndpoint.getResponse().getContentType());
        }
    }

    private void addContent(DropUnitEndpoint dropUnitEndpoint, Response.ResponseBuilder responseBuilder) {
        if (dropUnitEndpoint.getResponse().getBody() != null) {
            responseBuilder.entity(dropUnitEndpoint.getResponse().getBody());
        } else {
            responseBuilder.entity("");
        }
    }

    private void waitToRespond(int delay) {
        if (0 < delay) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                // do nothing
            }
        }
    }
}
