package com.example.rn_ble;

import java.text.DecimalFormat;

/**
 * Created by admin on 2017/5/8.
 */

public class MeasuringDistance {
    public MeasuringDistance() {
    }

    public static double calculateAccuracy(int mPower, double rssi) {
        try {
            double ex = 0.0D;
            if(rssi == 0.0D) {
                ex = -1.0D;
            }

            double ratio = rssi * 1.0D / (double)mPower;
            if(ratio < 1.0D) {
                ex = Math.pow(ratio, 10.0D);
            } else {
                double df = 0.89976D * Math.pow(ratio, 7.7095D) + 0.111D;
                ex = df;
            }

            DecimalFormat df1 = new DecimalFormat("#.00");
            return Double.valueOf(df1.format(ex)).doubleValue();
        } catch (Exception var9) {
            return -1.0D;
        }
    }
}
