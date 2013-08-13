package com.thoughtworks.healthgraphexplorer.service.model;

import java.util.List;

public class WeightSetFeed {
    private int size;
    private List<WeightSet> items;

    @Override
    public String toString() {
        return "WeightSetFeed{" +
                "size=" + size +
                ", items=" + items +
                '}';
    }

    public List<WeightSet> getItems() {
        return items;
    }
}
