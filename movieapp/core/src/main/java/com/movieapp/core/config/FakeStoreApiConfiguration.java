package com.movieapp.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Fake Store API Configuration",
        description = "Configuration for Fake Store API Service")
public @interface FakeStoreApiConfiguration {

    @AttributeDefinition(name = "API URL",
            description = "Base URL for the Fake Store API")
    String apiUrl() default "https://fakestoreapi.com";

    @AttributeDefinition(name = "Products Endpoint",
            description = "Endpoint for fetching products")
    String productsEndpoint() default "/products";

    @AttributeDefinition(name = "Connection Timeout",
            description = "Connection timeout in milliseconds")
    int connectionTimeout() default 5000;

    @AttributeDefinition(name = "Read Timeout",
            description = "Read timeout in milliseconds")
    int readTimeout() default 5000;
}