package com.samsung.cryptotracker.Chart;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.samsung.cryptotracker.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ChartMarker extends MarkerView {
    private TextView date;
    private TextView value;
    public ChartMarker(Context context, int layoutResource) {
        super(context, layoutResource);
        date = findViewById(R.id.date);
        value = findViewById(R.id.value);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formatedDate = sdf.format(new java.util.Date((long) e.getX()));

        date.setText(formatedDate);
        value.setText(e.getY() + " $");
        super.refreshContent(e, highlight);
    }
    private MPPointF mOffset;
    @Override
    public MPPointF getOffset() {
        if(mOffset == null) {
            mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
        }
        return mOffset;
    }
}
