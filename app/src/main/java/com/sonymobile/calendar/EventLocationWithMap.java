package com.sonymobile.calendar;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sonyericsson.calendar.util.CustomizeConfig;
import com.sonyericsson.calendar.util.RecurrenceRuleParser;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes2.dex */
public class EventLocationWithMap extends LinearLayout {
    private static final int MAP_ZOOM_LEVEL = 15;
    private static final int MAX_NUMBER_OF_ADDRESSES = 5;
    private static final String mMockLocation = "google.navigation:q=Godovik";
    private Context mContext;
    private FragmentManager mFragmentManager;
    private AsyncTask<String, Void, LatLng> mGeoLoactionTask;
    private String mLocationAddress;
    private TextView mLocationTextView;
    private RelativeLayout mMapContainer;
    private SupportMapFragment mMapFragment;
    private Button mNavigationButton;
    private Pattern mWildcardPattern;

    public EventLocationWithMap(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mWildcardPattern = Pattern.compile("^.*$");
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.event_location_with_map, (ViewGroup) this, true);
        this.mLocationTextView = (TextView) findViewById(R.id.where);
        this.mMapContainer = (RelativeLayout) findViewById(R.id.map_layout);
        this.mNavigationButton = (Button) findViewById(R.id.navigation);
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext().getApplicationContext()) == 9) {
            this.mMapContainer.setVisibility(8);
            this.mNavigationButton.setVisibility(8);
        }
        GoogleMapOptions googleMapOptions = new GoogleMapOptions();
        googleMapOptions.compassEnabled(false).rotateGesturesEnabled(false).tiltGesturesEnabled(false).zoomGesturesEnabled(false).zoomControlsEnabled(false).scrollGesturesEnabled(false);
        this.mMapFragment = SupportMapFragment.newInstance(googleMapOptions);
        this.mContext = getContext();
        checkForNavigationApp();
    }

    private void checkForNavigationApp() {
        if (Utils.isIntentRecipientAvailable(this.mContext, new Intent("android.intent.action.VIEW", Uri.parse(mMockLocation)))) {
            return;
        }
        this.mNavigationButton.setVisibility(8);
    }

    public void setLocation(String str, FragmentManager fragmentManager) {
        this.mLocationAddress = str;
        this.mFragmentManager = fragmentManager;
        if (TextUtils.isEmpty(str)) {
            setVisibility(8);
            return;
        }
        setVisibility(0);
        this.mLocationTextView.setAutoLinkMask(0);
        this.mLocationTextView.setText(str);
        this.mLocationTextView.setOnTouchListener(new View.OnTouchListener() { // from class: com.sonymobile.calendar.EventLocationWithMap.1
            private static final long CLICK_DELAY = 500;
            private long lastClickTime;

            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == 1) {
                    long jCurrentTimeMillis = System.currentTimeMillis();
                    if (this.lastClickTime + CLICK_DELAY > jCurrentTimeMillis) {
                        return true;
                    }
                    this.lastClickTime = jCurrentTimeMillis;
                }
                return false;
            }
        });
        if (!Linkify.addLinks(this.mLocationTextView, 15)) {
            Linkify.addLinks(this.mLocationTextView, this.mWildcardPattern, "geo:0,0?q=");
        }
        if (!Utils.isDataTrafficEnabled(getContext()) || CustomizeConfig.getInstance().getShowDataUsage(getContext())) {
            return;
        }
        AsyncTask<String, Void, LatLng> asyncTask = this.mGeoLoactionTask;
        if (asyncTask != null) {
            asyncTask.cancel(true);
        }
        this.mGeoLoactionTask = new GetLocationAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, str);
    }

    public void stopGeoLocationTask() {
        AsyncTask<String, Void, LatLng> asyncTask = this.mGeoLoactionTask;
        if (asyncTask != null) {
            asyncTask.cancel(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startMapActivity() {
        startMapActivity(new Intent("android.intent.action.VIEW", Uri.parse("geo:0,0?q=" + this.mLocationAddress)));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startMapActivity(Intent intent) {
        if (Utils.isIntentRecipientAvailable(this.mContext, intent)) {
            this.mContext.startActivity(intent);
        } else {
            Toast.makeText(this.mContext, R.string.no_navigation_app_failed_toast_txt, 0).show();
        }
    }

    private class GetLocationAsyncTask extends AsyncTask<String, Void, LatLng> {
        private GetLocationAsyncTask() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public LatLng doInBackground(String... strArr) {
            return getLocation(strArr[0]);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(final LatLng latLng) {
            if (latLng == null) {
                EventLocationWithMap.this.mMapContainer.setVisibility(8);
                return;
            }
            EventLocationWithMap.this.mMapContainer.setVisibility(0);
            FragmentTransaction fragmentTransactionBeginTransaction = EventLocationWithMap.this.mFragmentManager.beginTransaction();
            fragmentTransactionBeginTransaction.replace(R.id.map, EventLocationWithMap.this.mMapFragment);
            fragmentTransactionBeginTransaction.commit();
            EventLocationWithMap.this.mFragmentManager.executePendingTransactions();
            EventLocationWithMap.this.mMapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override // com.google.android.gms.maps.OnMapReadyCallback
                    public void onMapReady(GoogleMap map) {
                        if (map != null) {
                            map.addMarker(new MarkerOptions().position(latLng));
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
                            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() { // from class: com.sonymobile.calendar.EventLocationWithMap.GetLocationAsyncTask.1
                                @Override // com.google.android.gms.maps.GoogleMap.OnMapClickListener
                                public void onMapClick(LatLng latLng2) {
                                    EventLocationWithMap.this.startMapActivity();
                                }
                            });
                            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() { // from class: com.sonymobile.calendar.EventLocationWithMap.GetLocationAsyncTask.2
                                @Override // com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
                                public boolean onMarkerClick(Marker marker) {
                                    EventLocationWithMap.this.startMapActivity();
                                    return true;
                                }
                            });
                        }
                    }
                });
            EventLocationWithMap.this.mNavigationButton.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.EventLocationWithMap.GetLocationAsyncTask.3
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    EventLocationWithMap.this.startMapActivity(new Intent("android.intent.action.VIEW", Uri.parse("google.navigation:q=" + String.valueOf(latLng.latitude) + RecurrenceRuleParser.VALUE_SEPARATOR + String.valueOf(latLng.longitude))));
                }
            });
        }

        private LatLng getLocation(String str) {
            try {
                LatLng coordinates = LocationCoordinatesCache.getInstance().getCoordinates(str);
                if (coordinates != null) {
                    return coordinates;
                }
                List<Address> fromLocationName = new Geocoder(EventLocationWithMap.this.mContext, Locale.getDefault()).getFromLocationName(str, 5);
                if (fromLocationName != null && fromLocationName.size() != 0) {
                    Address address = fromLocationName.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    LocationCoordinatesCache.getInstance().addCoordinates(str, latLng);
                    return latLng;
                }
                return null;
            } catch (IOException e) {
                cancel(true);
                e.printStackTrace();
                cancel(true);
                return null;
            }
        }
    }
}
