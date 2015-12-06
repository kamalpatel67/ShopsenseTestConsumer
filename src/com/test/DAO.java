package com.test;

public interface DAO {

	public void prepare(String sql);
	public void setString(int index, String value);
	public int insert();
	public void close();
}
