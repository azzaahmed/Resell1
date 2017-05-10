package com.app.resell.Widget;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.app.resell.Data.FetchItems;
import com.app.resell.Item;
import com.app.resell.R;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by azza ahmed on 5/5/2017.
 */
public class WidgetRemoteViewsService  extends RemoteViewsService {
    private ArrayList<Item> itemsList = new ArrayList<>();
//    private FirebaseUser currentUser ;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
              //  getMyItems();
                itemsList= FetchItems.itemsList;
                Log.v("widget service","onDataSetChanged");
            }

            @Override
            public void onDestroy() {

            }

            @Override
            public int getCount() {

                Log.v("wigdet service",itemsList.size()+"");
                return itemsList.size();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_list_item);

                String price = itemsList.get(position).getPrice();
                String imageURL=itemsList.get(position).getImageUrl();

                views.setTextViewText(R.id.price, price);

                Bitmap image = null;

                try {
                    URL url = new URL(imageURL);
                     image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch(IOException e) {
                    System.out.println(e);
                }

                views.setImageViewBitmap(R.id.item_imageview,image);

               Log.v("widget service","item price "+price);

                final Intent fillInIntent = new Intent();

                fillInIntent.putExtra("selectedItem", itemsList.get(position));
                views.setOnClickFillInIntent(R.id.card_list_item, fillInIntent);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                   return new RemoteViews(getPackageName(), R.layout.widget_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }
        };
    }

}
