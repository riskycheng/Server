package entity;

public class Duanzi {
	private String time;
	private String content;
	private int praises;
	private int id;
	private int criticisms;

	public int getCriticisms() {
		return criticisms;
	}

	public void setCriticisms(int criticisms) {
		this.criticisms = criticisms;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getPraises() {
		return praises;
	}

	public void setPraises(int praises) {
		this.praises = praises;
	}

}
