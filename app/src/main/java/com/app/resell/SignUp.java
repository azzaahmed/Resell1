package com.app.resell;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.app.resell.Data.FireBaseCalls;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mukesh.countrypicker.fragments.CountryPicker;
import com.mukesh.countrypicker.interfaces.CountryPickerListener;
import com.mukesh.countrypicker.models.Country;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    // *********************** upload image ***********************************//
    private String profilePicPath;

    private Uri imageUri = null;

    private StorageReference mStorage;

    private static final int GALLERY_REQUEST = 1;

    private ImageView imageViewCircle;

    boolean profilePicAttached = false;

    Bitmap bitmap;

    private static final String TAG = "SignInActivity";

    private TextInputLayout inputLayoutName;
    private TextInputLayout inputLayoutAge;
    private TextInputLayout inputLayoutMobile;
    private TextInputLayout inputLayoutEmail;
    private TextInputLayout inputLayoutPass;
    private TextInputLayout inputLayoutGender;
    private TextInputLayout inputLayoutCountry;

    //defining view objects
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonSignUp;
    private ProgressDialog progressDialog;
    private EditText Name;
    private EditText age;
    private EditText mobile;
    private Spinner gender;


    private FireBaseCalls FireBaseCalls;
    private Activity Activity;

    //select country
    private EditText countryEditTextFrom;
    private CountryPicker mCountryPicker;
    private ImageView mCountryFlagImageViewFrom;
    private String country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // *********************** upload image ***********************************//
        imageViewCircle = (ImageView) findViewById(R.id.buttonChoose);
        imageViewCircle.setOnClickListener(this);

        mStorage = FirebaseStorage.getInstance().getReference();

        // ************************************************************************//

        // *********************** validation   ***********************************//
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutAge = (TextInputLayout) findViewById(R.id.input_layout_age);
        inputLayoutMobile = (TextInputLayout) findViewById(R.id.input_layout_mobile);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPass = (TextInputLayout) findViewById(R.id.input_layout_pass);
        inputLayoutGender = (TextInputLayout) findViewById(R.id.input_layout_Gender);
        inputLayoutCountry = (TextInputLayout) findViewById(R.id.input_layout_country);
        // ************************************************************************//


        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        buttonSignUp = (Button) findViewById(R.id.buttonSignup);

        progressDialog = new ProgressDialog(this);

        age = (EditText) findViewById(R.id.age);
        gender = (Spinner) findViewById(R.id.gender);
        Name = (EditText) findViewById(R.id.name);
        mobile = (EditText) findViewById(R.id.mobile);

        buttonSignUp.setOnClickListener(this);


        FireBaseCalls = new FireBaseCalls();
        Activity = this;


        //select country
        countryEditTextFrom = (EditText) findViewById(R.id.pick_country_from);
        mCountryFlagImageViewFrom = (ImageView) findViewById(R.id.row_icon_from);
        mCountryPicker = CountryPicker.newInstance("Select Country");
        setListener();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSignup:
                if (validForm()) {
                    registerUser();
                }
                break;
            // upload image
            case R.id.buttonChoose:
                showFileChooser();
                break;


        }
    }

    private void registerUser() {


        if (Utility.isOnline(this)) {
            if (profilePicAttached) {
                uploadImage();
            } else {
                FireBaseCalls.fireBaseRegistration(editTextEmail, editTextPassword, age, Name, mobile, gender, country, " ", getApplicationContext(), true, Activity);

            }
        } else Toast.makeText(this,this.getResources().getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
    }

    // image upload
    private void showFileChooser() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            imageUri = data.getData();
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8; // shrink it down otherwise we will use stupid amounts of memory

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }


            Picasso.with(SignUp.this)
                    .load(imageUri).fit().centerCrop()
                    .into(imageViewCircle);
            profilePicAttached = true;

        }

    }

    public void uploadImage() {

        progressDialog.setMessage(this.getResources().getString(R.string.Registration_item_wait));
        progressDialog.show();

        if (imageUri != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] bytes = baos.toByteArray();
            String base64Image = Base64.encodeToString(bytes, Base64.DEFAULT);// no need

            StorageReference filepath = mStorage.child("UsersImages").child(imageUri.getLastPathSegment());
            UploadTask uploadTask = filepath.putBytes(bytes);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    progressDialog.dismiss();
                    Toast.makeText(SignUp.this, getApplicationContext().getResources().getString(R.string.upload_image_fail), Toast.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    profilePicPath = taskSnapshot.getDownloadUrl() + "";
                    FireBaseCalls.fireBaseRegistration(editTextEmail, editTextPassword, age, Name, mobile, gender, country, profilePicPath, getApplicationContext(), false, Activity);
                }
            });

        }


    }

    private boolean validForm() {

        int counter = 0;
        if (Name.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError("please enter your name");
            requestFocus(Name);
            counter++;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }
        if (age.getText().toString().trim().isEmpty()) {
            inputLayoutAge.setError("please enter your age");
            requestFocus(age);
            counter++;
        } else {
            if (Integer.parseInt(age.getText().toString().trim()) < 12 || Integer.parseInt(age.getText().toString().trim()) > 90) {
                inputLayoutAge.setError("please enter reasonable age");
                requestFocus(age);
                counter++;
            } else
                inputLayoutAge.setErrorEnabled(false);
        }
        if (mobile.getText().toString().trim().isEmpty()) {
            inputLayoutMobile.setError("Invalid mobile number");
            requestFocus(mobile);
            counter++;
        } else {
            inputLayoutMobile.setErrorEnabled(false);
        }
        if (editTextEmail.getText().toString().trim().isEmpty()) {
            inputLayoutEmail.setError("Invalid Email");
            requestFocus(editTextEmail);
            counter++;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }
        if (editTextPassword.getText().toString().trim().isEmpty() || editTextPassword.getText().toString().trim().length() < 6) {
            inputLayoutPass.setError("password at least 6 characters");
            requestFocus(editTextPassword);
            counter++;
        } else {
            inputLayoutPass.setErrorEnabled(false);
        }
        if (!isValidEmail(editTextEmail.getText())) {
            inputLayoutEmail.setError("Invalid Email");
            requestFocus(editTextEmail);
            counter++;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        if (!isValidPhone(mobile.getText())) {
            inputLayoutMobile.setError("Invalid mobile number");
            requestFocus(mobile);
            counter++;
        } else {
            inputLayoutMobile.setErrorEnabled(false);
        }
        if (gender.getSelectedItem().toString().equals("Gender")) {
            inputLayoutGender.setError("Enter your gender");
            requestFocus(gender);
            counter++;
        } else {
            inputLayoutGender.setErrorEnabled(false);
        }
        if (countryEditTextFrom.getText().toString().trim().isEmpty()) {
            inputLayoutCountry.setError("Enter your country");
            requestFocus(countryEditTextFrom);
            counter++;
        } else {
            inputLayoutCountry.setErrorEnabled(false);
        }

        if (counter == 0) {
            return true;
        } else return false;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public final static boolean isValidPhone(CharSequence target) {
        int x = target.length();
        if (x != 11) {
            return false;
        }
        return !TextUtils.isEmpty(target) && Patterns.PHONE.matcher(target).matches();
    }


    //select country
    private void setListener() {
        mCountryPicker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode,
                                        int flagDrawableResID) {
                countryEditTextFrom.setText(name);
                mCountryFlagImageViewFrom.setImageResource(flagDrawableResID);
                country = name;
                mCountryPicker.getDialog().dismiss();
            }
        });
        countryEditTextFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountryPicker.show(SignUp.this.getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });

        country = getUserCountryInfo();
    }

    private String getUserCountryInfo() {
        Country country = mCountryPicker.getUserCountryInfo(SignUp.this);
        mCountryFlagImageViewFrom.setImageResource(country.getFlag());
        countryEditTextFrom.setText(country.getName());
        return country.getName();
    }


}