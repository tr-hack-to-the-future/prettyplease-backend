package com.prettyplease;

import com.amazonaws.services.lambda.runtime.Context;
import com.prettyplease.model.SponsorOfferCharityRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CharityOfferHandler implements com.amazonaws.services.lambda.runtime.RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger LOG = LogManager.getLogger(CharityOfferHandler.class);
    private String DB_HOST = System.getenv("DB_HOST");
    private String DB_NAME = System.getenv("DB_NAME");
    private String DB_USER = System.getenv("DB_USER");
    private String DB_PASSWORD = System.getenv("DB_PASSWORD");
    private static final String getByCharityIdSql = "SELECT o.offerId, o.sponsorId, o.requestId, o.offerStatus, o.offerAmount, o.isSingleEvent as isOfferSingleEvent," +
            " o.offerDurationInYears, o.createdAt, s.name as sponsorName, s.description as sponsorDescription, s.imageUrl as sponsorImageUrl, s.webUrl as sponsorWebUrl,\n" +
            "           f.requestId, f.charityId, f.eventDescription, f.incentive, f.amountRequested, f.amountAgreed, f.isSingleEvent, f.durationInYears,\n" +
            "         f.agreedDurationInYears, f.requestStatus, f.requestDate, f.dueDate FROM prettyplease.SponsorOffer o INNER JOIN prettyplease.Sponsor s " +
            "ON o.sponsorId = s.sponsorId INNER JOIN prettyplease.FundRequest f ON o.requestId = f.requestId WHERE f.charityId = ? ORDER BY f.requestStatus, o.createdAt";


    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        String httpMethod = (String) input.get("httpMethod");
        Object response = null;
        int statusCode = HttpStatus.OK;   // default to success

        if ("GET".equalsIgnoreCase(httpMethod)) {
            try {
                response = getOffers(input);
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
                .setStatusCode(200)
                .setObjectBody(response)
                .setHeaders(headers)
                .build();
    }


    private List<SponsorOfferCharityRequest> getOffers(Map<String, Object> input) throws SQLException, ClassNotFoundException {
        List<SponsorOfferCharityRequest> requests = new ArrayList<>();
        String charityId = (String) ((Map) input.get("pathParameters")).get("charityId");
        try (
                Connection connection = getDatabaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(getByCharityIdSql);
        ) {
            preparedStatement.setString(1, charityId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    buildOfferRequestFromDB(requests, resultSet);
                }
            }
        }
        return requests;
    }

    private void buildOfferRequestFromDB(List<SponsorOfferCharityRequest> offers, ResultSet resultSet) throws SQLException {
        // populate composite object from SponsorOffer-Sponsor-FundRequest tables
        SponsorOfferCharityRequest sponsorOfferCharityRequest = new SponsorOfferCharityRequest();
        sponsorOfferCharityRequest.setOfferId(resultSet.getString("offerId"));
        sponsorOfferCharityRequest.setSponsorId(resultSet.getString("sponsorId"));
        sponsorOfferCharityRequest.setRequestId(resultSet.getString("requestId"));
        sponsorOfferCharityRequest.setOfferStatus(resultSet.getString("offerStatus"));
        sponsorOfferCharityRequest.setOfferSingleEvent(Boolean.parseBoolean(resultSet.getString("isOfferSingleEvent")));
        sponsorOfferCharityRequest.setOfferDurationInYears(Integer.parseInt(resultSet.getString("durationInYears")));
        sponsorOfferCharityRequest.setCreatedAt(resultSet.getTimestamp("createdAt"));
        // Sponsor
        sponsorOfferCharityRequest.setSponsorName(resultSet.getString("sponsorName"));
        sponsorOfferCharityRequest.setSponsorDescription(resultSet.getString("sponsorDescription"));
        sponsorOfferCharityRequest.setSponsorImageUrl(resultSet.getString("sponsorImageUrl"));
        sponsorOfferCharityRequest.setSponsorWebUrl(resultSet.getString("sponsorWebUrl"));
        //Fund Request
        sponsorOfferCharityRequest.setCharityId(resultSet.getString("charityId"));
        sponsorOfferCharityRequest.setEventDescription(resultSet.getString("eventDescription"));
        sponsorOfferCharityRequest.setIncentive(resultSet.getString("incentive"));
        sponsorOfferCharityRequest.setAmountRequested(Integer.parseInt(resultSet.getString("amountRequested")));
        sponsorOfferCharityRequest.setAmountAgreed(Integer.parseInt(resultSet.getString("amountAgreed")));
        sponsorOfferCharityRequest.setSingleEvent(Boolean.parseBoolean(resultSet.getString("isSingleEvent")));
        sponsorOfferCharityRequest.setDurationInYears(Integer.parseInt(resultSet.getString("durationInYears")));
        sponsorOfferCharityRequest.setAgreedDurationInYears(Integer.parseInt(resultSet.getString("agreedDurationInYears")));
        sponsorOfferCharityRequest.setRequestStatus(resultSet.getString("requestStatus"));
        sponsorOfferCharityRequest.setRequestDate(resultSet.getDate("requestDate"));
        sponsorOfferCharityRequest.setDueDate(resultSet.getDate("dueDate"));
        sponsorOfferCharityRequest.setCreatedAt(resultSet.getTimestamp("createdAt"));

        offers.add(sponsorOfferCharityRequest);
    }


    private Connection getDatabaseConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager
                .getConnection(String.format("jdbc:mysql://%s/%s?user=%s&password=%s", DB_HOST, DB_NAME, DB_USER, DB_PASSWORD));
    }

}
