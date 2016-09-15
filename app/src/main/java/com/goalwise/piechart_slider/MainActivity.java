package com.goalwise.piechart_slider;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.lzyzsd.randomcolor.RandomColor;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;


public class MainActivity extends FragmentActivity implements SeekBar.OnSeekBarChangeListener ,OnChartValueSelectedListener{

    private PieChart mChart;
    private SeekBar mSeekBarX, mSeekBarY;
    private TextView tvX, tvY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvX = (TextView) findViewById(R.id.tvXMax);
        tvY = (TextView) findViewById(R.id.tvYMax);

        mSeekBarX = (SeekBar) findViewById(R.id.seekBar1);
        mSeekBarY = (SeekBar) findViewById(R.id.seekBar2);

        mSeekBarX.setProgress(6);
        mSeekBarY.setProgress(10);

        mChart = (PieChart) findViewById(R.id.chart1);
        mChart.setUsePercentValues(true);
        mChart.setDescription("");

        mChart.setExtraOffsets(5, 10, 5, 5);

        mChart.setDragDecelerationFrictionCoef(0.95f);
        //mChart.setCenterText(generateCenterSpannableText());

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);
        mChart.setHoleRadius(58f);

        // add a selection listener
        mChart.setOnChartValueSelectedListener(this);

        setData(10, 100,6);

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        Legend l = mChart.getLegend();
        l.setPosition(LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        mChart.setEntryLabelColor(Color.WHITE);
        //mChart.setEntryLabelTypeface(mTfRegular);
        mChart.setEntryLabelTextSize(12f);

        mSeekBarX.setOnSeekBarChangeListener(this);
        mSeekBarY.setOnSeekBarChangeListener(this);

    }

    private void setData(int count, float range, int stocks ) {

         ArrayList<String> mStocks=new ArrayList<String>();

        for (int i = 0; i < stocks ; i++){
            mStocks.add("Stock"+i);
        }
        for (int i = 0; i < count-stocks ; i++){
            mStocks.add("Debt"+i);
        }



        float mult = range;

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (int i = 0; i < count ; i++) {
            entries.add(new PieEntry((float) ((Math.random() * mult) + mult / 5), mStocks.get(i % mStocks.size())));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Stock Allocation");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);


        int[] greenColors=getGreenHues( stocks);
        int[] blueColors=getBlueHues(count-stocks);


        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : greenColors)
            colors.add(c);

        for (int c : blueColors)
            colors.add(c);


        /*for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());*/

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);

        mChart.setCenterText(generateCenterSpannableText(count, stocks));
        mChart.setDrawCenterText(true);

        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }

    private int[] getGreenHues(int count){
        RandomColor greenRandomColor = new RandomColor();
        int[] greenColors= greenRandomColor.random(RandomColor.Color.GREEN, count);
        return  greenColors;
    }

    private int[] getBlueHues(int count){
        RandomColor blueRandomColor = new RandomColor();

        int[] blueColors= blueRandomColor.random(RandomColor.Color.BLUE, count);
        return blueColors;
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if(mSeekBarY.getProgress()>=mSeekBarX.getProgress()) {
            tvX.setText("Stock " + (mSeekBarX.getProgress()));
            tvY.setText("Total " + (mSeekBarY.getProgress()));

            mSeekBarX.setMax(mSeekBarY.getProgress());


            setData(mSeekBarY.getProgress(), 100, mSeekBarX.getProgress());
        }

    }

    private SpannableString generateCenterSpannableText(int count, int stocks) {

        SpannableString s = new SpannableString("Stocks "+stocks+"/ Debts "+(count-stocks));
        s.setSpan(new RelativeSizeSpan(1.2f), 0, 17, 0);
        //s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
        //s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
        return s;
    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onValueSelected(Entry entry, Highlight highlight) {

    }

    @Override
    public void onNothingSelected() {

    }
}
