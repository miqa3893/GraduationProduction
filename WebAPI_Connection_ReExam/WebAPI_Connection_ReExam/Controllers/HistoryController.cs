using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Diagnostics;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Web.Http;
using WebAPI_Connection_ReExam.Custom;

//検査履歴を返却するコントローラ
namespace WebAPI_Connection_ReExam.Controllers
{
    public class HistoryController : ApiController
    {
        private string jsonString = "";

        [ActionName("get")]
        public HttpResponseMessage GetString(int year,int month,int day,string item)
        {
            bool isExistData = false;
            bool isEndOfData = false;

            var res = Request.CreateResponse(HttpStatusCode.OK);    //コード200：通信成功
            //jsonString = "Date>>>" + year + "/" + month + "/" + day + "\nItem>>>" + item;
            String dateParam = year + "-" + month + "-" + day.ToString("00");
            String mainSql =        //これに実際の検査記録を引っ張ってくるSQLを格納している
                "SELECT 計測日時,シリアルNo,従業員名,加工機.加工機ID,計測ポイントID,正寸値.正寸値,計測詳細.計測値,計測詳細.判定 " +
                "FROM 正寸値,計測詳細,型式,加工機,従業員,製造情報 " +
                "WHERE 製造情報.型番 = 計測詳細.型番 " +
                "AND 製造情報.製造順No = 計測詳細.製造順No " +
                "AND 製造情報.従業員コード = 従業員.従業員コード " +
                "AND 計測詳細.ロット位置 = 製造情報.ロットNo " +
                "AND 製造情報.加工機ID = 加工機.加工機ID " +
                "AND 計測ポイント = 正寸値.計測ポイントID " +
                "AND 型式.型番 = 計測詳細.型番 " +
                "AND 計測詳細.型番 = 正寸値.型番 " +
                "AND 計測詳細.型番 = '" + item + "' " +
                "AND 計測日時 BETWEEN '" + dateParam + " 00:00:00 ' AND '" + dateParam + " 23:59:59' " +
                "ORDER BY 計測日時 ASC , シリアルNo ASC ,計測ポイントID ASC; ";
            //offset...は特定の範囲のデータのみを取得する「SQL Server」専用のSQL文

            DatabaseConnectionHelper helper = new DatabaseConnectionHelper(mainSql);

            //計測ポイント数
            int itemLen = getMeasureCount(item);

            //データをリストに格納するためのやーつ
            List<IHistory> iHistory = new List<IHistory>();
            DateTime date;
            string serial="",name="",dateStr=null;
            int point,machineId=0;
            double correct, measure;
            string result;
           
            //データベースへ接続、結果を格納
            SqlDataReader reader = helper.DatabaseConnect();

            //データのセット
            do
            {
                List<ErrorDetail> errList = new List<ErrorDetail>();

                for (int i = 0; i < itemLen; i++)
                {
                    dateStr = null;
                    if (reader.Read())
                    {
                        //IHistoryに入れるフィールドを先に取得する
                        date = (DateTime)reader.GetValue(0);
                        dateStr = date.ToString("yyyy-MM-dd HH:mm:ss");
                        serial = reader.GetValue(1).ToString();
                        name = reader.GetValue(2).ToString();
                        machineId = int.Parse(reader.GetValue(3).ToString());

                        //IHistoryのListに入れるErrorDetailを取得し格納
                        point = int.Parse(reader.GetValue(4).ToString());
                        correct = double.Parse(reader.GetValue(5).ToString());
                        measure = double.Parse(reader.GetValue(6).ToString());
                        bool isCorrect = (bool)reader.GetValue(7);
                        if (!isCorrect) result = "NG";
                        else result = "OK";
                        errList.Add(new ErrorDetail(point, correct, measure, result));
                    }
                    //計測ポイント数に到達せず、データが終了したらそこまでのデータを取得し格納する
                    else
                    {
                        //レコードが何か一つでもあったら
                        if (!(dateStr==null))
                        {
                            iHistory.Add(new IHistory(serial, dateStr, name, machineId, errList));
                            isExistData = true;
                            isEndOfData = true;
                        }
                        //データが1つもないなら
                        else
                        {
                            isEndOfData = true;
                            isExistData = false;
                        }
                        break;      //forを抜ける
                    }
                }//for

                //データ終了でない場合は（全計測ポイントを収集できたら）
                if (!isEndOfData)
                {
                    try
                    {
                        iHistory.Add(new IHistory(serial, dateStr, name, machineId, errList));
                    }catch(OutOfMemoryException e)
                    {
                        iHistory = null;
                        errList = null;
                        GC.Collect();
                        isExistData = false;
                        break;
                    }
                    isExistData = true;
                }
                   
                //データの終了フラグが立っていたらループを抜ける
            } while (!isEndOfData);

            LHistory wk = new LHistory()
            {
                title = "検査履歴",
                history = iHistory
            };

            reader.Close();
            helper.closeDb();
            jsonString = JsonConvert.SerializeObject(wk);
            Debug.WriteLine(jsonString); 

            //StringBuilderクラスのインスタンス//Content-Typeを指定する
            res.Content = new StringContent(jsonString, Encoding.UTF8, "application/json");  //Content-Typeを指定 

            GC.Collect();
            return res;
        }

        //計測ポイント数を返却するメソッド
        public static int getMeasureCount(string item)
        {
            DatabaseConnectionHelper helper = new DatabaseConnectionHelper(
                "SELECT 計測ポイント数 FROM 型式 " +
                "WHERE 型番 = '" + item + "';");
            //製品の計測ポイント数…繰り返し数を引っ張る
            //読み込めなかった場合は0を返す
            SqlDataReader itemLenRead = helper.DatabaseConnect();
            if (!itemLenRead.Read()) return 0;
            else return int.Parse(itemLenRead.GetValue(0).ToString());
        }
    } 
}