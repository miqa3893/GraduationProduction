using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Web;

namespace WebAPI_Connection_ReExam.Custom
{
    [JsonObject("Litem")]
    public class LItem
    {
        [DataMember(Name = "title")]
        public string title { get; set; }

        [DataMember(Name = "item")]
        public List<Item> item { get; set; }
    }
}