package com.example.melophilia.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
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

import com.example.melophilia.Admin.adminHome;
import com.example.melophilia.MainActivity;
import com.example.melophilia.R;
import com.example.melophilia.User.userHome;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class loginActicity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;

    ProgressDialog progressDialog;
    EditText et_loginEmail, et_loginPassword;
    Button bt_signIn;
    TextView tv_signUp, tv_forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_acticity);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Authenticating");
        mAuth = FirebaseAuth.getInstance();
        et_loginEmail = findViewById(R.id.et_loginEmail);
        et_loginPassword = findViewById(R.id.et_loginPassword);
        bt_signIn = findViewById(R.id.bt_login);
        tv_forgotPassword = findViewById(R.id.tv_forgotPassword);
        tv_signUp = findViewById(R.id.tv_Register);
        bt_signIn.setOnClickListener(this);
        tv_forgotPassword.setOnClickListener(this);
        tv_signUp.setOnClickListener(this);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                startActivity(new Intent(loginActicity.this,userHome.class));
            }
        }

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
                                startActivity(new Intent(loginActicity.this, adminHome.class));
                            } else if (mAuth.getCurrentUser().isEmailVerified()) {
                                progressDialog.dismiss();
                                startActivity(new Intent(loginActicity.this, userHome.class));
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(loginActicity.this, "Please Verify your email", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(loginActicity.this, "Email or Password is incorrect.",
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
            startActivity(new Intent(loginActicity.this, forgotPasswordActivity.class));
        } else if (view == tv_signUp) {
            startActivity(new Intent(loginActicity.this, registerActivity.class));
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
            progressDialog.show();
            signin(useremailid, userpassword);
        }
    }

}
