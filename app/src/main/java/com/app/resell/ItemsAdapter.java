package com.app.resell;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by azza ahmed on 5/3/2017.
 */
public class ItemsAdapter extends FirebaseListAdapter<Item> {
    public ItemsAdapter(Activity activity, Class<Item> modelClass, int modelLayout, com.google.firebase.database.Query ref) {
        super(activity, modelClass, modelLayout, ref);
    }

//    public ItemsAdapter(Activity activity, Class<Item> modelClass, int modelLayout, Query ref) {
//        super(activity, modelClass, modelLayout, ref);
//    }


    /**
     * Protected method that populates the view attached to the adapter (list_view_active_lists)
     * with items inflated from single_active_list.xml
     * populateView also handles data changes and updates the listView accordingly
     */
//    @Override
//    protected void populateView(View view, Item item) {
//
//
//
//    }

    @Override
    protected void populateView(View view, Item item, int position) {

        Log.d("populate", "checking populate the view");
        TextView price = (TextView) view.findViewById(R.id.price);
        ImageView itemImage = (ImageView) view.findViewById(R.id.grid_item_imageview);

        price.setText(item.getPrice());

        Picasso.with(view.getContext())
                .load(item.getImageUrl()).fit().centerCrop()
                .into(itemImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        HomeFragment.progress.dismiss();
                    }

                    @Override
                    public void onError() {
                        HomeFragment.progress.dismiss();
                    }
                });
    }
}
