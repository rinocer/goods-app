package es.moldovan.givrsapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.DefaultSyncCallback;
import com.amazonaws.regions.Regions;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    private GoogleApiClient googleApiClient;

    @Bind(R.id.sign_in_button)
    SignInButton signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());

        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.


        // Build a GoogleApiClient with access to the Google Sign-In API and the
// options specified by gso.
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }


    private void xxx() {

        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "eu-west-1:69e18fb2-8c29-496c-9c5d-7e6cddbf9b17", // Identity Pool ID
                Regions.EU_WEST_1 // Region
        );


        ApiClientFactory factory = new ApiClientFactory().credentialsProvider(credentialsProvider);

        final ProjectMicroserviceClient client = factory.build(ProjectMicroserviceClient.class);
        client.givrPost();

        // Initialize the Cognito Sync client
        CognitoSyncManager syncClient = new CognitoSyncManager(
                getApplicationContext(),
                Regions.EU_WEST_1, // Region
                credentialsProvider);

// Create a record in a dataset and synchronize with the server
        Dataset dataset = syncClient.openOrCreateDataset("myDataset");
        dataset.put("myKey", "myValue");
        dataset.synchronize(new DefaultSyncCallback() {
            @Override
            public void onSuccess(Dataset dataset, List newRecords) {
                //Your handler code here
            }
        });




        /*GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        AccountManager am = AccountManager.get(this);
        Account[] accounts = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        String token = GoogleAuthUtil.getToken(getApplicationContext(), accounts[0].name,
                "audience:server:client_id:YOUR_GOOGLE_CLIENT_ID");
        Map<String, String> logins = new HashMap<String, String>();
        logins.put("accounts.google.com", token);
        credentialsProvider.setLogins(logins);*/
    }
}
