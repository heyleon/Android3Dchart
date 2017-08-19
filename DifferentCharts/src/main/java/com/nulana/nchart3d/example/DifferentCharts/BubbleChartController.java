/**
 * Disclaimer: IMPORTANT:  This Nulana software is supplied to you by Nulana
 * LTD ("Nulana") in consideration of your agreement to the following
 * terms, and your use, installation, modification or redistribution of
 * this Nulana software constitutes acceptance of these terms.  If you do
 * not agree with these terms, please do not use, install, modify or
 * redistribute this Nulana software.
 *
 * In consideration of your agreement to abide by the following terms, and
 * subject to these terms, Nulana grants you a personal, non-exclusive
 * license, under Nulana's copyrights in this original Nulana software (the
 * "Nulana Software"), to use, reproduce, modify and redistribute the Nulana
 * Software, with or without modifications, in source and/or binary forms;
 * provided that if you redistribute the Nulana Software in its entirety and
 * without modifications, you must retain this notice and the following
 * text and disclaimers in all such redistributions of the Nulana Software.
 * Except as expressly stated in this notice, no other rights or licenses, 
 * express or implied, are granted by Nulana herein, including but not limited 
 * to any patent rights that may be infringed by your derivative works or by other
 * works in which the Nulana Software may be incorporated.
 *
 * The Nulana Software is provided by Nulana on an "AS IS" basis.  NULANA
 * MAKES NO WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 * THE IMPLIED WARRANTIES OF NON-INFRINGEMENT, MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE, REGARDING THE NULANA SOFTWARE OR ITS USE AND
 * OPERATION ALONE OR IN COMBINATION WITH YOUR PRODUCTS.
 *
 * IN NO EVENT SHALL NULANA BE LIABLE FOR ANY SPECIAL, INDIRECT, INCIDENTAL
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) ARISING IN ANY WAY OUT OF THE USE, REPRODUCTION,
 * MODIFICATION AND/OR DISTRIBUTION OF THE NULANA SOFTWARE, HOWEVER CAUSED
 * AND WHETHER UNDER THEORY OF CONTRACT, TORT (INCLUDING NEGLIGENCE),
 * STRICT LIABILITY OR OTHERWISE, EVEN IF NULANA HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright (C) 2017 Nulana LTD. All Rights Reserved.
 */
 
package com.nulana.nchart3d.example.DifferentCharts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.util.LruCache;

import com.nulana.NChart.NChart;
import com.nulana.NChart.NChartAnimationType;
import com.nulana.NChart.NChartBrush;
import com.nulana.NChart.NChartBubbleSeries;
import com.nulana.NChart.NChartDelegate;
import com.nulana.NChart.NChartEventPhase;
import com.nulana.NChart.NChartFont;
import com.nulana.NChart.NChartMargin;
import com.nulana.NChart.NChartMarker;
import com.nulana.NChart.NChartMarkerShape;
import com.nulana.NChart.NChartModel;
import com.nulana.NChart.NChartPoint;
import com.nulana.NChart.NChartPointState;
import com.nulana.NChart.NChartSeries;
import com.nulana.NChart.NChartSeriesDataSource;
import com.nulana.NChart.NChartShadingModel;
import com.nulana.NChart.NChartSolidColorBrush;
import com.nulana.NChart.NChartTimeAxis;
import com.nulana.NChart.NChartTimeAxisDataSource;
import com.nulana.NChart.NChartTimeAxisLabelsLayout;
import com.nulana.NChart.NChartTimeAxisLabelsPosition;
import com.nulana.NChart.NChartTimeAxisTickShape;
import com.nulana.NChart.NChartTimeAxisTooltip;
import com.nulana.NChart.NChartTooltip;
import com.nulana.NChart.NChartView;

import java.io.InputStream;
import java.util.Random;

public class BubbleChartController implements NChartSeriesDataSource ,NChartTimeAxisDataSource,NChartDelegate {
    NChartView mNChartView;
    boolean drawIn3D;
    NChartBrush[][] brushes;


