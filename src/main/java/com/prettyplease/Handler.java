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
    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        LOG.info("received: {}", input);

        // create a database connection
        // create a prepared statement
        // run it to create a result set
        // build the sponsors list from the result set
        // return sponsors
        List<Sponsor> sponsors = new ArrayList<>();

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager
                    .getConnection("jdbc:mysql://localhost/prettyplease?"
                            + "user=user&password=pw");
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT * FROM prettyplease.Sponsor");


            while (resultSet.next()) {
                int sponsorId = resultSet.getInt("sponsorId");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                LOG.info("Sponsor: {} - {} - {}", sponsorId, name, description);
                sponsors.add(new Sponsor(sponsorId, name, description));
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
