package utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import server.ServerConnection;
import net.sf.json.JSONArray;
import entity.Car;

/**
 * 用于解析从客户端发送过来的json串解析、将本地查询结果封装成json
 * 
 * @author chengjian
 *
 */
public class ParsingTool {
	public static final int REQUEST_TYPE = 0;
	public static final String REQUEST_TYPE_STR = "type";
	public static final int CAR_SINGLE_INFO_START = 1;
	// 定义static 全局常量
	public static final String CAR_ATTRIBUTES[] = { "brand", "series",
			"series", "type", "price" };
	public static final String CAR_BRAND = "brand";
	public static final String CAR_SERIES = "series";
	public static final String CAR_TYPE = "type";
	public static final String CAR_PRICE = "price";
	public static final int CAR_BRAND_NO = 1;
	public static final int CAR_SERIES_NO = 2;
	public static final int CAR_TYPE_NO = 3;
	public static final int CAR_PRICE_NO = 4;
	public static final String LOAN_ATTRIBUTES[] = { "type", "first",
			"periods", "ratio" };
	public static final String LOAN_TYPE = "type";
	public static final String LOAN_FIRST = "first";
	public static final String LOAN_PERIOD = "peroids";
	public static final String LOAN_RATIO = "ratio";
	public static final int LOAN_TYPE_NO = 1;
	public static final int LOAN_FIRST_NO = 2;
	public static final int LOAN_PERIOD_NO = 3;
	public static final int LOAN_RATIO_NO = 4;
	// 测试
	static String json_car = "[{'type':'app_0_0'},{'brand':'奥迪','series':'奥迪A3','type':'1.2L跑车','price':500000},"
			+ "{'brand':'宝马','series':'宝马C3','type':'1.2L跑车','price':500000}]";

	String json_loan = "[{'type':'app_0_1'},{'type':'开心贷','first':30,'periods':12,'ratio':0.14},"
			+ "{'type':'畅想贷','first':40,'periods':36,'ratio':0.20}]";

	/**
	 * function：解析接收到的json串
	 * 
	 * @param json
	 *            接收到的字符串，转化为Car实体，从而进行数据库查询
	 */
	public static Car json2Car(String json) {
		Car car = new Car();
		JSONArray jsonArray = JSONArray.fromObject(json);
		// 获取车的品牌
		car.setBrand(jsonArray.getJSONObject(1).getString(CAR_BRAND).toString());
		// 获取车的系列
		car.setSeries(jsonArray.getJSONObject(1).getString(CAR_SERIES)
				.toString());
		// 获取车的车型
		car.setType(jsonArray.getJSONObject(1).getString(CAR_TYPE).toString());
		// 获取车的车型
		car.setPrice(jsonArray.getJSONObject(1).getInt(CAR_PRICE));
		return car;
	}

	/**
	 * function: 将查询到的多条car记录转成Json
	 * 
	 * @param cars
	 *            将服务器端查询到的多条car记录封装成json字符串
	 *            @param mode
	 *            mode=0 取车品牌；mode = 1 取车系列 ； mode = 2 取车型号 ； mode = 3 取车价格
	 */
	public static String Cars2Json(List<Car> cars ,int mode) {
		StringBuffer buffer = new StringBuffer();
		switch(mode){
		case ConstantParams.MODE_REQUEST_BRAND:
			buffer.append("[{'type':'" + ConstantParams.APP_0_0 + "'},");
			break;
		case ConstantParams.MODE_REQUEST_SERIES:
			buffer.append("[{'type':'" + ConstantParams.APP_0_1 + "'},");
			break;
		case ConstantParams.MODE_REQUEST_TYPE:
			buffer.append("[{'type':'" + ConstantParams.APP_0_2 + "'},");
			break;
		case ConstantParams.MODE_REQUEST_PRICE:
			buffer.append("[{'type':'" + ConstantParams.APP_0_3 + "'},");
			break;
		}
		// 循环将car的值置入json中
		for (Car car : cars) {
			buffer.append("{'" + CAR_BRAND + "':'" + car.getBrand() + "','");
			buffer.append(CAR_SERIES + "':'" + car.getSeries() + "','");
			buffer.append(CAR_TYPE + "':'" + car.getType() + "','");
			buffer.append(CAR_PRICE + "':" + car.getPrice() + "},");
		}
		// 将最后一个','去掉
		buffer.deleteCharAt(buffer.lastIndexOf(","));
		// 将json字符串补充完整
		buffer.append("]");
		return buffer.toString();
	}

