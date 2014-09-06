package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import entity.Duanzi;
import entity.Picture;
import entity.PraisesAndCriticisms;

import java.sql.Statement;
import java.text.SimpleDateFormat;

public class SQLexecution {
	Connection con = null;
	public final static int PageSize = 20;
	public static int cur_allNum = 0;
	public final static int BALANCE = 20;

	// 初始化，链接数据库
	public SQLexecution() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			con = DriverManager
					.getConnection(
							"jdbc:mysql://127.0.0.1:3306/duanzi?useUnicode=true&characterEncoding=UTF-8",
							"root", null);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			Statement statement = con.createStatement();
			ResultSet set = statement.executeQuery("select * from content ");
			set.last();
			// 初始化cur_AllNum
			cur_allNum = set.getRow();
		} catch (Exception e) {
			System.out.println("初始化cur_AllNum失败" + e.getMessage());
		}

	}

	// 更新段子：从数据库下载
	public List<Duanzi> downloadDuanzi(int pageIndex) {
		// 判断con是否通畅
		List<Duanzi> results = new ArrayList<Duanzi>();
		ResultSet set = null;

		Statement statement = null;
		if (con == null)
			return null;

		//如果con不是空，那么就初始化
		try {
			statement = con.createStatement();
		} catch (SQLException e1) {
			System.out.println("初始化statement失败！");
		}		
		
		// 首先根据pageIndex判断客户端过来的是下拉刷新还是上拉更多

		if (pageIndex < 0) {
			// pageIndex = pageIndex + 1;
			// pageIndex == -1 表示为下拉刷新，获取最新的段子
			// 首先获得总记录数目
			try {

				// 只要是下拉刷新，就加载前50条记录
				System.out.println("更新多于设定的balance = " + BALANCE
						+ " 只加载balance");
				set = statement
						.executeQuery("select * from content where visible = 1 order by time desc limit 0,10 ");

			} catch (Exception e) {
				System.out.println("downloadDuanzi Error:" + e.getMessage());
			}

		} else {
			// 否则则是上拉分页查询数据库
			// 如果请求的页数已经超过总记录的数目，则返回为空
			if ((pageIndex + 1) * PageSize > cur_allNum)
				return null;
			// 否则就按照设定的页面大小进行加载
			System.out.println("download sql:"
					+ "select * from content where visible = 1 order by time desc limit  "
					+ (pageIndex * PageSize) + "," + PageSize );
			try {
				set = statement
						.executeQuery("select * from content where visible = 1 order by time desc  limit "
								+ (pageIndex * PageSize) + "," + PageSize );
			} catch (Exception e) {
				System.out.println("上拉加载更多错误 " + e.getMessage());
			}
		}
		try {
			while (set.next()) {
				// 将结果赋给List
				Duanzi duanzi = new Duanzi();
				duanzi.setTime(set.getString(1));
				duanzi.setContent(set.getString(2));
				duanzi.setPraises(set.getInt(3));
				duanzi.setCriticisms(set.getInt(4));
				duanzi.setId(set.getInt(5));
				results.add(duanzi);
			}
		} catch (Exception e) {
			System.out.println("duanzi 解析错误" + e.getMessage());
		}

		return results;
	}

	// 下载图片，返回List<Picture>
	public List<Picture> downloadPicture(int pageIndex) {
		// 判断con是否通畅
		List<Picture> results = new ArrayList<Picture>();
		ResultSet set = null;
		int tempNum = 0;
		Statement statement = null;
		if (con == null)
			return null;
		try {
			statement = con.createStatement();
			ResultSet resultSet = statement
					.executeQuery("select * from pictures ");
			resultSet.last();
			tempNum = resultSet.getRow();
			System.out.print("tempNum 记录总数:" + tempNum);
			System.out.println(" cur_allNum 记录总数:" + cur_allNum);
		} catch (Exception e) {
			System.out.println("downloadDuanzi:获取当前总记录数失败 " + e.getMessage());
			tempNum = 0;
		}
		// 首先根据pageIndex判断客户端过来的是下拉刷新还是上拉更多

		if (pageIndex < 0) {
			pageIndex = -pageIndex + 1;
			// pageIndex == -1 表示为下拉刷新，获取最新的段子
			// 首先获得总记录数目
			try {

				// 判断总数目是否发声了改变
				if (tempNum - cur_allNum > BALANCE) {
					// 如果更新了很多，超过了设定的阈值，则只加载Balance的数目
					System.out.println("更新多于设定的balance = " + BALANCE
							+ " 只加载balance");
					set = statement
							.executeQuery("select * from pictures order by time desc limit 0,"
									+ (BALANCE + pageIndex * PageSize));
					cur_allNum = tempNum;
				} else {
					// 如果只更新了设定的阈值以内的数目，则就加载此增量
					System.out.println("更新少于设定的balance = " + BALANCE + "只加载增量");
					set = statement
							.executeQuery("select * from pictures order by time desc limit 0,"
									+ (pageIndex * PageSize));
					cur_allNum = tempNum;
				}

			} catch (Exception e) {
				System.out.println("downloadPicture Error:" + e.getMessage());
			}

		} else {
			// 否则则是上拉分页查询数据库
			// 如果请求的页数已经超过总记录的数目，则返回为空
			if ((pageIndex + 1) * PageSize > cur_allNum)
				return null;
			// 否则就按照设定的页面大小进行加载
			System.out.println("download sql:"
					+ "select * from pictures order by time  limit  "
					+ (pageIndex * PageSize) + "," + PageSize);
			try {
				set = statement
						.executeQuery("select * from pictures order by time desc limit "
								+ (pageIndex * PageSize) + "," + PageSize);
			} catch (Exception e) {
				System.out.println("上拉加载更多错误 " + e.getMessage());
			}
		}
		try {
			while (set.next()) {
				// 将结果赋给List
				Picture picture = new Picture();
				picture.setId(set.getInt(1));
				picture.setTime(set.getString(2));
				picture.setSmall(set.getString(3));
				picture.setFull(set.getString(4));
				results.add(picture);
			}
		} catch (Exception e) {
			System.out.println("Picture 解析错误" + e.getMessage());
		}

		return results;
	}

	// 发布段子，上传到数据库
	public void publishDuanzi(String duanziStr) {
		// 获取服务器系统时间
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String time = format.format(new Date());
		if (con == null) {
			return;
		}
		try {
			Statement statement = con.createStatement();
			statement
					.execute("insert into content (time,content,praises,criticisms,visible) values( '"
							+ time
							+ "','"
							+ duanziStr
							+ "',"
							+ 0
							+ ","
							+ 0
							+ ","
							+ 0
							+ ")");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 处理点赞和踩的操作
	public void updatePraises(PraisesAndCriticisms praisesAndCriticisms) {
		@SuppressWarnings("rawtypes")
		ArrayList array = praisesAndCriticisms.getPraised();
		if (array.size() == 0)
			return;
		StringBuffer buffer = new StringBuffer();
		buffer.delete(0, buffer.length());
		buffer.append("update content set praises = praises + 1 where");
		for (int i = 0; i < array.size(); i++) {
			buffer.append(" id = ").append(array.get(i)).append(" or");
		}
		int length = buffer.length();
		buffer.delete(length - 3, length);
		System.out.println(buffer.toString());
		if (con == null) {
			return;
		}
		try {
			Statement statement = con.createStatement();
			statement.execute(buffer.toString());

		} catch (Exception e) {
			System.out.println("Sqlexecution:" + e.getMessage());
		}
	}

	public void updateCriticisms(PraisesAndCriticisms praisesAndCriticisms) {
		@SuppressWarnings("rawtypes")
		ArrayList array = praisesAndCriticisms.getCriticismed();
		if (array.size() == 0)
			return;
		StringBuffer buffer = new StringBuffer();
		buffer.delete(0, buffer.length());
		buffer.append("update content set criticisms = criticisms + 1 where");
		for (int i = 0; i < array.size(); i++) {
			buffer.append(" id = ").append(array.get(i)).append(" or");
		}
		int length = buffer.length();
		buffer.delete(length - 3, length);
		System.out.println(buffer.toString());
		if (con == null) {
			return;
		}
		try {
			Statement statement = con.createStatement();
			statement.execute(buffer.toString());
		} catch (Exception e) {
			System.out.println("Sqlexecution:" + e.getMessage());
		}
	}
	
	
	
	//审核段子
	// 更新段子：从数据库下载
		public List<Duanzi> CheckDuanzi(int pageIndex) {
			// 判断con是否通畅
			List<Duanzi> results = new ArrayList<Duanzi>();
			ResultSet set = null;

			Statement statement = null;
			if (con == null)
				return null;

			//如果con不是空，那么就初始化
			try {
				statement = con.createStatement();
			} catch (SQLException e1) {
				System.out.println("初始化statement失败！");
			}		
			
			// 首先根据pageIndex判断客户端过来的是下拉刷新还是上拉更多

			if (pageIndex < 0) {
				// pageIndex = pageIndex + 1;
				// pageIndex == -1 表示为下拉刷新，获取最新的段子
				// 首先获得总记录数目
				try {

					// 只要是下拉刷新，就加载前50条记录
					System.out.println("更新多于设定的balance = " + BALANCE
							+ " 只加载balance");
					set = statement
							.executeQuery("select * from content where visible = 0 order by id desc limit 0,10 ");

				} catch (Exception e) {
					System.out.println("downloadDuanzi Error:" + e.getMessage());
				}

			} else {
				// 否则则是上拉分页查询数据库
				// 如果请求的页数已经超过总记录的数目，则返回为空
				if ((pageIndex + 1) * PageSize > cur_allNum)
					return null;
				// 否则就按照设定的页面大小进行加载
				System.out.println("download sql:"
						+ "select * from content where visible = 0 order by id desc limit  "
						+ (pageIndex * PageSize) + "," + PageSize );
				try {
					set = statement
							.executeQuery("select * from content where visible = 0 order by id desc  limit "
									+ (pageIndex * PageSize) + "," + PageSize );
				} catch (Exception e) {
					System.out.println("上拉加载更多错误 " + e.getMessage());
				}
			}
			try {
				while (set.next()) {
					// 将结果赋给List
					Duanzi duanzi = new Duanzi();
					duanzi.setTime(set.getString(1));
					duanzi.setContent(set.getString(2));
					duanzi.setPraises(set.getInt(3));
					duanzi.setCriticisms(set.getInt(4));
					duanzi.setId(set.getInt(5));
					results.add(duanzi);
				}
			} catch (Exception e) {
				System.out.println("duanzi 解析错误" + e.getMessage());
			}

			return results;
		}
	
}
