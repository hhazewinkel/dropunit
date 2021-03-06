package net.lisanza.dropunit.server.rest.controlers;

import net.lisanza.dropunit.server.rest.constants.RequestMappings;
import net.lisanza.dropunit.server.rest.dto.DropUnitEndpointDto;
import net.lisanza.dropunit.server.rest.dto.DropUnitRegistrationResponseDto;
import net.lisanza.dropunit.server.rest.dto.DropUnitRequestPatternsDto;
import net.lisanza.dropunit.server.services.DropUnitCount;
import net.lisanza.dropunit.server.services.DropUnitEndpoint;
import net.lisanza.dropunit.server.services.DropUnitEndpointRequest;
import net.lisanza.dropunit.server.services.DropUnitEndpointResponse;
import net.lisanza.dropunit.server.services.DropUnitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

import static net.lisanza.dropunit.server.rest.constants.RequestMappings.URI_CLEARALLDROPS;
import static net.lisanza.dropunit.server.rest.constants.RequestMappings.URI_COUNT_DROPID;
import static net.lisanza.dropunit.server.rest.constants.RequestMappings.URI_COUNT_NOTFOUND;
import static net.lisanza.dropunit.server.rest.constants.RequestMappings.URI_DELIVERY_ENDPOINT;
import static net.lisanza.dropunit.server.rest.constants.RequestMappings.URI_DELIVERY_ENDPOINT_DROPID;
import static net.lisanza.dropunit.server.rest.constants.RequestMappings.URI_DELIVERY_ENDPOINT_DROPID_REQUESTBODY;
import static net.lisanza.dropunit.server.rest.constants.RequestMappings.URI_DELIVERY_ENDPOINT_DROPID_RESPONSEBODY;
import static net.lisanza.dropunit.server.rest.constants.RequestMappings.URI_GETALLDROPS;
import static net.lisanza.dropunit.server.rest.constants.RequestMappings.URI_GETALLNOTFOUNDS;
import static net.lisanza.dropunit.server.rest.constants.RequestMappings.URI_RECIEVED_DROPID_NUMBER;

