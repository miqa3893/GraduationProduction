using System;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Web.Http;

//実験用で作ったコントローラだけど、完全にお遊び用
namespace WebAPI_Connection_ReExam.Controllers
{
    public class MikuController : ApiController
    {

        [ActionName("get")]
        public HttpResponseMessage getString(int id) {

            var jsonString = "愛が足りない！";
            var res = Request.CreateResponse(HttpStatusCode.OK);    //コード200：通信成功

            if((id == 39)||(id == 3939)){
                jsonString = "☆ミクさんマジ天使！！！☆";
            }

            //StringBuilderクラスのインスタンス//Content-Typeを指定する
            res.Content = new StringContent(jsonString, Encoding.UTF8, "application/json");  //Content-Typeを指定 

            return res;
        }
    }
}
