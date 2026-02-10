package model;

public class Airport {

	private String name;
	private String id;
	private int x,y;
	
	
	public Airport(String name, String id, int x, int y) {
		super();
		this.name = name;
		this.id = id;
		this.x = x;
		this.y = y;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public int getX() {
		return x;
	}


	public void setX(int x) {
		this.x = x;
	}


	public int getY() {
		return y;
	}


	public void setY(int y) {
		this.y = y;
	}
	
	
}
