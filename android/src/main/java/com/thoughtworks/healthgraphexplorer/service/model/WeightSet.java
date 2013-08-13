package com.thoughtworks.healthgraphexplorer.service.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class WeightSet {
    private String uri;
    private Date timestamp;
    private Double weight;
    @SerializedName("fat_percent")
    private Double fatPercent;

    public WeightSet() {
        this.timestamp = new Date();
    }

    @Override
    public String toString() {
        return "WeightSet{" +
                "uri='" + uri + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", weight=" + weight +
                ", fatPercent=" + fatPercent +
                '}';
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public void setFatPercent(Double fatPercent) {
        this.fatPercent = fatPercent;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Double getWeight() {
        return weight;
    }

    public Double getFatPercent() {
        return fatPercent;
    }
}
