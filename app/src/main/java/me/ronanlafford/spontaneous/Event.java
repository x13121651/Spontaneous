package me.ronanlafford.spontaneous;

/**
 * Created by 15the on 05/02/2017.
 */

public class Event {
    private String title;
    private String time;
    private String date;
    private String address;
    private String description;

    public Event(String title, String time, String date, String address, String description) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.address = address;
        this.description = description;
    }

    public Event() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
