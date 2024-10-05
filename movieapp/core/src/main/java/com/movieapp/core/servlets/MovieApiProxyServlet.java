package com.movieapp.core.servlets;

import com.movieapp.core.config.MovieApiConfig;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

@Component(
        service = { Servlet.class },
        property = {
                "sling.servlet.paths=/bin/movies"
        }
)
public class MovieApiProxyServlet extends SlingAllMethodsServlet {

    @Reference
    private MovieApiConfig movieApiConfig; // Inject the OSGi configuration

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String endpoint = request.getParameter("endpoint");
        String apiUrl = movieApiConfig.apiUrl();
        String apiKey = movieApiConfig.apiKey();

        // Construct the API URL with key
        String fullUrl = apiUrl + endpoint + "?api_key=" + apiKey + "&language=en-US";

        // Call external API and return the data
        URL url = new URL(fullUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        Scanner scanner = new Scanner(connection.getInputStream());
        StringBuilder data = new StringBuilder();
        while (scanner.hasNext()) {
            data.append(scanner.nextLine());
        }
        scanner.close();

        // Return the API response to the client
        response.setContentType("application/json");
        response.getWriter().write(data.toString());
    }
}
