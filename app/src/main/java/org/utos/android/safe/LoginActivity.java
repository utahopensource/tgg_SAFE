package org.utos.android.safe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, View.OnClickListener {

    // TODO: 1/25/17 need SHA1 certificate fingerprints file signing cert
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    // [START declare_auth_listener]
    private FirebaseAuth.AuthStateListener mAuthListener;
    // [END declare_auth_listener]

    private GoogleApiClient mGoogleApiClient;

    public ProgressDialog mProgressDialog;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorYellow));

        // set title works when language change
        setTitle(getString(R.string.app_name));

        // [START config_signin]
        // Configure Google Sign In
        // OAuth2 from app google account and put it in requestIdToken("Web client ID")
        // https://firebase.google.com/docs/auth/android/google-signin#authenticate_with_firebase
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestIdToken("189411335655-6335jinncvroojjfmh9dbngeat3ttrq7.apps.googleusercontent.com").
                requestEmail().build();
        // [END config_signin]

        mGoogleApiClient = new GoogleApiClient.Builder(this).
                addConnectionCallbacks(this).
                enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */).
                addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.bypass).setOnClickListener(this);


        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        // [START auth_state_listener]
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    // save info to SharedPreferences
                    SharedPreferences.Editor prefsEditor = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).edit();
                    prefsEditor.putString(LOGIN_NAME, user.getDisplayName());
                    prefsEditor.putString(LOGIN_PHOTO, user.getPhotoUrl().toString());
                    prefsEditor.putString(LOGIN_EMAIL, user.getEmail());
                    prefsEditor.putString(LOGIN_UNIQUE_ID, user.getUid());
                    prefsEditor.apply();
                    // Goto next activity if signed in is successful
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // [START_EXCLUDE]
                updateUI(user);
                // [END_EXCLUDE]
            }
        };
        // [END auth_state_listener]
    }

    // [START on_start_add_listener]
    @Override public void onStart() {
        super.onStart();

        // auto sign in
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

        //
        mAuth.addAuthStateListener(mAuthListener);

    }
    // [END on_start_add_listener]

    // [START on_stop_remove_listener]
    @Override public void onStop() {
        super.onStop();

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

        hideProgressDialog();
    }
    // [END on_stop_remove_listener]

    // [START onactivityresult]
    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult");
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                // Authenticate with a backend server https://developers.google.com/identity/sign-in/android/backend-auth
                GoogleSignInAccount acct = result.getSignInAccount();
                String idToken = acct.getIdToken();
                Log.d(TAG, "ID Token: " + idToken);
            } else {
                // Google Sign In failed, update UI appropriately
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    Log.w(TAG, "signInWithCredential", task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
                // [START_EXCLUDE]
                hideProgressDialog();
                // [END_EXCLUDE]
            }
        });
    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override public void onResult(@NonNull Status status) {
                updateUI(null);
            }
        });
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override public void onResult(@NonNull Status status) {
                updateUI(null);
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        //        if (user != null) {
        //            mStatusTextView.setText(getString(R.string.google_status_fmt, user.getEmail()));
        //            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));
        //
        //            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
        //            //            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        //        } else {
        //            mStatusTextView.setText(R.string.signed_out);
        //            mDetailTextView.setText(null);
        //
        //            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        //            //            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        //        }
    }

    @Override public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_in_button) {
            signIn();
        } else if (i == R.id.bypass) {
            Intent ibypas = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(ibypas);
        }
        // else if (i == R.id.disconnect_button) {
        //            revokeAccess();
        //        }
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    // check google play services is available and updated
    private boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            Log.d("PlayServicesAvailable", "false");
            return false;
        }
        Log.d("PlayServicesAvailable", "true");
        return true;
    }

    ///////////////////////////////////////
    // GoogleApiClient.OnConnectionFailedListener
    @Override public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "GoogleApiClient connection failed: " + connectionResult.toString(), Toast.LENGTH_SHORT).show();

        if (!connectionResult.hasResolution()) {
            // Show a localized error dialog.
            isGooglePlayServicesAvailable(this);
        } else {
            if (mGoogleApiClient != null) {
                mGoogleApiClient.connect();
            }
        }

    }
    // GoogleApiClient.OnConnectionFailedListener
    ///////////////////////////////////////

    ///////////////////////////////////////
    // GoogleApiClient.ConnectionCallbacks
    @Override public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "GoogleApiClient connected");

        // OAuth 2.0
        Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient).setResultCallback(new ResultCallback<GoogleSignInResult>() {
            @Override public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                if (googleSignInResult.isSuccess()) {
                    try {
                        //                        Log.d(TAG, "onConnected " + googleSignInResult.getSignInAccount().getIdToken());
                        // save OAUTH SharedPreferences
                        SharedPreferences.Editor prefsEditor = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).edit();
                        prefsEditor.putString(LOGIN_OAUTH2, googleSignInResult.getSignInAccount().getIdToken());
                        prefsEditor.apply();
                        // disconnect when done
                        mGoogleApiClient.disconnect();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d(TAG, "isSuccess FALSE");
                }

            }
        });
    }

    @Override public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
        // Attempt to reconnect
        mGoogleApiClient.connect();
    }
    // GoogleApiClient.ConnectionCallbacks
    ///////////////////////////////////////

}
