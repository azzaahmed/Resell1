package com.app.resell;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.firebase.client.Firebase;

/**
 * A placeholder fragment containing a simple view.
 */
public class HomeFragment extends Fragment {


    private static final String TAG = "HomeFragment";
    private GridView gridview;
    private ItemsAdapter imageAdapter;
    public static ProgressDialog progress;
    Firebase itemListsRef=null;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_home, container, false);

         progress = new ProgressDialog(getActivity());
        if(Utility.isOnline(getActivity())) {
        progress.setMessage("loading.....");
        progress.show();
        progress.setCancelable(false);
        }
        Firebase.setAndroidContext(getActivity());
        gridview = (GridView) view.findViewById(R.id.gridview);


        if(Utility.isOnline(getActivity())) {
            itemListsRef = new Firebase("https://resell-8d488.firebaseio.com/").child("items");

            if (itemListsRef == null) {
                Log.d(TAG, "check reference is null empty items");
                progress.dismiss();
            }

            imageAdapter = new ItemsAdapter(getActivity(), Item.class, R.layout.image_item, itemListsRef);

            gridview.setAdapter(imageAdapter);


            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Item selectedItem = imageAdapter.getItem(position);
                    Log.v(TAG, "item clicked");
                    Intent intent = new Intent(getActivity(), itemDetails.class).putExtra("selectedItem", selectedItem);
                    //startActivity(intent);
                    Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(getActivity()
                            , view.findViewById(R.id.grid_item_imageview), view.findViewById(R.id.grid_item_imageview).getTransitionName()).toBundle();
                    startActivity(intent, bundle);
                }
            });


        }
        else{
            Toast.makeText(getContext(), "no internet connection", Toast.LENGTH_LONG).show();
        }

        return view;

    }



    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onStart() {
        super.onStart();

    }

}

