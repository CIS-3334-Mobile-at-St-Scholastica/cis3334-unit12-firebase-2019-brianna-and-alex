package edu.css.cis3334_unit12_firebase_2019;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * @author Brianna Gerold
 * @author Alex Afanasiev
 * This is the login screen where the user can login with
 * google, or set up an account with thier email.
 */
public class MainActivity extends AppCompatActivity {

    private TextView textViewStatus;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private Button buttonGoogleLogin;
    private Button buttonCreateLogin;
    private Button buttonSignOut;
    private Button buttonStartChat;
    DatabaseReference myDbRef;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1000;

    @Override
    /**
     * This method is called when the activity is first created
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myDbRef = database.getReference("cis3334-unit12-firebase-2019-b");

        //set up buttons and text fields
        textViewStatus = (TextView) findViewById(R.id.textViewStatus);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonGoogleLogin = (Button) findViewById(R.id.buttonGoogleLogin);
        buttonCreateLogin = (Button) findViewById(R.id.buttonCreateLogin);
        buttonSignOut = (Button) findViewById(R.id.buttonSignOut);
        buttonStartChat = findViewById(R.id.buttonStartChat);

        //When the login button is clicked get the user name and password
        //and pass it to the signIn method
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                signIn(editTextEmail.getText().toString(), editTextPassword.getText().toString());
            }
        });

        //When the create login button is clicked get the email and password
        //and pass it to the createAccount method
        buttonCreateLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                createAccount(email, password);
            }
        });

        //When the Google Login button is clicked call the googleSignIn method
        buttonGoogleLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                googleSignIn();
            }
        });

        //When the sign out button is clicked, call the signOut method
        buttonSignOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                signOut();
            }
        });

        //When the start chat button is clicked
        //start the ChatActivity
        buttonStartChat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Take in the email and password of a new user and create a
     * new user
     * @param email
     * @param password
     */
    private void createAccount(String email, String password) {
        //firebase authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            //if there is a user set the status to signed in
                            if(user != null){
                                textViewStatus.setText("Signed In");
                            }
                        }
                    }
                });
    }

    /**
     * Sign in with an account
     * @param email
     * @param password
     */
    private void signIn(String email, String password){

        //use firebase authentication to sign in
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            textViewStatus.setText("Signed In");
                        }
                    }
                });
    }

    /**
     * Sign out from either google or firebase authentication
     */
    private void signOut () {
        //sign out from firebase authentication
        mAuth.signOut();
        //sign out from google
        mGoogleSignInClient.signOut();
        //change status
        textViewStatus.setText("Signed Out");
    }

    /**
     * sign in using google
     */
    private void googleSignIn() {
        //call google's sign in intent with result
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * When the sign in intent is finished, get the user
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    /**
     * Takes in the result of the intent and sees if there is someone signed in
     * @param completedTask
     */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            //if the result returned an account change the text so it shows the user is signed in
            if(account != null) {
                textViewStatus.setText("Signed In");
            }
        } catch (ApiException e) {
            //log the exception if there is an error
            Log.d("Error", e.toString());
        }
    }

    /**
     * When the app is started check to see if there is a user logged in
     */
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in with firebase authentication
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        //update text view
        if (currentUser != null || account != null) {
                textViewStatus.setText("Signed In");

        } else {
            // User is signed out
            textViewStatus.setText("Signed Out");
        }
    }
}
