package com.sonymobile.calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.legacy.app.FragmentCompat;
import com.sonyericsson.calendar.util.WeatherService;
import com.sonymobile.accuweather.WeatherLocation;
import com.sonymobile.calendar.utils.PermissionUtils;
import com.sonymobile.calendar.utils.UiUtils;
import com.sonymobile.calendar.weather.SearchForLocation;
import com.sonymobile.calendar.weather.SearchForLocationListener;
import com.sonymobile.calendar.weather.WeatherForecast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class WeatherLocationFragment extends Fragment implements SearchView.OnQueryTextListener, SearchForLocationListener, AdapterView.OnItemClickListener, FragmentCompat.OnRequestPermissionsResultCallback {
    private static final int MAX_RECENT_LOCATIONS = 10;
    private static final String SEARCH_CLOSE_ICON_HEIGHT = "search_close_icon_height";
    private static final String SEARCH_CLOSE_ICON_WIDTH = "search_close_icon_width";
    private static final String SEARCH_STRING = "searchString";
    private static final String SEARCH_VIEW_ICONIFIED = "SearchViewIconified";
    private static final String SEARHED_CITIES = "searched_cities";
    public static final String TAG = "EventInfoFragment";
    private boolean isAnyLBSEnabled;
    private int mDefaultCancelButtonHeight;
    private int mDefaultCancelButtonWidth;
    private WeatherLocation mLocation;
    private Object mParent;
    private SearchForLocation mSearchForLocation;
    private SearchView mSearchView;
    private ArrayList<WeatherLocation> mWeatherLocations;
    private boolean isSearchViewIconified = true;
    private String mSearchString = null;
    private ArrayList<WeatherLocation> mSearchedWeatherLocations = new ArrayList<>();

    @Override // com.sonymobile.calendar.weather.SearchForLocationListener
    public void getWeatherLocationsFromSearch(List<WeatherLocation> list) {
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.isAnyLBSEnabled = Utils.isAnyLocationProviderEnabled(getActivity());
        ListView listView = (ListView) layoutInflater.inflate(R.layout.weather_location_listview, viewGroup, false);
        populateListView(listView);
        if (bundle != null) {
            if (bundle.containsKey(SEARCH_STRING)) {
                this.mSearchString = bundle.getString(SEARCH_STRING);
                this.isSearchViewIconified = bundle.getBoolean(SEARCH_VIEW_ICONIFIED, true);
            }
            this.mSearchedWeatherLocations = bundle.getParcelableArrayList(SEARHED_CITIES);
            this.mDefaultCancelButtonHeight = bundle.getInt(SEARCH_CLOSE_ICON_HEIGHT, 0);
            this.mDefaultCancelButtonWidth = bundle.getInt(SEARCH_CLOSE_ICON_WIDTH, 0);
        }
        return listView;
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        setHasOptionsMenu(true);
        super.onCreate(bundle);
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.search_menu, menu);
        MenuItem menuItemFindItem = menu.findItem(R.id.action_search);
        if (menuItemFindItem != null) {
            menuItemFindItem.setVisible(false);
            this.mSearchView = (SearchView) MenuItemCompat.getActionView(menuItemFindItem);
            setupSearchView();
        }
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    private void populateListView(ListView listView) {
        this.mWeatherLocations = loadFromStorage();
        listView.setAdapter((ListAdapter) new WeatherLocationListAdapter(this.mWeatherLocations, getActivity()));
        listView.setOnItemClickListener(this);
    }

    private void setupCustomActionbar() {
        ActionBar supportActionBar = Utils.getSupportActionBar(getActivity());
        supportActionBar.setDisplayOptions(16, 16);
        if (!getResources().getBoolean(R.bool.tablet_mode)) {
            supportActionBar.setTitle(getString(R.string.preference_title_add_location));
        } else {
            supportActionBar.setTitle(R.string.preferences_display_weather);
        }
        supportActionBar.setDisplayShowTitleEnabled(true);
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        if (this.mSearchView != null) {
            supportActionBar.setCustomView(this.mSearchView, new ActionBar.LayoutParams(-2, -2, 8388629));
        }
    }

    private void setupSearchView() {
        if (Utils.isDataTrafficEnabled(getActivity())) {
            if (Utils.isTabletDevice(getActivity()) || UiUtils.isPortrait(getActivity())) {
                ((TextView) this.mSearchView.findViewById(R.id.search_src_text)).setTextColor(-1);
                ((ImageView) this.mSearchView.findViewById(R.id.search_button)).setImageResource(R.drawable.ic_menu_search_holo_dark);
            }
            this.mSearchView.setOnQueryTextListener(this);
            this.mSearchView.setQueryHint(getString(R.string.clr_strings_weather_search_city_hint));
            this.mSearchView.setInputType(8193);
            ViewGroup.LayoutParams layoutParams = ((ImageView) this.mSearchView.findViewById(R.id.search_close_btn)).getLayoutParams();
            if (layoutParams.height != 0 || layoutParams.width != 0) {
                this.mDefaultCancelButtonHeight = layoutParams.height;
                this.mDefaultCancelButtonWidth = layoutParams.width;
            }
            String str = this.mSearchString;
            if (str != null || !this.isSearchViewIconified) {
                this.mSearchView.setQuery(str, false);
                this.mSearchView.setIconified(false);
            }
        }
        cutomizeSearcView();
        setupCustomActionbar();
    }

    private ArrayList<WeatherLocation> loadFromStorage() {
        SharedPreferences sharedPreferences = GeneralPreferences.getSharedPreferences(getActivity());
        this.mWeatherLocations = new ArrayList<>();
        Set<String> stringSet = sharedPreferences.getStringSet(WeatherPreferences.KEY_WEATHER_SET, null);
        if (stringSet != null) {
            Iterator<String> it = stringSet.iterator();
            while (it.hasNext()) {
                try {
                    this.mWeatherLocations.add(WeatherLocation.fromJSON(new JSONObject(it.next())));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        Collections.sort(this.mWeatherLocations, new Comparator<WeatherLocation>() { // from class: com.sonymobile.calendar.WeatherLocationFragment.1
            @Override // java.util.Comparator
            public int compare(WeatherLocation weatherLocation, WeatherLocation weatherLocation2) {
                return weatherLocation.position - weatherLocation2.position;
            }
        });
        if (this.isAnyLBSEnabled) {
            this.mWeatherLocations.add(0, WeatherLocation.currentLocation(Utils.getCurrentLocationString(getActivity())));
        }
        return this.mWeatherLocations;
    }

    private void saveLocation(WeatherLocation weatherLocation) {
        SharedPreferences.Editor editorEdit = GeneralPreferences.getSharedPreferences(getActivity()).edit();
        if (this.isAnyLBSEnabled) {
            this.mWeatherLocations.remove(0);
        }
        if (this.mWeatherLocations.contains(weatherLocation)) {
            this.mWeatherLocations.remove(weatherLocation);
        }
        this.mWeatherLocations.add(0, weatherLocation);
        if (this.mWeatherLocations.size() > 10) {
            this.mWeatherLocations.remove(10);
        }
        HashSet hashSet = new HashSet();
        int i = 1;
        for (WeatherLocation weatherLocation2 : this.mWeatherLocations) {
            weatherLocation2.position = i;
            hashSet.add(weatherLocation2.getJSONObject().toString());
            i++;
        }
        editorEdit.putStringSet(WeatherPreferences.KEY_WEATHER_SET, hashSet);
        editorEdit.apply();
    }

    private Object isListening() {
        if (getTargetFragment() instanceof WeatherLocationSelectedListener) {
            return getTargetFragment();
        }
        if (getActivity() instanceof WeatherLocationSelectedListener) {
            return getActivity();
        }
        return null;
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Object objIsListening = isListening();
        this.mParent = objIsListening;
        if (objIsListening == null) {
            throw new ClassCastException("Class doesn't implement SearchForLocationListener");
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mSearchString = null;
        if (this.mSearchView != null) {
            ((InputMethodManager) getActivity().getSystemService("input_method")).hideSoftInputFromWindow(this.mSearchView.getWindowToken(), 0);
            if (this.mSearchView.getQuery().length() > 0) {
                this.mSearchString = this.mSearchView.getQuery().toString();
            }
            this.isSearchViewIconified = this.mSearchView.isIconified();
        }
        SharedPreferences sharedPreferences = GeneralPreferences.getSharedPreferences(getActivity());
        boolean z = true;
        if (!this.isAnyLBSEnabled && Utils.isCurrentLocationSet(getActivity()) && this.mLocation == null) {
            z = false;
        }
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        editorEdit.putBoolean(GeneralPreferences.KEY_WEATHER, z);
        editorEdit.apply();
        Utils.getSupportActionBar(getActivity()).setDisplayShowCustomEnabled(false);
        SearchForLocation searchForLocation = this.mSearchForLocation;
        if (searchForLocation != null) {
            searchForLocation.dismissAndInterrupt();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (this.mSearchView != null) {
            setupSearchView();
        }
        ArrayList<WeatherLocation> arrayList = this.mSearchedWeatherLocations;
        if (arrayList == null || arrayList.size() <= 0) {
            return;
        }
        if (this.mSearchForLocation == null) {
            this.mSearchForLocation = new SearchForLocation((Context) getActivity(), (SearchForLocationListener) this, true, this.mSearchedWeatherLocations);
            hideKeyboard();
        }
        this.mSearchForLocation.populateListView(this.mSearchedWeatherLocations);
    }

    @Override // androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString(SEARCH_STRING, this.mSearchString);
        bundle.putBoolean(SEARCH_VIEW_ICONIFIED, this.isSearchViewIconified);
        bundle.putInt(SEARCH_CLOSE_ICON_HEIGHT, this.mDefaultCancelButtonHeight);
        bundle.putInt(SEARCH_CLOSE_ICON_WIDTH, this.mDefaultCancelButtonWidth);
        SearchForLocation searchForLocation = this.mSearchForLocation;
        if (searchForLocation != null) {
            bundle.putParcelableArrayList(SEARHED_CITIES, searchForLocation.getWeatherLocations());
        }
    }

    @Override // androidx.appcompat.widget.SearchView.OnQueryTextListener
    public boolean onQueryTextSubmit(String str) {
        hideKeyboard();
        this.mSearchForLocation = new SearchForLocation((Context) getActivity(), (SearchForLocationListener) this, str, true);
        return false;
    }

    @Override // androidx.appcompat.widget.SearchView.OnQueryTextListener
    public boolean onQueryTextChange(String str) {
        cutomizeSearcView();
        return false;
    }

    private void cutomizeSearcView() {
        ImageView imageView = (ImageView) this.mSearchView.findViewById(R.id.search_close_btn);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(this.mDefaultCancelButtonWidth, this.mDefaultCancelButtonHeight));
        if (this.mSearchView.getQuery().length() > 0) {
            imageView.setLayoutParams(new LinearLayout.LayoutParams(this.mDefaultCancelButtonWidth, this.mDefaultCancelButtonHeight));
        } else {
            imageView.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        }
    }

    private void hideKeyboard() {
        View currentFocus = getActivity().getCurrentFocus();
        if (currentFocus != null) {
            ((InputMethodManager) getActivity().getSystemService("input_method")).hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
        getActivity().getWindow().setSoftInputMode(3);
        SearchView searchView = this.mSearchView;
        if (searchView != null) {
            searchView.clearFocus();
        }
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        if (i == 0 && getLocationMode() == 1) {
            new LocationSettingsDialog().show(getFragmentManager(), LocationSettingsDialog.TAG);
        }
        if (i == 0 && this.isAnyLBSEnabled) {
            if (Utils.isLocationEnabled(getActivity())) {
                saveCurrentLocationToPreference();
                return;
            } else {
                requestPermissions(new String[]{"android.permission.ACCESS_FINE_LOCATION"}, 0);
                return;
            }
        }
        WeatherLocation weatherLocation = this.mWeatherLocations.get(i);
        this.mLocation = weatherLocation;
        saveLocation(weatherLocation);
        writeLocationToPreference(this.mLocation.getJSONObject().toString());
    }

    private void writeLocationToPreference(String str) {
        SharedPreferences.Editor editorEdit = GeneralPreferences.getSharedPreferences(getActivity()).edit();
        editorEdit.putString(WeatherPreferences.KEY_CURRENT_LOCATION, str);
        editorEdit.putString(WeatherForecast.KEY_STORED_WEATHER_INFO, null);
        editorEdit.apply();
        WeatherService.getInstance().markWeatherInfoAsDirty();
        getFragmentManager().popBackStack();
    }

    private void saveCurrentLocationToPreference() {
        WeatherLocation weatherLocationCurrentLocation = WeatherLocation.currentLocation(Utils.getCurrentLocationString(getActivity()));
        this.mLocation = weatherLocationCurrentLocation;
        writeLocationToPreference(weatherLocationCurrentLocation.getJSONObject().toString());
    }

    @Override // androidx.fragment.app.Fragment
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (PermissionUtils.isAccessLocationGranted(getActivity())) {
            saveCurrentLocationToPreference();
        }
    }

    @Override // com.sonymobile.calendar.weather.SearchForLocationListener
    public void getWeatherLocationFromSearch(WeatherLocation weatherLocation) {
        saveLocation(weatherLocation);
        this.mLocation = weatherLocation;
        getFragmentManager().popBackStack();
        ((WeatherLocationSelectedListener) this.mParent).onCitySelected(weatherLocation);
    }

    public static class LocationSettingsDialog extends DialogFragment {
        public static final String TAG = "com.sonymobile.calendar.WeatherLocationFragment$LocationSettingsDialog";

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
            builder.setTitle(R.string.dialog_location_settings_disabled_title).setMessage(R.string.dialog_location_settings_disabled_message).setNegativeButton(android.R.string.cancel, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.location_settings_confirm_dialog_btn, new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.WeatherLocationFragment.LocationSettingsDialog.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    LocationSettingsDialog.this.startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
                }
            });
            AlertDialog alertDialogCreate = builder.create();
            alertDialogCreate.setCancelable(false);
            alertDialogCreate.setCanceledOnTouchOutside(false);
            return alertDialogCreate;
        }
    }

    private int getLocationMode() {
        try {
            return Settings.Secure.getInt(getActivity().getContentResolver(), "location_mode");
        } catch (Settings.SettingNotFoundException e) {
            Log.w(TAG, e);
            return 0;
        }
    }
}
