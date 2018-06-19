package com.example.miqa3893.LineManager;
//TotalLines:49
//IHistoryの中身に入れるデータ
public class MeasureData {
    private int point;      //計測ポイント
    private double correct;     //正寸値
    private double measure;     //計測値
    private String result;      //計測結果

    public MeasureData(int point, double correct, double measure, String result) {
        this.point = point;
        this.correct = correct;
        this.measure = measure;
        this.result = result;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public double getCorrect() {
        return correct;
    }

    public void setCorrect(double correct) {
        this.correct = correct;
    }

    public double getMeasure() {
        return measure;
    }

    public void setMeasure(double measure) {
        this.measure = measure;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
