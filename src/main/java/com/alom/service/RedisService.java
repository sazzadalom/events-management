package com.alom.service;

public interface RedisService {
	public void addHashObject(String hashKey, String filedKey, Object data);
	public Object retrieveHashObject(String hashKey, String filedKey);
}
