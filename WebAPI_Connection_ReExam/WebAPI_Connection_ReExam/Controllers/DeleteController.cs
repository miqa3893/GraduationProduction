using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using WebAPI_Connection_ReExam.Custom;

namespace WebAPI_Connection_ReExam.Controllers
{
    public class DeleteController : ApiController
    {
        [ActionName("drop")]
        public HttpResponseMessage GetDatabase(int year, int month, int day, string item)
        {
            string jsonString = null;
            var res = Request.CreateResponse(HttpStatusCode.OK);    //コード200：通信成功        
            String dateParam = year + "-" + month + "-" + day.ToString("00");
            DatabaseConnectionHelper helper = new DatabaseConnectionHelper();
            //itemがallであるなら、全消去
            if(item == "all")
            {
                helper.setSqlString("DELETE FROM 計測詳細;");
                if (helper.DatabaseConnect("delete"))
                {
                    jsonString = "successed.";
                    res.Content = new StringContent(jsonString, System.Text.Encoding.UTF8, "application/json");
                    Debug.WriteLine("Data deleted by Android.");
                    return res;
                }
                else
                {
                    jsonString = "failure.";
                    res = Request.CreateResponse(HttpStatusCode.BadRequest);    //コード400：削除失敗
                    res.Content = new StringContent(jsonString, System.Text.Encoding.UTF8, "application/json");
                    return res;
                }
            }

            //リクエストが0000-00-00であったら、itemを全消去
            if ((year + month + day) == 0)
            {
                helper.setSqlString("DELETE FROM 計測詳細 WHERE 型番 = '" + item +"';");
                if (helper.DatabaseConnect("delete"))
                {
                    jsonString = "successed.";
                }
                else
                {
                    jsonString = "failure.";
                    res = Request.CreateResponse(HttpStatusCode.BadRequest);    //コード400：削除失敗
                }
            }
            //全消しではない場合
            else
            {
                helper.setSqlString("DELETE FROM 計測詳細 " +
                    "WHERE 計測日時 BETWEEN '" + dateParam + " 00:00:00 ' AND '" + dateParam + " 23:59:59'　" +
                    "AND 型番 = '" + item + "';");
                if (helper.DatabaseConnect("delete"))
                {
                    jsonString = "successed.";
                }
                else
                {
                    jsonString = "failure.";
                    res = Request.CreateResponse(HttpStatusCode.BadRequest);    //コード400：削除失敗
                }
            }

            //StringBuilderクラスのインスタンス//Content-Typeを指定する
            res.Content = new StringContent(jsonString, System.Text.Encoding.UTF8, "application/json");  //Content-Typeを指定 
            return res;
        }
    }
}
