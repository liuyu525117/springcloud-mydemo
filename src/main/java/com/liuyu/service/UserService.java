package com.liuyu.service;

import java.util.List;

import com.liuyu.model.User;

public interface UserService {

	User getUserById(Integer id);
	
	User getUserById2(Integer id);

	public List<User> getUserList();

	public int add(User user);

	public int update(Integer id, User user);

	public int delete(Integer id);
	
}
