package es.moldovan.givrsapp;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunctionException;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.regions.Regions;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.moldovan.givrsapp.objs.Item;
import es.moldovan.givrsapp.objs.ListQuery;
import es.moldovan.givrsapp.objs.Project;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import me.gujun.android.taggroup.TagGroup;

public class CreateProjectActivity extends AppCompatActivity {

    private static final String TAG = "CreteProject"
            ;
    @Bind(R.id.mapview)
    MapView mapView;
    @Bind(R.id.tag_group)
    TagGroup tagGroup;
    @Bind(R.id.scrollView)
    ScrollView scrollView;
    @Bind(R.id.createProjectBanner)
    TextView bannerTextView;
    @Bind(R.id.createProjectTitle)
    EditText createProjectTitle;
    @Bind(R.id.createProjectDescription)
    EditText createProjectDescription;
    @Bind(R.id.createProjectImage)
    EditText createProjectImage;
    @Bind(R.id.createProjectInstructions)
    EditText createProjectInstructions;

    private Marker marker;
    private ProgressDialog progressDialog;

    private CognitoCachingCredentialsProvider credentialsProvider;
    private CognitoSyncManager syncClient;
    private LambdaInvokerFactory invokerFactory;
    private LambdaInterface lambdaInterface;
    private Dataset dataset;

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

        initCloudProviders();
    }

    private void initCloudProviders() {
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "eu-west-1:69e18fb2-8c29-496c-9c5d-7e6cddbf9b17", // Identity Pool ID
                Regions.EU_WEST_1 // Region
        );

        syncClient = new CognitoSyncManager(getApplicationContext(), Regions.EU_WEST_1, credentialsProvider);

        invokerFactory = new LambdaInvokerFactory(getApplicationContext(), Regions.EU_WEST_1, credentialsProvider);

        dataset = syncClient.openOrCreateDataset("users");

        lambdaInterface = invokerFactory.build(LambdaInterface.class, new LambdaJsonBinder());
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
                if (marker != null) marker.remove();
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(point)
                        .title("Here")
                        .snippet("Help is needed");
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
    public void onPause() {
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
    public void click() {
        Project project = new Project();
        project.setName(createProjectTitle.getText().toString());
        project.setDescription(createProjectDescription.getText().toString());
        project.setImage(createProjectImage.getText().toString());
        project.setInstructions(createProjectInstructions.getText().toString());
        project.setInitiator(dataset.get("name"));

        project.setLocation(new Double[]{marker.getPosition().getLongitude(), marker.getPosition().getLatitude()});

        List<Item> items = new ArrayList<Item>();
        for (String s : tagGroup.getTags()) {
            items.add(new Item(false, s));
        }
        project.setItems(items);

        createProject(project);


    }

    private void createProject(Project project) {
        new AsyncTask<Project, Void, Project>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(CreateProjectActivity.this);
                progressDialog.setTitle("Just a sec");
                progressDialog.setMessage("Work in progress");
                progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
                progressDialog.show();
            }

            @Override
            protected Project doInBackground(Project... params) {
                try {
                    return lambdaInterface.create(params[0]);
                } catch (LambdaFunctionException lfe) {
                    Log.e(TAG, "Failed to invoke echo", lfe);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Project result) {
                progressDialog.dismiss();
                if (result == null) {
                    return;
                }
                Log.d(TAG, "Got " + result.toString() + " projects");
                finish();
                //Static.project = result[0];
                //startActivity(new Intent(MainActivity.this, ProjectActivity.class));
                //Toast.makeText(MainActivity.this, result.toString(), Toast.LENGTH_LONG).show();
            }
        }.execute(project);
    }


}
