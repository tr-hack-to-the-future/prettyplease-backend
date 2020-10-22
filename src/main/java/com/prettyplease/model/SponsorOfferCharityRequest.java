package com.prettyplease.model;

import java.util.Date;

public class SponsorOfferCharityRequest {

    private String offerId;
    private String sponsorId;
    private String requestId;
    private String offerStatus;
    private int offerAmount;
    private boolean isOfferSingleEvent;
    private int offerDurationInYears;
    private Date createdAt;

    // Sponsor
    private String sponsorName;
    private String sponsorDescription;
    private String sponsorImageUrl;
    private String SponsorWebUrl;

    // Fund Request
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

    // TODO add Charity table columns

    public SponsorOfferCharityRequest() {
        super();
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getSponsorId() {
        return sponsorId;
    }

    public void setSponsorId(String sponsorId) {
        this.sponsorId = sponsorId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getOfferStatus() {
        return offerStatus;
    }

    public void setOfferStatus(String offerStatus) {
        this.offerStatus = offerStatus;
    }

    public int getOfferAmount() {
        return offerAmount;
    }

    public void setOfferAmount(int offerAmount) {
        this.offerAmount = offerAmount;
    }

    public boolean isOfferSingleEvent() {
        return isOfferSingleEvent;
    }

    public void setOfferSingleEvent(boolean offerSingleEvent) {
        isOfferSingleEvent = offerSingleEvent;
    }

    public int getOfferDurationInYears() {
        return offerDurationInYears;
    }

    public void setOfferDurationInYears(int offerDurationInYears) {
        this.offerDurationInYears = offerDurationInYears;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getSponsorName() {
        return sponsorName;
    }

    public void setSponsorName(String sponsorName) {
        this.sponsorName = sponsorName;
    }

    public String getSponsorDescription() {
        return sponsorDescription;
    }

    public void setSponsorDescription(String sponsorDescription) {
        this.sponsorDescription = sponsorDescription;
    }

    public String getSponsorImageUrl() {
        return sponsorImageUrl;
    }

    public void setSponsorImageUrl(String sponsorImageUrl) {
        this.sponsorImageUrl = sponsorImageUrl;
    }

    public String getSponsorWebUrl() {
        return SponsorWebUrl;
    }

    public void setSponsorWebUrl(String sponsorWebUrl) {
        SponsorWebUrl = sponsorWebUrl;
    }

    public String getCharityId() {
        return charityId;
    }

    public void setCharityId(String charityId) {
        this.charityId = charityId;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getIncentive() {
        return incentive;
    }

    public void setIncentive(String incentive) {
        this.incentive = incentive;
    }

    public int getAmountRequested() {
        return amountRequested;
    }

    public void setAmountRequested(int amountRequested) {
        this.amountRequested = amountRequested;
    }

    public int getAmountAgreed() {
        return amountAgreed;
    }

    public void setAmountAgreed(int amountAgreed) {
        this.amountAgreed = amountAgreed;
    }

    public boolean isSingleEvent() {
        return isSingleEvent;
    }

    public void setSingleEvent(boolean singleEvent) {
        isSingleEvent = singleEvent;
    }

    public int getDurationInYears() {
        return durationInYears;
    }

    public void setDurationInYears(int durationInYears) {
        this.durationInYears = durationInYears;
    }

    public int getAgreedDurationInYears() {
        return agreedDurationInYears;
    }

    public void setAgreedDurationInYears(int agreedDurationInYears) {
        this.agreedDurationInYears = agreedDurationInYears;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public String toString() {
        return "OfferRequest{" +
                "offerId='" + offerId + '\'' +
                ", sponsorId='" + sponsorId + '\'' +
                ", requestId='" + requestId + '\'' +
                ", offerStatus='" + offerStatus + '\'' +
                ", offerAmount=" + offerAmount +
                ", isOfferSingleEvent=" + isOfferSingleEvent +
                ", offerDurationInYears=" + offerDurationInYears +
                ", createdAt=" + createdAt +
                ", sponsorName='" + sponsorName + '\'' +
                ", sponsorDescription='" + sponsorDescription + '\'' +
                ", sponsorImageUrl='" + sponsorImageUrl + '\'' +
                ", SponsorWebUrl='" + SponsorWebUrl + '\'' +
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
                '}';
    }
}
