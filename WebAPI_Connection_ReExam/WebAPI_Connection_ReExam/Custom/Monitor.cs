using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using Newtonsoft.Json;

//生産ラインモニタ用
namespace WebAPI_Connection_ReExam.Custom
{
    [JsonObject("monitor")]
    public class Monitor
    {
        [JsonProperty("total")]
        private int total { get; set; }             //生産数合計
        [JsonProperty("correct")]
        private int correct { get; set; }           //良品数
        [JsonProperty("failure")]
        private int failure { get; set; }           //不良品数

        //コンストラクタ
        public Monitor(int total,int correct,int failure)
        {
            this.total = total;
            this.correct = correct;
            this.failure = failure;
        }
    }
}