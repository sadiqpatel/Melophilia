package com.example.melophilia.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.melophilia.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class registerActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;

    ProgressDialog progressDialog;
    EditText et_registerEmail, et_registerPassword;
    Button bt_signUp;
    TextView tv_signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering User...");
        mAuth = FirebaseAuth.getInstance();
        bt_signUp = findViewById(R.id.bt_register);
        et_registerEmail = findViewById(R.id.et_registerEmail);
        et_registerPassword = findViewById(R.id.et_registerPassword);
        tv_signIn = findViewById(R.id.tv_signin);
        tv_signIn.setOnClickListener(this);

        bt_signUp.setOnClickListener(this);

    }


    public void createUser(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mAuth.getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                Toast.makeText(registerActivity.this, "Registered Successfully. Please Check you email for verification", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(registerActivity.this, loginActicity.class));
                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(registerActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("user", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Log.w("user", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(registerActivity.this, "User already exist",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if (view == bt_signUp) {
            validate();
        } else if (view == tv_signIn) {
            startActivity(new Intent(registerActivity.this, loginActicity.class));
        }
    }

    private void validate() {
        String useremailid = et_registerEmail.getText().toString().trim();
        String userpassword = et_registerPassword.getText().toString().trim();
        if (useremailid.isEmpty()) {
            et_registerEmail.setError(getResources().getString(R.string.empty_email));
        } else if (userpassword.isEmpty()) {
            et_registerPassword.setError(getResources().getString(R.string.empty_password));
        } else if (!useremailid.matches(getResources().getString(R.string.emailPattern))) {
            Toast.makeText(this, "Enter proper EmailAddress", Toast.LENGTH_SHORT).show();
        } else if (userpassword.length() <= 5) {
            Toast.makeText(this, "Password should have atleast 6 characters", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.show();
            createUser(useremailid, userpassword);
        }
    }
}
