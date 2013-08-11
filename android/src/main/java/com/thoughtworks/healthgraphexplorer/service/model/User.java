package com.thoughtworks.healthgraphexplorer.service.model;

public class User {
    private int userId;
    private String profile;
    private String settings;
    private String fitnessActivities;
    private String strengthTrainingActivities;
    private String backgroundActivities;
    private String sleep;
    private String nutrition;
    private String weight;
    private String generalMeasurements;
    private String diabetes;
    private String records;
    private String team;

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", profile='" + profile + '\'' +
                ", settings='" + settings + '\'' +
                ", fitnessActivities='" + fitnessActivities + '\'' +
                ", strengthTrainingActivities='" + strengthTrainingActivities + '\'' +
                ", backgroundActivities='" + backgroundActivities + '\'' +
                ", sleep='" + sleep + '\'' +
                ", nutrition='" + nutrition + '\'' +
                ", weight='" + weight + '\'' +
                ", generalMeasurements='" + generalMeasurements + '\'' +
                ", diabetes='" + diabetes + '\'' +
                ", records='" + records + '\'' +
                ", team='" + team + '\'' +
                '}';
    }
}
