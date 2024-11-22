package com.movieapp.core.servlets.practice;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.json.*;
import javax.servlet.Servlet;
import java.io.IOException;
import java.util.Collections;
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
    private static final Logger log = LoggerFactory.getLogger(UserCreate.class);

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        JsonObjectBuilder responseJson = Json.createObjectBuilder();

        try {
            // Get parameters from the request
            String userId = request.getParameter("userid");
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");

            // Validate required fields
            if (userId == null || userId.isEmpty()) {
                response.setStatus(400);
                responseJson.add("status", "error");
                responseJson.add("message", "User ID is required");
                response.getWriter().write(responseJson.build().toString());
                return;
            }

            ResourceResolver resourceResolver = request.getResourceResolver();

            // Check if the user already exists (checking for a valid user resource)
            Resource existingUserResource = resourceResolver.resolve("/content/users/" + userId);
            if (existingUserResource != null && existingUserResource.getValueMap().containsKey("id")) {
                // If the user already exists, return a conflict status
                response.setStatus(409); // Conflict status code
                responseJson.add("status", "error");
                responseJson.add("message", "User already exists");
                response.getWriter().write(responseJson.build().toString());
                return;
            }

            // Check if parent path exists, create if it doesn't
            Resource parentResource = resourceResolver.getResource("/content/users");
            if (parentResource == null) {
                parentResource = createParentPath(resourceResolver);
            }

            // Create properties map for the new user
            Map<String, Object> properties = new HashMap<>();
            properties.put("jcr:primaryType", "sling:OrderedFolder");
            properties.put("id", userId);
            properties.put("firstname", firstName != null ? firstName : "");
            properties.put("lastname", lastName != null ? lastName : "");
            properties.put("email", email != null ? email : "");
            properties.put("phone", phone != null ? phone : "");

            // Create the resource
            Resource newUser = resourceResolver.create(parentResource, userId, properties);
            resourceResolver.commit();

            // Send success response with the userId
            responseJson.add("status", "success");
            responseJson.add("message", "User created successfully");
            responseJson.add("userId", userId);
            response.getWriter().write(responseJson.build().toString());

        } catch (Exception e) {
            log.error("Unexpected error", e);
            response.setStatus(500);
            responseJson.add("status", "error");
            responseJson.add("message", "Internal server error: " + e.getMessage());
            response.getWriter().write(responseJson.build().toString());
        }
    }

    private String generateUniqueUserId(ResourceResolver resourceResolver, String baseUserId) {
        String uniqueUserId = baseUserId;
        int counter = 1;

        // Keep checking until we find a unique ID
        while (resourceResolver.getResource("/content/users/" + uniqueUserId) != null) {
            uniqueUserId = baseUserId + counter;
            counter++;
        }

        return uniqueUserId;
    }

    private Resource createParentPath(ResourceResolver resourceResolver) throws PersistenceException {
        Resource content = resourceResolver.getResource("/content");
        if (content == null) {
            content = resourceResolver.create(resourceResolver.getResource("/"), "content",
                    Collections.singletonMap("jcr:primaryType", "sling:OrderedFolder"));
        }

        Resource users = resourceResolver.create(content, "users",
                Collections.singletonMap("jcr:primaryType", "sling:sling:OrderedFolder"));
        resourceResolver.commit();
        return users;
    }
    @Override
    protected void doPut(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String userid = request.getParameter("userid");
        String firstname = request.getParameter("firstName");
        String lastname = request.getParameter("lastName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");

        // Ensure the user ID is provided and valid
        if (userid == null || userid.isEmpty()) {
            response.setStatus(400);
            response.getWriter().write("{\"error\": \"User ID is required\"}");
            return;
        }

        ResourceResolver resourceResolver = request.getResourceResolver();
        Resource userResource = resourceResolver.resolve("/content/users/" + userid);

        if (userResource != null) {
            // Use ModifiableValueMap to modify resource properties
            ModifiableValueMap valueMap = userResource.adaptTo(ModifiableValueMap.class);

            if (valueMap != null) {
                // Only update the fields that have been provided (non-null and non-empty)
                if (firstname != null && !firstname.isEmpty()) {
                    valueMap.put("firstname", firstname);
                }
                if (lastname != null && !lastname.isEmpty()) {
                    valueMap.put("lastname", lastname);
                }
                if (email != null && !email.isEmpty()) {
                    valueMap.put("email", email);
                }
                if (phone != null && !phone.isEmpty()) {
                    valueMap.put("phone", phone);
                }

                // Commit the changes to the repository
                try {
                    resourceResolver.commit();
                    response.getWriter().write("{\"message\": \"User updated successfully.\"}");
                } catch (PersistenceException e) {
                    response.setStatus(500);
                    response.getWriter().write("{\"error\": \"Error updating user: " + e.getMessage() + "\"}");
                }
            } else {
                response.setStatus(400);
                response.getWriter().write("{\"error\": \"Unable to modify user resource.\"}");
            }
        } else {
            response.setStatus(404);
            response.getWriter().write("{\"error\": \"User not found.\"}");
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