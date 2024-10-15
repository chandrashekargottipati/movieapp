package com.movieapp.core.services;

import com.movieapp.core.config.FakeStoreApiConfiguration;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = FakeStoreApiService.class, immediate = true)
@Designate(ocd = FakeStoreApiConfiguration.class)
public class FakeStoreApiServiceImpl implements FakeStoreApiService {

    private static final Logger log = LoggerFactory.getLogger(FakeStoreApiServiceImpl.class);

    private FakeStoreApiConfiguration config;

    @Activate
    protected void activate(FakeStoreApiConfiguration config) {
        this.config = config;
        log.info("FakeStoreApiService activated with URL: {}", config.apiUrl());
    }

    @Override
    public JSONArray getProducts() throws Exception {
        String url = config.apiUrl() + config.productsEndpoint();
        return fetchJsonArray(url);
    }

    @Override
    public String getProduct(int id) throws Exception {
        String url = config.apiUrl() + config.productsEndpoint() + "/" + id;
        return fetchString(url);
    }

    @Override
    public JSONArray getProductsByCategory(String category) throws Exception {
        String url = config.apiUrl() + "/products/category/" + category;
        return fetchJsonArray(url);
    }

    private JSONArray fetchJsonArray(String url) throws Exception {
        String jsonStr = fetchString(url);
        return new JSONArray(jsonStr);
    }

    private String fetchString(String url) throws Exception {
        try (var httpClient = HttpClients.createDefault()) {
            var request = new HttpGet(url);

            try (var response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String jsonResponse = EntityUtils.toString(response.getEntity());

                if (statusCode == 200) {
                    return jsonResponse;
                } else {
                    log.error("Failed to fetch data from {}. Status code: {}", url, statusCode);
                    throw new Exception("Failed to fetch data");
                }
            }
        }
    }
}