package com.movieapp.core.config;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Movie API Configuration")
public @interface MovieApiConfig {

    @AttributeDefinition(name = "API Key", description = "API key for accessing the movie API")
    String apiKey() default "805060ea63e5ef2695e71512c2dbc79e";

    @AttributeDefinition(name = "API URL", description = "Base URL for the movie API")
    String apiUrl() default "https://api.themoviedb.org/3/";
}
