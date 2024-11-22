package com.movieapp.core.servlets.practice;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import java.io.IOException;

@Component(service = Servlet.class,
        property = {
                "sling.servlet.paths=" + "/bin/studentname"
        })
public class StudentNameServlet extends SlingAllMethodsServlet {

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        // Get the parameters from the request (if submitted)
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");

        // Check if the parameters are provided and show them, otherwise just show the form
        String formHtml = "<html><body>";

        if (firstName != null && lastName != null && !firstName.isEmpty() && !lastName.isEmpty()) {
            // If first name and last name are provided, show them in a message
            formHtml += "<h2>Thank you, " + firstName + " " + lastName + "!</h2>" +
                    "<p>Your details have been received.</p>";
        } else {
            // Display the form if no parameters are found
            formHtml += "<h2>Student Name Form</h2>" +
                    "<form action='/bin/studentname' method='GET'>" +
                    "<label for='firstName'>First Name:</label>" +
                    "<input type='text' id='firstName' name='firstName' required><br><br>" +
                    "<label for='lastName'>Last Name:</label>" +
                    "<input type='text' id='lastName' name='lastName' required><br><br>" +
                    "<input type='submit' value='Submit'>" +
                    "</form>";
        }

        formHtml += "</body></html>";

        // Set the content type to HTML
        response.setContentType("text/html");

        // Write the HTML to the response
        response.getWriter().write(formHtml);
    }
}
