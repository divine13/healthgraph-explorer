package com.thoughtworks.healthgraphexplorer.service.model;

import java.util.Date;

public class WeightSet {
    private String uri;
    private String timestamp;
    private Double weight;

    @Override
    public String toString() {
        return "WeightSet{" +
                "uri='" + uri + '\'' +
                ", timestamp=" + timestamp +
                ", weight=" + weight +
                '}';
    }
}
