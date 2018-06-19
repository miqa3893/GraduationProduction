using System.Web;
using System.Web.Mvc;

namespace WebAPI_Connection_ReExam
{
    public class FilterConfig
    {
        public static void RegisterGlobalFilters(GlobalFilterCollection filters)
        {
            filters.Add(new HandleErrorAttribute());
        }
    }
}
