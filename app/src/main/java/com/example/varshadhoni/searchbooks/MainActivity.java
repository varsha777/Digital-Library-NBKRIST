package com.example.varshadhoni.searchbooks;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.varshadhoni.searchbooks.MySQL.SenderReceiver;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class MainActivity extends AppCompatActivity {

    private SignInButton mgooglebtn;
    private final int RC_SIGN_IN=1;
    GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    final String TAG="MAIN_ACTIVITY";
    private FirebaseAuth.AuthStateListener mAuthListener;
    String guser_name;


    String urlAddress;
    int branchcode;
    SearchView sv;
    ListView lv;

    //dialog box values
        Spinner branch,selectNetwork;
    int spinner_position=0,spinner_position_net;

    String sub_IP,mobile_IP,college_ip="117.254.87.118",main_ip;


    ImageView noDataImg,noNetworkImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mobile_IP="117.254.87.118";
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);


        mAuth = FirebaseAuth.getInstance();
        mgooglebtn = (SignInButton) findViewById(R.id.signinbutton);

        

        //urlAddress= "http://"+mobile_IP+"/myFiles/cse/Searching.php";

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                   // lv.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this,"Thanks for SIGNIN enjoy app",Toast.LENGTH_LONG).show();
                    guser_name=user.getDisplayName();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed ou
                   // lv.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this,"PLEASE SIGNIN AND USE APP PRESS BOTTOM RIGHT TO SIGNIN ",
                            Toast.LENGTH_LONG).show();
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        Toast.makeText(MainActivity.this, "API ERROR", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(AppIndex.API).build();



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                final View mview = getLayoutInflater().inflate(R.layout.select_filter, null);
                mgooglebtn = (SignInButton) mview.findViewById(R.id.signinbutton);
                mgooglebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        signIn();
                    }
                });

                selectNetwork=(Spinner) mview.findViewById(R.id.snetwork);
                String net_names[]={"NBKRIST","Other Network"};
                ArrayAdapter<String> net_adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, net_names);
                selectNetwork.setAdapter(net_adapter);


                selectNetwork.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        spinner_position_net=position;
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


                branch = (Spinner) mview.findViewById(R.id.branch_spin);
                final String[] branchs = {
                        "Computer Science Engineering",
                        "Civil Engineering",
                        "Electronics and Communication engi",
                        "Electrical & Electronics Engineering",
                        "Mechanical Engineering"};

                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, branchs);
                branch.setAdapter(adapter);
                branch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        spinner_position = position;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (spinner_position)
                        {
                            case 0:
                                //Toast.makeText(MainActivity.this,"wifi connected",Toast.LENGTH_SHORT).show();
                                WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
                                WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                                int ip = wifiInfo.getIpAddress();
                                String ipAddress = Formatter.formatIpAddress(ip);
                                sub_IP=ipAddress.substring(0,10);
                                main_ip=sub_IP+"221";

                                mobile_IP=main_ip;
                                Log.e("HAI NBR", mobile_IP);
                                Toast.makeText(MainActivity.this, "You are in NBRIST Network", Toast.LENGTH_SHORT).show();
                                break;

                            case 1: mobile_IP=college_ip;
                                Toast.makeText(MainActivity.this, "You are Non-NBRIST", Toast.LENGTH_LONG).show();
                                break;
                        }


                        switch (spinner_position) {
                            case 0:
                                urlAddress = "http://"+mobile_IP+"/myFiles/cse/Searching.php";;
                                branchcode = 0;
                                break;
                            case 1:
                                urlAddress = "http://"+mobile_IP+"/myFiles/civil/Searching.php";;
                                branchcode = 1;
                                break;
                            case 2:
                                urlAddress = "http://"+mobile_IP+"/myFiles/ece/Searching.php";;
                                branchcode = 2;
                                break;
                            case 3:
                                urlAddress = "http://"+mobile_IP+"/myFiles/eee/Searching.php";;
                                branchcode = 3;
                                break;
                            case 4:
                                urlAddress = "http://"+mobile_IP+"/myFiles/mec/Searching.php";;
                                branchcode = 4;
                                break;
                        }


                    }
                });
                mBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                mBuilder.setView(mview);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

            }
        });




        lv = (ListView) findViewById(R.id.lv);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String a = (String) ((TextView) view).getText();
                Bundle obj = new Bundle();
                obj.putString("name", a);
                obj.putString("branch_code", String.valueOf(branchcode));
                obj.putString("gmail_name", guser_name);
                obj.putString("mobileIPAddress",mobile_IP);

                Intent i = new Intent(MainActivity.this, Video_Viewing.class);
                i.putExtras(obj);
                startActivity(i);

            }
        });

        sv = (SearchView) findViewById(R.id.sv);
        noDataImg = (ImageView) findViewById(R.id.nodataImg);
        noNetworkImg = (ImageView) findViewById(R.id.noserver);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SenderReceiver sr = new SenderReceiver(MainActivity.this, urlAddress, query, lv, noDataImg, noNetworkImg);
                sr.execute();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                SenderReceiver sr = new SenderReceiver(MainActivity.this, urlAddress, query, lv, noDataImg, noNetworkImg);
                sr.execute();
                return false;
            }
        });
    }




    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }





    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }



    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
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
                        // ...
                    }
                });
    }


}