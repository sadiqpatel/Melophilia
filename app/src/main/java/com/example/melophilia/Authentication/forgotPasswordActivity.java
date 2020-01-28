package com.example.melophilia.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.melophilia.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class forgotPasswordActivity extends AppCompatActivity {
    Button bt_sendMail;
    EditText et_email;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    Toolbar mActionBarToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending Mail");
        bt_sendMail = findViewById(R.id.bt_sendMail);
        et_email = findViewById(R.id.et_forgotEmail);


        mActionBarToolbar = (Toolbar) findViewById(R.id.confirm_order_toolbar_layout);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.forgot_passwrord));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        bt_sendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });
    }

    private void forgotPassword(String emailAddress) {
        mAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(forgotPasswordActivity.this, "Password Reset Mail Sent Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(forgotPasswordActivity.this, loginActivity.class));
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(forgotPasswordActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void validate() {
        String useremailid = et_email.getText().toString().trim();

        if (useremailid.isEmpty()) {
            et_email.setError(getResources().getString(R.string.empty_email));
        } else if (!useremailid.matches(getResources().getString(R.string.emailPattern))) {
            Toast.makeText(this, "Enter proper EmailAddress", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.show();
            forgotPassword(useremailid);
        }
    }


}
