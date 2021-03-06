package com.prettyplease.model.tables;

import com.prettyplease.model.RequestStatus;

import java.util.Date;

public class FundRequest {

    private String requestId;
    private String charityId;
    private String eventDescription;
    private String incentive;
    private int amountRequested;
    private int amountAgreed;
    private boolean isSingleEvent;
    private int durationInYears;
    private int agreedDurationInYears;
    private RequestStatus requestStatus;
    private Date requestDate;
    private Date dueDate;
    private Date createdAt;

    public FundRequest() {
        super();
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setCharityId(String charityId) {
        this.charityId = charityId;
    }

    public void setEventDescription(String eventDescription) {
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

    public void setRequestStatus(RequestStatus requestStatus) {
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

    public RequestStatus getRequestStatus() {
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

    @Override
    public String toString() {
        return "FundRequest{" +
                "requestId='" + requestId + '\'' +
                ", charityId='" + charityId + '\'' +
                ", eventDescription='" + eventDescription + '\'' +
                ", incentive='" + incentive + '\'' +
                ", amountRequested=" + amountRequested +
                ", amountAgreed=" + amountAgreed +
                ", isSingleEvent=" + isSingleEvent +
                ", durationInYears=" + durationInYears +
                ", agreedDurationInYears=" + agreedDurationInYears +
                ", requestStatus='" + requestStatus.toString() + '\'' +
                ", requestDate=" + requestDate +
                ", dueDate=" + dueDate +
                ", createdAt=" + createdAt +
                '}';
    }
}
