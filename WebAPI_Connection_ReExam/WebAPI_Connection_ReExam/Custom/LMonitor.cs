using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Web;
using Newtonsoft.Json;

namespace WebAPI_Connection_ReExam.Custom
{
    [JsonObject("LMonitor")]
    public class LMonitor
    {
        [DataMember(Name = "title")]
        public string title { get; set; }

        [DataMember(Name = "monitor")]
        public List<Monitor> monitor { get; set; }
    }
}