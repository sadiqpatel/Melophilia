package com.example.melophilia.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.melophilia.Authentication.loginActivity;
import com.example.melophilia.R;
import com.example.melophilia.User.home.FragHome;
import com.google.firebase.auth.FirebaseAuth;

public class userHome extends AppCompatActivity {
Toolbar mActionBarToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);


    mActionBarToolbar = (Toolbar) findViewById(R.id.confirm_order_toolbar_layout);
    setSupportActionBar(mActionBarToolbar);
    getSupportActionBar().setTitle(getResources().getString(R.string.user));

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frag, new FragHome()).commit();
        }


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
                startActivity(new Intent(userHome.this, loginActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Melophilla")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

}
