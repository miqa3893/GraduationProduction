using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Diagnostics;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Web.Http;
using WebAPI_Connection_ReExam.Custom;

namespace WebAPI_Connection_ReExam.Controllers
{
    public class ItemController : ApiController
    {
        private DatabaseConnectionHelper helper;
        string jsonString;

        //部品一覧を返却する
        [ActionName("get")]
        public HttpResponseMessage GetItem()
        {
            var res = Request.CreateResponse(HttpStatusCode.OK);    //通信成功
            helper = new DatabaseConnectionHelper("SELECT DISTINCT 型番,部品名 FROM 型式;");    //製品一覧を引っ張ってくるSQL

            //データをリストに格納するためのやーつ
            string id;
            string name;
            List<Item> item = new List<Item>();

            //データベースへ接続、結果を格納
            SqlDataReader reader = helper.DatabaseConnect();

            //データの取得に失敗した場合
            if (reader == null) {
                res = Request.CreateResponse(HttpStatusCode.NotFound);
                jsonString = "error";
                res.Content = new StringContent(jsonString, Encoding.UTF8, "application/json");  
                return res;
            }

            //ExamControllerを参照のこと
            LItem wk = new LItem()
            {
                title = "製品一覧",
                item = null
            };

            //データのセット
            while (reader.Read())
            {
                id = (string)reader.GetValue(0);
                name = (string)reader.GetValue(1);
                item.Add(new Item(id,name));
            }
            wk.item = item;

            reader.Close();
            helper.closeDb();

            jsonString = JsonConvert.SerializeObject(wk);
            Debug.WriteLine(jsonString);

            res.Content = new StringContent(jsonString, Encoding.UTF8, "application/json");  //Content-Typeを指定 
            return res;
        }
    }
}
