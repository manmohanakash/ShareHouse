package com.akash.sharehouse.models;

import java.io.Serializable;

public class House implements Serializable{

    private Integer houseID;
    private	String houseName;
    private	String houseNo;
    private	String houseStreet;
    private String houseCity;
    private String houseState;
    private String houseCountry;
    private String landmark;
    private String houseImg;
    private String description;
    private String mapX;
    private String mapY;
    private Integer maxWeeks;
    private Integer weekRate;
    private Integer sharedCounter;

    public House(){}

    public House(Integer houseID, String houseName, String houseNo, String houseStreet, String houseCity,
                 String houseState, String houseCountry, String landmark, String houseImg, String description, String mapX,
                 String mapY, Integer maxWeeks, Integer weekRate,Integer sharedCounter) {
        super();
        this.houseID = houseID;
        this.houseName = houseName;
        this.houseNo = houseNo;
        this.houseStreet = houseStreet;
        this.houseCity = houseCity;
        this.houseState = houseState;
        this.houseCountry = houseCountry;
        this.landmark = landmark;
        this.houseImg = houseImg;
        this.description = description;
        this.mapX = mapX;
        this.mapY = mapY;
        this.maxWeeks = maxWeeks;
        this.weekRate = weekRate;
        this.sharedCounter = sharedCounter;
    }

    public Integer getHouseID() {
        return houseID;
    }

    public void setHouseID(Integer houseID) {
        this.houseID = houseID;
    }

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    public String getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }

    public String getHouseStreet() {
        return houseStreet;
    }

    public void setHouseStreet(String houseStreet) {
        this.houseStreet = houseStreet;
    }

    public String getHouseCity() {
        return houseCity;
    }

    public void setHouseCity(String houseCity) {
        this.houseCity = houseCity;
    }

    public String getHouseState() {
        return houseState;
    }

    public void setHouseState(String houseState) {
        this.houseState = houseState;
    }

    public String getHouseCountry() {
        return houseCountry;
    }

    public void setHouseCountry(String houseCountry) {
        this.houseCountry = houseCountry;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getHouseImg() {
        return houseImg;
    }

    public void setHouseImg(String houseImg) {
        this.houseImg = houseImg;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMapX() {
        return mapX;
    }

    public void setMapX(String mapX) {
        this.mapX = mapX;
    }

    public String getMapY() {
        return mapY;
    }

    public void setMapY(String mapY) {
        this.mapY = mapY;
    }

    public Integer getMaxWeeks() {
        return maxWeeks;
    }
    public void setMaxWeeks(Integer maxWeeks) {
        this.maxWeeks = maxWeeks;
    }

    public void setWeekRate(Integer weekRate) {
        this.weekRate = weekRate;
    }

    public Integer getWeekRate() {
        return weekRate;
    }

    public Integer getSharedCounter() {
        return sharedCounter;
    }

    public void setSharedCounter(Integer sharedCounter) {
        this.sharedCounter = sharedCounter;
    }



    @Override
    public String toString() {
        return "House Object{" +
                "houseID=" + houseID +
                ", houseName='" + houseName + '\'' +
                ", houseNo='" + houseNo + '\'' +
                ", houseStreet='" + houseStreet + '\'' +
                ", houseCity='" + houseCity + '\'' +
                ", houseState='" + houseState + '\'' +
                ", houseCountry='" + houseCountry + '\'' +
                ", landmark='" + landmark + '\'' +
                ", houseImg='" + houseImg + '\'' +
                ", description='" + description + '\'' +
                ", mapX='" + mapX + '\'' +
                ", mapY='" + mapY + '\'' +
                ", maxWeeks='" + maxWeeks + '\'' +
                ", weekRate='" + weekRate + '\'' +
                ", sharedCounter=" + sharedCounter +
                '}';
    }
}

