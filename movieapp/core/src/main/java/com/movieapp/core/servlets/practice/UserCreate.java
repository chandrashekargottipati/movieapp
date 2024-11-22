package com.movieapp.core.servlets.practice;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.Servlet;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component(
        service = Servlet.class
)
@SlingServletPaths("/bin/userCreate")
public class UserCreate extends SlingAllMethodsServlet {

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        // Get the user ID from the request
        String userId = request.getParameter("id");

        ResourceResolver resourceResolver = request.getResourceResolver();
        response.setContentType("application/json");

        try {
            if (userId != null && !userId.isEmpty()) {
                // Fetch individual user data by ID
                JsonObject userJson = getUserDataById(resourceResolver, userId);
                if (userJson != null) {
                    response.getWriter().write(userJson.toString());
                } else {
                    response.setStatus(404);
                    response.getWriter().write("{\"error\": \"User not found\"}");
                }
            } else {
                // Fetch all users
                JsonArray usersJson = getAllUsers(resourceResolver);
                response.getWriter().write(usersJson.toString());
            }
        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Method to get individual user by ID
    private JsonObject getUserDataById(ResourceResolver resourceResolver, String userId) {
        try {
            Resource userResource = resourceResolver.resolve("/content/users/" + userId);
            if (userResource != null) {
                Node userNode = userResource.adaptTo(Node.class);
                return userNode != null ? buildUserJson(userNode) : null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method to get all users
    private JsonArray getAllUsers(ResourceResolver resourceResolver) {
        JsonArrayBuilder usersArrayBuilder = Json.createArrayBuilder();
        try {
            Resource usersResource = resourceResolver.resolve("/content/users");
            if (usersResource != null) {
                for (Resource userResource : usersResource.getChildren()) {
                    Node userNode = userResource.adaptTo(Node.class);
                    if (userNode != null) {
                        JsonObject userJson = buildUserJson(userNode);
                        usersArrayBuilder.add(userJson);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usersArrayBuilder.build();
    }

    // Helper method to build a JsonObject from the user node
    private JsonObject buildUserJson(Node userNode) throws RepositoryException {
        JsonObjectBuilder userJsonBuilder = Json.createObjectBuilder();

        // Safely retrieve properties with null checks
        userJsonBuilder.add("id", userNode.hasProperty("id") ? userNode.getProperty("id").getString() : "N/A");
        userJsonBuilder.add("firstname", userNode.hasProperty("firstname") ? userNode.getProperty("firstname").getString() : "N/A");
        userJsonBuilder.add("lastname", userNode.hasProperty("lastname") ? userNode.getProperty("lastname").getString() : "N/A");
        userJsonBuilder.add("email", userNode.hasProperty("email") ? userNode.getProperty("email").getString() : "N/A");
        userJsonBuilder.add("phone", userNode.hasProperty("phone") ? userNode.getProperty("phone").getString() : "N/A");

        return userJsonBuilder.build();
    }

    // Remaining methods (doPost, doPut, doDelete) remain the same as in the original code
    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String userid = request.getParameter("userid");
        String firstname = request.getParameter("firstName");
        String lastname = request.getParameter("lastName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");

        ResourceResolver resourceResolver = request.getResourceResolver();
        Resource userResources = resourceResolver.resolve("/content/users");
        Resource user = resourceResolver.resolve("/content/users/" + userid);

        // Create the user if it does not exist
        if (userResources != null) {
            Map<String, Object> props = new HashMap<>();
            props.put("id", userid);
            props.put("firstname", firstname);
            props.put("lastname", lastname);
            props.put("email", email);
            props.put("phone", phone);

            resourceResolver.create(userResources, userid, props);
            resourceResolver.commit();
            response.getWriter().write("User created successfully.");
        } else {
            response.getWriter().write("Error: Unable to resolve user resource.");
        }
    }

    @Override
    protected void doPut(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String userid = request.getParameter("userid");
        String firstname = request.getParameter("firstName");
        String lastname = request.getParameter("lastName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");

        ResourceResolver resourceResolver = request.getResourceResolver();
        Resource userResource = resourceResolver.resolve("/content/users/" + userid);

        if (userResource != null) {
            // Use ModifiableValueMap to modify resource properties
            ModifiableValueMap valueMap = userResource.adaptTo(ModifiableValueMap.class);

            if (valueMap != null) {
                // Set the new properties (or modify existing ones)
                valueMap.put("firstname", firstname != null ? firstname : "N/A");
                valueMap.put("lastname", lastname != null ? lastname : "N/A");
                valueMap.put("email", email != null ? email : "N/A");
                valueMap.put("phone", phone != null ? phone : "N/A");

                // Commit the changes to the repository
                resourceResolver.commit();
                response.getWriter().write("User updated successfully.");
            } else {
                response.getWriter().write("Unable to modify user resource.");
            }
        } else {
            response.getWriter().write("User not found.");
        }
    }

    @Override
    protected void doDelete(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String userid = request.getParameter("userid");
        ResourceResolver resourceResolver = request.getResourceResolver();
        Resource userResource = resourceResolver.resolve("/content/users/" + userid);

        if (userResource != null) {
            // Delete the user resource
            resourceResolver.delete(userResource);
            resourceResolver.commit();
            response.getWriter().write("User deleted successfully.");
        } else {
            response.getWriter().write("User not found.");
        }
    }
}