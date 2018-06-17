package rpc;

import java.io.BufferedReader;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

public class RpcHelper {
	// Parses a JSONObject from http request.
		public static JSONObject readJsonObject(HttpServletRequest request) {
			StringBuffer jb = new StringBuffer();
			String line = null;
			try {
				BufferedReader reader = request.getReader();
				while ((line = reader.readLine()) != null) {
					jb.append(line);
				}
				reader.close();
				return new JSONObject(jb.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	// Writes a JSONObject to http response.
	//写成static 的好处是： 使用起来方便 不用再new Rpc helper的object 出来了
		public static void writeJsonObject(HttpServletResponse response, JSONObject obj) {
			try {
				response.setContentType("application/json");
				response.addHeader("Access-Control-Allow-Origin", "*");
				PrintWriter out = response.getWriter();
				out.print(obj);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		// Writes a JSONArray to http response.
		public static void writeJsonArray(HttpServletResponse response, JSONArray array) {
			try {
				response.setContentType("application/json");
				//为前端做准备
				response.addHeader("Access-Control-Allow-Origin", "*");
				PrintWriter out = response.getWriter();
				out.print(array);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

}
