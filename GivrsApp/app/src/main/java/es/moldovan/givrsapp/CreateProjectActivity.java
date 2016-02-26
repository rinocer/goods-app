package es.moldovan.givrsapp;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.moldovan.givrsapp.objs.ListQuery;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import me.gujun.android.taggroup.TagGroup;

public class CreateProjectActivity extends AppCompatActivity {

    @Bind(R.id.mapview)
    MapView mapView;

    @Bind(R.id.tag_group)
    TagGroup tagGroup;

    @Bind(R.id.scrollView)
    ScrollView scrollView;

    @Bind(R.id.createProjectBanner)
    TextView bannerTextView;

    private Marker marker;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);
        ButterKnife.bind(this);

        setTitle("Create a new cause");

        setUpMap();
        mapView.onCreate(savedInstanceState);

        tagGroup.setTags(new String[]{"Hugs"});
        tagGroup.clearFocus();
        bannerTextView.requestFocus();


    }

    private void setUpMap() {
        mapView.setAccessToken("pk.eyJ1IjoibWFyaWFubW9sZG92YW4iLCJhIjoiY2llajJleDdxMDA0dHNtbHpieHZ1OGE5NiJ9.iQxkMy2s65B-YGJfCuYe8A");
        mapView.setStyleUrl(Style.EMERALD);
        mapView.setCenterCoordinate(new LatLng(38.91318, -77.03257));
        mapView.setZoomLevel(10);
        mapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        scrollView.requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return mapView.onTouchEvent(event);
            }
        });
        SmartLocation.with(this).location()
                .oneFix()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        mapView.setCenterCoordinate(new LatLng(location.getLatitude(), location.getLongitude()));
                    }
                });

        mapView.setOnMapClickListener(new MapView.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng point) {
                if(marker != null) marker.remove();
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(point)
                        .title("Hello World!")
                        .snippet("Welcome to my marker.");
                marker = mapView.addMarker(markerOptions);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause()  {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        progressDialog.dismiss();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @OnClick(R.id.createProjectSubmit)
    public void click(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Just a sec");
        progressDialog.setMessage("Work in progress");
        progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
        progressDialog.show();

    }
}
