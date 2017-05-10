package com.app.resell;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.app.resell.Data.FireBaseCalls;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mukesh.countrypicker.fragments.CountryPicker;
import com.mukesh.countrypicker.interfaces.CountryPickerListener;
import com.mukesh.countrypicker.models.Country;

// when first sign in with google to take the extra information
public class EditProfile extends AppCompatActivity {


    private EditText age;
    private EditText mobile;
    private Spinner gender;

    String mAge;

    String mGender;
    String mMobile, oldUrl;
    Account account;
    private FirebaseAuth firebaseAuth;

    private FireBaseCalls FireBaseCalls;
    FirebaseUser user;

    //select country
    private TextInputLayout input_layout_country;
    private EditText countryEditTextFrom;
    private CountryPicker mCountryPicker;
    private ImageView mCountryFlagImageViewFrom;
    private String country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        firebaseAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        account = (Account) intent.getSerializableExtra("accountinfo");

        age = (EditText) findViewById(R.id.age);
        gender = (Spinner) findViewById(R.id.gender);

        mobile = (EditText) findViewById(R.id.mobile);

        FireBaseCalls = new FireBaseCalls();

        //select country
        countryEditTextFrom = (EditText) findViewById(R.id.pick_country_from);
        mCountryFlagImageViewFrom = (ImageView) findViewById(R.id.row_icon_from);
        mCountryPicker = CountryPicker.newInstance("Select Country");
        setListener();
    }

    private void edit() {
        user = firebaseAuth.getCurrentUser();
        mAge = age.getText().toString().trim();

        mMobile = mobile.getText().toString().trim();
        mGender = gender.getSelectedItem().toString();


        if (account != null) {
            if (mAge.isEmpty()) mAge = account.getAge();

            if (mMobile.isEmpty()) mMobile = account.getMobile();

            oldUrl = account.getImage_url();

            mGender = gender.getSelectedItem().toString();

        } else {

            if (TextUtils.isEmpty(mGender)) {
                Toast.makeText(this, "Please enter your gender", Toast.LENGTH_LONG).show();
                return;

            }
            if (TextUtils.isEmpty(mAge)) {
                Toast.makeText(this, "Please enter your age", Toast.LENGTH_LONG).show();
                return;

            }
            if (TextUtils.isEmpty(mMobile)) {
                Toast.makeText(this, "Please enter your mobile number", Toast.LENGTH_LONG).show();
                return;

            }
            if (mGender.equals("Gender")) {
                Toast.makeText(this, "Please enter your Gender", Toast.LENGTH_LONG).show();
                return;
            }

            if (TextUtils.isEmpty(countryEditTextFrom.getText().toString().trim())) {
                Toast.makeText(this, "Please enter your country", Toast.LENGTH_LONG).show();
                return;

            }

        }

        if (Utility.isOnline(this))
            FireBaseCalls.addFireBaseExtraInfo(user.getDisplayName(), mAge, mMobile, mGender, user.getEmail(), user.getPhotoUrl() + "", this, country);
        else Toast.makeText(this, "no internet connection", Toast.LENGTH_LONG).show();

    }


    @Override
    public void onBackPressed() {
        if (account != null) {
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            edit();
            return true;
        }

        return super.onOptionsItemSelected(item);

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
                mCountryPicker.show(EditProfile.this.getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });

        country = getUserCountryInfo();
    }

    private String getUserCountryInfo() {
        Country country = mCountryPicker.getUserCountryInfo(EditProfile.this);
        mCountryFlagImageViewFrom.setImageResource(country.getFlag());
        countryEditTextFrom.setText(country.getName());
        return country.getName();
    }


}