    Random random = new Random();
    NChartModel model;
    Activity c;

    public BubbleChartController(boolean is3D, NChartView view, Activity c) {
        mNChartView = view;
        drawIn3D = is3D;
       this.c=c;
        // Create brushes.
        brushes = new NChartBrush[8][4];
//        brushes[0] = new NChartSolidColorBrush(Color.argb(255, (int) (0.38 * 255), (int) (0.8 * 255), (int) (0.91 * 255)));


//        brushes[1] = new NChartSolidColorBrush(Color.argb(255, (int) (0.8 * 255), (int) (0.86 * 255), (int) (0.22 * 255)));
//        brushes[2] = new NChartSolidColorBrush(Color.argb(255, (int) (0.9 * 255), (int) (0.29 * 255), (int) (0.51 * 255)));

        try {
            InputStream is = c.getAssets().open("model.ply");
            byte[] fileBytes = new byte[is.available()];
            is.read(fileBytes);
            is.close();
            model = new NChartModel(fileBytes);
        }
        catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
    }

    public void updateData() {
        // Switch on antialiasing.
        mNChartView.getChart().setShouldAntialias(true);

        if (drawIn3D) {
            // Switch 3D on.
            mNChartView.getChart().setDrawIn3D(true);
            mNChartView.getChart().getCartesianSystem().setMargin(new NChartMargin(50.0f, 50.0f, 10.0f, 20.0f));
            mNChartView.getChart().getPolarSystem().setMargin(new NChartMargin(50.0f, 50.0f, 10.0f, 20.0f));
        } else {
            mNChartView.getChart().getCartesianSystem().setMargin(new NChartMargin(10.0f, 10.0f, 10.0f, 20.0f));
            mNChartView.getChart().getPolarSystem().setMargin(new NChartMargin(10.0f, 10.0f, 10.0f, 20.0f));
        }

        // Create series that will be displayed on the chart.
        createSeries();

        // Update data in the chart.
        mNChartView.getChart().updateData();
    }

