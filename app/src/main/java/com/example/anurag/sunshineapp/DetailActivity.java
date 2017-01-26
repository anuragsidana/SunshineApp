package com.example.anurag.sunshineapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.anurag.sunshineapp.data.WeatherContract.WeatherEntry;

public class DetailActivity extends AppCompatActivity {
    private static final String[] FORECAST_COLUMNS = {
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP
    };
    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    static String mForecastStr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {

            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI,getIntent().getData());
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().add(R.id.weather_detail_container, detailFragment,DetailFragment.DETAIL_FRAG_TAG)
                    .commit();


        }

    }


}
