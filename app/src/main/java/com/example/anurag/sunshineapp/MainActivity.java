package com.example.anurag.sunshineapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    String mLocation;
    Boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLocation = Utility.getPreferredLocation(this);
        Log.d("chk","inside on Create");

        if (findViewById(R.id.weather_detail_container) != null) {
            // this will present only when screen size >600dp
            // if this present then activity should be in two pane mode
            Log.d("chk","inside");
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.weather_detail_container, new DetailFragment())
                        .commit();
            }
        } else {
            mTwoPane = false;
        }


    }


    @Override
    protected void onResume() {
        super.onResume();

        if (mLocation != Utility.getPreferredLocation(this)) {
            Log.d("location2", "called");
            ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            ff.onLocationChanged();
            mLocation = Utility.getPreferredLocation(this);
        }
    }
}
