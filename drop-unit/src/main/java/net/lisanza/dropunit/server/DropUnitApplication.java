package net.lisanza.dropunit.server;

import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Environment;
import net.lisanza.dropunit.server.config.yml.DropUnitConfiguration;
import net.lisanza.dropunit.server.config.yml.EndpointDocument;
import net.lisanza.dropunit.server.health.HealthCheckService;
import net.lisanza.dropunit.server.mappers.ExceptionHandler;
import net.lisanza.dropunit.server.mappers.ValidationHandler;
import net.lisanza.dropunit.server.rest.controlers.DropAssertionController;
import net.lisanza.dropunit.server.rest.controlers.DropRegistrationController;
import net.lisanza.dropunit.server.rest.controlers.DropUnitController;
import net.lisanza.dropunit.server.rest.dto.DropUnitHeaderDto;
import net.lisanza.dropunit.server.services.DropUnitCount;
import net.lisanza.dropunit.server.services.DropUnitEndpoint;
import net.lisanza.dropunit.server.services.DropUnitEndpointResponse;
import net.lisanza.dropunit.server.services.DropUnitService;
import org.glassfish.jersey.logging.LoggingFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.lisanza.dropunit.server.utils.FileUtils.readFromFile;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class DropUnitApplication<TypeOfConfiguration extends DropUnitConfiguration> extends Application<TypeOfConfiguration> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DropUnitApplication.class);

    @Override
    public void run(TypeOfConfiguration configuration, Environment environment) {
        // Setup dropunit service
        DropUnitCount dropUnitCount = new DropUnitCount();
        DropUnitService dropUnitService = new DropUnitService();

        // handle initial endpoints
        initConfig(configuration, dropUnitService);

        // Registration of the handlers / mappings
        environment.jersey().register(new ExceptionHandler());
        environment.jersey().register(new ValidationHandler());

        // Logging inbound request/response
        environment.jersey().register(new LoggingFeature(java.util.logging.Logger.getLogger("net.lisanza.dropunit.simulator.logging"),
                LoggingFeature.Verbosity.PAYLOAD_ANY));

        // Registration of the REST controllers
        environment.jersey().register(new DropUnitController(dropUnitService, dropUnitCount));
        environment.jersey().register(new DropRegistrationController(dropUnitService, dropUnitCount));
        environment.jersey().register(new DropAssertionController(dropUnitService, dropUnitCount));

        // Registration of the required Dropwizard health checks
        environment.healthChecks().register("HEALTH", new HealthCheckService());
    }

    protected void initConfig(TypeOfConfiguration config,
                              DropUnitService dropUnitService) {
        if (config.getEndpoints() != null) {
            for (EndpointDocument endpointDocument : config.getEndpoints()) {
                configEndpoint(endpointDocument, dropUnitService);
            }
        }
        dropUnitService.dropAll();
    }

    private void configEndpoint(EndpointDocument endpointDocument,
                                DropUnitService dropUnitService) {
        try {
            DropUnitEndpointResponse response = new DropUnitEndpointResponse()
                    .withCode(endpointDocument.getResponseCode())
                    .withHeaders(endpointDocument.getResponseHeaders())
                    .withContentType(endpointDocument.getResponseContentType())
                    .withBody("");
            if (isNotEmpty(endpointDocument.getResponseBodyFileName())) {
                response.withBody(readFromFile(endpointDocument.getResponseBodyFileName()));
            }
            LOGGER.debug("response: {}", response);
            dropUnitService.registerDefault(new DropUnitEndpoint()
                    .withUrl(endpointDocument.getPath())
                    .withMethod(endpointDocument.getMethod())
                    .withHeaders(toListOfHeaderDto(endpointDocument.getHeaders()))
                    .withDelay(endpointDocument.getDelay())
                    .withRequest(endpointDocument.getRequest())
                    .withResponse(response));
            LOGGER.debug("response: {}", response);
        } catch (NullPointerException e) {
            LOGGER.error("Cannot load endpoint: {} / {}: {}",
                    endpointDocument.getPath(), endpointDocument.getMethod(), e.getMessage());
            return;
        } catch (IOException e) {
            LOGGER.error("Cannot read endpoint {} / {} response body filename: {}",
                    endpointDocument.getPath(), endpointDocument.getMethod(),
                    endpointDocument.getResponseBodyFileName());
            LOGGER.warn("", e);
            return;
        }
        LOGGER.info("endpoints loaded: {}", endpointDocument.toString());
        LOGGER.debug("endpoints: {}", dropUnitService.infoLoadedEndpoints(new StringBuilder()));
    }

    private List<DropUnitHeaderDto> toListOfHeaderDto(Map<String, String> hdrs) {
        List<DropUnitHeaderDto> result = new ArrayList<>();
        if (hdrs != null) {
            for (String hdrKey : hdrs.keySet()) {
                result.add(new DropUnitHeaderDto(hdrKey, hdrs.get(hdrKey)));
            }
        }
        return result;
    }
}
