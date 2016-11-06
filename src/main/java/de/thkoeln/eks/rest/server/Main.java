package de.thkoeln.eks.rest.server;

import com.fasterxml.jackson.jaxrs.xml.JacksonJaxbXMLProvider;
import de.thkoeln.eks.rest.server.services.DriverService;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOG = Logger.getLogger(Main.class.getName());
    private static final String HOST = "http://localhost/";
    private static final int PORT = 9998;

    public static void main(String[] args) {
        // create config
        ResourceConfig config = new ResourceConfig();
        // register marshaller/unmarshaller
        config.register(JacksonJaxbXMLProvider.class, MessageBodyReader.class, MessageBodyWriter.class);
        // register logging-feature
        config.register(new LoggingFeature(LOG, Level.INFO, null, null));
        // register rest-endpoint
        config.register(DriverService.class);

        // start server
        URI baseUri = UriBuilder.fromUri(HOST).port(PORT).build();
        JdkHttpServerFactory.createHttpServer(baseUri, config);
    }

}
