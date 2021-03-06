package com.problem.contentcenter.service.content;

import com.problem.contentcenter.domain.dto.content.ShareDTO;
import com.problem.contentcenter.domain.dto.user.UserDTO;
import com.problem.contentcenter.domain.entity.Share;
import com.problem.contentcenter.mapper.ShareMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShareService {

	private final ShareMapper shareMapper;
	private final RestTemplate restTemplate;
	private final DiscoveryClient discoveryClient;

	private static final String USER_CENTER_INSTANCE = "user-center";

	public ShareDTO findById(Integer id) {
		//获取分享详情
		Share share = shareMapper.selectById(id);
		//发布人id
		Integer userId = share.getUserId();
		//随机取值获取userDto
		// ResponseEntity<UserDTO> userDTOResponseEntity = getUserDto(userId);
		//使用 ribbon获取UserDto
		ResponseEntity<UserDTO> userDTOResponseEntity = getUserDto4Ribbon(userId);
		ShareDTO shareDTO = new ShareDTO();
		BeanUtils.copyProperties(share, shareDTO);
		shareDTO.setWxNickName(Objects.requireNonNull(userDTOResponseEntity.getBody()).getWxNickname());
		return shareDTO;
	}


	private ResponseEntity<UserDTO> getUserDto(Integer userId) {
		List<ServiceInstance> serviceInstanceList = discoveryClient.getInstances(USER_CENTER_INSTANCE);
		List<String> serviceUrl = serviceInstanceList.stream().map(serviceInstance -> serviceInstance.getUri().toString()).collect(Collectors.toList());

		String host = serviceUrl.get(ThreadLocalRandom.current().nextInt(serviceUrl.size()));
		log.info("host:{}", host);
		return restTemplate.getForEntity(host + "/users/{id}", UserDTO.class, userId);
	}

	private ResponseEntity<UserDTO> getUserDto4Ribbon(Integer userId) {
		String host = "http://user-center";
		return restTemplate.getForEntity(host + "/users/{id}", UserDTO.class, userId);
	}

}
