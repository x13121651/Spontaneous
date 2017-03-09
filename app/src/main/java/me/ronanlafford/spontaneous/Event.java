package me.ronanlafford.spontaneous;

import java.sql.Time;
import java.util.Date;

/**
 * Created by 15the on 05/02/2017.
 */

public class Event {
    public String title;
    public String venue;
    public Date date;
    public Time time;
    public Float latitude;
    public Float longitude;
    public String description;

    public Event(String title, String venue, Date date, Time time, Float latitude, Float longitude, String description) {
        this.title = title;
        this.venue = venue;
        this.date = date;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
