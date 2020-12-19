package com.liuyu.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.liuyu.mapper.UserMapper;
import com.liuyu.mapper.UserMapper2;
import com.liuyu.model.User;
import com.liuyu.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private UserMapper2 userMapper2;

	@Override
	public User getUserById(Integer id) {
		return userMapper.getUserById(id);
	}

	@Override
	public User getUserById2(Integer id) {
		// TODO Auto-generated method stub
		return userMapper2.getUserById(id);
	}

	@Override
	public List<User> getUserList() {
		return userMapper.getUserList();
	}

	@Override
	public int add(User user) {
		return userMapper.add(user);
	}

	@Override
	public int update(Integer id, User user) {
		return userMapper.update(id, user);
	}

	@Override
	public int delete(Integer id) {
		return userMapper.delete(id);
	}


}
