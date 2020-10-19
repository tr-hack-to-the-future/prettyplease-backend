package com.prettyplease;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.*;
import java.util.Map;

public class CreateSponsorHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger LOG = LogManager.getLogger(CreateSponsorHandler.class);
    private String DB_HOST = System.getenv("DB_HOST");
    private String DB_NAME = System.getenv("DB_NAME");
    private String DB_USER = System.getenv("DB_USER");
    private String DB_PASSWORD = System.getenv("DB_PASSWORD");
    private static final String sql = "INSERT INTO prettyplease.Sponsor (sponsorId, name, description, imageUrl, webUrl, createdAt) VALUES (?, ?, ?, ?, ?, current_timestamp());";

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
// TODO remove logging
        LOG.info("\nreceived: {}\n", input.toString());
        LOG.info("\nResource: {}\n", (String) input.get("resource")); // e.g. {resource=/sponsor/{sponsorId}

        String body = (String) input.get("body");
        LOG.info("\nbody: {}\n", body); // e.g. POST JSON string

        // TODO surround with try...catch if invalid JSON - what should we do?
        // parse into JSON object
        JSONObject sponsor = new JSONObject(body);

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager
                    .getConnection(String.format("jdbc:mysql://%s/%s?user=%s&password=%s", DB_HOST, DB_NAME, DB_USER, DB_PASSWORD));
            Statement statement = connection.createStatement();


            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            buildPreparedStatement(sponsor, preparedStatement);
            LOG.info("\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + preparedStatement.toString() + "\n");
            int row = preparedStatement.executeUpdate();
            // TODO remove logging
            LOG.info("row inserted: {}", row);
        } catch (ClassNotFoundException | SQLException e) {
            LOG.error(e.getMessage());
        }

        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .setObjectBody(body)
                .build();
    }

    private void buildPreparedStatement(JSONObject sponsor, PreparedStatement preparedStatement) throws JSONException, SQLException {
        // TODO change ID to String
        String id = sponsor.getString("sponsorId");
        preparedStatement.setInt(1, Integer.valueOf(id));
        preparedStatement.setString(2, sponsor.getString("name"));
        preparedStatement.setString(3, sponsor.getString("description"));
        preparedStatement.setString(4, sponsor.getString("imageUrl"));
        preparedStatement.setString(5, sponsor.getString("webUrl"));
//                e.g.
//                body={
//                "sponsorId" : "101",
//                "name": "Retired Postmen Charity",
//                "description": "Etiam vel nisi lacinia, luctus turpis et, rutrum ipsum. ",
//                "imageUrl": "test post image url",
//                "webUrl": "test post web url"
//                }
    }

}
