package com.prettyplease;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.prettyplease.model.Sponsor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

        if ("GET".equalsIgnoreCase(httpMethod)) {
            response = getSponsors(input);
        } else if ("POST".equalsIgnoreCase((httpMethod))) {
//            String body = (String) input.get("body");
//            LOG.info("\nbody: {}\n", body); // e.g. POST JSON string

            // parse into JSON object
            try {
                JSONObject postBody = new JSONObject((String) input.get("body"));
                int rows = createSponsor(postBody);
                response = "Rows created: "  + rows;
            } catch (JSONException e) {
                LOG.info("Problem parsing POST data: {}", e.getMessage());
            }
        }

        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .setObjectBody(response)
                .build();
    }


    private List<Sponsor> getSponsors(Map<String, Object> input) {
        List<Sponsor> sponsors = new ArrayList<>();
        String sponsorId = (String) ((Map) input.get("pathParameters")).get("sponsorId");
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager
                    .getConnection(String.format("jdbc:mysql://%s/%s?user=%s&password=%s", DB_HOST, DB_NAME, DB_USER, DB_PASSWORD));
            Statement statement = connection.createStatement();

            PreparedStatement preparedStatement = connection.prepareStatement(getSql);
            preparedStatement.setString(1, sponsorId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                buildSponsorFromDB(sponsors, resultSet);
            }
        } catch (ClassNotFoundException | SQLException e) {
            LOG.error(e.getMessage());
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


    private int createSponsor(JSONObject postBody) {
        int rowsCreated = 0;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager
                    .getConnection(String.format("jdbc:mysql://%s/%s?user=%s&password=%s", DB_HOST, DB_NAME, DB_USER, DB_PASSWORD));
            Statement statement = connection.createStatement();

            PreparedStatement preparedStatement = connection.prepareStatement(createSql);
            // build the prepared statement from the JSON object
            preparedStatement.setString(1, postBody.getString("sponsorId"));
            preparedStatement.setString(2, postBody.getString("name"));
            preparedStatement.setString(3, postBody.getString("description"));
            preparedStatement.setString(4, postBody.getString("imageUrl"));
            preparedStatement.setString(5, postBody.getString("webUrl"));
//            LOG.info("\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + preparedStatement.toString() + "\n");
            rowsCreated = preparedStatement.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
            LOG.error(e.getMessage());
        }
        return rowsCreated;
    }
}
