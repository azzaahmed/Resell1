package com.app.resell;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by azza ahmed on 5/3/2017.
 */
public class ItemsAdapter extends FirebaseListAdapter<Item> {


    public int lastPosition = -1;
    Activity activity;

    public ItemsAdapter(Activity activity, Class<Item> modelClass, int modelLayout, com.google.firebase.database.Query ref) {
        super(activity, modelClass, modelLayout, ref);
        this.activity=activity;
    }


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
        setAnimation(itemImage, position);
    }

    //for animation of the list
    private void setAnimation(View viewToAnimate, int position) {

        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(activity.getApplicationContext(), android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
//            lastPosition = position;
        }
    }
}
