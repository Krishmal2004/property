package com.RealState.servlets;

import com.RealState.model.Property;
import com.google.gson.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

@WebServlet("/PropertyManagementServlet/*")
public class PropertyManagementServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String DATA_DIR = "/WEB-INF/data/properties.json";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    // PropertyData class inside servlet
    private static class PropertyData {
        List<Property> properties = new ArrayList<>();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String agentId = request.getParameter("agentId");

        try {
            String action = request.getPathInfo();
            if (action == null) action = "/";

            switch (action) {
                case "/properties":
                    getProperties(agentId, out);
                    break;
                case "/stats":
                    getAgentStats(agentId, out);
                    break;
                case "/appointments":
                    getAppointments(agentId, out);
                    break;
                default:
                    sendError(out, "Invalid endpoint");
            }
        } catch (Exception e) {
            sendError(out, e.getMessage());
        }
    }

    private void getAgentStats(String agentId, PrintWriter out) {
        JsonObject stats = new JsonObject();
        stats.addProperty("totalProperties", 0);
        stats.addProperty("activeListings", 0);
        stats.addProperty("pendingSales", 0);
        out.println(gson.toJson(stats));
    }

    private void getAppointments(String agentId, PrintWriter out) {
        JsonArray appointments = new JsonArray();
        out.println(gson.toJson(appointments));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            String action = request.getPathInfo();
            if (action == null) action = "/";

            switch (action) {
                case "/add":
                    addProperty(request, out);
                    break;
                case "/update":
                    updateProperty(request, out);
                    break;
                case "/delete":
                    deleteProperty(request, out);
                    break;
                default:
                    sendError(out, "Invalid endpoint");
            }
        } catch (Exception e) {
            sendError(out, e.getMessage());
        }
    }

    private void updateProperty(HttpServletRequest request, PrintWriter out) throws IOException {
        // Implementation for updating property
        JsonObject response = new JsonObject();
        response.addProperty("status", "success");
        response.addProperty("message", "Property updated");
        out.println(gson.toJson(response));
    }

    private void deleteProperty(HttpServletRequest request, PrintWriter out) throws IOException {
        // Implementation for deleting property
        JsonObject response = new JsonObject();
        response.addProperty("status", "success");
        response.addProperty("message", "Property deleted");
        out.println(gson.toJson(response));
    }

    private void getProperties(String agentId, PrintWriter out) throws IOException {
        PropertyData data = loadPropertyData();
        List<Property> agentProperties = filterPropertiesByAgent(data.properties, agentId);
        
        JsonObject response = new JsonObject();
        response.addProperty("timestamp", "2025-03-21 21:16:45");
        response.addProperty("status", "success");
        response.add("properties", gson.toJsonTree(agentProperties));
        
        out.println(gson.toJson(response));
    }

    private void addProperty(HttpServletRequest request, PrintWriter out) throws IOException {
        BufferedReader reader = request.getReader();
        Property newProperty = gson.fromJson(reader, Property.class);
        
        PropertyData data = loadPropertyData();
        newProperty.propertyId = "PROP" + System.currentTimeMillis();
        newProperty.createdDate = "2025-03-21 21:16:45";
        data.properties.add(newProperty);
        
        savePropertyData(data);
        
        JsonObject response = new JsonObject();
        response.addProperty("status", "success");
        response.addProperty("message", "Property added successfully");
        response.addProperty("propertyId", newProperty.propertyId);
        
        out.println(gson.toJson(response));
    }

    private PropertyData loadPropertyData() throws IOException {
        String filePath = getServletContext().getRealPath(DATA_DIR + "properties.json");
        File file = new File(filePath);
        
        if (!file.exists()) {
            return new PropertyData();
        }
        
        try (Reader reader = new FileReader(file)) {
            return gson.fromJson(reader, PropertyData.class);
        }
    }

    private void savePropertyData(PropertyData data) throws IOException {
        String filePath = getServletContext().getRealPath(DATA_DIR + "properties.json");
        File file = new File(filePath).getParentFile();
        if (!file.exists()) {
            file.mkdirs();
        }
        try (Writer writer = new FileWriter(getServletContext().getRealPath(DATA_DIR + "properties.json"))) {
            gson.toJson(data, writer);
        }
    }

    private void sendError(PrintWriter out, String message) {
        JsonObject error = new JsonObject();
        error.addProperty("status", "error");
        error.addProperty("message", message);
        error.addProperty("timestamp", "2025-03-21 21:16:45");
        out.println(gson.toJson(error));
    }

    private List<Property> filterPropertiesByAgent(List<Property> properties, String agentId) {
        List<Property> filtered = new ArrayList<>();
        for (Property prop : properties) {
            if (agentId.equals(prop.agentId)) {
                filtered.add(prop);
            }
        }
        return filtered;
    }
}