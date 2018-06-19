using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

//検査NGポイントを返却する
namespace WebAPI_Connection_ReExam.Custom
{
    [JsonObject("History")]
    public class History
    {
        [JsonProperty("date")]
        private string date { get; set; }     //検査日時
        [JsonProperty("serial")]
        private string serial { get; set; }     //シリアルNo
        [JsonProperty("point")]
        private int point { get; set; }     //計測ポイント
        [JsonProperty("name")]
        private string name { get; set; }      //加工者
        [JsonProperty("machine")]
        private int machine { get; set; }      //加工機
        [JsonProperty("correct")]
        private double correct { get; set; }      //正寸値
        [JsonProperty("failure")]
        private double failure { get; set; }      //計測値


        public History(String date,String serial,int point,string name,int machine,double correct,double failure)
        {
            this.date = date;
            this.serial = serial;
            this.point = point;
            this.name = name;
            this.machine = machine;
            this.correct = correct;
            this.failure = failure;
        }

    }
}