package rpc;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;
import external.TicketMasterAPI;

/**
 * Servlet implementation class SearchItem
 * Servlet 对应的url 
 */
@WebServlet("/search")
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * tomcat里所有东西都是server一部分
	 * servlet: SearchItem 是其中一个功能而已
	 * 这个功能是通过接受request 返回response
	 * write帮你往request里面加内容的一个变量
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * 给servlet发的是个get 请求，会执行这个method
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//(1) 列子
		//PrintWriter out = response.getWriter();
		//判断request里面有没有叫username的参数 
		//if (request.getParameter("username") != null) {
			//把username取出来
			//String username = request.getParameter("username");
			//打印到response里面去
			//out.print("Hello " + username);
		//}
		//close writer
		//out.close();
		
		//(2)列子
		//告诉浏览器我返回的是html的格式
		//response.setContentType("text/html");
		
		//PrintWriter out = response.getWriter();
		//out.println("<html><body>");
		//out.println("<h1>This is a HTML page</h1>");
		//out.println("</body></html>");
		
		//out.close();
		
		//json 返回json 格式resource 
		//response.setContentType("application/json");
		//PrintWriter out = response.getWriter();
		
		//String username = "";
		//if (request.getParameter("username") != null) {
			//username = request.getParameter("username");
		//}
		
		//返回一个json格式  JSONObject 
		//JSONObject obj = new JSONObject();
		
		//往json里面加 会throw exception 所以要用try
		//put key, value pair 
		
		//try {
			//obj.put("username", username);
		//} catch (JSONException e) {
			//e.printStackTrace();
		//}
		
		//out.print(obj);
		//out.close();	
		
		//clearly in the future we’ll return a list of nearby events for client
		//so let’s try to return a list of usernames first.
		
		//JSONArray array = new JSONArray();
		//try {
			//array.put(new JSONObject().put("usrename", "abcd"));
			//array.put(new JSONObject().put("usrename", "1234"));
		//} catch (JSONException e) {
			//e.printStackTrace();
		//}
		//RpcHelper.writeJsonArray(response, array);	
		
		
		//update after finish TicketMasterAPI.java file 
		//通过传进的经纬度参数
		String userId = request.getParameter("user_id");
		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		// term can be empty
		String keyword = request.getParameter("term");
		
		DBConnection connection = DBConnectionFactory.getConnection();
		List<Item> items = connection.searchItems(lat, lon, keyword);
		
		Set<String> favorite = connection.getFavoriteItemIds(userId);

        connection.close(); 
		JSONArray array = new JSONArray();
		try {
			for (Item item : items) {
				//把item 转为jsonobject 加到array里
				JSONObject obj = item.toJSONObject();
				obj.put("favorite", favorite.contains(item.getItemId()));
				array.put(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//调用rpchelper 把jsonarray写到response里ticketmasterapi 里的search，根据前端传进来的三个参数
		//然后把最后的结果 返回jsonarray array里面每个元素是个object 是item 里面的8个fields
		//这里是怎么通过servlet 来调用你的
		RpcHelper.writeJsonArray(response, array);
	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * 给servlet发的是post 请求，会执行这个method
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

}
