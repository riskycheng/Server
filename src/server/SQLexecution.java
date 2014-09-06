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

	// ��ʼ�����������ݿ�
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
			// ��ʼ��cur_AllNum
			cur_allNum = set.getRow();
		} catch (Exception e) {
			System.out.println("��ʼ��cur_AllNumʧ��" + e.getMessage());
		}

	}

	// ���¶��ӣ������ݿ�����
	public List<Duanzi> downloadDuanzi(int pageIndex) {
		// �ж�con�Ƿ�ͨ��
		List<Duanzi> results = new ArrayList<Duanzi>();
		ResultSet set = null;

		Statement statement = null;
		if (con == null)
			return null;

		//���con���ǿգ���ô�ͳ�ʼ��
		try {
			statement = con.createStatement();
		} catch (SQLException e1) {
			System.out.println("��ʼ��statementʧ�ܣ�");
		}		
		
		// ���ȸ���pageIndex�жϿͻ��˹�����������ˢ�»�����������

		if (pageIndex < 0) {
			// pageIndex = pageIndex + 1;
			// pageIndex == -1 ��ʾΪ����ˢ�£���ȡ���µĶ���
			// ���Ȼ���ܼ�¼��Ŀ
			try {

				// ֻҪ������ˢ�£��ͼ���ǰ50����¼
				System.out.println("���¶����趨��balance = " + BALANCE
						+ " ֻ����balance");
				set = statement
						.executeQuery("select * from content where visible = 1 order by time desc limit 0,10 ");

			} catch (Exception e) {
				System.out.println("downloadDuanzi Error:" + e.getMessage());
			}

		} else {
			// ��������������ҳ��ѯ���ݿ�
			// ��������ҳ���Ѿ������ܼ�¼����Ŀ���򷵻�Ϊ��
			if ((pageIndex + 1) * PageSize > cur_allNum)
				return null;
			// ����Ͱ����趨��ҳ���С���м���
			System.out.println("download sql:"
					+ "select * from content where visible = 1 order by time desc limit  "
					+ (pageIndex * PageSize) + "," + PageSize );
			try {
				set = statement
						.executeQuery("select * from content where visible = 1 order by time desc  limit "
								+ (pageIndex * PageSize) + "," + PageSize );
			} catch (Exception e) {
				System.out.println("�������ظ������ " + e.getMessage());
			}
		}
		try {
			while (set.next()) {
				// ���������List
				Duanzi duanzi = new Duanzi();
				duanzi.setTime(set.getString(1));
				duanzi.setContent(set.getString(2));
				duanzi.setPraises(set.getInt(3));
				duanzi.setCriticisms(set.getInt(4));
				duanzi.setId(set.getInt(5));
				results.add(duanzi);
			}
		} catch (Exception e) {
			System.out.println("duanzi ��������" + e.getMessage());
		}

		return results;
	}

	// ����ͼƬ������List<Picture>
	public List<Picture> downloadPicture(int pageIndex) {
		// �ж�con�Ƿ�ͨ��
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
			System.out.print("tempNum ��¼����:" + tempNum);
			System.out.println(" cur_allNum ��¼����:" + cur_allNum);
		} catch (Exception e) {
			System.out.println("downloadDuanzi:��ȡ��ǰ�ܼ�¼��ʧ�� " + e.getMessage());
			tempNum = 0;
		}
		// ���ȸ���pageIndex�жϿͻ��˹�����������ˢ�»�����������

		if (pageIndex < 0) {
			pageIndex = -pageIndex + 1;
			// pageIndex == -1 ��ʾΪ����ˢ�£���ȡ���µĶ���
			// ���Ȼ���ܼ�¼��Ŀ
			try {

				// �ж�����Ŀ�Ƿ����˸ı�
				if (tempNum - cur_allNum > BALANCE) {
					// ��������˺ܶ࣬�������趨����ֵ����ֻ����Balance����Ŀ
					System.out.println("���¶����趨��balance = " + BALANCE
							+ " ֻ����balance");
					set = statement
							.executeQuery("select * from pictures order by time desc limit 0,"
									+ (BALANCE + pageIndex * PageSize));
					cur_allNum = tempNum;
				} else {
					// ���ֻ�������趨����ֵ���ڵ���Ŀ����ͼ��ش�����
					System.out.println("���������趨��balance = " + BALANCE + "ֻ��������");
					set = statement
							.executeQuery("select * from pictures order by time desc limit 0,"
									+ (pageIndex * PageSize));
					cur_allNum = tempNum;
				}

			} catch (Exception e) {
				System.out.println("downloadPicture Error:" + e.getMessage());
			}

		} else {
			// ��������������ҳ��ѯ���ݿ�
			// ��������ҳ���Ѿ������ܼ�¼����Ŀ���򷵻�Ϊ��
			if ((pageIndex + 1) * PageSize > cur_allNum)
				return null;
			// ����Ͱ����趨��ҳ���С���м���
			System.out.println("download sql:"
					+ "select * from pictures order by time  limit  "
					+ (pageIndex * PageSize) + "," + PageSize);
			try {
				set = statement
						.executeQuery("select * from pictures order by time desc limit "
								+ (pageIndex * PageSize) + "," + PageSize);
			} catch (Exception e) {
				System.out.println("�������ظ������ " + e.getMessage());
			}
		}
		try {
			while (set.next()) {
				// ���������List
				Picture picture = new Picture();
				picture.setId(set.getInt(1));
				picture.setTime(set.getString(2));
				picture.setSmall(set.getString(3));
				picture.setFull(set.getString(4));
				results.add(picture);
			}
		} catch (Exception e) {
			System.out.println("Picture ��������" + e.getMessage());
		}

		return results;
	}

	// �������ӣ��ϴ������ݿ�
	public void publishDuanzi(String duanziStr) {
		// ��ȡ������ϵͳʱ��
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

	// ������޺ͲȵĲ���
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
	
	
	
	//��˶���
	// ���¶��ӣ������ݿ�����
		public List<Duanzi> CheckDuanzi(int pageIndex) {
			// �ж�con�Ƿ�ͨ��
			List<Duanzi> results = new ArrayList<Duanzi>();
			ResultSet set = null;

			Statement statement = null;
			if (con == null)
				return null;

			//���con���ǿգ���ô�ͳ�ʼ��
			try {
				statement = con.createStatement();
			} catch (SQLException e1) {
				System.out.println("��ʼ��statementʧ�ܣ�");
			}		
			
			// ���ȸ���pageIndex�жϿͻ��˹�����������ˢ�»�����������

			if (pageIndex < 0) {
				// pageIndex = pageIndex + 1;
				// pageIndex == -1 ��ʾΪ����ˢ�£���ȡ���µĶ���
				// ���Ȼ���ܼ�¼��Ŀ
				try {

					// ֻҪ������ˢ�£��ͼ���ǰ50����¼
					System.out.println("���¶����趨��balance = " + BALANCE
							+ " ֻ����balance");
					set = statement
							.executeQuery("select * from content where visible = 0 order by id desc limit 0,10 ");

				} catch (Exception e) {
					System.out.println("downloadDuanzi Error:" + e.getMessage());
				}

			} else {
				// ��������������ҳ��ѯ���ݿ�
				// ��������ҳ���Ѿ������ܼ�¼����Ŀ���򷵻�Ϊ��
				if ((pageIndex + 1) * PageSize > cur_allNum)
					return null;
				// ����Ͱ����趨��ҳ���С���м���
				System.out.println("download sql:"
						+ "select * from content where visible = 0 order by id desc limit  "
						+ (pageIndex * PageSize) + "," + PageSize );
				try {
					set = statement
							.executeQuery("select * from content where visible = 0 order by id desc  limit "
									+ (pageIndex * PageSize) + "," + PageSize );
				} catch (Exception e) {
					System.out.println("�������ظ������ " + e.getMessage());
				}
			}
			try {
				while (set.next()) {
					// ���������List
					Duanzi duanzi = new Duanzi();
					duanzi.setTime(set.getString(1));
					duanzi.setContent(set.getString(2));
					duanzi.setPraises(set.getInt(3));
					duanzi.setCriticisms(set.getInt(4));
					duanzi.setId(set.getInt(5));
					results.add(duanzi);
				}
			} catch (Exception e) {
				System.out.println("duanzi ��������" + e.getMessage());
			}

			return results;
		}
	
}
