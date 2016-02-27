package es.moldovan.givrsapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amazonaws.AmazonWebServiceClient;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.DefaultSyncCallback;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunctionException;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.regions.Regions;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.moldovan.givrsapp.objs.ListQuery;
import es.moldovan.givrsapp.objs.Project;
import es.moldovan.givrsapp.objs.SearchQuery;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesWithFallbackProvider;
import io.nlopez.smartlocation.location.providers.LocationManagerProvider;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Bind(R.id.my_recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.mainProgressBar)
    ProgressBar progressBar;

    private SearchView searchView;

    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager layoutManager = new LinearLayoutManager(this);

    private CognitoCachingCredentialsProvider credentialsProvider;
    private CognitoSyncManager syncClient;
    private LambdaInvokerFactory invokerFactory;
    private LambdaInterface lambdaInterface;

    private List<Project> firstProjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_action_plus);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                startActivity(new Intent(MainActivity.this, CreateProjectActivity.class));
            }
        });

        initCloudProviders();
    }

    private void initCloudProviders(){
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
    }

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
                firstProjects = Arrays.asList(result);
                injectProjects(Arrays.asList(result));
            }
        }.execute(listQuery);
    }

    private void injectProjects(final List<Project> projects) {
        mRecyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        ProjectAdapter projectAdapter = new ProjectAdapter(projects, SmartLocation.with(this).location().getLastLocation());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(projectAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(this,
                new RecyclerViewItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Project projectItem = projects.get(position);
                        Log.d(TAG, "onItemClick: " + projectItem.getName());

                        Intent intent = new Intent(MainActivity.this, ProjectActivity.class);
                        startActivity(intent);
                    }
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e(TAG, query);
                searchProject(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e(TAG, newText);
                if(TextUtils.isEmpty(newText)){
                    injectProjects(firstProjects);
                }
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_info) {
            showInfoDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showInfoDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Info")
            .setMessage("App developed in Madrid edition of HackForGood 2016. Marian Moldovan, Ovidiu Moldovan and Loredana Stan. Code at github.com/rinocer")
            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            })
            .setIcon(R.mipmap.ic_launcher)
            .show();
    }

    private void searchProject(String query){
        mRecyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        SearchQuery searchQuery = new SearchQuery(query);
        new AsyncTask<SearchQuery, Void, Project[]>() {

            @Override
            protected Project[] doInBackground(SearchQuery... params) {
                try {
                    return lambdaInterface.search(params[0]);
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
            }
        }.execute(searchQuery);
    }

}
