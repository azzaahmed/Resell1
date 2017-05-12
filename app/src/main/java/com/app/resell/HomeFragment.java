package com.app.resell;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mukesh.countrypicker.fragments.CountryPicker;
import com.mukesh.countrypicker.models.Country;

/**
 * A placeholder fragment containing a simple view.
 */
public class HomeFragment extends Fragment {


    private static final String TAG = "HomeFragment";
    private GridView gridview;
    private ItemsAdapter imageAdapter;
    public static ProgressDialog progress;
     DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    String itemLocationValuePref;
    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        progress = new ProgressDialog(getActivity());
        if (Utility.isOnline(getActivity())) {
            progress.setMessage(this.getResources().getString(R.string.loading));
            progress.show();
            progress.setCancelable(false);
        }

        Firebase.setAndroidContext(getActivity());
        gridview = (GridView) view.findViewById(R.id.gridview);


            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Item selectedItem = imageAdapter.getItem(position);
                    Log.v(TAG, "item clicked");
                    Intent intent = new Intent(getActivity(), ItemDetails.class).putExtra("selectedItem", selectedItem);
                    //startActivity(intent);
                    Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(getActivity()
                            , view.findViewById(R.id.grid_item_imageview), view.findViewById(R.id.grid_item_imageview).getTransitionName()).toBundle();
                    startActivity(intent, bundle);
                }
            });

        return view;

    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        itemLocationValuePref = sharedPrefs.getString(
                getString(R.string.pref_Location_key),
                getString(R.string.pref_location_all_countries));


        if (Utility.isOnline(getActivity())) {

            if (databaseReference.child("items") == null) {
                progress.dismiss();
            }

            if (itemLocationValuePref.equals(getString(R.string.pref_location_all_countries)))
                imageAdapter = new ItemsAdapter(getActivity(), Item.class, R.layout.image_item, databaseReference.child("items"));
            else {
                imageAdapter = new ItemsAdapter(getActivity(), Item.class, R.layout.image_item, databaseReference.child("items").orderByChild("country").equalTo(getCountryName()));
            }
            gridview.setAdapter(imageAdapter);
        } else {
            Toast.makeText(getContext(), this.getResources().getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
        }

    }

    private String getCountryName() {

        Country country =   CountryPicker.newInstance("Select Country").getUserCountryInfo(getActivity());

        return country.getName();
    }

}

