package com.prettyplease;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.prettyplease.model.Charity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CharityHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger LOG = LogManager.getLogger(CharityHandler.class);
    private final String DB_HOST = System.getenv("DB_HOST");
    private final String DB_NAME = System.getenv("DB_NAME");
    private final String DB_USER = System.getenv("DB_USER");
    private final String DB_PASSWORD = System.getenv("DB_PASSWORD");
    private static final String getSql = "SELECT * FROM prettyplease.Charity WHERE charityId = ?";
    private static final String createSql = "INSERT INTO prettyplease.Charity (charityId, name, description, imageUrl, webUrl, createdAt) VALUES (?, ?, ?, ?, ?, current_timestamp());";

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
// TODO remove logging
        LOG.info("\nreceived: {}\n", input);

        String httpMethod = (String) input.get("httpMethod");
        Object response = null;

        if ("GET".equalsIgnoreCase(httpMethod)) {
            response = getCharities(input);
        } else if ("POST".equalsIgnoreCase((httpMethod))) {
//            String body = (String) input.get("body");
//            LOG.info("\n body: {}\n", body); // e.g. POST JSON string

            try {
                JSONObject postBody = new JSONObject((String) input.get("body"));
                createCharity(postBody);
            } catch (JSONException e) {
                LOG.info("Problem parsing POST data: {}", e.getMessage());
            }
        }
        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .setObjectBody(response)
                .build();
    }


    private List<Charity> getCharities(Map<String, Object> input) {
        List<Charity> charities = new ArrayList<>();
        String charityId = (String) ((Map) input.get("pathParameters")).get("charityId");
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager
                    .getConnection(String.format("jdbc:mysql://%s/%s?user=%s&password=%s", DB_HOST, DB_NAME, DB_USER, DB_PASSWORD));
            PreparedStatement preparedStatement = connection.prepareStatement(getSql);
            preparedStatement.setString(1, charityId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                buildCharityFromDB(charities, resultSet);
            }
        } catch (ClassNotFoundException | SQLException e) {
            LOG.error(e.getMessage());
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


    private void createCharity(JSONObject postBody) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager
                    .getConnection(String.format("jdbc:mysql://%s/%s?user=%s&password=%s", DB_HOST, DB_NAME, DB_USER, DB_PASSWORD));
//            Statement statement = connection.createStatement();

            PreparedStatement preparedStatement = connection.prepareStatement(createSql);
            // build the prepared statement from the JSON object
            preparedStatement.setString(1, postBody.getString("charityId"));
            preparedStatement.setString(2, postBody.getString("name"));
            preparedStatement.setString(3, postBody.getString("description"));
            preparedStatement.setString(4, postBody.getString("imageUrl"));
            preparedStatement.setString(5, postBody.getString("webUrl"));

            int row = preparedStatement.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
            LOG.error(e.getMessage());
        }
    }
}
