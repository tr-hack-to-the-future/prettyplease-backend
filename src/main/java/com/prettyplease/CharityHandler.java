package com.prettyplease;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.prettyplease.model.tables.Charity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.*;
import java.util.Date;
import java.util.*;

public class CharityHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger LOG = LogManager.getLogger(CharityHandler.class);
    private final String DB_HOST = System.getenv("DB_HOST");
    private final String DB_NAME = System.getenv("DB_NAME");
    private final String DB_USER = System.getenv("DB_USER");
    private final String DB_PASSWORD = System.getenv("DB_PASSWORD");
    private static final String getSql = "SELECT charityId, name, description, imageUrl, webUrl, createdAt FROM prettyplease.Charity WHERE charityId = ?";
    private static final String createSql = "INSERT INTO prettyplease.Charity (charityId, name, description, imageUrl, webUrl, createdAt) VALUES (?, ?, ?, ?, ?, current_timestamp());";

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
// TODO remove logging
        LOG.info("\nreceived: {}\n", input);

        String httpMethod = (String) input.get("httpMethod");
        Object response = null;
        int statusCode = HttpStatus.OK;   // default to success

        if ("GET".equalsIgnoreCase(httpMethod)) {
            try {
                response = getCharities(input);
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
//            LOG.info("\n body: {}\n", body); // e.g. POST JSON string

            try {
                JSONObject postBody = new JSONObject((String) input.get("body"));
                response = createCharity(postBody);
            } catch (NullPointerException | JSONException e) {
                LOG.info("Problem parsing POST data: {}", e.getMessage());
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


    private List<Charity> getCharities(Map<String, Object> input) throws SQLException, ClassNotFoundException {
        List<Charity> charities = new ArrayList<>();
        String charityId = (String) ((Map) input.get("pathParameters")).get("charityId");
        try (
                Connection connection = getDatabaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(getSql);
        ) {
            preparedStatement.setString(1, charityId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    buildCharityFromDB(charities, resultSet);
                }
            }
        }
        return charities;
    }

    private void buildCharityFromDB(List<Charity> charities, ResultSet resultSet) throws SQLException {
        String id = resultSet.getString("charityId");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        String imageUrl = resultSet.getString("imageUrl");
        String webUrl = resultSet.getString("webUrl");
        Date createdAt = resultSet.getTimestamp("createdAt");
        Charity charity = new Charity(id, name, description);
        charity.setImageUrl(imageUrl);
        charity.setWebUrl(webUrl);
        charity.setCreatedAt(createdAt);

        charities.add(charity);
    }


    private String createCharity(JSONObject postBody) throws SQLException, ClassNotFoundException {
        String id = "";
        try (
                Connection connection = getDatabaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(createSql);
        ) {
            // build the prepared statement from the JSON object
            preparedStatement.setString(1, postBody.getString("charityId"));
            preparedStatement.setString(2, postBody.getString("name"));
            preparedStatement.setString(3, postBody.getString("description"));
            preparedStatement.setString(4, postBody.getString("imageUrl"));
            preparedStatement.setString(5, postBody.getString("webUrl"));

            int rowsCreated = preparedStatement.executeUpdate();
            if (rowsCreated == 1) {
                id = postBody.getString("charityId");
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
