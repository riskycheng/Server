package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import entity.Car;
import utils.ConstantParams;
import utils.ParsingTool;
public class MainServer extends HttpServlet {

	// 实例化ServerConnection:完成数据库的连接
	ServerConnection con = new ServerConnection();
	// 实例化ParsingTool
	ParsingTool parsingTool = new ParsingTool();
	// 结果
	List<Car> Cars = new ArrayList<Car>();

	/**
	 * function:处理用户请求
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// 接收客户端发送过来的流信息
		InputStream inStream = req.getInputStream();
		// 通过流获取数据流
		DataInputStream dataInStream = new DataInputStream(inStream);
		// 请求编码设置为UTF
		String requestData = dataInStream.readUTF();
		// 打印看一下
		System.out.println("requestData:" + requestData);
		log("requestData:" + requestData);
		// 获得请求类型
	 
		
		String RequestType = ParsingTool.getJsonRequestType(requestData);
	//	System.out.println("RequestType:" + ParsingTool.getJsonRequestType(requestData));
		// 将客户端发过来的数据封装成Car实体
  	   final Car inCar = ParsingTool.json2Car(requestData);
		
		
		
		// 定义返回结果的数据流
		DataOutputStream dataOutStream = new DataOutputStream(
				resp.getOutputStream());
		// 根据请求的类型采用相应的处理策略
		if (RequestType.equals(ConstantParams.APP_0_0)) {
			// 如果请求是APP_0_0:即为汽车的品牌
			Thread thread = new Thread(new Runnable() {
				public void run() {
					// 请求数据库，下载车的品牌
					
					Cars = con.QueryCars(inCar,
							ConstantParams.MODE_REQUEST_BRAND);
				}
			});
			thread.start();

			// 等待该线程执行完毕
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 调用发送的方法
			sendCars(dataOutStream, Cars, ConstantParams.MODE_REQUEST_BRAND);
		} else if (RequestType.equals(ConstantParams.APP_0_1)) {
			// 如果请求是APP_0_1:即为汽车的系列
			Thread thread = new Thread(new Runnable() {
				public void run() {
					// 请求数据库，下载车的系列
					Cars = con.QueryCars(inCar,
							ConstantParams.MODE_REQUEST_SERIES);
				}
			});
			thread.start();

			// 等待该线程执行完毕
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 调用发送的方法
			sendCars(dataOutStream, Cars, ConstantParams.MODE_REQUEST_SERIES);
		} else if (RequestType.equals(ConstantParams.APP_0_2)) {
			// 如果请求是APP_0_2:即为汽车的型号
			Thread thread = new Thread(new Runnable() {
				public void run() {
					// 请求数据库，下载车的型号
					Cars = con.QueryCars(inCar,
							ConstantParams.MODE_REQUEST_TYPE);
				}
			});
			thread.start();

			// 等待该线程执行完毕
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 调用发送的方法
			sendCars(dataOutStream, Cars, ConstantParams.MODE_REQUEST_TYPE);
		} else if (RequestType.equals(ConstantParams.APP_0_3)) {
			// 如果请求是APP_0_3:即为汽车的价格
			Thread thread = new Thread(new Runnable() {
				public void run() {
					// 请求数据库，下载车的价格
//					Cars = con.QueryCars(inCar,
//							ConstantParams.MODE_REQUEST_PRICE);
				}
			});
			thread.start();

			// 等待该线程执行完毕
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 调用发送的方法
			sendCars(dataOutStream, Cars, ConstantParams.MODE_REQUEST_PRICE);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(req, resp);
	}

	/**
	 * function:将查询结果返回给客户端
	 * 
	 * @param dataOutStream
	 *            发送流
	 * @param cars
	 *            传递进来的汽车List
	 */
	public void sendCars(DataOutputStream dataOutStream, List<Car> cars,
			int mode) {
		// 首先需要将cars转化为Json数据
		if (cars == null)
			return;
		String json = ParsingTool.Cars2Json(cars, mode);
		// 通过outStream将数据发送出去
		try {
			dataOutStream.writeUTF(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	

}
