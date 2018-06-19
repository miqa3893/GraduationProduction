package com.example.miqa3893.LineManager;
//TotalLines:51
public class Monitor {
    private float total;
    private float correct;
    private float failure;

    public Monitor() {
    }

    public Monitor(float total, float correct, float failure) {
        super();
        this.total = total;
        this.correct = correct;
        this.failure = failure;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public float getCorrect() {
        return correct;
    }

    public void setCorrect(float correct) {
        this.correct = correct;
    }

    public float getFailure() {
        return failure;
    }

    public void setFailure(float failure) {
        this.failure = failure;
    }
}
