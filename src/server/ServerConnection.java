package server;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import entity.Car;
import utils.ParsingTool;

/**
 * function:ʵ�����ݿ�����Ӳ���
 * 
 * @author chengjian
 */

public class ServerConnection {
	private Connection con = null;
    public static ResultSet sets;
    Statement statement = null;
	// ���캯���������ݿ�
	public ServerConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			con = DriverManager
					.getConnection(
							"jdbc:mysql://127.0.0.1:3306/car?useUnicode=true&characterEncoding=UTF-8",
							"root", null);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	
	/**
	 * function:���ղ��ֳ�����Ϣ����ɲ�ѯ���ݿ�,�����Ĳ�ѯ���ת��ΪList<Car>
	 * @param car
	 * ��Ϊ���û���ǰ��ѡ��Ĳ�����Ϣ��װΪCarʵ��
	 * @param mode
	 * ������������ͣ�0��ʾ�������г�Ʒ�ƣ�1��ʾ���ض�Ӧ��Ʒ�Ƶĳ�ϵ��2��ʾ��Ӧ�ĳ���
	 */
	public List<Car> QueryCars(Car car,int mode){
		List<Car> cars = new ArrayList<Car>();
		switch(mode){
		case 0:
			//��ѯ���г���Ʒ��
			try {
				statement = con.createStatement();
				ResultSet set = statement.executeQuery("select distinct(brand) from mycar");
				cars = ParsingTool.resultSet2Cars(set, 0);
				statement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			break;
			
		case 1:
			//��ѯ����ĳһ���ƶ�Ӧ�ĳ�ϵ
			try {
				statement = con.createStatement();
				ResultSet set = statement.executeQuery("select distinct(series) from mycar where brand='" + car.getBrand() + "'");
				cars = ParsingTool.resultSet2Cars(set, 1);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			break;
			
		case 2:
			//��ѯ����ĳһ���ƶ�Ӧ�ĳ�ϵ�Ķ�Ӧ����
			try {
				statement = con.createStatement();
				ResultSet set = statement.executeQuery("select distinct(type) from mycar where series='" + car.getSeries() + "'");
				cars = ParsingTool.resultSet2Cars(set, 2);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			break;
		case 3:
			//��ѯ����ĳһ���ƶ�Ӧ�ĳ�ϵ�Ķ�Ӧ���͵�ĳһ���͵ļ۸�:��ȫ����Ϣ
			try {
				statement = con.createStatement();
				ResultSet set = statement.executeQuery("select * from mycar where type='" + car.getType() + "' and series='" + car.getSeries() + "'");
				cars = ParsingTool.resultSet2Cars(set, 3);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			break;
		}
		
		return cars;
	}
}
