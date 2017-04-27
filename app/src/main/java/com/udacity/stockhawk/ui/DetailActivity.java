package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.AppConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.chart)
    LineChart lineChart;
    List<Entry> entries;
    ArrayList<String> xValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);


        setupLineChartStyle();

        entries = new ArrayList<>();
        xValues = new ArrayList<>();


        if(getIntent().getExtras() != null) {
            String symbol = getIntent().getExtras().getString(AppConstants.INTENT_EXTRA_SYMBOL);
            String history = getIntent().getExtras().getString(AppConstants.INTENT_EXTRA_HISTORY);
            String[] historyArr = history.split("\n");

            for (int i = 0; i < historyArr.length; i++) {

                String[] historyEntry = historyArr[i].split(", ");

                xValues.add(historyEntry[0]);
                float yValue = Float.parseFloat(historyEntry[1]);
                entries.add(new Entry(i,yValue));


                Log.d("xValue", historyEntry[0]);
                Log.d("yValue", historyEntry[1]);
            }
            setTitle(symbol);
        }

        LineDataSet dataSet = new LineDataSet(entries, "Stock Max Per Month");
        dataSet.enableDashedLine(10f, 5f, 0f);
        dataSet.enableDashedHighlightLine(10f, 5f, 0f);
        dataSet.setColor(Color.BLACK);
        dataSet.setCircleColor(Color.BLACK);
        dataSet.setLineWidth(1f);
        dataSet.setCircleRadius(3f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextSize(9f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(getResources().getColor(R.color.colorAccent));
        dataSet.setFormLineWidth(1f);
        dataSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        dataSet.setFormSize(15.f);

      //  dataSet.setColor(R.color.colorAccent);

        LineData data = new LineData(dataSet);
        data.setValueTextColor(getResources().getColor(R.color.colorPrimary));


        lineChart.setData(data);
        lineChart.animateX(1000);
        lineChart.invalidate();



        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return  xValues.get((int) value);
            }
        });

    }

    private void setupLineChartStyle() {

        // no description text
        lineChart.getDescription().setEnabled(false);

        // enable touch gestures
        lineChart.setTouchEnabled(true);

        // enable scaling and dragging
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(true);

        lineChart.setBackgroundColor(Color.WHITE);
    }
}
