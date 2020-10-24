package com.prettyplease;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prettyplease.model.tables.SponsorOffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OfferHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger LOG = LogManager.getLogger(OfferHandler.class);
    private String DB_HOST = System.getenv("DB_HOST");
    private String DB_NAME = System.getenv("DB_NAME");
    private String DB_USER = System.getenv("DB_USER");
    private String DB_PASSWORD = System.getenv("DB_PASSWORD");
    //    private static final String getSql = "SELECT sponsorId, name, description, imageUrl, webUrl, createdAt FROM prettyplease.Sponsor WHERE sponsorId = ?";
    private static final String createSql = "INSERT INTO prettyplease.SponsorOffer (offerId, sponsorId, requestId, offerStatus, offerAmount, isSingleEvent, offerDurationInYears, createdAt) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, current_timestamp());";

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
            LOG.info("GET Sponsor Offers function not yet created.");
            statusCode = HttpStatus.BAD_REQUEST;   // Bad Request
            response = "GET Offers function not yet created.";

//            try {
//                response = getSponsors(input);
//            } catch (ClassNotFoundException e) {
//                LOG.info("Problem setting up database connection: {}", e.getMessage());
//                statusCode = HttpStatus.BAD_REQUEST;   // Bad Request
//                response = e.getMessage();
//            } catch (SQLException e) {
//                LOG.error("Database/SQL problem: {}", e.getMessage());
//                statusCode = HttpStatus.CONFLICT;   // Conflict
//                response = e.getMessage();
//            }
        } else if ("POST".equalsIgnoreCase((httpMethod))) {
//            String body = (String) input.get("body");
//            LOG.info("\nbody: {}\n", body); // e.g. POST JSON string
            try {
                response = createOffer((String) input.get("body"));
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

//
//    private List<Sponsor> getSponsors(Map<String, Object> input) throws SQLException, ClassNotFoundException {
//        List<Sponsor> sponsors = new ArrayList<>();
//        String sponsorId = (String) ((Map) input.get("pathParameters")).get("sponsorId");
//        try (
//                Connection connection = getDatabaseConnection();
//                PreparedStatement preparedStatement = connection.prepareStatement(getSql);
//        ) {
//            preparedStatement.setString(1, sponsorId);
//            try (ResultSet resultSet = preparedStatement.executeQuery()) {
//                while (resultSet.next()) {
//                    buildSponsorFromDB(sponsors, resultSet);
//                }
//            }
//        }
//        return sponsors;
//    }
//
//    private void buildSponsorFromDB(List<Sponsor> sponsors, ResultSet resultSet) throws SQLException {
//        String id = resultSet.getString("sponsorId");
//        String name = resultSet.getString("name");
//        String description = resultSet.getString("description");
//        String imageUrl = resultSet.getString("imageUrl");
//        String webUrl = resultSet.getString("webUrl");
//        Date createdAt = resultSet.getTimestamp("createdAt");
//        Sponsor sponsor = new Sponsor(id, name, description);
//        sponsor.setImageUrl(imageUrl);
//        sponsor.setWebUrl(webUrl);
//        sponsor.setCreatedAt(createdAt);
//
//        sponsors.add(sponsor);
//    }

    private String createOffer(String body) throws ClassNotFoundException, SQLException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        SponsorOffer offer = mapper.readValue(body, SponsorOffer.class);

//        JSONObject postBody = new JSONObject(body);
        String id = "";
        try (
                Connection connection = getDatabaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(createSql);
        ) {
            // build the prepared statement from the JSON object
            // create a random UUID for the requestI
            UUID offerId = UUID.randomUUID();
            preparedStatement.setString(1, offerId.toString());
            preparedStatement.setString(2, offer.getSponsorId());
            preparedStatement.setString(3, offer.getRequestId());
            preparedStatement.setString(4, offer.getOfferStatus());
            preparedStatement.setInt(5, offer.getOfferAmount());
            // MySQL 5 uses TinyInt instead of Boolean
            preparedStatement.setInt(6, (offer.isSingleEvent() ? 1 : 0));
            preparedStatement.setInt(7, offer.getOfferDurationInYears());
            LOG.info("\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + preparedStatement.toString() + "\n");

            int rowsCreated = preparedStatement.executeUpdate();
            if (rowsCreated == 1) {
                id = offer.getOfferId();
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
