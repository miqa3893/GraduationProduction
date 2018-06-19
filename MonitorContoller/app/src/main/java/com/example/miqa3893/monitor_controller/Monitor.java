package com.example.miqa3893.monitor_controller;

public class Monitor {
    private double total;
    private double correct;
    private double failure;

    public Monitor() {
    }

    public Monitor(double total, double correct, double failure) {
        this.total = total;
        this.correct = correct;
        this.failure = failure;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getCorrect() {
        return correct;
    }

    public void setCorrect(double correct) {
        this.correct = correct;
    }

    public double getFailure() {
        return failure;
    }

    public void setFailure(double failure) {
        this.failure = failure;
    }
}
