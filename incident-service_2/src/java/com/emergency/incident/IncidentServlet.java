package com.emergency.incident;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet(name = "IncidentServlet", urlPatterns = {"/incident_service"})
// Enables processing of file uploads up to 5MB
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, maxFileSize = 1024 * 1024 * 5) 
public class IncidentServlet extends HttpServlet {

    private static final List<Incident> incidentDatabase = Collections.synchronizedList(new ArrayList<>());
    
    // Defines where pictures get saved on your machine during testing
    private static final String UPLOAD_DIR = "uploaded_images";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            // Read form parameter inputs strings
            String incidentType = request.getParameter("incidentType");
            String description = request.getParameter("description");
            String rawLat = request.getParameter("latitude");
            String rawLng = request.getParameter("longitude");
            String severity = request.getParameter("severity");

            // Extract the binary picture file from the request stream
            Part filePart = request.getPart("incidentImage");
            String fileName = "no_image.png";
            
            if (filePart != null && filePart.getSize() > 0) {
                // Get original submitted picture name
                fileName = getSubmittedFileName(filePart);
                
                // Creates application directory folder path inside your local computer workspace target
                String appPath = request.getServletContext().getRealPath("");
                String savePath = appPath + File.separator + UPLOAD_DIR;
                File fileSaveDir = new File(savePath);
                if (!fileSaveDir.exists()) {
                    fileSaveDir.mkdir(); // Creates folder if it's missing
                }
                
                // Write the picture safely to disk
                filePart.write(savePath + File.separator + fileName);
            }

            double latitude = Double.parseDouble(rawLat);
            double longitude = Double.parseDouble(rawLng);

            // Log new incident into database with its respective photo path name string
            Incident newRecord = new Incident(incidentType, description, latitude, longitude, severity, fileName);
            incidentDatabase.add(newRecord);

            System.out.println("🚨 SOS Recorded! Photo Saved Successfully: " + fileName);

            response.setStatus(HttpServletResponse.SC_CREATED); // 201 Created Status
            out.print("{\n"
                + "  \"httpStatus\": 201,\n"
                + "  \"status\": \"Success\",\n"
                + "  \"message\": \"SOS Signal with Photographic Evidence successfully logged.\",\n"
                + "  \"evidenceSavedAs\": \"" + fileName + "\"\n"
                + "}");

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"status\": \"Error\", \"message\": \"Failed to save image attachment structural data.\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        StringBuilder jsonArray = new StringBuilder("[\n");
        synchronized(incidentDatabase) {
            for (int i = 0; i < incidentDatabase.size(); i++) {
                Incident inc = incidentDatabase.get(i);
                jsonArray.append("  {\n")
                    .append("    \"incidentType\": \"").append(inc.getIncidentType()).append("\",\n")
                    .append("    \"description\": \"").append(inc.getDescription()).append("\",\n")
                    .append("    \"latitude\": ").append(inc.getLatitude()).append(",\n")
                    .append("    \"longitude\": ").append(inc.getLongitude()).append(",\n")
                    .append("    \"severity\": \"").append(inc.getSeverity()).append("\",\n")
                    .append("    \"uploadedPhoto\": \"").append(inc.getImageName()).append("\",\n")
                    .append("    \"timestamp\": \"").append(inc.getTimestamp().toString()).append("\"\n")
                    .append("  }");
                if (i < incidentDatabase.size() - 1) {
                    jsonArray.append(",\n");
                }
            }
        }
        jsonArray.append("\n]");
        
        response.setStatus(HttpServletResponse.SC_OK);
        out.print(jsonArray.toString());
    }

    // Helper technique extracting pure name parameters from content headers
    private String getSubmittedFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return "unknown.png";
    }
}