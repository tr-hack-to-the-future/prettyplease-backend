package com.prettyplease;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prettyplease.model.tables.Sponsor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.*;
import java.util.Date;
import java.util.*;

public class SponsorHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger LOG = LogManager.getLogger(SponsorHandler.class);
    private String DB_HOST = System.getenv("DB_HOST");
    private String DB_NAME = System.getenv("DB_NAME");
    private String DB_USER = System.getenv("DB_USER");
    private String DB_PASSWORD = System.getenv("DB_PASSWORD");
    private static final String getSql = "SELECT sponsorId, name, description, imageUrl, webUrl, createdAt FROM prettyplease.Sponsor WHERE sponsorId = ?";
    private static final String createSql = "INSERT INTO prettyplease.Sponsor (sponsorId, name, description, imageUrl, webUrl, createdAt) VALUES (?, ?, ?, ?, ?, current_timestamp());";

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
// TODO remove logging
        LOG.info("\nreceived: {}\n", input);
//        String httpMethod = (String)input.get("httpMethod");
//        LOG.info("\nHttp Method: {}\n", httpMethod); // e.g. httpMethod=GET
//        LOG.info("\nResource: {}\n", (String)input.get("resource")); // e.g. {resource=/sponsor/{sponsorId}

        String httpMethod = (String) input.get("httpMethod");
        Object response = null;
        int statusCode = HttpStatus.OK;   // default to success

        if ("GET".equalsIgnoreCase(httpMethod)) {
            try {
                response = getSponsors(input);
            } catch (ClassNotFoundException e) {
                LOG.info("Problem setting up database connection: {}", e.getMessage());
                statusCode = HttpStatus.BAD_REQUEST;   // Bad Request
                response = e.getMessage();
            } catch (SQLException e) {
                LOG.error("Database/SQL problem: {}", e.getMessage());
                statusCode = HttpStatus.CONFLICT;   // Conflict
                response = e.getMessage();
            }
        } else if ("POST".equalsIgnoreCase((httpMethod))) {
//            String body = (String) input.get("body");
//            LOG.info("\nbody: {}\n", body); // e.g. POST JSON string
            try {
                response = createSponsor((String) input.get("body"));
            } catch (IOException e) {
                LOG.info("Problem parsing request: {}", e.getMessage());
                statusCode = HttpStatus.BAD_REQUEST;   // Bad Request
                response = e.getMessage();
            } catch (ClassNotFoundException e) {
                LOG.info("Problem setting up database connection: {}", e.getMessage());
                statusCode = HttpStatus.BAD_REQUEST;   // Bad Request
                response = e.getMessage();
            } catch (SQLException e) {
                LOG.error("Database/SQL problem: {}", e.getMessage());
                statusCode = HttpStatus.CONFLICT;   // Conflict
                response = e.getMessage();
            }
        }
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Headers", "Content-Type");

        return ApiGatewayResponse.builder()
                .setStatusCode(statusCode)
                .setObjectBody(response)
                .setHeaders(headers)
                .build();
    }


    private List<Sponsor> getSponsors(Map<String, Object> input) throws SQLException, ClassNotFoundException {
        List<Sponsor> sponsors = new ArrayList<>();
        String sponsorId = (String) ((Map) input.get("pathParameters")).get("sponsorId");
        try (
                Connection connection = getDatabaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(getSql);
        ) {
            preparedStatement.setString(1, sponsorId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    buildSponsorFromDB(sponsors, resultSet);
                }
            }
        }
        return sponsors;
    }

    private void buildSponsorFromDB(List<Sponsor> sponsors, ResultSet resultSet) throws SQLException {
        String id = resultSet.getString("sponsorId");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        String imageUrl = resultSet.getString("imageUrl");
        String webUrl = resultSet.getString("webUrl");
        Date createdAt = resultSet.getTimestamp("createdAt");
        Sponsor sponsor = new Sponsor(id, name, description);
        sponsor.setImageUrl(imageUrl);
        sponsor.setWebUrl(webUrl);
        sponsor.setCreatedAt(createdAt);

        sponsors.add(sponsor);
    }

    private String createSponsor(String body) throws ClassNotFoundException, SQLException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        Sponsor sponsor = mapper.readValue(body, Sponsor.class);

//        JSONObject postBody = new JSONObject(body);
        String id = "";
        try (
                Connection connection = getDatabaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(createSql);
        ) {
            // build the prepared statement from the JSON object
            preparedStatement.setString(1, sponsor.getSponsorId());
            preparedStatement.setString(2, sponsor.getName());
            preparedStatement.setString(3, sponsor.getDescription());
            preparedStatement.setString(4, sponsor.getImageUrl());
            preparedStatement.setString(5, sponsor.getWebUrl());
            LOG.info("\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + preparedStatement.toString() + "\n");

//        JSONObject postBody = new JSONObject(body);
//        String id = "";
//        try (
//                Connection connection = getDatabaseConnection();
//                PreparedStatement preparedStatement = connection.prepareStatement(createSql);
//        ) {
//            // build the prepared statement from the JSON object
//            preparedStatement.setString(1, postBody.getString("sponsorId"));
//            preparedStatement.setString(2, postBody.getString("name"));
//            preparedStatement.setString(3, postBody.getString("description"));
//            preparedStatement.setString(4, postBody.getString("imageUrl"));
//            preparedStatement.setString(5, postBody.getString("webUrl"));
////            LOG.info("\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + preparedStatement.toString() + "\n");
            int rowsCreated = preparedStatement.executeUpdate();
            if (rowsCreated == 1) {
//                id = postBody.getString("sponsorId");
                id = sponsor.getSponsorId();
            }
        }
        return id;
    }

    private Connection getDatabaseConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager
                .getConnection(String.format("jdbc:mysql://%s/%s?user=%s&password=%s", DB_HOST, DB_NAME, DB_USER, DB_PASSWORD));
    }

}
