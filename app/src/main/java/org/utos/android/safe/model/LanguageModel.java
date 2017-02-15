package org.utos.android.safe.model;

import android.os.Parcel;
import android.os.Parcelable;

public class LanguageModel implements Parcelable {

    private String language, local;

    public LanguageModel() {
    }

    public LanguageModel(String language, String local) {
        this.language = language;
        this.local = local;
    }

    /////////////////////////////////////////////
    //getters
    public String getLanguage() {
        return language;
    }

    public String getLocal() {
        return local;
    }

    /////////////////////////////////////////////
    //setters
    public void setLanguage(String language) {
        this.language = language;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    ////////////////////////////////
    // http://www.parcelabler.com/
    // Parcelable
    private LanguageModel(Parcel in) {
        language = in.readString();
        local = in.readString();
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(language);
        dest.writeString(local);
    }

    @SuppressWarnings("unused") public static final Creator<LanguageModel> CREATOR = new Creator<LanguageModel>() {
        @Override public LanguageModel createFromParcel(Parcel in) {
            return new LanguageModel(in);
        }

        @Override public LanguageModel[] newArray(int size) {
            return new LanguageModel[size];
        }
    };
    // Parcelable
    ////////////////////////////////

}

