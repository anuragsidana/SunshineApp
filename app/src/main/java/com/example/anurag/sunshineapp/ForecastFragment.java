package com.example.anurag.sunshineapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.anurag.sunshineapp.data.WeatherContract;
import com.example.anurag.sunshineapp.sync.SunshineSyncAdapter;

/**
 * Created by anurag on 9/6/2016.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener {
    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;
    private static final int FORECAST_LOADER = 0;
    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };
    private static String SELECTED_KEY = "position";
    ArrayAdapter<String> adapter;
    ForecastAdapter mForecastAdapter;
    Intent shareIntent;
    ListView listView;
    // private ShareActionProvider mShareActionProvider;
    private int mPosition;
    private boolean mUseTodayLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_layout, container, false);
       /* String[] string = {
                "anurag",
                "anurag",
                "anurag",
                "anurag",
                "anurag"
        };
        List<String> weekForecast = new ArrayList<String>(Arrays.asList(string));

        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, weekForecast);

        listView.setAdapter(adapter);
        */
        listView = (ListView) v.findViewById(R.id.list);
        View emptyView = v.findViewById(R.id.listview_forecast_empty);
        listView.setEmptyView(emptyView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
                if (cursor != null) {
                    String locationString = Utility.getPreferredLocation(getContext());
//                    Intent intent = new Intent(getContext(), DetailActivity.class).setData(
//                            WeatherContract.WeatherEntry.buildWeatherLocationWithDate(locationString, cursor.getLong(COL_WEATHER_DATE))
//                    );
                    // startActivity(intent);
                    ((CallBack) getActivity()).onItemSelected(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(locationString, cursor.getLong(COL_WEATHER_DATE)));

                }
                mPosition = i;
            }

        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

// use refresh button to fetch data from the internet again
        // updateWeather();
      /*  String locationSettings = Utility.getPreferredLocation(getActivity());
        //Sort order ascending by date

        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(locationSettings, System.currentTimeMillis());
        Cursor cursor = getActivity().getContentResolver().query(weatherForLocationUri, null, null, null, sortOrder);
        mForecastAdapter = new ForecastAdapter(getContext(), cursor, 0);
        listView.setAdapter(mForecastAdapter);
        //  updateWeather();
        */
        // updateWeather();
        mForecastAdapter = new ForecastAdapter(getContext(), null, 0);
        listView.setAdapter(mForecastAdapter);
        return v;
    }


    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
        if (mForecastAdapter != null) {
            mForecastAdapter.setUseTodayLayout(mUseTodayLayout);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.d("anurag", "called");
    }


    public void openLocation() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String location = preferences.getString(getString(R.string.pref_location_key), "335001");
        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", location)
                .build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivity(intent);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Log.d("anurag", "inside");
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.settings) {
            startActivity(new Intent(getContext(), SettingsActivity.class));
        }

        if (id == R.id.refresh) {
            updateWeather();

            Log.d("anurag", "called again");

        }
        if (id == R.id.launch_map) {
            openLocation();
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateWeather() {
        Log.d("inside", "inside on update");
//        FetchWeatherTask task = new FetchWeatherTask(getContext());
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        String location = preferences.getString(getString(R.string.pref_location_key), "94043");
//        Log.d("location", "location we r sending is " + location);
//        task.execute(location);

        // getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
        // fetchWeatherTask task = new fetchWeatherTask();

//        // task.execute(location);
//        Intent alarmIntent = new Intent(getActivity(), SunshineService.AlarmReceiver.class);
//        alarmIntent.putExtra(SunshineService.LOCATION_QUERY_EXTRA, Utility.getPreferredLocation(getActivity()));

//Wrap in a pending intent which only fires once.
//        PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0, alarmIntent, PendingIntent.FLAG_ONE_SHOT);//getBroadcast(context, 0, i, 0);
//
//        AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
//
////Set the AlarmManager to wake up the system.
//        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pi);


//        Intent intent = new Intent(getActivity(), SunshineService.class);
//        intent.putExtra(SunshineService.LOCATION_QUERY_EXTRA,
//                Utility.getPreferredLocation(getActivity()));
//        getActivity().startService(intent);


        //  SunshineSyncAdapter.syncImmediately(getActivity());

    }


    public void onLocationChanged() {
        updateWeather();
        getLoaderManager().restartLoader(FORECAST_LOADER, null, this);

    }

    // loader callbacks

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String locationSettings = Utility.getPreferredLocation(getActivity());
        //Sort order ascending by date

        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(locationSettings, System.currentTimeMillis());


        return new CursorLoader(getContext(), weatherForLocationUri, FORECAST_COLUMNS, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mForecastAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            listView.smoothScrollToPosition(mPosition);
        }
        updateEmptyView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mForecastAdapter.swapCursor(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

//    public void updateEmptyView() {
//        if (mForecastAdapter.getCount() == 0) {
//            TextView textView = (TextView) getView().findViewById(R.id.listview_forecast_empty);
//            if (null != textView) {
//                int message = R.string.empty_forecast_list;
//                if (!Utility.isNetworkAvailable(getActivity())) {
//                    message = R.string.empty_forecast_list_no_network;
//                }
//                textView.setText(message);
//            }
//
//        }
//    }

    private void updateEmptyView() {
        if (mForecastAdapter.getCount() == 0) {
            TextView tv = (TextView) getView().findViewById(R.id.listview_forecast_empty);
            if (null != tv) {
                // if cursor is empty, why? do we have an invalid location
                int message = R.string.empty_forecast_list;
                @SunshineSyncAdapter.LocationStatus int location = Utility.getLocationStatus(getActivity());
                switch (location) {
                    case SunshineSyncAdapter.LOCATION_STATUS_SERVER_DOWN:
                        message = R.string.empty_forecast_list_server_down;
                        break;
                    case SunshineSyncAdapter.LOCATION_STATUS_SERVER_INVALID:
                        message = R.string.empty_forecast_list_server_error;
                        break;
                    case SunshineSyncAdapter.LOCATION_STATUS_INVALID:
                        message=R.string.empty_forecast_list_invalid_location;
                        break;
                    default:
                        if (!Utility.isNetworkAvailable(getActivity())) {
                            message = R.string.empty_forecast_list_no_network;
                        }
                }
                tv.setText(message);
            }
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(R.string.pref_location_status_key)) {
            Log.d("anu","get called ");
            updateEmptyView();
        }
    }

    @Override
    public void onResume() {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onPause() {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }
}
