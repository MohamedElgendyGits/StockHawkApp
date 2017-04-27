package com.udacity.stockhawk.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.AppConstants;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by Mohamed on 4/25/2017.
 */

public class StockWidgetData implements RemoteViewsService.RemoteViewsFactory {

    private Context context;
    private Cursor cursor;
    private Intent intent;
    private DecimalFormat dollarFormatWithPlus;
    private DecimalFormat dollarFormat;
    private DecimalFormat percentageFormat;

    //For obtaining the activity's context and intent
    public StockWidgetData(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
    }

    private void initCursor(){
        if (cursor != null) {
            cursor.close();
        }
        final long identityToken = Binder.clearCallingIdentity();
        /**This is done because the widget runs as a separate thread
         when compared to the current app and hence the app's data won't be accessible to it */

        cursor = context.getContentResolver().query(Contract.Quote.URI,
                Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                null, null, Contract.Quote.COLUMN_SYMBOL);

        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onCreate() {
        initCursor();
        if (cursor != null) {
            cursor.moveToFirst();
        }

        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix("+$");
        percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");
    }

    @Override
    public void onDataSetChanged() {
        /** Listen for data changes and initialize the cursor again **/
        initCursor();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        /** Populate your widget's single list item **/
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.list_item_stock_widget);
        cursor.moveToPosition(i);

        remoteViews.setTextViewText(R.id.symbol,cursor.getString(Contract.Quote.POSITION_SYMBOL));
        remoteViews.setTextViewText(R.id.price,dollarFormat.format(cursor.getFloat(Contract.Quote.POSITION_PRICE)));


        float rawAbsoluteChange = cursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
        float percentageChange = cursor.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);
        if (rawAbsoluteChange > 0) {
            remoteViews.setInt(R.id.change,"setBackgroundResource",R.drawable.percent_change_pill_green);
        } else {
            remoteViews.setInt(R.id.change,"setBackgroundResource",R.drawable.percent_change_pill_red);
        }

        String change = dollarFormatWithPlus.format(rawAbsoluteChange);
        String percentage = percentageFormat.format(percentageChange / 100);
        if (PrefUtils.getDisplayMode(context).equals(context.getString(R.string.pref_display_mode_absolute_key))) {
            remoteViews.setTextViewText(R.id.change,change);
        } else {
            remoteViews.setTextViewText(R.id.change,percentage);
        }

        // set Onclick Item Intent
        Intent onClickItemIntent = new Intent();
        int symbolColumn = cursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL);
        int historyColumn = cursor.getColumnIndex(Contract.Quote.COLUMN_HISTORY);
        onClickItemIntent.putExtra(AppConstants.INTENT_EXTRA_SYMBOL,cursor.getString(symbolColumn));
        onClickItemIntent.putExtra(AppConstants.INTENT_EXTRA_HISTORY,cursor.getString(historyColumn));
        remoteViews.setOnClickFillInIntent(R.id.list_item_stock_row,onClickItemIntent);


        return remoteViews;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public long getItemId(int i) {
        cursor.moveToPosition(i);
        return cursor.getLong(cursor.getColumnIndex(Contract.Quote._ID));
    }

    @Override
    public void onDestroy() {
        if (cursor!=null)
            cursor.close();
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


}
