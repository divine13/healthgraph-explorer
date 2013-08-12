package com.thoughtworks.healthgraphexplorer.service.model;

public class WeightSet {
    private String uri;
    // of course timestamp should be of type Date, but since the API uses a format that
    // GSON can't parse, I'll leave this for later (would need to inject a deserializer into GSON)
    private String timestamp;
    private Double weight;

    public WeightSet() {
    }

    public WeightSet(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "WeightSet{" +
                "uri='" + uri + '\'' +
                ", timestamp=" + timestamp +
                ", weight=" + weight +
                '}';
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
