package com.app.resell;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyItems1 extends AppCompatActivity {
    private static final String TAG = "myItems";
    private ArrayList<Item> itemsList = new ArrayList<>();
    private FirebaseUser currentUser;
    ProgressDialog progress;
    private RecyclerView recyclerView;
    private MyItemAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_items);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progress = new ProgressDialog(this);
        if (Utility.isOnline(this)) {
            progress.setMessage("loading.....");
            progress.show();
            progress.setCancelable(false);
        }
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new MyItemAdapter(itemsList, this);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);


        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }
                })
        );

        if (Utility.isOnline(this))
            getMyItems();
        else Toast.makeText(this,this.getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    


    public void getMyItems() {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        databaseReference.child("items").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                itemsList.clear();
                Log.e("Count ", "" + snapshot.getChildrenCount());
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Item item = postSnapshot.getValue(Item.class);
                    Log.e("Get Data", item.getDescription());
                    if (currentUser != null && item.getUserId() != null)
                        if (item.getUserId().equals(currentUser.getUid())) {
                            itemsList.add(item);
                            Log.d(TAG, "item id " + item.getItem_id());

                            mAdapter.notifyDataSetChanged();
                            progress.dismiss();
                        }
                }
                progress.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("The read failed: ", databaseError.getMessage());
            }


        });

    }

}