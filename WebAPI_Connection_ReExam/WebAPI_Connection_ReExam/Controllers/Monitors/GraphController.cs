using System;
using System.Windows.Forms.DataVisualization.Charting;
using System.Diagnostics;
using System.IO;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Web.Http;
using System.Drawing;
using System.Drawing.Imaging;
using WebAPI_Connection_ReExam.Custom;

//画像（グラフ）を排出するWeb API
namespace WebAPI_Connection_ReExam.Controllers
{
    public class GraphController : ApiController
    {
        private DatabaseConnectionHelper helper;
        private string jsonString = "";
        //円グラフ描画用
        private const string IMAGE_FOLDER_PATH = @"C:\Users\mikul\Documents\visual studio 2017\Projects\WebAPI_Connection_ReExam\WebAPI_Connection_ReExam\images\";
        private const int PIE_START = 0;        //描画始点
        private const int PIE_END = 800;        //描画終点
        private const int FONT_SIZE_LABEL = 64;  //ラベルフォントサイズ
        private const int FONT_SIZE_TEXT = 44;   //凡例フォントサイズ
        private const string STRING_CORRECT = "良品";
        private const string STRING_FAILURE = "不良品";

        [ActionName("get")]
        public HttpResponseMessage GetImage(int year,int month,int day,string item)
        {
 
            var res = Request.CreateResponse(HttpStatusCode.OK);    //コード200：通信成功
            res.Content = new StringContent("グラフは準備中", Encoding.UTF8, "application/json");  //Content-Typeを指定 
            string dateStr = year + "-" + month + "-" + day;

            try
            {
                switch (item)
                {
                    //裏仕様：駅メモなミクさんが登場するよ！
                    case "miku":
                        var stream = new FileStream(IMAGE_FOLDER_PATH + "img00.png", FileMode.Open);
                        var imgres = new HttpResponseMessage()
                        {
                            Content = new StreamContent(stream),
                            StatusCode = System.Net.HttpStatusCode.OK
                        };

                        imgres.Content.Headers.ContentType
                            = new System.Net.Http.Headers.MediaTypeHeaderValue("image/png");
                        return imgres;

                    default:
                        //グラフ描画に成功したら
                        string sqlCommand = dateStr;           //SQLコマンド
                        if (myGraphFactory(400, 7))
                        {
                            //グラフを実際にデータとして送信
                            var str = new FileStream(IMAGE_FOLDER_PATH + "graph.png", FileMode.Open);
                            var img = new HttpResponseMessage()
                            {
                                Content = new StreamContent(str),
                                StatusCode = System.Net.HttpStatusCode.OK
                            };

                            img.Content.Headers.ContentType
                                = new System.Net.Http.Headers.MediaTypeHeaderValue("image/png");
                            return img;
                        }
                        break;


                }
            }
            catch(Exception e)
            {
                Debug.WriteLine(e.ToString());
            }

            return res;
        }

        //<summary>
        // 円グラフを生成します
        //</summary>
        //<param name = "total"> 生産数（合計）</param>
        //<param name = "falture"> 不良品数 </param>
        private Boolean myGraphFactory(int total, int falture)
        {
            //生産数 - 不良数 から 良品数を算出
            int correct = total - falture;

            //円グラフを塗りつぶす際の色と角度設定
            SolidBrush cBrush = new SolidBrush(Color.SkyBlue);      //円グラフの「良品数」
            SolidBrush fBrush = new SolidBrush(Color.Tomato);      //円グラフの「不良品数」
            float cAngle, fAngle;

            //割合を算出する
            double cRate, fRate;
            cRate = (double)correct / (double)total;
            fRate = 1.0 - cRate;

            //良品数÷合計数 * 360 度 で良品数と不良品数の円グラフ角度を算出
            cAngle = (int)(correct * 360 / total);
            fAngle = 360 - cAngle;

            //予め用意された「graph.png」を描画キャンパスとする
            //オープン状態で書き込みができない状態を防止するため、FileStreamに取り込んで加工する
            FileStream fs = new FileStream(IMAGE_FOLDER_PATH + "graph.png",FileMode.OpenOrCreate);
            Image img = Image.FromStream(fs);
            fs.Close();

            Graphics graph = Graphics.FromImage(img);
            graph.SmoothingMode = System.Drawing.Drawing2D.SmoothingMode.AntiAlias;             //アンチエイリアスを指定する
            graph.Clear(Color.White);               //キャンバス背景を白にする(一旦初期化)

            //縁取り用文字オブジェクト
            System.Drawing.Drawing2D.GraphicsPath corTGp = new System.Drawing.Drawing2D.GraphicsPath();
            System.Drawing.Drawing2D.GraphicsPath corLGp = new System.Drawing.Drawing2D.GraphicsPath();
            System.Drawing.Drawing2D.GraphicsPath falTGp = new System.Drawing.Drawing2D.GraphicsPath();
            System.Drawing.Drawing2D.GraphicsPath falLGp = new System.Drawing.Drawing2D.GraphicsPath();
    
            //文字フォント追加
            FontFamily tff = new FontFamily("01フロップデザイン");
            FontFamily lff = new FontFamily("Century Gothic");

            graph.FillPie(cBrush, PIE_START, PIE_START, PIE_END, PIE_END, -90, cAngle);             //グラフ描画
            float labelFontSize = FONT_SIZE_LABEL * graph.DpiY / 72;                                //文字サイズをポイント単位にする
            float textFontSize = FONT_SIZE_TEXT * graph.DpiY / 72;
            //文字の追加
            corTGp.AddString(STRING_CORRECT,tff,(int)FontStyle.Bold,textFontSize,new Point(460,530),StringFormat.GenericDefault);
            corLGp.AddString(cRate.ToString("#.00%"), lff, (int)FontStyle.Bold, labelFontSize, new Point(460, 590), StringFormat.GenericDefault);
            graph.FillPath(Brushes.CadetBlue, corTGp);              //文字内の着色
            graph.FillPath(Brushes.CadetBlue, corLGp);
            graph.DrawPath(Pens.Gray, corTGp);                      //文字の縁取り
            graph.DrawPath(Pens.Gray, corLGp);
            //以下同じ
            graph.FillPie(fBrush, PIE_START, PIE_START, PIE_END, PIE_END, -90 + cAngle, fAngle);
            falTGp.AddString(STRING_FAILURE, tff, (int)FontStyle.Bold, textFontSize, new Point(100, 130), StringFormat.GenericDefault);
            falLGp.AddString(fRate.ToString("#.00%"), lff, (int)FontStyle.Bold,labelFontSize, new Point(100, 190), StringFormat.GenericDefault);
            graph.FillPath(Brushes.OrangeRed, falTGp);
            graph.FillPath(Brushes.OrangeRed, falLGp);
            graph.DrawPath(Pens.Gray, falTGp);
            graph.DrawPath(Pens.Gray, falLGp);

            //画像としてグラフを保存
            try
            {
                img.Save(IMAGE_FOLDER_PATH + "graph.png");
            }
            catch (Exception e)
            {
                Debug.WriteLine(e.ToString());
                return false;
            }

            //メモリ解放
            img.Dispose();
            graph.Dispose();

            return true;
        }
    }
}
