using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Web;

namespace WebAPI_Connection_ReExam.Custom
{
    public class LHistory
    {
        [DataMember(Name = "title")]
        public string title { get; set; }

        [DataMember(Name = "history")]
        public List<IHistory> history { get; set; }
    }
}