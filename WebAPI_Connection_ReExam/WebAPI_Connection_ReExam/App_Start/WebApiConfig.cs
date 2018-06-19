using System;
using System.Collections.Generic;
using System.Linq;
using System.Web.Http;

/*九州職業能力開発大学校　生産電子情報システム技術科　開発課題テーマ
 * 三次元検査システムの開発（さんじげんちほー）
 * Author   ：@miqa3893（すーみん）
 * UpDate   ：2017-12-01
 * Version  ：1.05β
 */

namespace WebAPI_Connection_ReExam
{
    public static class WebApiConfig
    {
        public static void Register(HttpConfiguration config)
        {
            //アクセス用のアドレス：
            //学内    ：http://10.111.1.39:3939/
            //ルータ   ：http://192.168.179.4:39390/

            // Web API ルート
            config.MapHttpAttributeRoutes();

            //実験用コントローラ
            config.Routes.MapHttpRoute(
                name: "SQLExamApi",
                routeTemplate: "api/{controller}/{action}",
                defaults: new { controller = "exam", action = "get"}
            );

            //検査履歴（管理者端末用）
            config.Routes.MapHttpRoute(
                name: "HistoryApi",
                routeTemplate: "api/{controller}/{action}/{year}-{month}-{day}/{item}",
                defaults: new { controller = "history"}
            );

            //検査履歴（管理者端末用）
            config.Routes.MapHttpRoute(
                name: "DeleteApi",
                routeTemplate: "api/{controller}/{action}/{year}-{month}-{day}/{item}",
                defaults: new { controller = "delete",action = "delete" }
            );

            //管理者端末用ホーム画面
            config.Routes.MapHttpRoute(
                name: "ItemListApi",
                routeTemplate: "api/{controller}/{action}",
                defaults: new { controller = "item", action = "get"}
            );

            //ミクさんマジ天使！！
            config.Routes.MapHttpRoute(
                name: "DispMikuApi",
                routeTemplate: "api/{controller}/{action}/{id}",
                defaults: new { controller = "miku", action = "get", id = 0 }
            );

            //生産ラインモニタ用
            config.Routes.MapHttpRoute(
                name: "DispMonitorApi",
                routeTemplate: "api/{controller}/{action}/{year}-{month}-{day}/{item}",
                defaults: new { controller = "monitor", action = "get"}
            );

            //生産ラインモニタ用(グラフ),id = 39 とすると実験用
            config.Routes.MapHttpRoute(
                name: "GraphApi",
                routeTemplate: "api/{controller}/{action}/{date}",
                defaults: new { controller = "graph", action = "get" , date = 0}
            );

        }
    }
}
