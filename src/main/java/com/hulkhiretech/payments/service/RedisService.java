package com.hulkhiretech.payments.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RedisService {
	
	
	private final RedisTemplate<String,String> redisTemplate;
	
	private final ListOperations<String,String> listOperations;
	
	private final ValueOperations<String,String> valueOperations;
	
	private final HashOperations<String,String,String> mapOperations;
	
	@Autowired
	public RedisService(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
		this.listOperations = redisTemplate.opsForList();
		this.valueOperations = redisTemplate.opsForValue();
		this.mapOperations = redisTemplate.opsForHash();
	}
	
	public void setValueToList(String key, String value) {
		log.info("Adding value to list in Redis: key={}, value={}", key, value);
		
            		listOperations.rightPush(key, value);
	}
	
	public List<String> getListValues(String key) {
		
		List<String> list=listOperations.range(key, 0, -1);
		log.info("Retrieved list from Redis: key={}, values={}", key, list);
		return list;
	}
	
	public void setValue(String key, String value) {
		valueOperations.set(key, value);
	}
	
	public String getValue(String key) {
		return valueOperations.get(key);
	}
	
	public void setExprirykeyvalue(String key, String value, long timeout) {
		valueOperations.set(key, value, timeout, TimeUnit.SECONDS);
	}
	
	public void setMapValue(String mapName, String key, String value) {
		mapOperations.put(mapName, key, value);
	}
	
	public String getMapValue(String mapName, String key) {
		return mapOperations.get(mapName, key);
	}
	
	public Map<String,String> getAllEntries(String mapName) {
		return mapOperations.entries(mapName);
	}
	
	
	
	
	
	
	

}
