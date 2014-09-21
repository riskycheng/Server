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
import entity.User;

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
							"jdbc:mysql://localhost:3306/car",
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
						.executeQuery("select distinct(brand),brandvalue from mycar order by brandvalue");
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
	 * function:完成针对确定的某一车子修改其价格
	 * 
	 * @param car
	 *            传进来的car参数即为客户端发过来的数据，找到该车的品牌，系列和型号，并将其对应的数据库的价格改为此处的值
	 */
	public int updatePrice(Car car) {
		int BrandValue = car.getBrandValue();
		int SeriesValue = car.getSeriesValue();
		int TypeValue = car.getTypeValue();
		int newPrice = car.getPrice();
		// either (1) the row count for SQL Data Manipulation Language (DML)
		// statements or (2) 0 for SQL statements that return nothing
		int IsUpdated = 0;
		// 根据此处的三条信息去查找数据
		try {
			statement = con.createStatement();
			// 查询的SQL语句
			String sqlStr = "update mycar set price = " + newPrice
					+ " where brandvalue='" + BrandValue
					+ "' and seriesvalue='" + SeriesValue + "' and typevalue='"
					+ TypeValue + "'";
			IsUpdated = statement.executeUpdate(sqlStr);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.print("修改车价失败！");
		}
		System.out.print(IsUpdated);
		return IsUpdated;
	}

	/**
	 * 根据type类型查找(开心贷）的利率
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

	/**
	 * function:更新开心贷款的利率：分别是12、24、36期的利率
	 */
	public int updateRate(double rate ,String type) {
		// either (1) the row count for SQL Data Manipulation Language (DML)
		// statements or (2) 0 for SQL statements that return nothing
		int IsUpdated = 0;
		// 根据此处的三条信息去查找数据
		try {
			statement = con.createStatement();
			// 查询的SQL语句
			String sqlStr = "update loan set rate = " + rate + " where type='" + type + "'";
			IsUpdated = statement.executeUpdate(sqlStr);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.print("修改利率失败！");
		}
		System.out.print(IsUpdated);
		return IsUpdated;
	} 
	
	
	/**
	 * function:查询用户表
	 * @return 
	 * 0:表示用户名不存在， 1：密码错误 ； 2：正确登录
	 */
	public int loginCheck(User user){
		int IsOK = 0;
		String name = user.getUserName();
		String password = user.getUserPassword();
		try {
			statement = con.createStatement();
			//查询语句
			String sqlStr = "select password from login where user='" + name + "'";
			ResultSet set = statement.executeQuery(sqlStr);
			//将结果集置于第一个
			set.beforeFirst();
			if(!set.next()){
				IsOK = 0;
				return IsOK;
			}else if(set.getString(1).equals(password)){
				//比较该用户名的密码是否正确
				IsOK = 2;
				return IsOK;
			}else{
				IsOK = 1;
				return IsOK;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("登录失败：IsOK=" + IsOK + ";" + e.getMessage().toString());
		}
		
		return 0;
	}
	
	

}
