package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sun.misc.Cleaner;
import utils.JsonParsing;

import entity.ClientPackage;
import entity.Duanzi;
import entity.Picture;
import entity.PraisesAndCriticisms;

public class MyServer extends HttpServlet {
	// 实例化SQLexecution
	SQLexecution sqLexecution = new SQLexecution();
	// 实例化JsonParsing
	JsonParsing jsonParsing = new JsonParsing();
	// 实例化PraisesAndroidCriticisms对象
	PraisesAndCriticisms praisesAndCriticisms;
	// 创建Duanzi的结果集对象
	List<Duanzi> duanzis;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// 接收客户端消息
		InputStream inputStream = req.getInputStream();
		DataInputStream dataInputStream = new DataInputStream(inputStream);
		// 读取请求编码
		String requestStr = dataInputStream.readUTF();

		// type是用来表示该客户端请求的类型，0表示异常
		int type = 0;
		// content用来表示该客户端请求的内容
		String content = "";
		// 调用parsingClientJson来转化
		ClientPackage clientPackage = jsonParsing.parsingClientJson(requestStr);
		type = clientPackage.getType();
		content = clientPackage.getContent();
		// 定义返回结果和返回结果数据流

		DataOutputStream dataOutputStream = new DataOutputStream(
				resp.getOutputStream());
		if (type == 0)
			return;
		switch (type) {
		case 1:// 请求段子的相关方法
			final int result_PageIndex = Integer.parseInt(content);

			
			
			 Thread t = new Thread(new Runnable() {

				public void run() {
					// TODO Auto-generated method stub
					duanzis = sqLexecution.downloadDuanzi(result_PageIndex);
				}
		           
		             
		        });
		        t.start();
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
         
			if (duanzis != null) {
				String json = jsonParsing.listDuanzis2String(duanzis);
				dataOutputStream.writeUTF(json);
			} else {
				Duanzi duanzi = new Duanzi();
				duanzi.setContent("no more duanzi,wait later...");
				duanzi.setTime("2014-06-08 10:23:24.0");
				ArrayList list = new ArrayList();
				list.add(duanzi);
				dataOutputStream.writeUTF(jsonParsing.listDuanzis2String(list));
			}
		//	new sendJsonThread(dataOutputStream).start();

			break;
		case 2:// 发送点赞的相关方法,content = "1,2,3;3,4,6";
			String duanziID = content;
			// sqLexecution.addPraises(duanziID);
			// 将String类型的content转化为PraisesAndCriticisms对象
			praisesAndCriticisms = jsonParsing.Json2PAC(content);
			if (praisesAndCriticisms.getPraised().size() != 0)
				sqLexecution.updatePraises(praisesAndCriticisms);
			sqLexecution.updateCriticisms(praisesAndCriticisms);
			dataOutputStream.writeUTF("add praises success");
			System.out.println(duanziID);
			break;
		case 3:// 发送段子的相关方法
			String duanzi_content = content;
			// 将段子写进数据库
			sqLexecution.publishDuanzi(duanzi_content);
			dataOutputStream.writeUTF("publish duanzi success");
			break;

		case 4:// 请求图片的相关方法
			int result_PictureIndex = Integer.parseInt(content);
			List<Picture> pictures = sqLexecution
					.downloadPicture(result_PictureIndex);
			if (pictures != null) {
				String json = jsonParsing.listpictures2String(pictures);
				dataOutputStream.writeUTF(json);
			} else {
				Picture picture = new Picture();
				picture.setSmall("no more picture");
				ArrayList list = new ArrayList();
				list.add(picture);
				dataOutputStream.writeUTF(jsonParsing.listDuanzis2String(list));
			}
			break;
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

	private class getDuanziThread extends Thread {
		int pageIndex;

		public getDuanziThread(int pageIndex) {
			this.pageIndex = pageIndex;
		}

		@Override
		public void run() {
			duanzis = sqLexecution.downloadDuanzi(pageIndex);
		}

	}

	// 发送json的线程
	private class sendJsonThread extends Thread {
		DataOutputStream outputStream;

		public sendJsonThread(DataOutputStream outputStream) {
			this.outputStream = outputStream;
		}
		
		@Override
		public void run() {
			String json = jsonParsing.listDuanzis2String(duanzis);
			try {
				outputStream.writeUTF(json);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
	}

}
