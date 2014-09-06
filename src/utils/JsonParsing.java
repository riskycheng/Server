package utils;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import entity.ClientPackage;
import entity.Duanzi;
import entity.Picture;
import entity.PraisesAndCriticisms;

public class JsonParsing {
private final String HOST = "http://42.51.158.184:8080/Server/pics/";
	public ClientPackage parsingClientJson(String json) {
		ClientPackage clientPackage = new ClientPackage();
		JSONArray jsonArray = JSONArray.fromObject(json);
		clientPackage.setType(Integer.parseInt(jsonArray.getJSONObject(0)
				.getString("type")));
		clientPackage.setContent(jsonArray.getJSONObject(0)
				.getString("content"));
		return clientPackage;

	}

	// List<Duanzi> -> string
	public String listDuanzis2String(List<Duanzi> duanzis) {
		StringBuffer json = new StringBuffer();
		Duanzi duanzi = null;
		json.append("[");
		for (int i = 0; i < duanzis.size(); i++) {
			duanzi = duanzis.get(i);
			json.append("{");
			json.append("\"time\":\"").append(duanzi.getTime()).append("\",");
			json.append("\"content\":\"").append(duanzi.getContent())
					.append("\",");
			json.append("\"praises\":").append(duanzi.getPraises()).append(",");
			json.append("\"criticisms\":").append(duanzi.getCriticisms())
					.append(",");
			json.append("\"id\":").append(duanzi.getId()).append("},");
		}
		// 删除最后的一个逗号
		json.deleteCharAt(json.lastIndexOf(","));
		json.append("]");
		System.out.println(json);
		return json.toString();
	}


	// List<Picture> -> string
	public String listpictures2String(List<Picture> pictures) {
		StringBuffer json = new StringBuffer();
		Picture picture = null;
		json.append("[");
		for (int i = 0; i < pictures.size(); i++) {
			picture = pictures.get(i);
			json.append("{");
			json.append("\"id\":").append(picture.getId()).append(",");
			json.append("\"time\":\"").append(picture.getTime()).append("\",");
			json.append("\"small\":\"").append(HOST).append(picture.getSmall())
					.append("\",");	 
			json.append("\"full\":\"").append(HOST).append(picture.getFull()).append("\"},");
		}
		// 删除最后的一个逗号
		json.deleteCharAt(json.lastIndexOf(","));
		json.append("]");
		System.out.println(json);
		return json.toString();
	}
	
	
	// 将客户端发过来praises和criticisms的报文进行解析
	public PraisesAndCriticisms Json2PAC(String content) {
		PraisesAndCriticisms praisesAndCriticisms = new PraisesAndCriticisms();
		// 先用分号将字符串分成两个String数组
		String temp_array[] = content.split(";");
		// 首先判断点赞的数组是否为空
		if (0 != content.indexOf(";")) {
			// 对每个数组再按照逗号分成String数组
			String arrayA[] = temp_array[0].split(",");
			// 将该字符串数组中的值赋给PraisesAndCriticisms对象
			for (int i = 0; i < arrayA.length; i++) {
				praisesAndCriticisms.setPraised(Integer.parseInt(arrayA[i]));
			}
		}
		if (content.length() - 1 != content.indexOf(";")) {
			String arrayB[] = temp_array[1].split(",");
			for (int j = 0; j < arrayB.length; j++) {
				praisesAndCriticisms
						.setCriticismed(Integer.parseInt(arrayB[j]));
			}
		}
		return praisesAndCriticisms;
	}

}
