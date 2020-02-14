package com.example.melophilia.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.melophilia.Home.homeActivity;
import com.example.melophilia.R;
import com.google.firebase.auth.FirebaseAuth;

public class authenticationActivity extends AppCompatActivity implements View.OnClickListener {
    Button bt_signin, bt_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        bt_signin = findViewById(R.id.bt_signin);
        bt_signup = findViewById(R.id.bt_signup);
        bt_signin.setOnClickListener(this);
        bt_signup.setOnClickListener(this);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                startActivity(new Intent(authenticationActivity.this, homeActivity.class));
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view == bt_signin) {
            startActivity(new Intent(authenticationActivity.this, loginActivity.class));

        } else {
            startActivity(new Intent(authenticationActivity.this, registerActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
