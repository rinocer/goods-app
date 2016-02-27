package es.moldovan.givrsapp;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunctionException;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.regions.Regions;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.moldovan.givrsapp.objs.ListQuery;
import es.moldovan.givrsapp.objs.Project;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class SearchActivity extends AppCompatActivity {

    @Bind(R.id.my_recycler_view_s)
    RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);


    private static final String TAG = "SearchActivity";

    private LambdaInvokerFactory invokerFactory;
    private LambdaInterface lambdaInterface;

    @Bind(R.id.toolbar_s)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_search);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_s);
        fab.setImageResource(R.drawable.ic_action_plus);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                startActivity(new Intent(SearchActivity.this, CreateProjectActivity.class));
            }
        });

     //   initCloudProviders();
    }

   /* private void initCloudProviders(){
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "eu-west-1:69e18fb2-8c29-496c-9c5d-7e6cddbf9b17", // Identity Pool ID
                Regions.EU_WEST_1 // Region
        );

        syncClient = new CognitoSyncManager(getApplicationContext(), Regions.EU_WEST_1, credentialsProvider);

        invokerFactory = new LambdaInvokerFactory(getApplicationContext(), Regions.EU_WEST_1, credentialsProvider);

        // Create the Lambda proxy object with default Json data binder.
        // You can provide your own data binder by implementing
        // LambdaDataBinder
        lambdaInterface = invokerFactory.build(LambdaInterface.class, new LambdaJsonBinder());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Dataset dataset = syncClient.openOrCreateDataset("users");
        //Check if logged in
        if(dataset.get("email") == null) {
            startActivity(new Intent(this, LoginActivity.class));
        }
        else getLocation();

    }*/

    private void getLocation(){
        Log.d(TAG, "Waiting location");
        SmartLocation.with(this).location()
            .oneFix()
            .start(new OnLocationUpdatedListener() {
                @Override
                public void onLocationUpdated(Location location) {
                    Log.e(TAG, location.toString());
                    ListQuery listQuery = new ListQuery(location.getLatitude(), location.getLongitude(), 100000000d);
                    getProjects(listQuery);
                }
            });
    }

    private void getProjects(ListQuery listQuery){
        new AsyncTask<ListQuery, Void, Project[]>() {

            @Override
            protected Project[] doInBackground(ListQuery... params) {
                try {
                    return lambdaInterface.list(params[0]);
                } catch (LambdaFunctionException lfe) {
                    Log.e(TAG, "Failed to invoke echo", lfe);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Project[] result) {
                if (result == null) {
                    return;
                }
                Log.d(TAG, "Got " + result.length + " projects");
                injectProjects(Arrays.asList(result));
                //Static.project = result[0];
                //startActivity(new Intent(MainActivity.this, ProjectActivity.class));
                //Toast.makeText(MainActivity.this, result.toString(), Toast.LENGTH_LONG).show();
            }
        }.execute(listQuery);
    }

    private void injectProjects(final List<Project> projects) {
        ProjectAdapter projectAdapter = new ProjectAdapter(projects);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(projectAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(this,
                new RecyclerViewItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Project projectItem = projects.get(position);
                        Log.d(TAG, "onItemClick: " + projectItem.getName());

                        Intent intent = new Intent(SearchActivity.this, ProjectActivity.class);
                        startActivity(intent);
                    }
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