	/**
	 * function:完成将Result结果集转化为List结果
	 * 
	 * @param sets
	 *            传递进来的查询结果ResultSet
	 * @param mode
	 *            转化类型：0表示车品牌；1表示车系；2表示车型;3：表示全部信息
	 */
	public static List<Car> resultSet2Cars(ResultSet sets, int mode) {
		List<Car> cars = new ArrayList<Car>();

		try {
			sets.beforeFirst();

			switch (mode) {

			case 0:
				while (sets.next()) {
					Car car = new Car();
					car.setBrand(sets.getString(CAR_SINGLE_INFO_START));
					// car.setSeries(sets.getString(CAR_SERIES_NO));
					// car.setType(sets.getString(CAR_TYPE_NO));
					// car.setPrice(sets.getInt(CAR_PRICE_NO));
					cars.add(car);
				}
				break;
			case 1:
				while (sets.next()) {
					Car car = new Car();
					// car.setBrand(sets.getString(CAR_BRAND_NO));
					car.setSeries(sets.getString(CAR_SINGLE_INFO_START));
					// car.setType(sets.getString(CAR_TYPE_NO));
					// car.setPrice(sets.getInt(CAR_PRICE_NO));
					cars.add(car);
				}
				break;

			case 2:
				while (sets.next()) {
					Car car = new Car();
					// car.setBrand(sets.getString(CAR_BRAND_NO));
					// car.setSeries(sets.getString(CAR_SERIES_NO));
					car.setType(sets.getString(CAR_SINGLE_INFO_START));
					// car.setPrice(sets.getInt(CAR_PRICE_NO));
					cars.add(car);
				}
				break;
			case 3:
				while (sets.next()) {
					Car car = new Car();
//					car.setBrand(sets.getString(CAR_BRAND_NO));
//					car.setSeries(sets.getString(CAR_SERIES_NO));
//					car.setType(sets.getString(CAR_TYPE_NO));
					car.setPrice(sets.getInt(CAR_SINGLE_INFO_START));
					
					cars.add(car);
				}
				break;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cars;
	}

	/**
	 * function:实现获取从客户端获取的请求类型：返回值：0_0为请求车品牌，0_1为请求车系列，0_2为请求车型号，0_3为请求车价格
	 * 
	 * @param json
	 *            传递进来的json数据
	 */
	public static String getJsonRequestType(String json) {
		JSONArray jsonArray = JSONArray.fromObject(json);
		String type = jsonArray.getJSONObject(REQUEST_TYPE).getString(
				REQUEST_TYPE_STR);
		return type;
	}

	// test
	public static void main(String[] args) {
		Car carA = new Car();
		carA.setBrand("A 奥迪");
		carA.setSeries("奥迪A3");
		carA.setType("2014款xx型1.2L");
		carA.setPrice(100);
		// Car carB = new Car();
		// carB.setBrand("b");
		// carB.setSeries("b_0");
		// carB.setType("b_0_0");
		// carB.setPrice(200);
		// List<Car> cars = new ArrayList<Car>();
		// cars.add(carA);
		// cars.add(carB);
		// System.out.println(Cars2Json(cars));
		// String json = Cars2Json(cars);
		// Car car = json2Car(json);
		// if (car.getType().equals(""))
		// System.out.print("没有");
		// System.out.print(car.getType());

		// ServerConnection conn = new ServerConnection();
		// cars = resultSet2Cars(conn.sets);
		// for(Car car:cars){
		// System.out.println(car.getBrand() +";" + car.getSeries() + ";" +
		// car.getType() + ";" + car.getPrice());
		// }
//		ServerConnection conn = new ServerConnection();
//		List<Car> cars = conn.QueryCars(carA, 3);
//		for (Car car : cars) {
//			System.out.println(car.getBrand() + ";" + car.getSeries() + ";"
//					+ car.getType() + ";" + car.getPrice());
//		}
		
 		System.out.println(getJsonRequestType(json_car));
	}
 
}
