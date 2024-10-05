package com.movieapp.core.services;

import com.movieapp.core.config.MovieApiConfig;
import com.movieapp.core.services.MovieApiService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

@Component(service = MovieApiService.class, immediate = true)
@Designate(ocd = MovieApiConfig.class)
public class MovieApiServiceImpl implements MovieApiService {

    private String apiKey;
    private String apiUrl;

    @Activate
    @Modified
    protected void activate(MovieApiConfig config) {
        this.apiKey = config.apiKey();
        this.apiUrl = config.apiUrl();
    }

    @Override
    public String getApiKey() {
        return this.apiKey;
    }

    @Override
    public String getApiUrl() {
        return this.apiUrl;
    }
}
