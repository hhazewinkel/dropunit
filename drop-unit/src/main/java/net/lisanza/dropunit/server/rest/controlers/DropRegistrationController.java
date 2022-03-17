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
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import static net.lisanza.dropunit.server.rest.constants.RequestMappings.URI_DELIVERY_ENDPOINT;
import static net.lisanza.dropunit.server.rest.constants.RequestMappings.URI_DELIVERY_ENDPOINT_DROPID_REQUESTBODY;
import static net.lisanza.dropunit.server.rest.constants.RequestMappings.URI_DELIVERY_ENDPOINT_DROPID_RESPONSEBODY;

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
}
