package com.app.resell.Data;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.app.resell.Account;
import com.app.resell.Home;
import com.app.resell.Item;
import com.app.resell.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.HashMap;


/**
 * Created by azza ahmed on 5/10/2017.
 */
public class FireBaseCalls {


    //defining firebase auth object
    private FirebaseAuth firebaseAuth;
    //defining a database reference
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private ProgressDialog progressDialog;

    public FireBaseCalls() {
    }

    //sign up
    public void fireBaseRegistration(EditText editTextEmail, EditText editTextPassword, EditText age, EditText name, EditText mobile, Spinner gender, final String country, final String profilePicPath, final Context context, final boolean noProfilePictureFlag, final Activity activity) {

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(activity.getResources().getString(R.string.Registration_wait));
        progressDialog.show();

        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        final String mAge = age.getText().toString().trim();
        final String mName = name.getText().toString().trim();
        final String mMobile = mobile.getText().toString().trim();
        final String mGender = gender.getSelectedItem().toString();
        final String picPath = profilePicPath;
        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if (task.isSuccessful()) {

                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Account myAccount;
                            if (!noProfilePictureFlag)
                                myAccount = new Account(mName, mAge, mMobile, mGender, email, picPath, country);
                            else
                                myAccount = new Account(mName, mAge, mMobile, mGender, email, user.getPhotoUrl() + "", country);

                            DatabaseReference x = databaseReference.child("users").child(user.getUid());
                            x.setValue(myAccount);
                            String key = x.getKey();
                            HashMap<String, Object> result = new HashMap<>();
                            result.put("id", key);
                            x.updateChildren(result);

                            Toast.makeText(context,activity.getResources().getString(R.string.Registration_success) , Toast.LENGTH_SHORT).show();
                            Bundle myBundle = new Bundle();
                            myBundle.putSerializable(activity.getResources().getString(R.string.acount_info), (Serializable) myAccount);
                            Intent myIntent = new Intent(activity, Home.class);
                            myIntent.putExtras(myBundle);
                            activity.finish();
                            activity.startActivity(myIntent);


                        } else {
                            //display some message here
                            Toast.makeText(context,activity.getResources().getString(R.string.acount_exists), Toast.LENGTH_LONG).show();

                        }
                        progressDialog.dismiss();
                    }
                });

    }


    //add  extra info when sign in with google
    public void addFireBaseExtraInfo(String mName, String mAge, String mMobile, String mGender, String email, String photoUrl, Activity activity, String country) {

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        user = firebaseAuth.getCurrentUser();
        Account myAccount = new Account(mName, mAge, mMobile, mGender, email, photoUrl, country);

        DatabaseReference x = databaseReference.child("users").child(user.getUid());
        x.setValue(myAccount);
        // pushing key as id field in the table after pushing object
        String key = x.getKey();
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", key);
        x.updateChildren(result);
        // startActivity(new Intent(this, Profile.class));
        activity.finish();

    }

    public void addItem(String description, String price, String size, String profilePicPath, final Context context, final Activity activity) {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        user = firebaseAuth.getCurrentUser();
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(activity.getResources().getString(R.string.Registration_item_wait));
        progressDialog.show();
        final String[] country = new String[1];
        if (user != null) {
            final Item item = new Item(description, size, price, profilePicPath);
            databaseReference.child("users").child(user.getUid()).child("country").addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    country[0] = (String) snapshot.getValue();
                    // DatabaseReference x = databaseReference.child("items").child(user.getUid()).push();
                    DatabaseReference x = databaseReference.child("items").push();
                    x.setValue(item);
                    String key = x.getKey();
                    HashMap<String, Object> result = new HashMap<>();
                    result.put("item_id", key);
                    result.put("country", country[0]);
                    result.put("userId", user.getUid());
                    x.updateChildren(result);

                    progressDialog.dismiss();
                    Toast.makeText(activity, activity.getResources().getString(R.string.item_saved), Toast.LENGTH_SHORT).show();
                    activity.finish();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
        }


    }


}
