package com.app.resell;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class ItemDetailsFragment extends Fragment {
    private Item clickedItem;
    TextView price, size, description, ownerName;
    ImageView itemImage;
    ImageView profileImage;
    private DatabaseReference databaseReference;
    String TAG = "itemDetailsFragment";

    public ItemDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_details, container, false);


        getActivity().getWindow().setSharedElementEnterTransition(TransitionInflater.from(getActivity())
                .inflateTransition(R.transition.move));

        databaseReference = FirebaseDatabase.getInstance().getReference();


        Intent intent = getActivity().getIntent();

        clickedItem = (Item) intent.getSerializableExtra("selectedItem");

        price = (TextView) view.findViewById(R.id.price);
        description = (TextView) view.findViewById(R.id.description);
        size = (TextView) view.findViewById(R.id.size);
        profileImage = (ImageView) view.findViewById(R.id.item_photo);
        itemImage = (ImageView) view.findViewById(R.id.header_cover_image);
        ownerName = (TextView) view.findViewById(R.id.user_profile_name);
        Log.d(TAG, "on create fragment details");


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //preview  owner profile profile activity;
                Intent intent = new Intent(getActivity(), Profile.class).putExtra("displayOwner", true);

                intent.putExtra("OwnerId", clickedItem.getUserId());
                Log.d(TAG, "onclick link");
                startActivity(intent);

            }
        });
        if (Utility.isOnline(getActivity())) {
            if (clickedItem != null)
                getUserInfo(clickedItem.getUserId());
            else {
                Log.v(TAG, "clicked item null");

            }
        } else Toast.makeText(getContext(), this.getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        return view;
    }


    public void settingData() {
        price.setText(clickedItem.getPrice());
        description.setText(clickedItem.getDescription());

        // clickedItem.getUserId();
        size.setText(clickedItem.getSize());
        Picasso.with(getActivity())
                .load(clickedItem.getImageUrl()).fit().centerCrop()
                .into(itemImage, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        scheduleStartPostponedTransition(itemImage);
                    }

                    @Override
                    public void onError() {

                    }
                });


    }


    public void getUserInfo(String user_id) {

        final Account[] account = new Account[1];


        databaseReference.child("users").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                account[0] = dataSnapshot.getValue(Account.class);

                if (account[0] != null) {
                    Log.d("My profile", "account from get user info method");

                    if (account[0].getImage_url() != null)
                        Picasso.with(getActivity())
                                .load(account[0].getImage_url()).fit().centerCrop()
                                .into(profileImage);

                    if (account[0].getName() != null) ownerName.setText(account[0].getName());

                    settingData();


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }


    ///to make sure transition is postponed to the right time
    private void scheduleStartPostponedTransition(final View sharedElement) {
        sharedElement.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                        getActivity().startPostponedEnterTransition();
                        return true;
                    }
                });
    }


}
