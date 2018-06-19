using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace WebAPI_Connection_ReExam.Custom
{
    //Android側からのGETリクエストを受ける独自クラス
    public class RequestValue
    {
        private string workName { get; set; }   //@param1 部品名
        private int count { get; set; }         //@param2 必要結果数

    }
}