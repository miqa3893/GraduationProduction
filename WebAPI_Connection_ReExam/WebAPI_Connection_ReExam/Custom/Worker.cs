using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Web;

namespace WebAPI_Connection_ReExam.Custom
{
    //データベース問い合わせからの受け取り（従業員）
    [JsonObject("worker")]
    public class Worker
    {

        [JsonProperty("id")]
        private int workerId { get; set; }           //従業員コード
        [JsonProperty("name")]
        private string name { get; set; }            //従業員名
        [JsonProperty("date")]
        private DateTime entryDate { get; set; }     //入社日
        [JsonProperty("type")]
        private string workerType { get; set; }      //従業員種別

        public Worker(int workerId,string name,DateTime entryDate, string workerType)
        {
            this.workerId = workerId;
            this.name = name;
            this.entryDate = entryDate;
            this.workerType = workerType;
        }

    }
}