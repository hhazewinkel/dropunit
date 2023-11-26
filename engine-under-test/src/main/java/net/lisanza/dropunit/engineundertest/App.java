package net.lisanza.dropunit.engineundertest;

import com.codahale.metrics.health.HealthCheck;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Environment;
import net.lisanza.dropunit.engineundertest.config.yml.AppConfiguration;
import net.lisanza.dropunit.engineundertest.controller.ProxyController;
import org.glassfish.jersey.logging.LoggingFeature;

public class App extends Application<AppConfiguration> {

    public static void main(String[] args) throws Exception {
        // Start the application
        new App().run(args);
    }

    @Override
    public void run(AppConfiguration configuration, Environment environment) throws Exception {

        // Logging inbound request/response
        environment.jersey().register(new LoggingFeature(java.util.logging.Logger.getLogger("net.lisanza.dropunit.engineundertest.logging"),
                LoggingFeature.Verbosity.PAYLOAD_ANY));

        // Registration of the REST controllers
        environment.jersey().register(new ProxyController(configuration.getProxyUrl()));

        // Registration of the required Dropwizard health checks
        environment.healthChecks().register("HEALTH", new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.unhealthy("No check functions are implemented yet!!!");
            }
        });

    }
}
