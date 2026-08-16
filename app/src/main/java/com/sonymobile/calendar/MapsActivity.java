package com.sonymobile.calendar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sonymobile.calendar.permissions.PermissionHandlerActivity;
import com.sonymobile.calendar.permissions.PermissionItem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class MapsActivity extends PermissionHandlerActivity implements OnAddressPickedListener, GoogleMap.OnMyLocationButtonClickListener, OnMapReadyCallback {
    public static final String ADDRESS_FIELD = "addressField";
    private static final float DEFAULT_ZOOM = 15.0f;
    private static final int MAX_NUMBER_OF_HITS = 10;
    private EditText mAddressField;
    private ProgressBar mAddressProgressIndicator;
    private AlertDialog mDialog;
    private GoogleMap mMap;
    private ProgressBar mMapProgressIndicator;
    private Toolbar mToolbar;

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.maps_activity);
        initMap();
    }

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity
    public PermissionItem[] getRequiredPermission() {
        return new PermissionItem[]{new PermissionItem("android.permission.ACCESS_FINE_LOCATION", null, null, R.drawable.ic_location, false)};
    }

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity
    public void onRequestPermissionResult(String[] strArr, int[] iArr) {
        GoogleMap googleMap = this.mMap;
        if (googleMap != null) {
            goToInitialLocation(googleMap);
            this.mMap.setMyLocationEnabled(Utils.isLocationEnabled(this));
        }
    }

    private void initToolbar(GoogleMap googleMap) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.map_toolbar);
        this.mToolbar = toolbar;
        toolbar.setBackgroundResource(R.color.toolbar_background_color);
        initToolbarMenu(googleMap);
        initToolbarNavigationButton();
    }

    private void initToolbarMenu(GoogleMap googleMap) {
        View viewInflate = getLayoutInflater().inflate(R.layout.maps_actionbar, (ViewGroup) null);
        View viewFindViewById = findViewById(R.id.map_toolbar);
        this.mToolbar.addView(viewInflate);
        viewFindViewById.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.MapsActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MapsActivity.this.saveLocation();
                MapsActivity.this.finish();
            }
        });
        initAddressField(viewInflate);
        goToInitialLocation(googleMap);
    }

    private void initToolbarNavigationButton() {
        this.mToolbar.setNavigationIcon(R.drawable.ic_close_w);
        this.mToolbar.setNavigationOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.MapsActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MapsActivity.this.finish();
            }
        });
    }

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        if (hasInternetAccess()) {
            return;
        }
        showNoInternetDialog();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onBackPressed() {
        saveLocation();
        super.onBackPressed();
    }

    private void initAddressField(View view) {
        String stringExtra = getIntent().getStringExtra(ADDRESS_FIELD);
        this.mAddressProgressIndicator = (ProgressBar) findViewById(R.id.maps_address_progressIndicator);
        EditText editText = (EditText) view.findViewById(R.id.maps_address_field);
        this.mAddressField = editText;
        editText.setText(stringExtra);
        this.mAddressField.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: com.sonymobile.calendar.MapsActivity.3
            @Override // android.widget.TextView.OnEditorActionListener
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 6 || MapsActivity.this.mAddressField.getText().length() <= 1) {
                    return false;
                }
                MapsActivity mapsActivity = MapsActivity.this;
                mapsActivity.searchLocationFromAddressAsync(mapsActivity.mAddressField.getText().toString());
                return false;
            }
        });
    }

    private void initMap() {
        this.mMapProgressIndicator = (ProgressBar) findViewById(R.id.map_progressIndicator);
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }

    @Override // com.google.android.gms.maps.OnMapReadyCallback
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        googleMap.setMyLocationEnabled(true);
        this.mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() { // from class: com.sonymobile.calendar.MapsActivity.4
            @Override // com.google.android.gms.maps.GoogleMap.OnMapClickListener
            public void onMapClick(LatLng latLng) {
                MapsActivity mapsActivity = MapsActivity.this;
                mapsActivity.updateMarker(latLng, mapsActivity.mMap);
                MapsActivity.this.searchAddressFromLocationAsync(latLng);
            }
        });
        this.mMap.setOnMyLocationButtonClickListener(this);
        this.mMap.setMyLocationEnabled(Utils.isLocationEnabled(this));
        initToolbar(this.mMap);
    }

    private void goToInitialLocation(GoogleMap googleMap) {
        if (this.mAddressField.getText().length() > 1) {
            searchLocationFromAddressAsync(this.mAddressField.getText().toString());
        } else if (Utils.isLocationEnabled(this)) {
            searchLocationFromGps(googleMap);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateMarker(LatLng latLng, GoogleMap googleMap) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        googleMap.clear();
        googleMap.addMarker(markerOptions);
    }

    private boolean hasInternetAccess() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService("connectivity");
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveLocation() {
        Intent intent = new Intent();
        intent.putExtra(ADDRESS_FIELD, this.mAddressField.getText().toString());
        setResult(-1, intent);
    }

    private void showNoInternetDialog() {
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog == null || !alertDialog.isShowing()) {
            AlertDialog alertDialogCreate = new AlertDialog.Builder(this, R.style.AlertDialogTheme).create();
            this.mDialog = alertDialogCreate;
            alertDialogCreate.setTitle(R.string.no_internet_connection_dialog_title);
            this.mDialog.setMessage(getResources().getString(R.string.no_internet_connection_dialog_text));
            this.mDialog.setButton(-1, getResources().getString(R.string.clr_strings_button_title_ok_txt), new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.MapsActivity.5
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    MapsActivity.this.finish();
                }
            });
            this.mDialog.show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void searchAddressFromLocationAsync(LatLng latLng) {
        new GetAddressAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, latLng);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void searchLocationFromAddressAsync(String str) {
        new GetLocationAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, str);
    }

    private void searchLocationFromGps(GoogleMap googleMap) {
        Location currentLocation = Utils.getCurrentLocation(this);
        if (currentLocation == null) {
            Toast.makeText(this, R.string.toast_no_location_service_found, 0).show();
        } else {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM));
        }
    }

    @Override // com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
    public boolean onMyLocationButtonClick() {
        int i;
        try {
            i = Settings.Secure.getInt(getContentResolver(), "location_mode");
        } catch (Settings.SettingNotFoundException unused) {
            i = 3;
        }
        if (i != 0) {
            return false;
        }
        Toast.makeText(this, R.string.toast_no_location_service_found, 0).show();
        return true;
    }

    private class GetAddressAsyncTask extends AsyncTask<LatLng, Void, String> {
        private GetAddressAsyncTask() {
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            MapsActivity.this.mAddressField.setText("");
            MapsActivity.this.mAddressField.setHint("");
            MapsActivity.this.mAddressField.setEnabled(false);
            MapsActivity.this.mAddressProgressIndicator.setVisibility(0);
            super.onPreExecute();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public String doInBackground(LatLng... latLngArr) {
            return getAddress(latLngArr[0]);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(String str) {
            if (str.equals("")) {
                return;
            }
            MapsActivity.this.mAddressProgressIndicator.setVisibility(8);
            MapsActivity.this.mAddressField.setHint(R.string.hint_where);
            MapsActivity.this.mAddressField.setText(str);
            MapsActivity.this.mAddressField.setEnabled(true);
            super.onPostExecute(str);
        }

        @Override // android.os.AsyncTask
        protected void onCancelled() {
            MapsActivity.this.mAddressProgressIndicator.setVisibility(8);
            MapsActivity.this.mAddressProgressIndicator.setVisibility(8);
            MapsActivity.this.mAddressField.setHint(R.string.hint_where);
            MapsActivity.this.mAddressField.setEnabled(true);
            Toast.makeText(MapsActivity.this, R.string.toast_no_address_found, 0).show();
            super.onCancelled();
        }

        private String getAddress(LatLng latLng) {
            try {
                List<Address> fromLocation = new Geocoder(MapsActivity.this, Locale.getDefault()).getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (fromLocation != null && fromLocation.size() != 0) {
                    ArrayList<String> arrayList = new ArrayList<>();
                    for (int i = 0; i < 3; i++) {
                        processText(fromLocation.get(0).getAddressLine(i), arrayList);
                    }
                    StringBuilder sb = new StringBuilder("");
                    if (arrayList.size() > 0) {
                        sb = new StringBuilder(arrayList.get(0));
                        for (int i2 = 1; i2 < arrayList.size(); i2++) {
                            sb.append(", ").append(arrayList.get(i2));
                        }
                    }
                    return sb.toString();
                }
                cancel(true);
                return "";
            } catch (IOException e) {
                cancel(true);
                e.printStackTrace();
                cancel(true);
                return "";
            }
        }

        private void processText(String str, List<String> list) {
            if (str != null) {
                list.add(str);
            }
        }
    }

    private class GetLocationAsyncTask extends AsyncTask<String, Void, List<Address>> {
        private String address;

        private GetLocationAsyncTask() {
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            MapsActivity.this.mMapProgressIndicator.setVisibility(0);
            super.onPreExecute();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public List<Address> doInBackground(String... strArr) {
            this.address = strArr[0];
            return getLocation(strArr[0]);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(List<Address> list) {
            if (list == null || list.size() == 0) {
                showNothingFoundMessage();
            } else if (list.size() == 1) {
                MapsActivity.this.goToAddress(list.get(0));
            } else if (list.size() > 1) {
                showAddressPicker(list);
            }
            MapsActivity.this.mMapProgressIndicator.setVisibility(8);
            super.onPostExecute(list);
        }

        @Override // android.os.AsyncTask
        protected void onCancelled() {
            MapsActivity.this.mMapProgressIndicator.setVisibility(8);
            showNothingFoundMessage();
            super.onCancelled();
        }

        private List<Address> getLocation(String str) {
            try {
                return new Geocoder(MapsActivity.this, Locale.getDefault()).getFromLocationName(str, 10);
            } catch (IOException e) {
                cancel(true);
                e.printStackTrace();
                cancel(true);
                return null;
            }
        }

        private void showNothingFoundMessage() {
            MapsActivity mapsActivity = MapsActivity.this;
            Toast.makeText(mapsActivity, String.format(mapsActivity.getResources().getString(R.string.toast_no_location_found), this.address), 0).show();
        }

        private void showAddressPicker(List<Address> list) {
            if (MapsActivity.this.isFinishing() || MapsActivity.this.isDestroyed()) {
                return;
            }
            AddressSuggestionPicker.newInstance(list).show(MapsActivity.this.getSupportFragmentManager(), AddressSuggestionPicker.TAG);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void goToAddress(Address address) {
        if (this.mMap != null) {
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            updateMarker(latLng, this.mMap);
            this.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
        }
    }

    @Override // com.sonymobile.calendar.OnAddressPickedListener
    public void onAddressPicked(Address address, CharSequence charSequence) {
        this.mAddressField.setText(charSequence);
        goToAddress(address);
    }
}
