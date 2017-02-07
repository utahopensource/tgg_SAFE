package org.utos.android.safe.util.localjson;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zachariah.davis on 1/24/17.
 */

public class LanguagesWorkers {

    private final Context ctx;

    public LanguagesWorkers(Context _ctx) {
        ctx = _ctx;
    }

    private String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = ctx.getAssets().open("language.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public ArrayList<HashMap<String, String>> getLanguages() {

        //
        ArrayList<HashMap<String, String>> caseWorkersList = new ArrayList<>();

        try {
            JSONObject jsonObj = new JSONObject(loadJSONFromAsset());

            // Getting JSON Array node
            JSONArray caseWorkers = jsonObj.getJSONArray("Languages");

            // looping through All bases
            for (int i = 0; i < caseWorkers.length(); i++) {
                JSONObject c = caseWorkers.getJSONObject(i);

                String language = c.getString("language");
                String locale = c.getString("locale");

                // tmp hashmap for single contact
                HashMap<String, String> contactTemp = new HashMap<>();

                // adding each child node to HashMap key => value
                contactTemp.put("language", language);
                contactTemp.put("locale", locale);

                // adding contact to contact list
                caseWorkersList.add(contactTemp);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return caseWorkersList;
    }

}
