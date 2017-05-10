package com.app.resell.Data;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by azza ahmed on 5/9/2017.
 */
public class ItemJobService extends JobService {


    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.v("job service", " ");
        Intent nowIntent = new Intent(getApplicationContext(), ItemIntentService.class);
        getApplicationContext().startService(nowIntent);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }


}

