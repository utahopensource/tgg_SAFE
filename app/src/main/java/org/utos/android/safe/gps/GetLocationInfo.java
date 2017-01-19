package org.utos.android.safe.gps;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by zachariah.davis on 1/17/17.
 */

public class GetLocationInfo {

    private Context context;
    private double lat;
    private double lng;

    public GetLocationInfo(Context _context, double _lat, double _lng) {
        context = _context;
        lat = _lat;
        lng = _lng;
    }

    /**
     * Will be able to return location info if needed
     *
     * String address = addresses.get(0).getAddressLine(0);
     * String city = addresses.get(0).getLocality();
     * String state = addresses.get(0).getAdminArea();
     * String postalCode = addresses.get(0).getPostalCode();
     */
    public List<Address> getLocInfo() {
        //
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return addresses;
    }
}
