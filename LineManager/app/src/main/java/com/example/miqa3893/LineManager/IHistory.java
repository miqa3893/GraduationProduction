package com.example.miqa3893.LineManager;

import java.util.List;
//TotalLines:41
public class IHistory {
    //基本データ
    private String serial;
    private String date;
    private String name;        //加工担当者
    private int machineId;      //加工機番号
    private List<MeasureData> list;     //計測詳細データ

    public IHistory(String serial, String date, String name, int machineId,List<MeasureData> list) {
        this.serial = serial;
        this.date = date;
        this.name = name;
        this.machineId = machineId;
        this.list = list;
    }

    public String getSerial() {
        return serial;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public int getMachineId() {
        return machineId;
    }

    public List<MeasureData> getList() {
        return list;
    }
}
