package entity;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class Item {
	private String itemId;
	private String name;
	private double rating;
	private String address;
	private Set<String> categories;
	private String imageUrl;
	private String url;
	private double distance;//通过距离的远近 进行排序 近的先推荐
	
	/**
	 *把一个itembuilder的object 转换成一个item的object
	 *用到了builder的pattern   
	 */
	private Item(ItemBuilder builder) {
		this.itemId = builder.itemId;
		this.name = builder.name;
		this.rating = builder.rating;
		this.address = builder.address;
		this.categories = builder.categories;
		this.imageUrl = builder.imageUrl;
		this.url = builder.url;
		this.distance = builder.distance;
	}
	
	//Add toJSONObject() method. Why? To convert an Item object a JSONObject instance 
	//because in our application, frontend code cannot understand Java class, it can only understand JSON.
	//把item 对象变成一个object对象
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("item_id", itemId);
			obj.put("name", name);
			obj.put("rating", rating);
			obj.put("address", address);
			obj.put("categories", new JSONArray(categories));
			obj.put("image_url", imageUrl);
			obj.put("url", url);
			obj.put("distance", distance);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}



	
	public String getItemId() {
		return itemId;
	}
	public String getName() {
		return name;
	}
	public double getRating() {
		return rating;
	}
	public String getAddress() {
		return address;
	}
	public Set<String> getCategories() {
		return categories;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public String getUrl() {
		return url;
	}
	public double getDistance() {
		return distance;
	}
	
	
	
	
	/**
	 * 1.新建一个itembuilder的函数
	 * item class 对应的private field 比较多 生写构造函数比较痛苦 
	 * This is a builder pattern in Java. 所以需要一个builder pattern 
	 * 专门针对于如果一个class里面private field 比较多的
	 * 在class 里面建立一个叫builder的inner class 去build object 
	 * 通过builder的inner class 帮你创建class所对应的object 
	 * 这样就不用写各式各样的constructor 
	 * 生成item 所对应的object 
	 */
	//必须生成static 
	//因为item是通过 itembuilder build出来的
	//如果不是static的话 如果你想使itembuilder 就必须得现有item object 
    //因为item 是item builder build 出来的 
	//这里用了static 的话就先通过itembuilder 建立一个itembuilder的object， 再通过itembuilder的build来build一个item的object 
	public static class ItemBuilder {
		private String itemId;
		private String name;
		private double rating;
		private String address;
		private Set<String> categories;
		private String imageUrl;
		private String url;
		private double distance;
		
		public void setItemId(String itemId) {
			this.itemId = itemId;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setRating(double rating) {
			this.rating = rating;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public void setCategories(Set<String> categories) {
			this.categories = categories;
		}

		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public void setDistance(double distance) {
			this.distance = distance;
		}
		
		/**
		 * 2.要有一个build 函数 
		 * 帮你把一个itembuilder所对应的object 转换成一个item的object 
		 */
		//这个函数必须放在itembuilder这个里面 
		//必须是个inner class 
		//因为item 的constructor 是private 
		//如果不是inner class 他就调用不了
		//意义是 通过 build 创建item 的object 
		public Item build() {
			return new Item(this);
		}

	}

}