    void createSeries() {
        for (int i = 0; i < 8; ++i) {
            NChartBubbleSeries series = new NChartBubbleSeries();
            series.setDataSource(this);
            series.tag = i;
            mNChartView.getChart().addSeries(series);
        }

        m_timestamps = new String[4];
        for (int i = 0; i < 4; i++) {
            m_timestamps[i] = "No" + i;
        }
        mNChartView.getChart().getCartesianSystem().getXAxis().setHasOffset(false);
        mNChartView.getChart().getCartesianSystem().getYAxis().setHasOffset(false);
        mNChartView.getChart().getCartesianSystem().getZAxis().setHasOffset(false);

        NChart chart = mNChartView.getChart();

        // Time axis settings
        chart.getTimeAxis().setTickColor(Color.argb(255, 111, 111, 111));
        chart.getTimeAxis().setTickShape(NChartTimeAxisTickShape.Line);
        chart.getTimeAxis().setTickTitlesColor(Color.argb(255, 145, 143, 141));
        chart.getTimeAxis().setTickTitlesFont(new NChartFont(11.0f));
        chart.getTimeAxis().setTickTitlesLayout(NChartTimeAxisLabelsLayout.ShowFirstLastLabelsOnly);
        chart.getTimeAxis().setTickTitlesPosition(NChartTimeAxisLabelsPosition.Beneath);
        chart.getTimeAxis().setMargin(new NChartMargin(20.0f, 20.0f, 10.0f, 0.0f));
        chart.getTimeAxis().setAutohideTooltip(false);

        // Time axis tooltip settings
        chart.getTimeAxis().setTooltip(new NChartTimeAxisTooltip());
        chart.getTimeAxis().getTooltip().setMargin(new NChartMargin(0.0f, 0.0f, 2.0f, 0.0f));
        chart.getTimeAxis().getTooltip().setTextColor(Color.argb(255, 145, 143, 141));
        chart.getTimeAxis().getTooltip().setFont(new NChartFont(11.0f));
        chart.getTimeAxis().setDataSource(this);

        chart.getTimeAxis().setImages(null, null, null, null,
                loadBitmap(R.drawable.play_play_sel),
                loadBitmap(R.drawable.play_pushed_light),
                loadBitmap(R.drawable.pause_light),
                loadBitmap(R.drawable.pause_pushed_light),
                loadBitmap(R.drawable.slider_light),
                loadBitmap(R.drawable.handler_light)
        );

    }
    public Bitmap loadBitmap(int resId) {
        final String imageKey = String.valueOf(resId);

        Bitmap bitmap = getBitmapFromMemCache(imageKey);
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(c.getResources(), resId);
            addBitmapToMemoryCache(String.valueOf(resId), bitmap);
        }
        return bitmap;
    }
    LruCache<String, Bitmap> mMemoryCache;

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public NChartPoint[] points(NChartSeries series) {
        // Create points with some data for the series.
        NChartPoint[] result = new NChartPoint[8];


//        NChartPointState state = NChartPointState.PointStateWithXYZ(random.nextInt(10) + 1, random.nextInt(10) + 1, random.nextInt(10) + 1);
//        state.setMarker(new NChartMarker());
//        state.getMarker().setSize(random.nextInt(60) + 20);
//        state.getMarker().setBrush(brushes[series.tag]);
//        state.getMarker().getBrush().setShadingModel(NChartShadingModel.Phong);
////        state.getMarker().setShape(NChartMarkerShape.Sphere);
//        state.getMarker().setModel(model);
//        result.add(new NChartPoint(state, series));
//
        for (int i = 0; i < 8; ++i) {
            NChartPointState[] states = new NChartPointState[4];
            for (int j = 0; j < 4; ++j) {
                brushes[i][j] = new NChartSolidColorBrush(Color.argb(255, (int) (0.38 * 255), (int) (0.8 * 255), (int) (0.91 * 255)));

                states[j] = NChartPointState.PointStateWithXYZ(random.nextInt(10) + i*j, random.nextInt(10) + 7, random.nextInt(10) + 11);

                mNChartView.getChart().setPointSelectionEnabled(true);
                states[j].setMarker(new NChartMarker());
                states[j].getMarker().setSize(random.nextInt(6) + 2 * j);
                states[j].getMarker().setBrush(brushes[i][j]);
                states[j].getMarker().setShape(NChartMarkerShape.Sphere);
                states[j].getMarker().getBrush().setShadingModel(NChartShadingModel.Phong);
            }
            result[i] = new NChartPoint(states, series);
        }




//
        return result;

    }
//
    public NChartPoint[] extraPoints(NChartSeries series) {
        return null;
    }

    public String name(NChartSeries series) {
        return null;
    }

    public Bitmap image(NChartSeries series) {
        return null;
    }
    private String[] m_timestamps;

    @Override
    public String[] timestamps(NChartTimeAxis nChartTimeAxis) {
        return m_timestamps;
    }

    @Override
    public void timeIndexChanged(NChart nChart, double v) {
        NChart chart = mNChartView.getChart();
        if (chart != null) {
            for (NChartSeries series : chart.getSeries()) {
                if (series != null) {
                    for (NChartPoint point : series.getPoints()) {
                        updateTooltipText(point);
                    }
                }
            }
        }
    }
    @SuppressLint("DefaultLocale")
    void updateTooltipText(NChartPoint point) {
        if (point == null || point.getCurrentState() == null || point.getTooltip() == null)
            return;


        NChartTooltip tooltip = point.getTooltip();
        NChartPointState state = point.getCurrentState();
        point.getCurrentState();
//        state.getBrush().
        tooltip.setText(String.format("%s", state.getValue()));



    }
    @Override
    public void pointSelected(NChart nChart, NChartPoint nChartPoint) {

    }

    @Override
    public void didEndAnimating(NChart nChart, Object o, NChartAnimationType nChartAnimationType) {

    }

    @Override
    public void didChangeZoomPhase(NChart nChart, NChartEventPhase nChartEventPhase) {

    }
}
