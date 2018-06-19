#define OLIVE_SERVER

using System;
using System.Collections.Generic;
using System.Data;
using System.Data.SqlClient;
using System.Diagnostics;
using System.Linq;
using System.Web;

namespace WebAPI_Connection_ReExam.Custom
{
    //データベース連携を行うクラス
    public class DatabaseConnectionHelper 
    {
        //ヘッダに付加する「Content-Type」の指定
        public const string CONTENT_TYPE_JSON = "application/json";       //json
        public const string CONTENT_TYPE_BITMAP = "image/x-bmp";          //bmp
        public const string CONTENT_TYPE_JPEG = "image/jpeg";             //jpg

        //SQL Serverへの接続文字列(パラメータ詳細はミニノート参照のこと)
        //Data Sourceは、サーバ名を直接コピペすると間違いないはず
        //かならずこの文字列の頭には@をつけること（'\'が誤認識される）
        //主要データベースサーバ（olive\sqlexpress）
        // #define OLIVE_SERVER　を付けると、この文字列が切り替わる
#if OLIVE_SERVER
        private static string OLIVE_CONNECTION_STRING =
            @"Data Source=OLIVE;" +
            "Initial Catalog=3Dsystem;" +
            "User Id=sa;" +
            "Password=password!;";
#else
        private static string MIQA3893_CONNECTION_STRING =
            @"Data Source=MIQA3893-MAIN\SQLEXPRESS;" +
            "Initial Catalog=3Dsystem;" +
            "User Id=sa;" +
            "Password=sql_mikuln;";
#endif



        private SqlConnection cn = new SqlConnection();
        private SqlCommand command = new SqlCommand();
        private SqlDataAdapter adapter = new SqlDataAdapter();


        //SQL文を格納する
        private String SqlString { get; set; }
        public string getSqlString()
        {
            return this.SqlString;
        }

        public void setSqlString(string str)
        {
            this.SqlString = str ;
        }

        //コンストラクタ（なにもしない）
        public DatabaseConnectionHelper()
        {
            return;
        }

        //コンストラクタ（SQL文を取得して格納する）
        public DatabaseConnectionHelper(String str)
        {
            this.SqlString = str;
            return;
        }

        //与えられたSQL文でデータベースに接続し、データを返却する（SELECT専用）
        public SqlDataReader DatabaseConnect()
        {
            DataSet data = new DataSet();

            //ミニノート 13ページを参照
#if OLIVE_SERVER
            cn.ConnectionString = OLIVE_CONNECTION_STRING;
#else
            cn.ConnectionString = MIQA3893_CONNECTION_STRING;
#endif
            command.Connection = cn;
            command.CommandText = SqlString;
            try
            {
                cn.Open();
            }
            catch(Exception e)
            {
                Debug.WriteLine(e.ToString());
                return null;
            }

            //Readerに問い合わせ結果を格納
            var reader = command.ExecuteReader();

            //データベースサーバから返却された生データを返却
            return reader;
        }

        //与えられたSQL文でデータベースに接続し、データを返却する（SELECT専用）
        public bool DatabaseConnect(string cmd)
        {
            DataSet data = new DataSet();

            //ミニノート 13ページを参照
#if OLIVE_SERVER
            cn.ConnectionString = OLIVE_CONNECTION_STRING;
#else
            cn.ConnectionString = MIQA3893_CONNECTION_STRING;
#endif
            //コマンドを指定
            command = cn.CreateCommand();
            command.CommandText = SqlString;
            try
            {
                cn.Open();
                command.ExecuteNonQuery();
            }
            catch (Exception e)
            {
                Debug.WriteLine(e.ToString());
                return false;
            }

            //成功したらtrueを返す
            return true;
        }

        //データベースへの接続を閉じます
        public void closeDb()
        {
            try
            {
                cn.Close();
            }
            catch (Exception)
            {
                //return false;
            }

            //return true;
        }
    }
}