using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Web;
using Newtonsoft.Json;

//IHistoryに使うリストの中身
namespace WebAPI_Connection_ReExam.Custom
{
    [DataContract]
    public class ErrorDetail
    {
        [DataMember(Name = "point")]
        private int point;          //計測ポイント
        [DataMember(Name = "correct")]
        private double correct;     //正寸値
        [DataMember(Name = "measure")]
        private double measure;     //計測値
        [DataMember(Name = "result")]
        private string result;      //判定

        public ErrorDetail(int point,double correct,double measure,string result)
        {
            this.point = point;
            this.correct = correct;
            this.measure = measure;
            this.result = result;
        }
    }
}