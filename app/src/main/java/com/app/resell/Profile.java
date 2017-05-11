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
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mukesh.countrypicker.fragments.CountryPicker;
import com.mukesh.countrypicker.interfaces.CountryPickerListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    // *********************** upload image ***********************************//

    private String profilePicPath;

    private Uri imageUri = null;

    private StorageReference mStorage;

    private static final int GALLERY_REQUEST = 1;

    boolean profilePicAttached = false;

    CircleImageView profileImageEdit;

    Bitmap bitmap;

    //TextView Name;
    TextView age;
    TextView gender;
    TextView email;
    TextView mobile;
    TextView Country;
    ImageView profileImage;
    RelativeLayout mobileLayout;

    //firebase auth object
    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;

    ProgressDialog progress;

    private boolean displayOwner = false;
    private String profileOwnerId;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    Account myAccount;

    FloatingActionButton fab;

    String mAge;
    String mName;
    String mGender, mEmail;
    String mMobile, mCountry;

    EditText nameEdit;
    EditText ageEdit;
    EditText emailEdit;
    Spinner genderEdit;
    EditText mobileEdit;

    FirebaseUser user;
    boolean pencil;

    //select country
    private EditText countryEditTextFrom;
    private CountryPicker mCountryPicker;

    Activity Activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Activity = this;
        mStorage = FirebaseStorage.getInstance().getReference();


        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progress = new ProgressDialog(this);
        if (Utility.isOnline(this)) {
            progress.setMessage("Loading.....");
            progress.show();
            progress.setCancelable(false);
        } else Toast.makeText(this,this.getResources().getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();

        mobileLayout = (RelativeLayout) findViewById(R.id.mobilelayout);

        age = (TextView) findViewById(R.id.age);
        email = (TextView) findViewById(R.id.Email);
        gender = (TextView) findViewById(R.id.Gender);
        mobile = (TextView) findViewById(R.id.mobile);
        Country = (TextView) findViewById(R.id.country);

        profileImage = (ImageView) findViewById(R.id.profileImage);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        Intent intent = getIntent();
        //from post to get the post owner profile
        displayOwner = intent.getBooleanExtra("displayOwner", false);
        profileOwnerId = intent.getStringExtra("OwnerId");
        fab = (FloatingActionButton) findViewById(R.id.fab);

        pencil = true;
        profileImageEdit = (CircleImageView) findViewById(R.id.profile_imageedit);


        if (displayOwner) {


            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();

            p.setAnchorId(View.NO_ID);

            fab.setLayoutParams(p);
            fab.setVisibility(View.GONE);

            if (Utility.isOnline(this))
                getUserInfo(null, false, profileOwnerId);
            mobileLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + mobile.getText()));
                    startActivity(intent);
                }
            });


        } else {
            if (user != null) {
                Log.d("profile", "my profile");
                if (Utility.isOnline(this))
                    getUserInfo(user, true, "");

                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if (pencil) {
                            if (Utility.isOnline(Activity) && myAccount != null) {
                                editClicked();
                                fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_done_white_24dp));
                                pencil = false;
                                profileImageEdit.setVisibility(View.VISIBLE);
                            } else if (!
                                    Utility.isOnline(Activity))
                                Toast.makeText(getApplicationContext(),getApplicationContext().getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                        } else {

                            if (Utility.isOnline(Activity)) {
                                fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pencil));
                                try {
                                    saveEdit();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                pencil = true;
                                profileImageEdit.setVisibility(View.GONE);

                            } else
                                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


                profileImageEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showFileChooser();
                    }
                });


            }


        }


        nameEdit = (EditText) findViewById(R.id.nameE);
        ageEdit = (EditText) findViewById(R.id.ageE);
        emailEdit = (EditText) findViewById(R.id.EmailE);

        genderEdit = (Spinner) findViewById(R.id.GenderE);

        mobileEdit = (EditText) findViewById(R.id.mobileE);

        //country
        countryEditTextFrom = (EditText) findViewById(R.id.pick_country_from);
        mCountryPicker = CountryPicker.newInstance("Select Country");
        setListener();


    }

    public void getUserInfo(FirebaseUser user, final boolean ViewMyProfile, String id) {
        final FirebaseUser currentUser = user;
        final Account[] account = new Account[1];

        String user_id;

        if (ViewMyProfile)
            user_id = currentUser.getUid();
        else user_id = id;

        databaseReference.child("users").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                account[0] = dataSnapshot.getValue(Account.class);

                if (ViewMyProfile)
                    myAccount = dataSnapshot.getValue(Account.class);

                if (account[0] != null) {
                    Log.d("My profile", "inside the data listener" + account[0].getName() + "  " + account[0].getAge() + "  " + account[0].getGender() + "   " + account[0].getMobile());
                    Log.d("My profile", "account from get user info method");
                    if (account[0].getName() != null)
                        collapsingToolbarLayout.setTitle(account[0].getName());
                    Log.d("My profile", "name not null");
                    email.setText(account[0].getEmail());

                    if (ViewMyProfile) {
                        if (account[0].getImage_url() != null)
                            Picasso.with(Profile.this)
                                    .load(account[0].getImage_url()).fit().centerCrop()
                                    .into(profileImage);

                        else Picasso.with(Profile.this)
                                .load(currentUser.getPhotoUrl()).fit()
                                .into(profileImage);  //*hena lo mala2tsh sora fel database a5od bta3t google
                    } else {
                        if (account[0].getImage_url() != null)
                            Picasso.with(Profile.this)
                                    .load(account[0].getImage_url()).fit().centerCrop()
                                    .into(profileImage);
                    }
                    if (account[0].getAge() != null) age.setText(account[0].getAge());
                    if (account[0].getMobile() != null) mobile.setText(account[0].getMobile());
                    if (account[0].getGender() != null) gender.setText(account[0].getGender());
                    if (account[0].getCountry() != null) Country.setText(account[0].getCountry());


                    progress.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (!displayOwner)
            getMenuInflater().inflate(R.menu.menu_profile, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_items) {
            startActivity(new Intent(this, MyItems1.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void editClicked() {

        RelativeLayout ageLayout = (RelativeLayout) findViewById(R.id.agelayout);
        RelativeLayout emailLayout = (RelativeLayout) findViewById(R.id.emaillayout);
        RelativeLayout genderLayout = (RelativeLayout) findViewById(R.id.genderlayout);
        RelativeLayout countryLayout = (RelativeLayout) findViewById(R.id.countryLayout);
        RelativeLayout mobileLayout = (RelativeLayout) findViewById(R.id.mobilelayout);

        RelativeLayout editAgeLayout = (RelativeLayout) findViewById(R.id.editagelayout);
        RelativeLayout editEmailLayout = (RelativeLayout) findViewById(R.id.emaillayoutedit);
        RelativeLayout editGenderLayout = (RelativeLayout) findViewById(R.id.editgenderlayout);
        RelativeLayout editCountryLayout = (RelativeLayout) findViewById(R.id.editCountrylayout);
        RelativeLayout editMobileLayout = (RelativeLayout) findViewById(R.id.editmobilelayout);

        View dividerAfterName = findViewById(R.id.dividerAfterName);
        dividerAfterName.setVisibility(View.VISIBLE);

        ageLayout.setVisibility(View.GONE);
        emailLayout.setVisibility(View.VISIBLE);
        genderLayout.setVisibility(View.GONE);
        mobileLayout.setVisibility(View.GONE);
        countryLayout.setVisibility(View.GONE);

        RelativeLayout namelayout = (RelativeLayout) findViewById(R.id.namelayout);
        namelayout.setVisibility(View.VISIBLE);


        editAgeLayout.setVisibility(View.VISIBLE);
        editEmailLayout.setVisibility(View.GONE);
        editMobileLayout.setVisibility(View.VISIBLE);
        editGenderLayout.setVisibility(View.VISIBLE);
        editCountryLayout.setVisibility(View.VISIBLE);

        nameEdit.setHint(myAccount.getName());
        ageEdit.setHint(myAccount.getAge());
        emailEdit.setHint(myAccount.getEmail());
        mobileEdit.setHint(myAccount.getMobile());
        countryEditTextFrom.setHint(myAccount.getCountry());
    }

    public void saveEdit() throws IOException {

        user = firebaseAuth.getCurrentUser();
        mAge = ageEdit.getText().toString().trim();
        mName = nameEdit.getText().toString().trim();
        mMobile = mobileEdit.getText().toString().trim();
        mGender = genderEdit.getSelectedItem().toString();
        mEmail = emailEdit.getText().toString().trim();
        mCountry = countryEditTextFrom.getText().toString().trim();

        if (myAccount != null) {
            if (mAge.isEmpty()) mAge = myAccount.getAge();
            if (mName.isEmpty()) mName = myAccount.getName();
            if (mMobile.isEmpty()) mMobile = myAccount.getMobile();
            if (mEmail.isEmpty()) mEmail = myAccount.getEmail();
            if (mCountry.isEmpty()) mCountry = myAccount.getCountry();

            mGender = genderEdit.getSelectedItem().toString();
            if (mGender.equals("Gender")) {
                mGender = myAccount.getGender();
            }

        } else {
            if (TextUtils.isEmpty(mEmail)) {
                Toast.makeText(this,this.getResources().getString(R.string.enter_email) , Toast.LENGTH_LONG).show();
                return;
            }
            if (TextUtils.isEmpty(mAge)) {
                Toast.makeText(this,this.getResources().getString(R.string.enter_age), Toast.LENGTH_LONG).show();
                return;

            }
            if (TextUtils.isEmpty(mMobile)) {
                Toast.makeText(this, this.getResources().getString(R.string.enter_mobile), Toast.LENGTH_LONG).show();
                return;

            }
            if (TextUtils.isEmpty(mGender)) {
                Toast.makeText(this, this.getResources().getString(R.string.enter_gender), Toast.LENGTH_LONG).show();
                return;

            }
            if (TextUtils.isEmpty(mCountry)) {
                Toast.makeText(this, this.getResources().getString(R.string.enter_country), Toast.LENGTH_LONG).show();
                return;
            }
            if (TextUtils.isEmpty(mName) && user.getDisplayName() == null) {
                Toast.makeText(this, this.getResources().getString(R.string.enter_name), Toast.LENGTH_LONG).show();
                return;

            } else if (user.getDisplayName() != null) mName = user.getDisplayName();

        }

        if (validForm()) {
            if (profilePicAttached) {
                UploadImage();
            } else {
                fireBaseEdit(false);
            }
        }
    }

    public void returnProfileView() {
        RelativeLayout ageLayout = (RelativeLayout) findViewById(R.id.agelayout);
        RelativeLayout emailLayout = (RelativeLayout) findViewById(R.id.emaillayout);
        RelativeLayout genderLayout = (RelativeLayout) findViewById(R.id.genderlayout);
        RelativeLayout countryLayout = (RelativeLayout) findViewById(R.id.countryLayout);
        RelativeLayout mobileLayout = (RelativeLayout) findViewById(R.id.mobilelayout);

        RelativeLayout editAgeLayout = (RelativeLayout) findViewById(R.id.editagelayout);
        RelativeLayout editEmailLayout = (RelativeLayout) findViewById(R.id.emaillayoutedit);
        RelativeLayout editGenderLayout = (RelativeLayout) findViewById(R.id.editgenderlayout);
        RelativeLayout editCountryLayout = (RelativeLayout) findViewById(R.id.editCountrylayout);
        RelativeLayout editMobileLayout = (RelativeLayout) findViewById(R.id.editmobilelayout);
        View dividerAfterName = findViewById(R.id.dividerAfterName);
        dividerAfterName.setVisibility(View.GONE);

        ageLayout.setVisibility(View.VISIBLE);
        emailLayout.setVisibility(View.VISIBLE);
        genderLayout.setVisibility(View.VISIBLE);
        mobileLayout.setVisibility(View.VISIBLE);
        countryLayout.setVisibility(View.VISIBLE);

        RelativeLayout nameLayout = (RelativeLayout) findViewById(R.id.namelayout);
        nameLayout.setVisibility(View.GONE);


        editAgeLayout.setVisibility(View.GONE);
        editEmailLayout.setVisibility(View.GONE);
        editMobileLayout.setVisibility(View.GONE);
        editGenderLayout.setVisibility(View.GONE);
        editCountryLayout.setVisibility(View.GONE);
    }

    // *********************** upload image ***********************************//

    private void showFileChooser() {
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
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
//                Bitmap bitmap = BitmapFactory.decodeFile(imageUri.getPath(), options);
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //  profile_imageedit.setImageURI(imageUri);
            Picasso.with(this).load(imageUri).fit().centerCrop().into(profileImageEdit);
            profilePicAttached = true;

        }
    }


    public void UploadImage() throws IOException {

        progress.setMessage(this.getResources().getString(R.string.updating));
        progress.show();

        if (imageUri != null) {
//            //Getting the Bitmap from Gallery
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] bytes = baos.toByteArray();
            StorageReference filepath = mStorage.child("UsersImages").child(imageUri.getLastPathSegment());
            UploadTask uploadTask = filepath.putBytes(bytes);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    progress.dismiss();
                    Toast.makeText(Profile.this, getApplicationContext().getResources().getString(R.string.upload_image_fail), Toast.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    profilePicPath = taskSnapshot.getDownloadUrl() + "";
                    fireBaseEdit(true);
                }
            });

        }
    }


    public void fireBaseEdit(boolean withImage) {
        Account myaccount;

        if (withImage)
            myaccount = new Account(mName, mAge, mMobile, mGender, mEmail, profilePicPath, mCountry);
        else
            myaccount = new Account(mName, mAge, mMobile, mGender, mEmail, myAccount.getImage_url(), mCountry);

        //to set without id as field of account object
//        databaseReference.child("users").child(user.getUid()).setValue(myaccount);

        DatabaseReference x = databaseReference.child("users").child(user.getUid());
        x.setValue(myaccount);
        // pushing key as id field in the table after pushing object
        String key = x.getKey();
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", key);
        x.updateChildren(result);

        progress.dismiss();

        returnProfileView();


    }

    //select country
    private void setListener() {
        mCountryPicker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode,
                                        int flagDrawableResID) {
                countryEditTextFrom.setText(name);

                mCountry = name;
                mCountryPicker.getDialog().dismiss();
            }
        });
        countryEditTextFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountryPicker.show(Profile.this.getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });


    }


    //validations when edit
    private boolean validForm() {
        TextInputLayout InputLayoutAgeEdit = (TextInputLayout) findViewById(R.id.InputLayoutAgeEdit);
        TextInputLayout InputLayoutMobileEdit = (TextInputLayout) findViewById(R.id.InputLayoutMobileEdit);

        int counter = 0;


        if (Integer.parseInt(mAge) < 12 || Integer.parseInt(mAge) > 90) {
            InputLayoutAgeEdit.setError("please enter reasonable age");
            requestFocus(ageEdit);
            counter++;
        } else InputLayoutAgeEdit.setErrorEnabled(false);


        if (!isValidPhone(mMobile)) {
            InputLayoutMobileEdit.setError("Invalid mobile number");
            requestFocus(mobileEdit);
            counter++;
        } else {
            InputLayoutMobileEdit.setErrorEnabled(false);
        }

        return counter == 0;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public static boolean isValidPhone(CharSequence target) {
        int x = target.length();
        if (x != 11) {
            return false;
        }
        return !TextUtils.isEmpty(target) && Patterns.PHONE.matcher(target).matches();
    }

}
