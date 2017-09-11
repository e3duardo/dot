package com.magicbox.dot.adapter;

import com.robinhood.spark.SparkAdapter;

/**
 * Criado por eduardo em 11/09/17.
 */

public class MinhaSemanaAdapter extends SparkAdapter {

    private float[] yData;

    public MinhaSemanaAdapter(float[] yData) {
        this.yData = yData;
    }

    @Override
    public int getCount() {
        return yData.length;
    }

    @Override
    public Object getItem(int index) {
        return yData[index];
    }

    @Override
    public float getY(int index) {
        return yData[index];
    }

}
