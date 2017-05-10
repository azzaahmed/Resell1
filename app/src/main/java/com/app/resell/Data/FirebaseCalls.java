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
 * Created by azza ahmed on 4/30/2017.
 */
public  class  FireBaseCalls {
    //defining firebaseauth object
    private FirebaseAuth firebaseAuth;
    //defining a database reference
    private DatabaseReference databaseReference;
    private FirebaseUser user;
   private ProgressDialog progressDialog;

    public FireBaseCalls() {
    }

    //sign up
    public  void  fireBaseRegistration(EditText editTextEmail,EditText editTextPassword,EditText age,EditText Name,EditText mobile,Spinner gender, final String country,final String profile_pic_path, final Context context, final boolean noProfilePictureFlag, final Activity Activity){

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(Activity);
        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        final String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();
        final String mAge=age.getText().toString().trim();
        final String mName= Name.getText().toString().trim();
        final String mMobile=mobile.getText().toString().trim();
        final String mGender=gender.getSelectedItem().toString();
        final String pic_path=profile_pic_path;
        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(Activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if (task.isSuccessful()) {

                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Account myAccount;
                            if (!noProfilePictureFlag)
                                myAccount = new Account(mName, mAge, mMobile, mGender, email, pic_path, country);
                            else
                                myAccount = new Account(mName, mAge, mMobile, mGender, email, user.getPhotoUrl() + "", country);

                            DatabaseReference x = databaseReference.child("users").child(user.getUid());
                            x.setValue(myAccount);
                            String key = x.getKey();
                            HashMap<String, Object> result = new HashMap<>();
                            result.put("id", key);
                            x.updateChildren(result);

                            Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show();
                            Bundle myBundle = new Bundle();
                            myBundle.putSerializable("accountinfo", (Serializable) myAccount);
                            Intent myIntent = new Intent(Activity, Home.class);
                            myIntent.putExtras(myBundle);
                            Activity.finish();
                            Activity.startActivity(myIntent);


                        } else {
                            //display some message here
                            Toast.makeText(context, "Account already exists", Toast.LENGTH_LONG).show();

                        }
                        progressDialog.dismiss();
                    }
                });

    }


    //add  extra info when sign in with google
    public void AddFireBaseExtraInfo(String mName,String mAge,String mMobile,String mGender, String email ,String photoUrl, Activity Activity,String country){

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        user = firebaseAuth.getCurrentUser();
        Account myAccount = new Account(mName, mAge, mMobile, mGender, email,photoUrl,country);

        DatabaseReference x=  databaseReference.child("users").child(user.getUid());
        x.setValue(myAccount);
        // pushing key as id field in the table after pushing object
        String key= x.getKey();
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", key);
        x.updateChildren(result);
        // startActivity(new Intent(this, Profile.class));
        Activity.finish();

    }

    public void AddItem(String description,String price, String size,String profile_pic_path, final Context context, final Activity Activity){
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        user = firebaseAuth.getCurrentUser();
        progressDialog = new ProgressDialog(Activity);
        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();
        final String[] country = new String[1];
        if(user!=null){
            final Item item = new Item(description,size,price,profile_pic_path);
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
                        result.put("userId",user.getUid());
                    x.updateChildren(result);

                    progressDialog.dismiss();
                    Toast.makeText(Activity, "Item saved", Toast.LENGTH_SHORT).show();
                        Activity.finish();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
        }


    }



}
