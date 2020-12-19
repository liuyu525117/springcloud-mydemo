package com.liuyu.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.liuyu.model.User;

/**
 * 采用无配置文件版
 * @author liuyu
 *
 */
//@Repository
public interface UserMapper2 {

	User getUserById(Integer id);

	public List<User> getUserList();

	public int add(User user);

	public int update(Integer id, User user);

	public int delete(Integer id);

}
