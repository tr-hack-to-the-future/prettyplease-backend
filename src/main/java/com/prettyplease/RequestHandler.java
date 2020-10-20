package com.prettyplease;

import com.amazonaws.services.lambda.runtime.Context;
import com.prettyplease.model.FundRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RequestHandler implements com.amazonaws.services.lambda.runtime.RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger LOG = LogManager.getLogger(RequestHandler.class);
    private String DB_HOST = System.getenv("DB_HOST");
    private String DB_NAME = System.getenv("DB_NAME");
    private String DB_USER = System.getenv("DB_USER");
    private String DB_PASSWORD = System.getenv("DB_PASSWORD");
    private static final String getSql = "SELECT requestId, charityId, eventDescription, incentive, amountRequested, amountAgreed, isSingleEvent, durationInYears, agreedDurationInYears, requestStatus, requestDate, dueDate, createdAt " +
            "FROM prettyplease.FundRequest WHERE requestId = ?";
    private static final String createSql = "INSERT INTO prettyplease.FundRequest " +
            "(requestId, charityId , eventDescription, incentive, amountRequested, amountAgreed, isSingleEvent, durationInYears, agreedDurationInYears, requestStatus, requestDate, dueDate) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp());";


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
            response = getRequests(input);
        } else if ("POST".equalsIgnoreCase((httpMethod))) {
//            String body = (String) input.get("body");
//            LOG.info("\nbody: {}\n", body); // e.g. POST JSON string

            // parse into JSON object
            try {
                JSONObject postBody = new JSONObject((String) input.get("body"));
                int rows= createFundRequest(postBody);
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


    private List<FundRequest> getRequests(Map<String, Object> input) {
        List<FundRequest> requests = new ArrayList<>();
        String sponsorId = (String) ((Map) input.get("pathParameters")).get("requestId");
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager
                    .getConnection(String.format("jdbc:mysql://%s/%s?user=%s&password=%s", DB_HOST, DB_NAME, DB_USER, DB_PASSWORD));
            Statement statement = connection.createStatement();

            PreparedStatement preparedStatement = connection.prepareStatement(getSql);
            preparedStatement.setString(1, sponsorId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                buildFundRequestFromDB(requests, resultSet);
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
        Date requestDate = resultSet.getDate("requestDate");
        Date dueDate = resultSet.getDate("dueDate");
        Date createdAt = resultSet.getTimestamp("createdAt");
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


    private int createFundRequest(JSONObject postBody) {
        int rowsCreated = 0;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager
                    .getConnection(String.format("jdbc:mysql://%s/%s?user=%s&password=%s", DB_HOST, DB_NAME, DB_USER, DB_PASSWORD));
            Statement statement = connection.createStatement();

            PreparedStatement preparedStatement = connection.prepareStatement(createSql);
            // build the prepared statement from the JSON object
            // create a random UUID for the requestI
            UUID requestId = UUID.randomUUID();
            preparedStatement.setString(1, requestId.toString());
            preparedStatement.setString(2, postBody.getString("charityId"));
            preparedStatement.setString(3, postBody.getString("eventDescription"));
            preparedStatement.setString(4, postBody.getString("incentive"));
            preparedStatement.setString(5, postBody.getString("amountRequested"));
            preparedStatement.setString(6, postBody.getString("amountAgreed"));
            preparedStatement.setString(7, postBody.getString("isSingleEvent"));
            preparedStatement.setInt(8, Integer.parseInt(postBody.getString("durationInYears")));
            preparedStatement.setInt(9, Integer.parseInt(postBody.getString("agreedDurationInYears")));
            preparedStatement.setString(10, postBody.getString("requestStatus"));
            preparedStatement.setDate(11, java.sql.Date.valueOf(postBody.getString("requestDate")));
            preparedStatement.setDate(13, java.sql.Date.valueOf(postBody.getString("dueDate")));

            LOG.info("\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + preparedStatement.toString() + "\n");
            rowsCreated = preparedStatement.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
            LOG.error(e.getMessage());
        }
        return rowsCreated;
    }
}
