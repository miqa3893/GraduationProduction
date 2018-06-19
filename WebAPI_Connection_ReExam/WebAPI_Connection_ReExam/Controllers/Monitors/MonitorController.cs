using Newtonsoft.Json;
using System;
using System.Diagnostics;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Web.Http;
using WebAPI_Connection_ReExam.Custom;
using System.Data.SqlClient;
using System.Collections.Generic;

namespace WebAPI_Connection_ReExam.Controllers
{
    public class MonitorController : ApiController
    {
        private DatabaseConnectionHelper helper;
        string jsonString,sqlCommand;

        //部品一覧を返却する
        [ActionName("get")]
        public HttpResponseMessage GetData(int year,int month,int day,string item)
        {
            int measurePoints = 0;
            string dateStr = year + "-" + month + "-" + day;
            var res = Request.CreateResponse(HttpStatusCode.OK);    //通信成功

            //部品名指定…その部品の検査状況を返却するSQL発行
            //部品名未指定…全部品の検査状況の（ry
            if(item == "all")
            {
                sqlCommand = "select count(*) AS 生産数," +
                    "count(case when 判定 = 1 then 判定 else null end) AS 良品," +
                    "count(case when 判定 = 0 then 判定 else null end) AS 不良品 " +
                    "FROM 計測詳細 " +
                    "WHERE 計測日時 BETWEEN '" + dateStr + " 00:00:00' AND '" + dateStr + " 23:59:59';";
            }
            else
            {
                measurePoints = HistoryController.getMeasureCount(item);

                sqlCommand = "select count(*) AS 生産数," +
                    "count(case when 判定 = 1 then 判定 else null end) AS 良品," +
                    "count(case when 判定 = 0 then 判定 else null end) AS 不良品 " +
                    "FROM 計測詳細 " +
                    "WHERE 型番 = '" + item + "' " +
                    "AND 計測日時 BETWEEN '" + dateStr + " 00:00:00' AND '" + dateStr + " 23:59:59';";
            }

            helper = new DatabaseConnectionHelper(sqlCommand);

            int total, correct, failure;    //検査数・良品・不良品
            List<Monitor> monitor = new List<Monitor>();

            //データベースへ接続、結果を格納
            SqlDataReader reader = helper.DatabaseConnect();

            //データの取得に失敗した場合
            if (reader == null)
            {
                res = Request.CreateResponse(HttpStatusCode.NotFound);
                jsonString = "error";
                res.Content = new StringContent(jsonString, Encoding.UTF8, "application/json");
                return res;
            }

            LMonitor wk = new LMonitor()
            {
                title = "検査状況",
                monitor = null
            };

            //データのセット
            while (reader.Read())
            {
                total = Int32.Parse(reader.GetValue(0).ToString());
                correct = Int32.Parse(reader.GetValue(1).ToString());
                failure = Int32.Parse(reader.GetValue(2).ToString());
                if (item != "all")
                {
                    monitor.Add(new Monitor(total/measurePoints, correct/measurePoints, failur/measurePoints));
                }
                else
                {
                    monitor.Add(new Monitor(total, correct, failure));
                }                
            }
            wk.monitor = monitor;

            reader.Close();
            helper.closeDb();

            jsonString = JsonConvert.SerializeObject(wk);
            Debug.WriteLine(jsonString);

            res.Content = new StringContent(jsonString, Encoding.UTF8, "application/json");

            //res = monitorDebugData(res);
            return res;
        }

        //private void randData()
        //{
        //    //乱数を生成し、擬似的なデータを送信する
        //    Random rand = new System.Random();
        //    int temp = rand.Next(1,25);
        //    int w = rand.Next(4);
        //    count += temp;
        //    correct += temp - w;
        //    failure += w;
        //}


        ////開発テスト用
        ////テスト運用用なので、データベースへの接続はしません
        //private HttpResponseMessage monitorDebugData(HttpResponseMessage res) 
        //{
        //    Monitor monitor;
        //    randData();
        //    monitor = new Monitor(count, correct, failure, (double)failure*100/count);
        //    string str = JsonConvert.SerializeObject(monitor);
        //    res.Content = new StringContent(str, Encoding.UTF8, "application/json");
        //    Debug.WriteLine(str);
        //    return res;
        //}
    }
}
