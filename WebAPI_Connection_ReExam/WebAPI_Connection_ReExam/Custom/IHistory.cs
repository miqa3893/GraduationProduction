using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Web;
using Newtonsoft.Json;

//LHistoryとは役目が違うので変数定義にご注意を
namespace WebAPI_Connection_ReExam.Custom
{
    [DataContract]
    public class IHistory
    {
        [DataMember(Name = "serial")]
        private string serial;      //シリアルNo.
        [DataMember(Name = "date")]
        private string date;        //検査日時
        [DataMember(Name = "name")]
        private string name;        //加工者
        [DataMember(Name = "machine")]
        private int machine;        //加工機ID
        [DataMember(Name = "measures")]
        private List<ErrorDetail> list; //エラー検査履歴

        public IHistory(string serial,string date,string name,int machine,List<ErrorDetail> list)
        {
            this.serial = serial;
            this.date = date;
            this.name = name;
            this.machine = machine;
            this.list = list;
        }
    }
}