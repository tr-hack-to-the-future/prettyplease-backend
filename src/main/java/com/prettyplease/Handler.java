package com.prettyplease;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.prettyplease.model.Sponsor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Handler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger LOG = LogManager.getLogger(Handler.class);
    private String DB_HOST = System.getenv("DB_HOST");
    private String DB_NAME = System.getenv("DB_NAME");
    private String DB_USER = System.getenv("DB_USER");
    private String DB_PASSWORD = System.getenv("DB_PASSWORD");

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        // TODO remove logging
        LOG.info("\nreceived: {}\n", input);
        String httpMethod = (String)input.get("httpMethod");
        LOG.info("\nHttp Method: {}\n", httpMethod); // e.g. httpMethod=GET
        LOG.info("\nResource: {}\n", (String)input.get("resource")); // e.g. {resource=/sponsor/{sponsorId}

        // TODO rename this to SponsorHandler? One handler per event? GET, POST, UPDATE, DELETE?

        List<Sponsor> sponsors = new ArrayList<>();
        String sponsorId = (String) ((Map)input.get("pathParameters")).get("sponsorId");
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager
                    .getConnection(String.format("jdbc:mysql://%s/%s?user=%s&password=%s", DB_HOST, DB_NAME, DB_USER, DB_PASSWORD));
            Statement statement = connection.createStatement();

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM prettyplease.Sponsor WHERE sponsorId = ?");
            preparedStatement.setString(1, sponsorId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("sponsorId");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
//                LOG.info("Sponsor: {} - {} - {}", id, name, description);
                sponsors.add(new Sponsor(id, name, description));
            }
        }
        catch (ClassNotFoundException | SQLException e) {
            LOG.error(e.getMessage());
        }

        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .setObjectBody(sponsors)
                .build();
    }

}
