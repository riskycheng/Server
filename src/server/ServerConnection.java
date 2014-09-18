package server;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import utils.ParsingTool;
import entity.Car;

/**
 * function:实现数据库的连接操作
 * 
 * @author chengjian
 */

public class ServerConnection {
	private Connection con = null;
	public static ResultSet sets;
	Statement statement = null;

	// 构造函数连接数据库
	public ServerConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			con = DriverManager
					.getConnection(
							"jdbc:mysql://localhost/car?useUnicode=true&characterEncoding=UTF-8",
							"root", null);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	/**
	 * function:依照部分车的信息，完成查询数据库,将车的查询结果转化为List<Car>
	 * 
	 * @param car
	 *            此为将用户在前端选择的部分信息封装为Car实体
	 * @param mode
	 *            这是请求的类型：0表示下载所有车品牌，1表示下载对应车品牌的车系，2表示对应的车型
	 * @throws UnsupportedEncodingException
	 */
	public List<Car> QueryCars(Car car, int mode)
			throws UnsupportedEncodingException {
		List<Car> cars = new ArrayList<Car>();
		switch (mode) {
		case 0:
			// 查询所有车的品牌
			try {
				statement = con.createStatement();
				ResultSet set = statement
						.executeQuery("select distinct(brand),brandvalue from mycar");
				cars = ParsingTool.resultSet2Cars(set, 0);
				statement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;

		case 1:
			// 查询所有某一车牌对应的车系
			try {
				statement = con.createStatement();
				ResultSet set = statement
						.executeQuery("select distinct(series),seriesvalue from mycar where brandvalue="
								+ car.getBrandValue());
				cars = ParsingTool.resultSet2Cars(set, 1);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;

		case 2:
			// 查询所有某一车牌对应的车系的对应车型
			try {
				statement = con.createStatement();
				ResultSet set = statement
						.executeQuery("select distinct(type),typevalue from mycar where seriesvalue="
								+ car.getSeriesValue());
				cars = ParsingTool.resultSet2Cars(set, 2);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;
		case 3:
			// 查询所有某一车牌对应的车系的对应车型的某一车型的价格:即全部信息
			try {
				statement = con.createStatement();
				ResultSet set = statement
						.executeQuery("select price from mycar where typevalue="
								+ car.getTypeValue()
								+ " and seriesvalue="
								+ car.getSeriesValue()
								+ " and brandvalue="
								+ car.getBrandValue());
				cars = ParsingTool.resultSet2Cars(set, 3);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}

		return cars;
	}

	/**
	 * 根据type类型查找利率
	 */
	public double queryRate(String loanType) {
		double rate = 0.0;
		try {
			statement = con.createStatement();
			ResultSet set = statement
					.executeQuery("select rate from loan where type='"
							+ loanType + "'");
			set.beforeFirst();
			while (set.next())
				rate = set.getDouble(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rate;
	}
}