@Produces(MediaType.APPLICATION_JSON) // we produce always JSON. We might read other media types.
@Path(RequestMappings.DROP_UNIT_SERVICE)
public class DropRegistrationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DropRegistrationController.class);

    private final DropUnitCount dropUnitCount;
    private final DropUnitService dropUnitService;

    public DropRegistrationController(DropUnitService dropUnitService,
                                      DropUnitCount dropUnitCount) {
        this.dropUnitService = dropUnitService;
        this.dropUnitCount = dropUnitCount;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(URI_DELIVERY_ENDPOINT)
    public DropUnitRegistrationResponseDto registerEndpoint(@Valid DropUnitEndpointDto dto) {
        try {
            LOGGER.debug("Called registerEndpoint");
            return new DropUnitRegistrationResponseDto()
                    .withResult("OK")
                    .withId(dropUnitService.register(new DropUnitEndpoint()
                            .withUrl(dto.getUrl())
                            .withHeaders(dto.getRequestHeaders())
                            .withMethod(dto.getMethod())
                            .withResponse(new DropUnitEndpointResponse()
                                    .withCode(dto.getResponseCode())
                                    .withHeaders(dto.getResponseHeaders()))
                            .withDelay(dto.getResponseDelay())));
        } catch (Exception e) {
            LOGGER.warn("Failure in registration of endpoint", e);
        }
        throw new InternalServerErrorException();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(URI_DELIVERY_ENDPOINT_DROPID_REQUESTBODY)
    public DropUnitRegistrationResponseDto registerRequestPatterns(@PathParam("dropId") String dropId,
                                                                   @Valid DropUnitRequestPatternsDto requestDto) {
        try {
            LOGGER.debug("Called registerRequestPatterns {}", dropId);
            return new DropUnitRegistrationResponseDto()
                    .withId(dropId)
                    .withResult(dropUnitService.registerRequest(dropId, new DropUnitEndpointRequest()
                            .withContentType(requestDto.getRequestContentType())
                            .withPatterns(requestDto.getPatterns())));
        } catch (Exception e) {
            LOGGER.warn("Failure generating response registerDropUnitRequestBody", e);
        }
        throw new InternalServerErrorException();
    }

    @PUT
    @Path(URI_DELIVERY_ENDPOINT_DROPID_REQUESTBODY)
    public DropUnitRegistrationResponseDto registerRequestBody(@Context HttpServletRequest request,
                                                               @PathParam("dropId") String dropId,
                                                               String requestBody) {
        try {
            LOGGER.debug("Called registerRequestBody {}", dropId);
            return new DropUnitRegistrationResponseDto()
                    .withId(dropId)
                    .withResult(dropUnitService.registerRequest(dropId, new DropUnitEndpointRequest()
                            .withContentType(request.getContentType())
                            .withBody(requestBody)));
        } catch (Exception e) {
            LOGGER.warn("Failure generating response registerDropUnitRequestBody", e);
        }
        throw new InternalServerErrorException();
    }

    @PUT
    @Path(URI_DELIVERY_ENDPOINT_DROPID_RESPONSEBODY)
    public DropUnitRegistrationResponseDto registerResponseBody(@Context HttpServletRequest request,
                                                                @PathParam("dropId") String dropId,
                                                                String responseBody) {
        try {
            LOGGER.debug("Called registerResponseBody {}", dropId);
            return new DropUnitRegistrationResponseDto()
                    .withId(dropId)
                    .withResult(dropUnitService.registerResponse(dropId, new DropUnitEndpointResponse()
                            .withContentType(request.getContentType())
                            .withBody(responseBody)));
        } catch (Exception e) {
            LOGGER.warn("Failure generating response putDropUnitResponseBody", e);
        }
        throw new InternalServerErrorException();
    }

    @DELETE
    @Path(URI_CLEARALLDROPS)
    public String clearAllDrop() {
        LOGGER.debug("Called clearAllDrop");
        return dropUnitService.dropAll();
    }

    @GET
    @Path(URI_GETALLDROPS)
    public Collection<DropUnitEndpoint> getAllDrop() {
        try {
            LOGGER.debug("Called getAllDrop");
            return dropUnitService.getAllRegistrations();
        } catch (Exception e) {
            LOGGER.warn("Failure generating response getAllDrop", e);
        }
        throw new InternalServerErrorException();
    }

    @GET
    @Path(URI_GETALLNOTFOUNDS)
    public Collection<DropUnitEndpoint> getNotFound() {
        try {
            LOGGER.debug("Called getAllDrop");
            return dropUnitService.getAllRegistrations();
        } catch (Exception e) {
            LOGGER.warn("Failure generating response getNotFound", e);
        }
        throw new InternalServerErrorException();
    }

    @GET
    @Path(URI_COUNT_NOTFOUND)
    public DropUnitRegistrationResponseDto getCountNotFound() {
        try {
            LOGGER.debug("Called getCountNotFound");
            return new DropUnitRegistrationResponseDto()
                    .withResult("OK")
                    .withCount(dropUnitService.getAllNotFound().size());
        } catch (Exception e) {
            LOGGER.warn("Failure generating response getCountNotFound", e);
        }
        throw new InternalServerErrorException();
    }

    @GET
    @Path(URI_COUNT_DROPID)
    public DropUnitRegistrationResponseDto getDropCount(@PathParam("dropId") String dropId) {
        boolean found = false;
        try {
            LOGGER.debug("Called getDropCount");
            DropUnitEndpoint endpoint = dropUnitService.lookupEndpoint(dropId);
            if (endpoint != null) {
                found = true;
                return new DropUnitRegistrationResponseDto()
                        .withId(dropId)
                        .withResult("OK")
                        .withCount(endpoint.getCount());
            }
        } catch (Exception e) {
            LOGGER.warn("Failure generating response getDropCount", e);
        }
        if (found) {
            throw new NotFoundException("could not find endpoint:" + dropId);
        } else {
            throw new InternalServerErrorException();
        }
    }

    @DELETE
    @Path(URI_DELIVERY_ENDPOINT_DROPID)
    public DropUnitRegistrationResponseDto deleteEndpoint(@PathParam("dropId") String dropId) {
        boolean found = false;
        try {
            LOGGER.debug("Called getDropCount");
            DropUnitEndpoint endpoint = dropUnitService.deregister(dropId);
            if (endpoint != null) {
                found = true;
                return new DropUnitRegistrationResponseDto()
                        .withId(dropId)
                        .withResult("deleted")
                        .withCount(endpoint.getCount());
            }
        } catch (Exception e) {
            LOGGER.warn("Failure generating response getDropCount", e);
        }
        if (found) {
            throw new NotFoundException("could not find endpoint:" + dropId);
        } else {
            throw new InternalServerErrorException();
        }
    }

    @GET
    @Path(URI_RECIEVED_DROPID_NUMBER)
    public String getRecieved(@PathParam("dropId") String dropId,
                              @PathParam("number") int number) {
        boolean found = false;
        try {
            LOGGER.debug("Called getDropCount");
            DropUnitEndpoint endpoint = dropUnitService.lookupEndpoint(dropId);
            if (endpoint != null) {
                found = true;
                if ((0 < number)
                        && (number <= endpoint.getReceivedSize())) {
                    return endpoint.getReceived(number).getBody();
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Failure generating response getDropCount", e);
        }
        if (found) {
            throw new NotFoundException("could not find endpoint:" + dropId);
        } else {
            throw new InternalServerErrorException();
        }
    }
}
