package me.ronanlafford.spontaneous;

/**
 * Created by 15the on 19/12/2016.
 */

public class Location {
    private int id;
    private String name;
    private String address;
    private String lat;
    private String lng;
    private String type;

    //Create constructor for the 4 fields to be used in database
    public Location(int id, String name, String address, String lat, String lng, String type) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.type = type;

    }

    //Empty constructor
    public Location() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    //Override the String method to get the variables into the listview as proper strings
    @Override
    public String toString() {
        return name + address + lat + lng + type;
    }

}


