package com.app.resell;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

//sign in
public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener, LoaderManager.LoaderCallbacks {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private Bundle extras;

    //defining view objects
    private EditText editTextEmail;
    private EditText editTextPassword;
    private ProgressDialog progressDialog;


    //defining views
    private Button buttonSignIn;
    private TextView textViewSignUp;


    //defining firebaseauth object
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference databaseReference;
    GoogleSignInAccount accountt;

    boolean SignedInClick;
    int counterIn = 0;
    SharedPreferences prefs = null;
    Bitmap mBitmap;
    ImageView mWelcome;
    LinearLayout signInLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //first run
        prefs = getSharedPreferences("com.app.resell", MODE_PRIVATE);
        mWelcome = (ImageView) findViewById(R.id.welcome);
        signInLayout = (LinearLayout) findViewById(R.id.signinLayout);

        buttonSignIn = (Button) findViewById(R.id.buttonSignin);
        textViewSignUp = (TextView) findViewById(R.id.textViewSignUp);
        //attaching click listener
        buttonSignIn.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);


        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        //initializing views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        progressDialog = new ProgressDialog(this);

        Intent myIntent = getIntent(); // gets the previously created intent

        extras = getIntent().getExtras();
        Log.d(TAG, "in create");


        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        // [END build_client]

        // [START customize_button]
        // Customize sign-in button. The sign-in button can be displayed in
        // multiple sizes and color schemes. It can also be contextually
        // rendered based on the requested scopes. For example. a red button may
        // be displayed when Google+ scopes are requested, but a white button
        // may be displayed when only basic profile is requested. Try adding the
        // Scopes.PLUS_LOGIN scope to the GoogleSignInOptions to see the
        // difference.
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setScopes(gso.getScopeArray());
        // [END customize_button]

        SignedInClick = false;

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid() + "  " + user.getProviders() + "  " + user.getDisplayName());

