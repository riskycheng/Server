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
		// ɾ������һ������
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
		// ɾ������һ������
		json.deleteCharAt(json.lastIndexOf(","));
		json.append("]");
		System.out.println(json);
		return json.toString();
	}
	
	
	// ���ͻ��˷�����praises��criticisms�ı��Ľ��н���
	public PraisesAndCriticisms Json2PAC(String content) {
		PraisesAndCriticisms praisesAndCriticisms = new PraisesAndCriticisms();
		// ���÷ֺŽ��ַ����ֳ�����String����
		String temp_array[] = content.split(";");
		// �����жϵ��޵������Ƿ�Ϊ��
		if (0 != content.indexOf(";")) {
			// ��ÿ�������ٰ��ն��ŷֳ�String����
			String arrayA[] = temp_array[0].split(",");
			// �����ַ��������е�ֵ����PraisesAndCriticisms����
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
