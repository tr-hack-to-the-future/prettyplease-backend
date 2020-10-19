package com.prettyplease;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.prettyplease.model.Charity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class GetCharityHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger LOG = LogManager.getLogger(GetCharityHandler.class);
    private final String DB_HOST = System.getenv("DB_HOST");
    private final String DB_NAME = System.getenv("DB_NAME");
    private final String DB_USER = System.getenv("DB_USER");
    private final String DB_PASSWORD = System.getenv("DB_PASSWORD");
    private static final String sql = "SELECT * FROM prettyplease.Charity WHERE charityId = ?";

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

        List<Charity> charities = new ArrayList<>();
        String charityId = (String) ((Map)input.get("pathParameters")).get("charityId");
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager
                    .getConnection(String.format("jdbc:mysql://%s/%s?user=%s&password=%s", DB_HOST, DB_NAME, DB_USER, DB_PASSWORD));
            Statement statement = connection.createStatement();

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, charityId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                buildCharityFromDB(charities, resultSet);
            }
        }
        catch (ClassNotFoundException | SQLException e) {
            LOG.error(e.getMessage());
        }

        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .setObjectBody(charities)
                .build();
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

}
