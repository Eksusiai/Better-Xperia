package com.sonymobile.calendar.weather;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.KeyEvent;
import android.widget.Toast;
import com.sonymobile.accuweather.AccuWeatherSearchForLocation;
import com.sonymobile.accuweather.WeatherLocation;
import com.sonymobile.calendar.R;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class SearchForLocation {
    private Context mContext;
    private AlertDialog mDialog;
    private boolean mGetSingleCity;
    private SearchForLocationListener mListener;
    private ProgressDialog mProgressDialog;
    AsyncTask<String, Void, List<WeatherLocation>> mTask;
    private ArrayList<WeatherLocation> mWeatherLocations;

    public SearchForLocation(Context context, SearchForLocationListener searchForLocationListener, String str, boolean z) {
        this.mWeatherLocations = new ArrayList<>();
        this.mGetSingleCity = z;
        this.mContext = context;
        this.mListener = searchForLocationListener;
        findCityFromSearchTerm(str);
    }

    public SearchForLocation(Context context, SearchForLocationListener searchForLocationListener, boolean z, ArrayList<WeatherLocation> arrayList) {
        new ArrayList();
        this.mGetSingleCity = z;
        this.mContext = context;
        this.mListener = searchForLocationListener;
        this.mWeatherLocations = arrayList;
    }

    private void findCityFromSearchTerm(String str) {
        AsyncTask<String, Void, List<WeatherLocation>> asyncTaskCreateTextSearchTask = createTextSearchTask();
        this.mTask = asyncTaskCreateTextSearchTask;
        asyncTaskCreateTextSearchTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, str);
    }

    private AsyncTask<String, Void, List<WeatherLocation>> createTextSearchTask() {
        return new AsyncTask<String, Void, List<WeatherLocation>>() { // from class: com.sonymobile.calendar.weather.SearchForLocation.1
            private boolean success;

            @Override // android.os.AsyncTask
            protected void onPreExecute() {
                this.success = false;
                SearchForLocation.this.mProgressDialog = new ProgressDialog(SearchForLocation.this.mContext);
                SearchForLocation.this.mProgressDialog.setMessage(SearchForLocation.this.mContext.getString(R.string.dialog_progress_searching_text));
                SearchForLocation.this.mProgressDialog.setProgressStyle(0);
                SearchForLocation.this.mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.sonymobile.calendar.weather.SearchForLocation.1.1
                    @Override // android.content.DialogInterface.OnCancelListener
                    public void onCancel(DialogInterface dialogInterface) {
                        cancel(true);
                    }
                });
                SearchForLocation.this.mProgressDialog.show();
                super.onPreExecute();
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(List<WeatherLocation> list) {
                if (SearchForLocation.this.mProgressDialog != null) {
                    SearchForLocation.this.mProgressDialog.dismiss();
                }
                if (!this.success || list == null || list.size() < 1) {
                    Toast.makeText(SearchForLocation.this.mContext, SearchForLocation.this.mContext.getString(R.string.toast_search_city_failed), 0).show();
                    return;
                }
                SearchForLocation.this.mWeatherLocations = new ArrayList(list);
                if (!SearchForLocation.this.mGetSingleCity) {
                    SearchForLocation.this.mListener.getWeatherLocationsFromSearch(list);
                } else {
                    SearchForLocation.this.populateListView(list);
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public List<WeatherLocation> doInBackground(String... strArr) {
                String str = (strArr == null || strArr.length <= 0) ? null : strArr[0];
                if (str == null || str.length() == 0) {
                    return null;
                }
                this.success = true;
                return new AccuWeatherSearchForLocation().getLocations(SearchForLocation.this.mContext, str, true);
            }
        };
    }

    public void populateListView(final List<WeatherLocation> list) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext, R.style.AlertDialogTheme);
        String[] strArr = new String[list.size()];
        String str = list.get(0).name;
        for (int i = 0; i < list.size(); i++) {
            strArr[i] = list.get(i).state.replaceAll("\\(\\)", "");
        }
        builder.setTitle(str);
        builder.setItems(strArr, new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.weather.SearchForLocation.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
                SearchForLocation.this.mListener.getWeatherLocationFromSearch((WeatherLocation) list.get(i2));
            }
        });
        AlertDialog alertDialogCreate = builder.create();
        this.mDialog = alertDialogCreate;
        alertDialogCreate.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.sonymobile.calendar.weather.SearchForLocation.3
            @Override // android.content.DialogInterface.OnKeyListener
            public boolean onKey(DialogInterface dialogInterface, int i2, KeyEvent keyEvent) {
                if (i2 != 4) {
                    return false;
                }
                SearchForLocation.this.mWeatherLocations.clear();
                return false;
            }
        });
        this.mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.sonymobile.calendar.weather.SearchForLocation.4
            @Override // android.content.DialogInterface.OnCancelListener
            public void onCancel(DialogInterface dialogInterface) {
                SearchForLocation.this.mWeatherLocations.clear();
            }
        });
        this.mDialog.show();
    }

    public void dismissDialog() {
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.mDialog = null;
        }
    }

    public void dismissAndInterrupt() {
        dismissDialog();
        ProgressDialog progressDialog = this.mProgressDialog;
        if (progressDialog != null) {
            progressDialog.cancel();
        }
    }

    public ArrayList<WeatherLocation> getWeatherLocations() {
        return this.mWeatherLocations;
    }
}
