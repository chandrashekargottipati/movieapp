package com.movieapp.core.services;

import org.json.JSONArray;

public interface FakeStoreApiService {
    JSONArray getProducts() throws Exception;
    String getProduct(int id) throws Exception;

    JSONArray getProductsByCategory(String category) throws Exception;
}