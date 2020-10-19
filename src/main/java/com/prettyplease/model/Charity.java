package com.prettyplease.model;

import java.util.Date;

public class Charity {

    private String charityId;
    private String name;
    private String description;
    private String imageUrl;
    private String webUrl;
    private Date createdAt;

    public Charity(String charityId, String name, String description) {
        this.charityId = charityId;
        this.name = name;
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getCharityId() {
        return charityId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Charity{" +
                "charityId=" + charityId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", webUrl='" + webUrl + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