//  counterin is needed as for some reason the listener is triggered two times (found out it is an issue with firebase listener itself)
                    if (!SignedInClick) {
                        if (counterIn != 1 && counterIn != 2) {
                            Log.d(TAG, "sent to Home from auth listener");
                            Log.d(TAG, "counter" + counterIn);
                            startActivity(new Intent(getApplicationContext(), Home.class));


                            finish();
                            counterIn++;
                        } else {
                            counterIn++;
                        }
                    }
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };


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

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "in start and " + mGoogleApiClient.isConnected());

        if (extras != null) {
            Log.d(TAG, "in extra and " + mGoogleApiClient.isConnected());

            String SignOutFlag = extras.getString("SignOut_flag");
            if (SignOutFlag.equals("true"))
                mGoogleApiClient.connect();
        }
        Log.v(TAG, "pref " + prefs.getBoolean("firstrun", true));
        if (Utility.isOnline(this)) {
            if (prefs.getBoolean("firstrun", true)) {
                Log.v(TAG, "first run");
                // Do first run stuff here then set 'firstrun' as false
                // using the following line to edit/commit prefs
                signInLayout.setVisibility(View.GONE);
                getLoaderManager().initLoader(0, null, this);
                prefs.edit().putBoolean("firstrun", false).commit();
            } else {
                Log.v(TAG, "not first run");

            }
        } else Toast.makeText(this, "no internet connection", Toast.LENGTH_SHORT).show();
        firebaseAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG, "in RC sugn in and" + mGoogleApiClient.isConnected());
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }


    }
    // [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());


        if (result.isSuccess()) {

            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            firebaseAuthWithGoogle(acct);


        } else {
            // Signed out, show unauthenticated UI.
            Log.d(TAG, "signed out un auth UI and" + mGoogleApiClient.isConnected());
            updateUI(false);
        }
    }
    // [END handleSignInResult]

    // [START signIn]
    private void signIn() {
        Log.d(TAG, "in sign in and" + mGoogleApiClient.isConnected());
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    // [START signOut]
    private void signOut() {

        Log.d(TAG, "in sign out and" + mGoogleApiClient.isConnected());
        //  firebaseAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });

        Log.d(TAG, "in sign out2 and" + mGoogleApiClient.isConnected());
    }
    // [END signOut]


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        //  mGoogleApiClient.connect();
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Please Wait...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
        } else {
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                SignedInClick = true;
                signIn();
                break;

            case R.id.textViewSignUp:
                startActivity(new Intent(this, SignUp.class));
                break;

            case R.id.buttonSignin:
                SignedInClick = true;
                userLogin();
                break;

        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnect ");

        if (extras != null) {
            String SignOut_flag = extras.getString("SignOut_flag");
            if (SignOut_flag.equals("true")) {
                Log.d(TAG, "onConnect and if flag true  ");
                signOut();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }


    // [START auth_with_google]
    // when sign button is clicked
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        accountt = acct;
        //  final FirebaseUser user = firebaseAuth.getCurrentUser();
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // in sign in succeed auth listener is fired automatically
                        if (firebaseAuth.getCurrentUser() != null)
                            checkAccountExists(firebaseAuth.getCurrentUser());


                        // [START_EXCLUDE]
                        hideProgressDialog();

                        // [END_EXCLUDE]


                    }
                });


    }
    // [END auth_with_google]


    //method for user login
    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        //checking if email and passwords are empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        //logging in the user
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        //if the task is successful
                        if (task.isSuccessful()) {
                            startActivity(new Intent(getApplicationContext(), Home.class));
                            finish();

                        } else {
                            Toast.makeText(MainActivity.this, "wrong username or password", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    public void checkAccountExists(FirebaseUser user) {

        final Account[] account = new Account[1];

        databaseReference.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                account[0] = dataSnapshot.getValue(Account.class);

                if (account[0] != null) {
                    Log.d("Main Activity", "account exist check from main activity");
                    startActivity(new Intent(getApplicationContext(), Home.class));
                    finish();
                } else {
                    finish();
                    //if it is the first time to sign in with google go to edit profile to add the extra needed info for the profile
                    startActivity(new Intent(getApplicationContext(), EditProfile.class));

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        //url of welcome image that is loaded when you first install the app
        final String URL = "https://drive.google.com/uc?id=0Bx2A07u8aRxSbG1Dc1NJNmpTRzA";

        return new AsyncTaskLoader<Bitmap>(this) {
            Bitmap mBitmap = null;

            @Override
            protected void onStartLoading() {
                if (mBitmap != null) {
                    deliverResult(mBitmap);
                } else {
                    showProgressDialog();
                    forceLoad();
                }
            }

            @Override
            public Bitmap loadInBackground() {
                Log.v(TAG, "in background");
                return download_Image(URL);
            }

            public void deliverResult(Bitmap data) {
                mBitmap = data;
                super.deliverResult(data);
            }

            private Bitmap download_Image(String url) {
                Bitmap bm = null;
                try {
                    URL aURL = new URL(url);
                    URLConnection conn = aURL.openConnection();
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(is);
                    bm = BitmapFactory.decodeStream(bis);
                    bis.close();
                    is.close();
                } catch (IOException e) {
                    Log.e("Hub", "Error getting the image from server : " + e.getMessage().toString());
                }
                Log.v(TAG, "create bitmap " + bm);

                return bm;
            }
        };

    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        Log.v(TAG, "load finished " + (Bitmap) data);
        hideProgressDialog();
        mWelcome.setImageBitmap((Bitmap) data);
        mWelcome.setVisibility(View.VISIBLE);
        Runnable mRunnable;
        Handler mHandler = new Handler();

        mRunnable = new Runnable() {

            @Override
            public void run() {
                mWelcome.setVisibility(View.GONE);
                signInLayout.setVisibility(View.VISIBLE);
            }
        };
        mHandler.postDelayed(mRunnable, 5 * 1000);
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

}
