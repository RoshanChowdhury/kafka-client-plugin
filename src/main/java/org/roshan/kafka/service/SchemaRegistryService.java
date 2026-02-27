package org.roshan.kafka.service;

import com.google.gson.*;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.roshan.kafka.model.ClusterConfig;
import java.io.*;
import java.net.*;
import java.util.*;

@Service(Service.Level.PROJECT)
public final class SchemaRegistryService {
    
    public static SchemaRegistryService getInstance(Project project) {
        return project.getService(SchemaRegistryService.class);
    }

    public List<String> listSubjects(ClusterConfig config) throws IOException {
        String url = config.getSchemaRegistryUrl() + "/subjects";
        String response = sendGetRequest(url);
        JsonArray array = JsonParser.parseString(response).getAsJsonArray();
        List<String> subjects = new ArrayList<>();
        for (JsonElement element : array) {
            subjects.add(element.getAsString());
        }
        return subjects;
    }

    public List<Integer> getSchemaVersions(ClusterConfig config, String subject) throws IOException {
        String url = config.getSchemaRegistryUrl() + "/subjects/" + subject + "/versions";
        String response = sendGetRequest(url);
        JsonArray array = JsonParser.parseString(response).getAsJsonArray();
        List<Integer> versions = new ArrayList<>();
        for (JsonElement element : array) {
            versions.add(element.getAsInt());
        }
        return versions;
    }

    public String getSchema(ClusterConfig config, String subject, int version) throws IOException {
        String url = config.getSchemaRegistryUrl() + "/subjects/" + subject + "/versions/" + version;
        String response = sendGetRequest(url);
        JsonObject obj = JsonParser.parseString(response).getAsJsonObject();
        return obj.get("schema").getAsString();
    }

    public String getLatestSchema(ClusterConfig config, String subject) throws IOException {
        return getSchema(config, subject, -1);
    }

    public Map<String, String> compareSchemaVersions(ClusterConfig config, String subject, int version1, int version2) throws IOException {
        String schema1 = getSchema(config, subject, version1);
        String schema2 = getSchema(config, subject, version2);
        
        Map<String, String> comparison = new HashMap<>();
        comparison.put("version1", String.valueOf(version1));
        comparison.put("version2", String.valueOf(version2));
        comparison.put("schema1", schema1);
        comparison.put("schema2", schema2);
        comparison.put("compatible", checkCompatibility(schema1, schema2));
        
        return comparison;
    }

    public boolean validateSchema(ClusterConfig config, String subject, String schema) throws IOException {
        String url = config.getSchemaRegistryUrl() + "/subjects/" + subject + "/versions";
        JsonObject payload = new JsonObject();
        payload.addProperty("schema", schema);
        
        try {
            sendPostRequest(url, payload.toString());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public int registerSchema(ClusterConfig config, String subject, String schema) throws IOException {
        String url = config.getSchemaRegistryUrl() + "/subjects/" + subject + "/versions";
        JsonObject payload = new JsonObject();
        payload.addProperty("schema", schema);
        
        String response = sendPostRequest(url, payload.toString());
        JsonObject obj = JsonParser.parseString(response).getAsJsonObject();
        return obj.get("id").getAsInt();
    }

    private String checkCompatibility(String schema1, String schema2) {
        return schema1.equals(schema2) ? "IDENTICAL" : "DIFFERENT";
    }

    private String sendGetRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }

    private String sendPostRequest(String urlString, String payload) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        
        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload.getBytes());
            os.flush();
        }
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }
}
