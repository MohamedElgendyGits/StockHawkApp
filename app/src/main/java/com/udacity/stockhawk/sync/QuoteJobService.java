package com.udacity.stockhawk.sync;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

import timber.log.Timber;

public class QuoteJobService extends JobService {

    /**
    * @Commented By Mohamed Elgendy
    *  a method that the system triggers when you invoke your JobInfo task.
    *  Its parameter gives us  the access to jobId (params.getJobId()) to identify our task and
    *  PersistableBundle with some extras (which we can pass through JobInfo during task invocation).
    *
    *  For quick tasks onStartJob should return false, however, for longer ones (network operations, etc.),
    *  we have  to call jobFinished(JobParameters params, boolean needsRescheduled)
    *  when our operations are done (in this case onStartJob should return true).
    *  An important note is that onStartJob is invoked in the main thread of the application
    *  (it’s our responsibility to move the time consuming operations off thread).
    *
    *  i.e The onStartJob is performed in the main thread,
    *  if you start asynchronous processing in this method, return true otherwise false.
    */

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Timber.d("Intent handled");

        // we call intent service to do our background call
        Intent nowIntent = new Intent(getApplicationContext(), QuoteIntentService.class);
        getApplicationContext().startService(nowIntent);
        return true;
    }


    /**
    * @Commented By Mohamed Elgendy
    * will be called by the OS if the current work should be cancelled due to environment conditions
    * (e.g. our job is restricted to be done in idle state, but our device is no longer in it).
    * If the job is cancelled and we would like to retry it, the method should return true.
    * Rescheduling can also be invoked by passing true to jobFinished method.
    */

    // but for the project conditions we don't need to retry the job
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }


}
