package com.app.resell;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by azza ahmed on 5/4/2017.
 */
public class MyItemAdapter extends RecyclerView.Adapter<MyItemAdapter.MyViewHolder> {

    private List<Item> itemsList;
    private Activity activity;
    public int lastPosition = -1;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView price, description, size;
        private ImageView image, deleteButton;


        public MyViewHolder(View view) {
            super(view);
            price = (TextView) view.findViewById(R.id.price);
            description = (TextView) view.findViewById(R.id.description);
            size = (TextView) view.findViewById(R.id.size);
            image = (ImageView) view.findViewById(R.id.item_imageview);
            deleteButton = (ImageView) view.findViewById(R.id.delete);
        }
    }

    public MyItemAdapter(List<Item> itemsList, Activity activity) {
        this.itemsList = itemsList;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_item_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Item item = itemsList.get(position);
        holder.price.setText(item.getPrice());
        holder.size.setText(item.getSize());
        holder.description.setText(item.getDescription());
        Picasso.with(activity).load(item.getImageUrl()).fit().centerCrop()
                .into(holder.image);

        if (Utility.isOnline(activity))
            delete(holder, position);
        else Toast.makeText(activity, "no internet connection", Toast.LENGTH_LONG).show();

        // Here you apply the animation of item when the view is bound
        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public void clear() {
        itemsList.clear();
    }

    public void delete(MyViewHolder holder, final int position) {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference x = databaseReference.child("items").child(itemsList.get(position).getItem_id());
                        x.removeValue();


                        Toast toast = Toast.makeText(activity, "Item is deleted", Toast.LENGTH_SHORT);
                        toast.show();
//                        Intent intent = new Intent( ActiveListDetailsActivity.this,myOfferedPosts.class);
//                        startActivity(intent);

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked

                        break;
                }
            }
        };
        final AlertDialog.Builder DeleteConfirmMessage = new AlertDialog.Builder(activity);
        DeleteConfirmMessage.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener);
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteConfirmMessage.show();

            }
        });


    }

    //for animation of the list
    private void setAnimation(View viewToAnimate, int position) {

        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(activity.getApplicationContext(), android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}

