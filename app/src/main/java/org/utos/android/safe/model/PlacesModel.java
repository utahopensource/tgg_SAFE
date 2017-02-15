package org.utos.android.safe.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PlacesModel implements Parcelable {

    private String title, miles, placeID, lat, lng;
    private double rating;

    public PlacesModel() {
    }

    public PlacesModel(String name, double rating, String miles, String placeID, String lat, String lng) {
        this.title = name;
        this.rating = rating;
        this.miles = miles;
        this.placeID = placeID;
        this.lat = lat;
        this.lng = lng;
    }

    /////////////////////////////////////////////
    //getters
    public String getName() {
        return title;
    }

    public double getRating() {
        return rating;
    }

    public String getMiles() {
        return miles;
    }

    public String getPlaceID() {
        return placeID;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    /////////////////////////////////////////////
    //setters
    public void setName(String name) {
        this.title = name;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setMiles(String miles) {
        this.miles = miles;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    ////////////////////////////////
    // http://www.parcelabler.com/
    // Parcelable
    private PlacesModel(Parcel in) {
        title = in.readString();
        miles = in.readString();
        placeID = in.readString();
        lat = in.readString();
        lng = in.readString();
        rating = in.readDouble();
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(miles);
        dest.writeString(placeID);
        dest.writeString(lat);
        dest.writeString(lng);
        dest.writeDouble(rating);
    }

    @SuppressWarnings("unused") public static final Parcelable.Creator<PlacesModel> CREATOR = new Parcelable.Creator<PlacesModel>() {
        @Override public PlacesModel createFromParcel(Parcel in) {
            return new PlacesModel(in);
        }

        @Override public PlacesModel[] newArray(int size) {
            return new PlacesModel[size];
        }
    };
    // Parcelable
    ////////////////////////////////

}

