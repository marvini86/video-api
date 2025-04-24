package com.eeze.app.videoapi.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

/** Configuration for OpenAPI documentation. */
@OpenAPIDefinition(
    info =
        @Info(
            title = "Video Streaming API",
            version = "v1",
            description = "API for managing video streamings",
            contact = @Contact(name = "Your Name", email = "your.email@example.com"),
            license = @License(name = "Apache 2.0", url = "http://springdoc.org")),
    servers = @Server(url = "http://localhost:8080", description = "Local server"))
public class OpenApiConfig {}
