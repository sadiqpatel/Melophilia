package com.example.melophilia.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.melophilia.Home.homeActivity;
import com.example.melophilia.R;
import com.example.melophilia.utils.noInternet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class loginActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;

    ProgressDialog progressDialog;
    EditText et_loginEmail, et_loginPassword;
    Button bt_signIn;
    TextView tv_signUp, tv_forgotPassword;
    private static final int MY_PERMISSIONS_REQUEST_READ = 1;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(loginActivity.this,authenticationActivity.class));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Authenticating");
        progressDialog.setCancelable(false);

        mAuth = FirebaseAuth.getInstance();
        et_loginEmail = findViewById(R.id.et_loginEmail);
        et_loginPassword = findViewById(R.id.et_loginPassword);
        bt_signIn = findViewById(R.id.bt_login);
        tv_forgotPassword = findViewById(R.id.tv_forgotPassword);
        tv_signUp = findViewById(R.id.tv_Register);
        bt_signIn.setOnClickListener(this);
        tv_forgotPassword.setOnClickListener(this);
        tv_signUp.setOnClickListener(this);

        choosePermission();

    }

    public void signin(final String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("login1234", "email" + email);
                            if (email.equals("admin12345@gmail.com")) {
                                progressDialog.dismiss();
                                startActivity(new Intent(loginActivity.this, homeActivity.class));
                            } else if (mAuth.getCurrentUser().isEmailVerified()) {
                                progressDialog.dismiss();
                                startActivity(new Intent(loginActivity.this, homeActivity.class));
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(loginActivity.this, "Please Verify your email", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(loginActivity.this, "Email or Password is incorrect.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.item_logout:
                FirebaseAuth.getInstance().signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == bt_signIn) {
            validate();
        } else if (view == tv_forgotPassword) {
            startActivity(new Intent(loginActivity.this, forgotPasswordActivity.class));
        } else if (view == tv_signUp) {
            startActivity(new Intent(loginActivity.this, registerActivity.class));
        }
    }
    public void choosePermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(loginActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Toast.makeText(getApplicationContext(), "Go to settings and give storage permission to the app", Toast.LENGTH_LONG).show();

            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(loginActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ);

                // MY_PERMISSIONS_REQUEST_READ is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }


    public void validate() {
        String useremailid = et_loginEmail.getText().toString().trim();
        String userpassword = et_loginPassword.getText().toString().trim();
        if (useremailid.isEmpty()) {
            et_loginEmail.setError(getResources().getString(R.string.empty_email));
        } else if (userpassword.isEmpty()) {
            et_loginPassword.setError(getResources().getString(R.string.empty_password));
        } else if (!useremailid.matches(getResources().getString(R.string.emailPattern))) {
            Toast.makeText(this, "Enter proper EmailAddress", Toast.LENGTH_SHORT).show();
        } else {
            if (!(noInternet.isInternetAvailable(getApplicationContext()))) //returns true if internet available
            {
                Toast.makeText(getApplicationContext(), "Check your internet Connection", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.show();
                signin(useremailid, userpassword);
            }

        }
    }


}
