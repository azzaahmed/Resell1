package com.app.resell;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.app.resell.Data.FetchItems;
import com.google.firebase.auth.FirebaseAuth;

public class Home extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(Home.this, AddItem.class));
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        FetchItems.syncImmediately(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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
        } else if (id == R.id.action_signOut) {
            firebaseAuth.signOut();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class).putExtra("SignOut_flag", "true");

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.action_profile) {
            startActivity(new Intent(getApplicationContext(), Profile.class));
        } else if (id == R.id.action_myitems) {
            startActivity(new Intent(getApplicationContext(), MyItems.class));
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        finish();
    }


}





