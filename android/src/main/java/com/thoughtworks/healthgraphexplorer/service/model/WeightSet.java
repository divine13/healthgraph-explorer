package com.thoughtworks.healthgraphexplorer.service.model;

import com.google.gson.annotations.SerializedName;

public class WeightSet {
    private String uri;
    // of course timestamp should be of type Date, but since the API uses a format that
    // GSON can't parse, I'll leave this for later (would need to inject a deserializer into GSON)
    private String timestamp;
    private Double weight;
    @SerializedName("fat_percent")
    private Double fatPercent;

    public WeightSet() {
    }

    public WeightSet(String timestamp) {
        this.timestamp = timestamp;
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
}
