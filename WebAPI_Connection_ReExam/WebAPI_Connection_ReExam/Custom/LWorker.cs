using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Web;

namespace WebAPI_Connection_ReExam.Custom
{
    [JsonObject("LWorker")]
    public class LWorker
    {
        [DataMember(Name = "title")]
        public string title { get; set; }

        [DataMember(Name = "worker")]
        public List<Worker> worker { get; set; }
    }
}