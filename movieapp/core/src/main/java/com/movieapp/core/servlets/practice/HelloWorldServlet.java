package com.movieapp.core.servlets.practice;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component(service = Servlet.class,
           property = {
               Constants.SERVICE_DESCRIPTION + "=Hello World Servlet",
               "sling.servlet.paths=" + "/bin/helloworld"
           })
public class HelloWorldServlet extends SlingAllMethodsServlet {

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        // Set the response content type
        response.setContentType("text/html");
        
        // Get current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);

        // Generate the HTML response
        String htmlResponse = "<html><head><title>Hello World</title></head><body>"
                + "<h1>Hello World!</h1>"
                + "<p>Current Date and Time: " + formattedDateTime + "</p>"
                + "</body></html>";
        
        // Write the response
        response.getWriter().write(htmlResponse);
    }
}
