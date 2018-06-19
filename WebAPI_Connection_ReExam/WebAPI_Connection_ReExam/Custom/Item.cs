using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;


namespace WebAPI_Connection_ReExam.Custom
{
    [JsonObject("item")]
    public class Item
    {
        [JsonProperty("id")]
        private string id { get; set; }            //型番

        [JsonProperty("name")]
        private string name { get; set; }            //型番名

        public Item(string id,string name)
        {
            this.id = id;
            this.name = name;
        }
    }
}