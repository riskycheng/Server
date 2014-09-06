package entity;

import java.util.ArrayList;

public class PraisesAndCriticisms {
	private ArrayList praised = new ArrayList();
	private ArrayList criticismed = new ArrayList();

	public ArrayList getCriticismed() {
		return criticismed;
	}

	public void setCriticismed(int DuanziID) {
		criticismed.add(DuanziID);
	}

	public ArrayList getPraised() {
		return praised;
	}

	public void setPraised(int DuanziID) {
		praised.add(DuanziID);
	}

}
