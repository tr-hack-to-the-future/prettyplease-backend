package com.prettyplease;

import com.amazonaws.services.lambda.runtime.Context;
import com.prettyplease.model.FundRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.*;
import java.util.*;

public class RequestHandler implements com.amazonaws.services.lambda.runtime.RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger LOG = LogManager.getLogger(RequestHandler.class);
    private String DB_HOST = System.getenv("DB_HOST");
    private String DB_NAME = System.getenv("DB_NAME");
    private String DB_USER = System.getenv("DB_USER");
    private String DB_PASSWORD = System.getenv("DB_PASSWORD");
    private static final String getSql = "SELECT requestId, charityId, eventDescription, incentive, amountRequested, amountAgreed, isSingleEvent, durationInYears, agreedDurationInYears, requestStatus, requestDate, dueDate, createdAt " +
            "FROM prettyplease.FundRequest WHERE requestId = ?";
    private static final String createSql = "INSERT INTO prettyplease.FundRequest " +
            "(requestId, charityId , eventDescription, incentive, amountRequested, amountAgreed, isSingleEvent, durationInYears, agreedDurationInYears, requestStatus, requestDate, dueDate, createdAt) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp())";

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        String httpMethod = (String) input.get("httpMethod");
        Object response = null;

        if ("GET".equalsIgnoreCase(httpMethod)) {
            response = getRequests(input);
        } else if ("POST".equalsIgnoreCase((httpMethod))) {
            // parse into JSON object
            try {
                JSONObject postBody = new JSONObject((String) input.get("body"));
                response = createFundRequest(postBody);
            } catch (JSONException e) {
                LOG.error("Problem parsing POST data: {}", e.getMessage());
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


    private List<FundRequest> getRequests(Map<String, Object> input) {
        List<FundRequest> requests = new ArrayList<>();
        String sponsorId = (String) ((Map) input.get("pathParameters")).get("requestId");
        try (
                Connection connection = getDatabaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(getSql);
        ) {
            preparedStatement.setString(1, sponsorId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    buildFundRequestFromDB(requests, resultSet);
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            LOG.error(e.getMessage());
        }
        return requests;
    }

    private void buildFundRequestFromDB(List<FundRequest> requests, ResultSet resultSet) throws SQLException {
        String requestId = resultSet.getString("requestId");
        String charityId = resultSet.getString("charityId");
        String eventDescription = resultSet.getString("eventDescription");
        String incentive = resultSet.getString("incentive");
        int amountRequested = Integer.parseInt(resultSet.getString("amountRequested"));
        int amountAgreed = Integer.parseInt(resultSet.getString("amountAgreed"));
        Boolean isSingleEvent = Boolean.parseBoolean(resultSet.getString("isSingleEvent"));
        int durationInYears = Integer.parseInt(resultSet.getString("durationInYears"));
        int agreedDurationInYears = Integer.parseInt(resultSet.getString("agreedDurationInYears"));
        String requestStatus = resultSet.getString("requestStatus");
        java.util.Date requestDate = resultSet.getDate("requestDate");
        java.util.Date dueDate = resultSet.getDate("dueDate");
        java.util.Date createdAt = resultSet.getTimestamp("createdAt");
        // populate FundRequest object
        FundRequest fundRequest = new FundRequest(requestId, charityId, eventDescription);
        fundRequest.setIncentive(incentive);
        fundRequest.setAmountRequested(amountRequested);
        fundRequest.setAmountAgreed(amountAgreed);
        fundRequest.setSingleEvent(isSingleEvent);
        fundRequest.setDurationInYears(durationInYears);
        fundRequest.setAgreedDurationInYears(agreedDurationInYears);
        fundRequest.setRequestStatus(requestStatus);
        fundRequest.setRequestDate(requestDate);
        fundRequest.setDueDate(dueDate);
        fundRequest.setCreatedAt(createdAt);

        requests.add(fundRequest);
    }


    private String createFundRequest(JSONObject postBody) {
        String id = "";
        try (
                Connection connection = getDatabaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(createSql);
        ) {
            // build the prepared statement from the JSON object
            // create a random UUID for the requestI
            UUID requestId = UUID.randomUUID();
            preparedStatement.setString(1, requestId.toString());
            preparedStatement.setString(2, postBody.getString("charityId"));
            preparedStatement.setString(3, postBody.getString("eventDescription"));
            preparedStatement.setString(4, postBody.getString("incentive"));
            preparedStatement.setInt(5, postBody.getInt("amountRequested"));
            preparedStatement.setInt(6, postBody.getInt("amountAgreed"));
            // MySQL 5 uses TinyInt instead of Boolean
            preparedStatement.setInt(7, (postBody.getBoolean("isSingleEvent") ? 1 : 0));
            preparedStatement.setInt(8, postBody.getInt("durationInYears"));
            preparedStatement.setInt(9, postBody.getInt("agreedDurationInYears"));
            preparedStatement.setString(10, postBody.getString("requestStatus"));
            // TODO is request Date the current date?
            // TODO fix these dates later
//            LocalDate localDate = LocalDate.now();
//            LOG.info("\nlocal date " + localDate + "\n");
            preparedStatement.setDate(11, null);
            preparedStatement.setDate(12, null);
//            preparedStatement.setDate(11, java.sql.Date.valueOf(localDate));
//            preparedStatement.setDate(12, parseDate(postBody.getString("dueDate")));
            LOG.info("\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + preparedStatement.toString() + "\n");
            int rowsCreated = preparedStatement.executeUpdate();
            if (rowsCreated == 1) {
                id = requestId.toString();
            }
        } catch (ClassNotFoundException | SQLException e) {
            LOG.error(e.getMessage());
        }
        return id;
    }


    private Connection getDatabaseConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager
                .getConnection(String.format("jdbc:mysql://%s/%s?user=%s&password=%s", DB_HOST, DB_NAME, DB_USER, DB_PASSWORD));
    }

}
