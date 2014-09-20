package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import entity.Car;
import entity.User;
import utils.ConstantParams;
import utils.ParsingTool;

public class MainServer extends HttpServlet {

	// 实例化ServerConnection:完成数据库的连接
	ServerConnection con = new ServerConnection();
	// 实例化ParsingTool
	ParsingTool parsingTool = new ParsingTool();
	// 结果
	List<Car> Cars = new ArrayList<Car>();
	String loanJson = null;
	String updateResultJson = null;

	/**
	 * function:处理用户请求
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// // 接收客户端发送过来的流信息
		// InputStream inStream = req.getInputStream();
		// // 通过流获取数据流
		// DataInputStream dataInStream = new DataInputStream(inStream);
		// // 请求编码设置为UTF
		// String requestData = dataInStream.readUTF();
		final String requestData = req.getParameter("json");
		// 打印看一下
		System.out.println("requestData:" + requestData);
		log("requestData:" + requestData);
		// 获得请求类型

		String RequestType = ParsingTool.getJsonRequestType(requestData);

		// final Car inCar = ParsingTool.json2Car(requestData);

		// 定义返回结果的数据流
		DataOutputStream dataOutStream = new DataOutputStream(
				resp.getOutputStream());
		// 根据请求的类型采用相应的处理策略
		if (RequestType.equals(ConstantParams.APP_0_0)) {

			// 如果请求是APP_0_0:即为汽车的品牌
			Thread thread = new Thread(new Runnable() {
				public void run() {
					// 请求数据库，下载车的品牌
					Car inCar = ParsingTool.json2Car(requestData);
					try {
						Cars = con.QueryCars(inCar,
								ConstantParams.MODE_REQUEST_BRAND);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
					Car inCar = ParsingTool.json2Car(requestData);
					try {
						Cars = con.QueryCars(inCar,
								ConstantParams.MODE_REQUEST_SERIES);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
					Car inCar = ParsingTool.json2Car(requestData);
					try {
						Cars = con.QueryCars(inCar,
								ConstantParams.MODE_REQUEST_TYPE);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
					Car inCar = ParsingTool.json2Car(requestData);
					try {
						Cars = con.QueryCars(inCar,
								ConstantParams.MODE_REQUEST_PRICE);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
		} else if (RequestType.equals(ConstantParams.APP_0_4)
				|| RequestType.equals(ConstantParams.APP_0_5)
				|| RequestType.equals(ConstantParams.APP_0_6)) {
			// 如果请求是APP_0_4,05,06,07:即为开心贷的12期
			Thread thread = new Thread(new Runnable() {
				public void run() {
					// 请求数据库，下载车的价格
					String loanType = ParsingTool
							.parsingGetRateType(requestData);
					try {
						loanJson = ParsingTool.loan2Json(con
								.queryRate(loanType));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
			dataOutStream.writeUTF(loanJson);
		} else if (RequestType.equals(ConstantParams.APP_0_7)) {
			// 如果请求是APP_0_7:即为汽车的价格
			Thread thread = new Thread(new Runnable() {
				public void run() {
					Car inCar = ParsingTool.json2Car(requestData);
					try {
						// 更新车价
						updateResultJson = ParsingTool
								.parsingUpdateReslut2Json(con
										.updatePrice(inCar));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						System.out.println("更新车价失败："
								+ e.getMessage().toString());
					}
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
			// 将更新的反馈结果给客户端
			dataOutStream.writeUTF(updateResultJson);
		} else if (RequestType.equals(ConstantParams.APP_0_8)) {
			// 如果请求是APP_0_8:即为修改利率
			Thread thread = new Thread(new Runnable() {
				public void run() {
					// 解析loan的类型
					String type = ParsingTool.parsingGetUpdateRateType(requestData);
					try {
						// 更新利率
						updateResultJson = ParsingTool.parsingUpdateReslut2Json(con
								.updateRate(ParsingTool
										.parsingUpdateRate(requestData), type));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						System.out.println("更新利率失败："
								+ e.getMessage().toString());
					}
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
			// 将更新的反馈结果给客户端
			dataOutStream.writeUTF(updateResultJson);
		}else if (RequestType.equals(ConstantParams.APP_0_9)) {
			// 如果请求是APP_0_8:即为修改利率
			Thread thread = new Thread(new Runnable() {
				public void run() {
					User user = ParsingTool.ParsingJson2User(requestData);
					try {
						// 更新利率
						updateResultJson = ParsingTool.parsingLoginResult2Json(con.loginCheck(user));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						System.out.println("登录失败："
								+ e.getMessage().toString());
					}
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
			// 将更新的反馈结果给客户端
			dataOutStream.writeUTF(updateResultJson);
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
