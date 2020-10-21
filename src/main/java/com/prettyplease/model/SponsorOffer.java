package com.prettyplease.model;

import java.util.Date;

public class SponsorOffer {

    private String offerId;
    private String sponsorId;
    private String requestId;
    private String offerStatus;
    private int offerAmount;
    private boolean isSingleEvent;
    private int offerDurationInYears;
    private Date createdAt;

    public SponsorOffer(String offerId, String sponsorId, String requestId, String offerStatus) {
        this.offerId = offerId;
        this.sponsorId = sponsorId;
        this.requestId = requestId;
        this.offerStatus = offerStatus;
    }

    public void setOfferAmount(int offerAmount) {
        this.offerAmount = offerAmount;
    }

    public void setSingleEvent(boolean singleEvent) {
        isSingleEvent = singleEvent;
    }

    public void setOfferDurationInYears(int offerDurationInYears) {
        this.offerDurationInYears = offerDurationInYears;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getOfferId() {
        return offerId;
    }

    public String getSponsorId() {
        return sponsorId;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getOfferStatus() {
        return offerStatus;
    }

    public int getOfferAmount() {
        return offerAmount;
    }

    public boolean isSingleEvent() {
        return isSingleEvent;
    }

    public int getOfferDurationInYears() {
        return offerDurationInYears;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "SponsorOffer{" +
                "offerId='" + offerId + '\'' +
                ", sponsorId='" + sponsorId + '\'' +
                ", requestId='" + requestId + '\'' +
                ", offerStatus='" + offerStatus + '\'' +
                ", offerAmount=" + offerAmount +
                ", isSingleEvent=" + isSingleEvent +
                ", offerDurationInYears=" + offerDurationInYears +
                ", createdAt=" + createdAt +
                '}';
    }
}
