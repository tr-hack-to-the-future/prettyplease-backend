package com.prettyplease.model;

import java.util.Date;

public class CharityRequest {
    private String requestId;
    private String charityId;
    private String eventDescription;
    private String incentive;
    private int amountRequested;
    private int amountAgreed;
    private boolean isSingleEvent;
    private int durationInYears;
    private int agreedDurationInYears;
    private String requestStatus;
    private Date requestDate;
    private Date dueDate;
    private Date createdAt;
    // charity fields
    private String charityName;
    private String charityDescription;
    private String charityImageUrl;
    private String charityWebUrl;

    public CharityRequest() {
    }

    public CharityRequest(String requestId, String charityId, String eventDescription) {
        this.requestId = requestId;
        this.charityId = charityId;
        this.eventDescription = eventDescription;
    }

    public void setIncentive(String incentive) {
        this.incentive = incentive;
    }

    public void setAmountRequested(int amountRequested) {
        this.amountRequested = amountRequested;
    }

    public void setAmountAgreed(int amountAgreed) {
        this.amountAgreed = amountAgreed;
    }

    public void setSingleEvent(boolean singleEvent) {
        isSingleEvent = singleEvent;
    }

    public void setDurationInYears(int durationInYears) {
        this.durationInYears = durationInYears;
    }

    public void setAgreedDurationInYears(int agreedDurationInYears) {
        this.agreedDurationInYears = agreedDurationInYears;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setCharityName(String charityName) {
        this.charityName = charityName;
    }

    public void setCharityDescription(String charityDescription) {
        this.charityDescription = charityDescription;
    }

    public void setCharityImageUrl(String charityImageUrl) {
        this.charityImageUrl = charityImageUrl;
    }

    public void setCharityWebUrl(String charityWebUrl) {
        this.charityWebUrl = charityWebUrl;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getCharityId() {
        return charityId;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public String getIncentive() {
        return incentive;
    }

    public int getAmountRequested() {
        return amountRequested;
    }

    public int getAmountAgreed() {
        return amountAgreed;
    }

    public boolean isSingleEvent() {
        return isSingleEvent;
    }

    public int getDurationInYears() {
        return durationInYears;
    }

    public int getAgreedDurationInYears() {
        return agreedDurationInYears;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getCharityName() {
        return charityName;
    }

    public String getCharityDescription() {
        return charityDescription;
    }

    public String getCharityImageUrl() {
        return charityImageUrl;
    }

    public String getCharityWebUrl() {
        return charityWebUrl;
    }

    @Override
    public String toString() {
        return "CharityFundRequest{" +
                "requestId='" + requestId + '\'' +
                ", charityId='" + charityId + '\'' +
                ", eventDescription='" + eventDescription + '\'' +
                ", incentive='" + incentive + '\'' +
                ", amountRequested=" + amountRequested +
                ", amountAgreed=" + amountAgreed +
                ", isSingleEvent=" + isSingleEvent +
                ", durationInYears=" + durationInYears +
                ", agreedDurationInYears=" + agreedDurationInYears +
                ", requestStatus='" + requestStatus + '\'' +
                ", requestDate=" + requestDate +
                ", dueDate=" + dueDate +
                ", createdAt=" + createdAt +
                ", charityName='" + charityName + '\'' +
                ", charityDescription='" + charityDescription + '\'' +
                ", charityImageUrl='" + charityImageUrl + '\'' +
                ", charityWebUrl='" + charityWebUrl + '\'' +
                '}';
    }
}
