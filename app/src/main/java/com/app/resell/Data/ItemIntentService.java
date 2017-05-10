package com.app.resell.Data;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by azza ahmed on 5/9/2017.
 */
public class ItemIntentService extends IntentService {

    public ItemIntentService (){
        super(ItemIntentService.class.getSimpleName());

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v("intent service", " ");
        FetchItems.getMyItems(getApplicationContext());
    }
}
