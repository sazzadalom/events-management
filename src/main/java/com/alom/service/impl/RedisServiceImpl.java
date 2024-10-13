package com.alom.service.impl;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.alom.service.RedisService;

@Service
public class RedisServiceImpl implements RedisService {

	private final RedisTemplate<String, Object> redisTemplate;
	
	public RedisServiceImpl(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}
	
	

	@Override
	public void addHashObject(String hashKey, String filedKey, Object data) {
		redisTemplate.opsForHash().put(hashKey, filedKey, data);

	}

	@Override
	public Object retrieveHashObject(String hashKey, String filedKey) {
		return redisTemplate.opsForHash().get(hashKey, filedKey);
	}

}
