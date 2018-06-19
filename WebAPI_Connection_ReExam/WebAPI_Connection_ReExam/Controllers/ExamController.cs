using WebAPI_Connection_ReExam.Custom;
using Newtonsoft.Json;
using System;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Web.Http;
using System.Collections.Generic;
using System.Data.SqlClient;

//開発実験用に使っているAPI（従業員一覧を返却するコントローラ）
namespace WebAPI_Connection_ReExam.Controllers
{
    public class ExamController : ApiController
    {
        private DatabaseConnectionHelper helper;
        private string jsonString = "実験用文字列";

        [ActionName("get")]
        public HttpResponseMessage GetString()
        {

            var res = Request.CreateResponse(HttpStatusCode.OK);    //コード200：通信成功

            helper = new DatabaseConnectionHelper(
                "SELECT 従業員コード,従業員名,入社日,従業員種別名 FROM 従業員,従業員種別 " +
                "WHERE 従業員.従業員種別コード = 従業員種別.従業員種別コード;");

            //データをリストに格納するためのやーつ
            int workerId;
            string name;
            DateTime entryDate;
            string workerType;
            List<Worker> worker = new List<Worker>();

            //データベースへ接続、結果を格納
            //最初に空のリストを生成、セット可能とする
            SqlDataReader reader = helper.DatabaseConnect();

            LWorker wk = new LWorker()
            {
                title = "従業員",
                worker = null
            };

            //データのセット
            while (reader.Read())
            {
                workerId = int.Parse(reader.GetValue(0).ToString());
                name = (string)reader.GetValue(1);
                entryDate = DateTime.Parse(reader.GetValue(2).ToString());
                workerType = reader.GetValue(3).ToString();
                worker.Add(new Worker(workerId, name, entryDate, workerType));
            }
            wk.worker = worker;

            reader.Close();
            helper.closeDb();

            jsonString = JsonConvert.SerializeObject(wk);
            //Debug.WriteLine(jsonString);

            //StringBuilderクラスのインスタンス//Content-Typeを指定する
            res.Content = new StringContent(jsonString, Encoding.UTF8, "application/json");  //Content-Typeを指定 

            return res;
        }
    }
}
