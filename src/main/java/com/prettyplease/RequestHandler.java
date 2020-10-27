package com.prettyplease;

import com.amazonaws.services.lambda.runtime.Context;
import com.prettyplease.model.CharityRequest;
import com.prettyplease.model.tables.FundRequest;
import com.prettyplease.model.RequestStatus;
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
//    private static final String getSql = "SELECT requestId, charityId, eventDescription, incentive, amountRequested, amountAgreed, isSingleEvent, durationInYears, agreedDurationInYears, requestStatus, requestDate, dueDate, createdAt " +
//            "FROM prettyplease.FundRequest WHERE requestId = ?";
private static final String getSql = "SELECT f.requestId, f.charityId, f.eventDescription, f.incentive, " +
        "f.amountRequested, f.amountAgreed, f.isSingleEvent, f.durationInYears, f.agreedDurationInYears, " +
        "f.requestStatus, f.requestDate, f.dueDate, f.createdAt, " +
        "c.name as charityName, c.description as charityDescription, " +
        "c.imageUrl as charityImgUrl, c.webUrl as charityWebUrl \n" +
        "FROM prettyplease.FundRequest f INNER JOIN prettyplease.Charity c\n" +
        "ON c.charityId = f.charityId WHERE requestId = ?";
    private static final String createSql = "INSERT INTO prettyplease.FundRequest " +
            "(requestId, charityId , eventDescription, incentive, amountRequested, amountAgreed, isSingleEvent, durationInYears, agreedDurationInYears, requestStatus, requestDate, dueDate, createdAt) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp())";
    private static final String updateRequestStatusSql = "UPDATE prettyplease.FundRequest SET requestStatus = 'ACCEPTED' WHERE requestId = ?;";


    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        String httpMethod = (String) input.get("httpMethod");
        Object response = null;
        int statusCode = HttpStatus.OK;   // default to success

        if ("GET".equalsIgnoreCase(httpMethod)) {
            try {
                response = getRequests(input);
            } catch (ClassNotFoundException e) {
                LOG.info("Problem setting up database connection: {}", e.getMessage());
                statusCode = HttpStatus.BAD_REQUEST;   // Bad Request
                response = e.getMessage();
            } catch (SQLException e) {
                LOG.error("Database/SQL problem: {}", e.getMessage());
                statusCode = HttpStatus.CONFLICT;   // Conflict
                response = e.getMessage();
            }
        } else if ("POST".equalsIgnoreCase((httpMethod))) {
            // parse into JSON object
            try {
                response = createFundRequest((String) input.get("body"));
            } catch (JSONException e) {
                LOG.error("Problem parsing POST data: {}", e.getMessage());
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
        } else if ("PUT".equalsIgnoreCase(httpMethod)) {
            try {
                response = updateRequestStatus(input);
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

    private List<CharityRequest> getRequests(Map<String, Object> input) throws SQLException, ClassNotFoundException {
        List<CharityRequest> requests = new ArrayList<>();
        String requestId = (String) ((Map) input.get("pathParameters")).get("requestId");
        try (
                Connection connection = getDatabaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(getSql);
        ) {
            preparedStatement.setString(1, requestId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    buildCharityFundRequestFromDB(requests, resultSet);
                }
            }
        }
        return requests;
    }

    private void buildCharityFundRequestFromDB(List<CharityRequest> requests, ResultSet resultSet) throws SQLException {
        // populate CharityFundRequest object
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
        FundRequest fundRequest = new FundRequest();
        fundRequest.setRequestId(requestId);
        fundRequest.setCharityId(charityId);
        fundRequest.setEventDescription(eventDescription);
        fundRequest.setIncentive(incentive);
        fundRequest.setAmountRequested(amountRequested);
        fundRequest.setAmountAgreed(amountAgreed);
        fundRequest.setSingleEvent(isSingleEvent);
        fundRequest.setDurationInYears(durationInYears);
        fundRequest.setAgreedDurationInYears(agreedDurationInYears);
        fundRequest.setRequestStatus(RequestStatus.valueOf(requestStatus.toUpperCase()));
        fundRequest.setRequestDate(requestDate);
        fundRequest.setDueDate(dueDate);
        fundRequest.setCreatedAt(createdAt);

        requests.add(fundRequest);
    }


    private String createFundRequest(String body) throws SQLException, ClassNotFoundException {
        JSONObject postBody = new JSONObject(body);
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
            // TODO these dates are part of the advanced feature set
//            LocalDate localDate = LocalDate.now();
//            LOG.info("\nlocal date " + localDate + "\n");
            preparedStatement.setDate(11, null);
            preparedStatement.setDate(12, null);
//            preparedStatement.setDate(11, java.sql.Date.valueOf(localDate));
//            preparedStatement.setDate(12, parseDate(postBody.getString("dueDate")));
            LOG.info("\n" + preparedStatement.toString() + "\n");
            int rowsCreated = preparedStatement.executeUpdate();
            if (rowsCreated == 1) {
                id = requestId.toString();
            }
        }
        return id;
    }

    private Object updateRequestStatus(Map<String, Object> input) throws ClassNotFoundException, SQLException {
        String id = "";
        String requestId = (String) ((Map) input.get("pathParameters")).get("requestId");
        try (
                Connection connection = getDatabaseConnection();
                PreparedStatement updateStatement = connection.prepareStatement(updateRequestStatusSql);
        ) {
            // update the request status to ACCEPTED
            updateStatement.setString(1, requestId.toString());
            int updatedRows = updateStatement.executeUpdate();
            LOG.info("\n" + updateStatement.toString() + " : rows updated = " + updatedRows + "\n");
            if (updatedRows == 1) {
                id = requestId;
                LOG.info("Updated ACCEPTED status for requestId: " + requestId);
            } else {
                // can be an input data issue, so this may not be a database problem
                throw new SQLException("Problem updating FundRequest status for requestId: " + requestId);
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
