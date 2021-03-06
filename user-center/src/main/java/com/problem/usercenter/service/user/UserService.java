package com.problem.usercenter.service.user;

import com.problem.usercenter.domain.entity.user.User;
import com.problem.usercenter.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserService {


	private final UserMapper userMapper;

	public User findById(Integer id) {
		return this.userMapper.selectByPrimaryKey(id);
	}
}
