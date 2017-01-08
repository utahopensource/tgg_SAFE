package org.utos.android.safe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class NonUrgentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_urgent);
    }
}
