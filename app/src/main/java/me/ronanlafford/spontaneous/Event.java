package me.ronanlafford.spontaneous;

import android.os.Parcel;
import android.os.Parcelable;


public class Event implements Parcelable {
    private String title;
    private String time;
    private String date;
    private String address;
    private String description;
    private String imageUri;
    private String latitude;
    private String longitude;


    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Event(String title, String time, String date, String address, String description, String imageUri, String latitude, String longitude) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.address = address;
        this.description = description;
        this.imageUri = imageUri;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Event(Parcel in) {
        title = in.readString();
        date = in.readString();
        time = in.readString();
        address = in.readString();
        description = in.readString();
        imageUri = in.readString();
        latitude = in.readString();
        longitude = in.readString();
    }

    public Event() {

    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(address);
        dest.writeString(description);
        dest.writeString(imageUri);
        dest.writeString(latitude);
        dest.writeString(longitude);
    }

    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        public Event[] newArray(int size) {
            return new Event[size];

        }
    };
}
