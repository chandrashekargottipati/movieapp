package com.movieapp.core.servlets;

import com.movieapp.core.services.FakeStoreApiService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONArray;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component(service = Servlet.class,
        property = {
                "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                "sling.servlet.paths=" + "/bin/myapp/fakestore/products"
        })
public class FakeStoreApiServlet extends SlingAllMethodsServlet {

    private static final Logger log = LoggerFactory.getLogger(FakeStoreApiServlet.class);

    @Reference
    private FakeStoreApiService fakeStoreApiService;

    private static final List<String> VALID_CATEGORIES = Arrays.asList(
            "electronics",
            "jewelery",
            "men's clothing",
            "women's clothing"
    );

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");

        String productId = request.getParameter("id");
        String category = request.getParameter("category");

        try {
            if (productId != null && !productId.isEmpty()) {
                // Fetch product by ID
                String product = fakeStoreApiService.getProduct(Integer.parseInt(productId));
                response.getWriter().write(product);
            } else if (category != null && !category.isEmpty()) {
                // Validate the category and fetch products from the service
                if (VALID_CATEGORIES.contains(category)) {
                    JSONArray filteredProducts = fakeStoreApiService.getProductsByCategory(category);
                    response.getWriter().write(filteredProducts.toString());
                } else {
                    // Handle invalid category
                    response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Invalid category. Valid categories are: "
                            + String.join(", ", VALID_CATEGORIES) + "\"}");
                }
            } else {
                // Fetch all products if no parameters are provided
                JSONArray products = fakeStoreApiService.getProducts();
                response.getWriter().write(products.toString());
            }
        } catch (Exception e) {
            log.error("Error fetching products", e);
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
