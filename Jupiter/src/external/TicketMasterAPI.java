package external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
import entity.Item.ItemBuilder;

public class TicketMasterAPI {
	//访问ticketmaster的url 网址
	private static final String URL = "https://app.ticketmaster.com/discovery/v2/events.json";
	//默认为空的长度为0的关键词 无关键词
	private static final String DEFAULT_KEYWORD = ""; // no restriction
	private static final String API_KEY = "qNTubHcacn3h5UGsEnqU1P8kcHIfGdUt";
	
	public List<Item> search(double lat, double lon, String keyword) {
		// Encode keyword in url since it may contain special characters
		//判读是否为空 默认长度为0的string 
		if (keyword == null) {
			keyword= DEFAULT_KEYWORD;
		}
		
		//url 里面有些字符是不支持的 特殊字符在浏览器里做是会帮你转换的 
		//现在要把url里特殊字符进行转码
		//唯一有可能是特殊字符的是keywork 因为是用户传进来的 把参数keyword 进行转码 调用java 自带的URLEncoder.encode
		try {
			keyword= java.net.URLEncoder.encode(keyword, "UTF-8"); //进行转码 rike sun => rike20%sun
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Convert lat/lon to geo hash 把经纬度转换成geohash 
		String geoHash = GeoHash.encodeGeohash(lat, lon, 9);
		
		// Make your url query part like: "apikey=12345&geoPoint=abcd&keyword=music&radius=50"
		//现在定义query 
		// 4个％ 就要有4个string 替换他
		String query = String.format("apikey=%s&geoPoint=%s&keyword=%s&radius=%s", API_KEY, geoHash, keyword, 50); // 50 代表默认的50 mile 搜索范围
		try {
			//建立连接
			// Open a HTTP connection between your Java application and TicketMaster based on url
			//拼成一个完整的url 
			HttpURLConnection connection = (HttpURLConnection) new URL(URL + "?" + query).openConnection(); //openConnection 是打开连接
			// Set requrest method to GET 发送get请求 
			connection.setRequestMethod("GET");
			
			// Send request to TicketMaster and get response, response code could be
			// returned directly
			// response body is saved in InputStream of connection.
			
			//先发送请求 得到状态结果 202 , 404..
			int responseCode = connection.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + URL + "?" + query);
			//print 用来debug, 结果会print出200 or ... 如果不是200 要么url 要么url里面的参数有问题
			System.out.println("Response Code : " + responseCode);
			
			//通过bufferreader 把response 的内容读出来
			//从string 里面读内容的时候就需要一个BufferedReader的class
			//从客户端的角度来看，客户端的输入 对应的是服务器里的输出（server response)
			//connection.getInputStream 得到服务器给你返回的结果 通过buffer 把它一行行读出来
			// Now read response body to get events data
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuilder response = new StringBuilder();   //一行一行读出response 同步等response 减少系统的负担
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			//把string 变成jsonobject 因为她的class 支持这种构造函数
			JSONObject obj = new JSONObject(response.toString());
			//jsonobject 会有三个key value pair (link embedded page)
			//我们要得到_embedded 里面叫events的array得到 
			if (obj.isNull("_embedded")) {  //得到下划线embedded的key 存在不存在 如果不存在 说明什么活动都没搜到
				return new ArrayList<>();
			}
			//如果存在， 就通过jsonobject把_embedded对应的value 拿出来
			JSONObject embedded = obj.getJSONObject("_embedded");
			
			//通过jsonobject 把下划线embedded对应的value拿出来
			JSONArray events = embedded.getJSONArray("events");
			//item 组成的arraylist 
			return getItemList(events);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
    }

	/**
	 * Helper methods
	 */
    //注释就是一个活动
	//  {
	//    "name": "laioffer",
              //    "id": "12345",
              //    "url": "www.laioffer.com",
	//    ...
	//    "_embedded": { //找到_embedded key 对应的value : venues 
	//	    "venues": [  //venues 是个json array 里面每一个json object（2个key: address, city) 就是1个举办地点的信息
	//	        {
	//		        "address": {
	//		           "line1": "101 First St,",
	//		           "line2": "Suite 101",
	//		           "line3": "...",
	//		        },
	//		        "city": {
	//		        	"name": "San Francisco"
	//		        }
	//		        ...
	//	        },
	//	        ...
	//	    ]
	//    }
	//    ...
	//  }
	
	//找到存在的address 
	//https://developer.ticketmaster.com/products-and-docs/apis/discovery-api/v2/#search-events-v2
	private String getAddress(JSONObject event) throws JSONException {
		//check _embedded 这个key 存在不存在
		if (!event.isNull("_embedded")) {
			JSONObject embedded = event.getJSONObject("_embedded");  //对应的是注释里面_embedded 一直到结束那部分
			
			if (!embedded.isNull("venues")) {
				//得到注释里venues 下面到结束的那部分内容 是个jsonarray 
				JSONArray venues = embedded.getJSONArray("venues");
		        //遍历array 
				for (int i = 0; i < venues.length(); ++i) {
					JSONObject venue = venues.getJSONObject(i);
					
					//有个stringbuilder 为了拼成一个完整的address
					StringBuilder sb = new StringBuilder();
					
					if (!venue.isNull("address")) {
						JSONObject address = venue.getJSONObject("address");
						//address 里面还是个jsonarray 
						if (!address.isNull("line1")) {
							sb.append(address.getString("line1"));
						}
						if (!address.isNull("line2")) {
							sb.append(address.getString("line2"));
						}
						if (!address.isNull("line3")) {
							sb.append(address.getString("line3"));
						}
						sb.append(",");
					}
					
					if (!venue.isNull("city")) {
						JSONObject city = venue.getJSONObject("city");
						if (!city.isNull("name")) {
							sb.append(city.getString("name"));
						}
					}
					
					if (!sb.toString().equals("")) {
						return sb.toString();
					}
				}
			}
		}
		
		return "";
	}

	//https://developer.ticketmaster.com/products-and-docs/apis/discovery-api/v2/#search-events-v2
	// {"images": [{"url": "www.example.com/my_image.jpg"}, ...]}
	private String getImageUrl(JSONObject event) throws JSONException {
		if (!event.isNull("images")) {
			JSONArray array = event.getJSONArray("images");
			for (int i = 0; i < array.length(); i++) {
				JSONObject image = array.getJSONObject(i);
				//看image 有没有url这个key 有的话就取出来
				if (!image.isNull("url")) {
					return image.getString("url");
				}
			}
		}
		return "";
	}

	//https://developer.ticketmaster.com/products-and-docs/apis/discovery-api/v2/#search-events-v2
	// {"classifications" : [{"segment": {"name": "music"}}, ...]}
	//类型的名字在classification -> segment->name 里面
	private Set<String> getCategories(JSONObject event) throws JSONException {
		Set<String> categories = new HashSet<>();
		if (!event.isNull("classifications")) {
			JSONArray classifications = event.getJSONArray("classifications");
			for (int i = 0; i < classifications.length(); i++) {
				JSONObject classification = classifications.getJSONObject(i);
				if (!classification.isNull("segment")) {
					JSONObject segment = classification.getJSONObject("segment");
					if (!segment.isNull("name")) {
						String name = segment.getString("name");
						categories.add(name);
					}
				}
			}
		}
		return categories;
	}

	
	// Convert JSONArray to a list of item objects.
	private List<Item>getItemList(JSONArray events) throws JSONException {
		List<Item> itemList = new ArrayList<>();
		//遍历itemList 
		for (int i = 0; i < events.length(); ++i) {
			JSONObject event = events.getJSONObject(i);
			
			ItemBuilder builder = new ItemBuilder();
			
			if (!event.isNull("name")) {
				builder.setName(event.getString("name"));
			}
			if (!event.isNull("id")) {
				builder.setItemId(event.getString("id"));
			}
			if (!event.isNull("url")) {
				builder.setUrl(event.getString("url"));
			}
			if (!event.isNull("rating")) {
				builder.setRating(event.getDouble("rating"));
			}
			if (!event.isNull("distance")) {
				builder.setDistance(event.getDouble("distance"));
			}
			
			builder.setCategories(getCategories(event));
			builder.setAddress(getAddress(event));
			builder.setImageUrl(getImageUrl(event));
			
			//把builder转换成成item object 加到itemList 
			itemList.add(builder.build());
		}
		
		return itemList;
	}
	
	//queryAPI函数 调用serach 得到JSONArray
		//private : 是因为就是用来内部debug的一个 别人不会调用
		//查看search的结果是什么
		//参数是经度纬度 
		private void queryAPI(double lat, double lon) {
			List<Item> itemList = search(lat, lon, null);
			try {
				for (Item item : itemList) {
					//把每个item 取出来变成jsonobject, 再把jsonobject 打印出来
					JSONObject jsonObject = item.toJSONObject();
					System.out.println(jsonObject);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	/**
	 * Main entry for sample TicketMaster API requests.
	 * 结果的每一行都代表是一个活动 jsonobject key value pair的格式 
	 * 测试search的请求 
	 * 把对应的object 返回给前端的javascript 最后用html 显示出来
	 * 返回的太杂 需要数据清理
	 */
	public static void main(String[] args) {
		TicketMasterAPI tmApi = new TicketMasterAPI();
		// Mountain View, CA
		// tmApi.queryAPI(37.38, -122.08);
		// London, UK
		// tmApi.queryAPI(51.503364, -0.12);
		// Houston, TX
		tmApi.queryAPI(29.682684, -95.295410);
	}


}

