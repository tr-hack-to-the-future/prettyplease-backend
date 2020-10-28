package com.prettyplease;

import com.amazonaws.services.lambda.runtime.Context;
import com.prettyplease.model.CharityRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CharityGetRequestHandler implements com.amazonaws.services.lambda.runtime.RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger LOG = LogManager.getLogger(CharityGetRequestHandler.class);
    private String DB_HOST = System.getenv("DB_HOST");
    private String DB_NAME = System.getenv("DB_NAME");
    private String DB_USER = System.getenv("DB_USER");
    private String DB_PASSWORD = System.getenv("DB_PASSWORD");
    private static final String getRequestsByCharityIdSql = "SELECT f.requestId, f.charityId, f.eventDescription, f.incentive, " +
            "f.amountRequested, f.amountAgreed, f.isSingleEvent, f.durationInYears, f.agreedDurationInYears, " +
            "f.requestStatus, f.requestDate, f.dueDate, f.createdAt, " +
            "c.name as charityName, c.description as charityDescription, " +
            "c.imageUrl as charityImgUrl, c.webUrl as charityWebUrl \n" +
            "FROM prettyplease.FundRequest f INNER JOIN prettyplease.Charity c\n" +
            "ON c.charityId = f.charityId WHERE f.charityId = ?;";


    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        String httpMethod = (String) input.get("httpMethod");
        Object response = null;
        int statusCode = HttpStatus.OK;   // default to success

        if ("GET".equalsIgnoreCase(httpMethod)) {
            try {
                String charityId = (String) ((Map) input.get("pathParameters")).get("charityId");
                response = getRequestsById(input, charityId);
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


    private List<CharityRequest> getRequestsById(Map<String, Object> input, String charityId) throws SQLException, ClassNotFoundException {
        List<CharityRequest> requests = new ArrayList<>();
        try (
                Connection connection = getDatabaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(getRequestsByCharityIdSql);
        ) {
            preparedStatement.setString(1, charityId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    buildCharityRequestFromDB(requests, resultSet);
                }
            }
        }
        return requests;
    }

    private void buildCharityRequestFromDB(List<CharityRequest> requests, ResultSet resultSet) throws SQLException {
        // populate CharityRequest object
        CharityRequest charityRequest = new CharityRequest(resultSet.getString("requestId"),
                resultSet.getString("charityId"), resultSet.getString("eventDescription"));
        charityRequest.setIncentive(resultSet.getString("incentive"));
        charityRequest.setAmountRequested(Integer.parseInt(resultSet.getString("amountRequested")));
        charityRequest.setAmountAgreed(Integer.parseInt(resultSet.getString("amountAgreed")));
        charityRequest.setSingleEvent(Boolean.parseBoolean(resultSet.getString("isSingleEvent")));
        charityRequest.setDurationInYears(Integer.parseInt(resultSet.getString("durationInYears")));
        charityRequest.setAgreedDurationInYears(Integer.parseInt(resultSet.getString("agreedDurationInYears")));
        charityRequest.setRequestStatus(resultSet.getString("requestStatus"));
        charityRequest.setRequestDate(resultSet.getDate("requestDate"));
        charityRequest.setDueDate(resultSet.getDate("dueDate"));
        charityRequest.setCreatedAt(resultSet.getTimestamp("createdAt"));
        charityRequest.setCharityName(resultSet.getString("charityName"));
        charityRequest.setCharityDescription(resultSet.getString("charityDescription"));
        charityRequest.setCharityImageUrl(resultSet.getString("charityImgUrl"));
        charityRequest.setCharityWebUrl(resultSet.getString("charityWebUrl"));

        requests.add(charityRequest);
    }


    private Connection getDatabaseConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager
                .getConnection(String.format("jdbc:mysql://%s/%s?user=%s&password=%s", DB_HOST, DB_NAME, DB_USER, DB_PASSWORD));
    }

}
