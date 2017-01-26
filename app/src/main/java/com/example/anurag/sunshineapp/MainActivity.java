package com.example.anurag.sunshineapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.anurag.sunshineapp.sync.SunshineSyncAdapter;

public class MainActivity extends AppCompatActivity implements CallBack {

    String mLocation;
    Boolean mTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
        mLocation = Utility.getPreferredLocation(this);
        Log.d("chk", "inside on Create");

        if (findViewById(R.id.weather_detail_container) != null) {
            // this will present only when screen size >600dp
            // if this present then activity should be in two pane mode
            Log.d("chk", "inside");
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.weather_detail_container, new DetailFragment(),DetailFragment.DETAIL_FRAG_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
//            getSupportActionBar().setElevation(0f);
        }

        ForecastFragment forecastFragment =  ((ForecastFragment)getSupportFragmentManager()
                                .findFragmentById(R.id.fragment_forecast));
               forecastFragment.setUseTodayLayout(!mTwoPane);


        SunshineSyncAdapter.initializeSyncAdapter(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        String location = Utility.getPreferredLocation(this);
        if (location!=null&&!location.equals(mLocation)) {
            Log.d("location2", "called");
            ForecastFragment ff = (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            if(ff!=null) {
                ff.onLocationChanged();
            }
            DetailFragment df= (DetailFragment) getSupportFragmentManager().findFragmentByTag(DetailFragment.DETAIL_FRAG_TAG);
            if(null!=df){
                df.onLocationChanged(location);
            }
            mLocation = location;
        }
    }

    @Override
    public void onItemSelected(Uri dateUri) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable("uri", dateUri);
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.weather_detail_container, detailFragment, DetailFragment.DETAIL_FRAG_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class).setData(dateUri);
            startActivity(intent);
        }
    }


}
