package com.prettyplease.service;

import com.prettyplease.SponsorHandler;
import com.prettyplease.model.tables.Sponsor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

// Created as an example of unit testing of database services with mocks
// TODO complete switch from Handlers into services, including update of serverless.yml
public class SponsorServiceImpl {
    private static final Logger LOG = LogManager.getLogger(SponsorServiceImpl.class);

    private String DB_HOST = System.getenv("DB_HOST");
    private String DB_NAME = System.getenv("DB_NAME");
    private String DB_USER = System.getenv("DB_USER");
    private String DB_PASSWORD = System.getenv("DB_PASSWORD");
    private static final String getSql = "SELECT sponsorId, name, description, imageUrl, webUrl, createdAt FROM prettyplease.Sponsor WHERE sponsorId = ?";


    public List<Sponsor> getSponsors(String sponsorId) throws SQLException, ClassNotFoundException {
        List<Sponsor> sponsors = new ArrayList<>();
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

    private Connection getDatabaseConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager
                .getConnection(String.format("jdbc:mysql://%s/%s?user=%s&password=%s", DB_HOST, DB_NAME, DB_USER, DB_PASSWORD));
    }
}
