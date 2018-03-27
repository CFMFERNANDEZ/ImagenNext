package com.epson.moverio.bt300.sample.samplehwkey;

/**
 * Created by CellFusion on 3/26/2018.
 */

public class Metric {

    private String metricCode;
    private double spectedQuantity;
    private double inputMetric;

    public Metric(String code, double spected, double input){
        this.metricCode = code;
        this.spectedQuantity = spected;
        this.inputMetric = input;
    }

    public String getCode(){
        return this.metricCode;
    }

    public double getSpectedQuantity(){
        return this.spectedQuantity;
    }

    public double getInputMetric(){
        return this.inputMetric;
    }
}
