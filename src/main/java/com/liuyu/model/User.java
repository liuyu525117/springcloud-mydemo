package com.liuyu.model;

import java.io.Serializable;
import java.util.Date;

/*
 * 实现序列化，不然redis反序列化会失败
 */
public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String username;
	private int age;
	private Date ctm;

	public User() {
	}

	public User(String username, int age) {
		this.username = username;
		this.age = age;
		this.ctm = new Date();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Date getCtm() {
		return ctm;
	}

	public void setCtm(Date ctm) {
		this.ctm = ctm;
	}

}
