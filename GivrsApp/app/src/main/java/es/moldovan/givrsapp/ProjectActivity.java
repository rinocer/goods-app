package es.moldovan.givrsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunctionException;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.regions.Regions;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.moldovan.givrsapp.objs.Item;
import es.moldovan.givrsapp.objs.Join;
import es.moldovan.givrsapp.objs.Project;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

/**
 * Created by OvidiuMircea on 26/02/2016.
 */
public class ProjectActivity extends AppCompatActivity {


    private static final String TAG = "ProjectActivity";
    private Project project;

    @Bind(R.id.projectImage)
    ImageView imageViewProject;
    @Bind(R.id.projectDescription)
    TextView projectDescription;
    @Bind(R.id.projectInitiator)
    TextView projectInitiator;
    @Bind(R.id.projectCheckBoxLayout)
    LinearLayout projectCheckboxLayout;
    @Bind(R.id.projectInstructions)
    TextView projectInstructions;
    @Bind(R.id.projectInstructionsLayout)
    LinearLayout projectInstructionsLayout;
    @Bind(R.id.scrollView)
    ScrollView scrollView;
    @Bind(R.id.mapviewx)
    MapView mapView;

    private CognitoCachingCredentialsProvider credentialsProvider;
    private CognitoSyncManager syncClient;
    private LambdaInvokerFactory invokerFactory;
    private LambdaInterface lambdaInterface;
    private Dataset dataset;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ButterKnife.bind(this);

        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        }, 250);

        mapView.setAccessToken("pk.eyJ1IjoibWFyaWFubW9sZG92YW4iLCJhIjoiY2lsNHZ2dmVpMDBlZnd4bTQxYzVza3dqayJ9.dl6i6GOklTYQQleJhc28zg");
        mapView.setStyleUrl(Style.EMERALD);
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

        mapView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mapView.setLatLng(new LatLng(project.getLocation()[1], project.getLocation()[0]));
                mapView.setZoom(10);
                mapView.addPolygon(getPolygonCircleForCoordinate(new LatLng(project.getLocation()[1], project.getLocation()[0]),1000).fillColor(Color.parseColor("#4400bcd4")).strokeColor(Color.parseColor("#0097A7")));
            }
        },250);

        mapView.onCreate(savedInstanceState);

        initCloudProviders();
        getData();
        bindData();
    }

    private void initCloudProviders(){
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

    private void getData() {
        project = Static.project;
    }

    private void bindData() {
        setTitle(project.getName());
        projectDescription.setText(project.getDescription());
        projectInitiator.setText(project.getInitiator());

        for (Item item : project.getItems()) {
            final CheckBox check = new CheckBox(this);
            check.setTextSize(18);
            float scale = getResources().getDisplayMetrics().density;
            int dpAsPixels = (int) (8 * scale + 0.5f);
            check.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);

            if(item.getGivr() != null){
                check.setChecked(true);
                check.setClickable(false);
                if(item.getGivr().equals(dataset.get("name")))
                    projectInstructionsLayout.setVisibility(ImageView.VISIBLE);
            }

            check.setText(item.getName());
            check.setTag(item.get_id());
            check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    check.setChecked(true);
                    check.setClickable(false);
                    join(check.getTag().toString());
                    projectInstructionsLayout.setVisibility(ImageView.VISIBLE);
                    Snackbar.make(imageViewProject, "Awesome, check the delivery instructions!", Snackbar.LENGTH_SHORT).show();
                }
            });



            projectCheckboxLayout.addView(check);
        }


        projectInstructions.setText(project.getInstructions());
        imageViewProject.setColorFilter(Color.parseColor("#660097A7"));
        String image = project.getImage() == null ? "https://blog.pusher.com/wp-content/uploads/2013/11/hack4good.png" : project.getImage();
        Picasso.with(imageViewProject.getContext()).load(image).into(imageViewProject);
    }


    private void join(String itemId) {
        Join join = new Join(project.get_id(), itemId, dataset.get("name"));
        new AsyncTask<Join, Void, Project>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(ProjectActivity.this);
                progressDialog.setTitle("Just a sec");
                progressDialog.setMessage("Work in progress");
                progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
                progressDialog.show();
            }

            @Override
            protected Project doInBackground(Join... params) {
                try {
                    return lambdaInterface.join(params[0]);
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
                //Static.project = result[0];
                //startActivity(new Intent(MainActivity.this, ProjectActivity.class));
                //Toast.makeText(MainActivity.this, result.toString(), Toast.LENGTH_LONG).show();
            }
        }.execute(join);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_project, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            case R.id.action_share:
                shareProject();
                return true;
            case R.id.action_fav:
                Snackbar.make(imageViewProject, "Infinite gratitude ;)", Snackbar.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void shareProject() {
        StringBuilder builder = new StringBuilder();
        for (Item item : project.getItems()) {
            builder.append(item.getName() + ", ");
        }
        String shareBody = "Just saw a project on Givr that needs your help. You can collaborate offering " + builder.toString();
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Givrs needed");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody.substring(0, shareBody.length() - 1));
        startActivity(Intent.createChooser(sharingIntent, "Share"));
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
    }


    private PolygonOptions getPolygonCircleForCoordinate(LatLng coordinate, double meterRadius) {
        int degreesBetweenPoints = 8;
        int numberOfPoints = (int) Math.floor(360 / degreesBetweenPoints);
        double distRadians = meterRadius / 6371000.0;
        double centerLatRadians = coordinate.getLatitude() * Math.PI / 180;
        double centerLonRadians = coordinate.getLongitude() * Math.PI / 180;

        LatLng[] coordinates = new LatLng[numberOfPoints];

        for (int i = 0; i < numberOfPoints; i++) {
            double degrees = i * degreesBetweenPoints;
            double degreeRadians = degrees * Math.PI / 180;
            double pointLatRadians = Math.asin(Math.sin(centerLatRadians)
                    * Math.cos(distRadians) + Math.cos(centerLatRadians) * Math.sin(distRadians) * Math.cos(degreeRadians));
            double pointLonRadians = centerLonRadians + Math.atan2(Math.sin(degreeRadians) * Math.sin(distRadians)
                    * Math.cos(centerLatRadians), Math.cos(distRadians) - Math.sin(centerLatRadians)
                    * Math.sin(pointLatRadians));
            double pointLat = pointLatRadians * 180 / Math.PI;
            double pointLon = pointLonRadians * 180 / Math.PI;
            LatLng point = new LatLng(pointLat, pointLon);
            coordinates[i] = point;
        }

        PolygonOptions polygonOptions = new PolygonOptions();
        polygonOptions.addAll(Arrays.asList(coordinates));
        polygonOptions.alpha(0.6f);

        return polygonOptions;
    }
}
