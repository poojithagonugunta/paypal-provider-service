package com.hulkhiretech.payments.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hulkhiretech.payments.service.RedisService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
public class RedisController {
	
	private final RedisService redisService;
	
	
	@PostMapping("/List/{key}")
	public void addToList(@PathVariable String key, @RequestBody String value) {
		redisService.setValueToList(key, value);
	}
	
	@GetMapping("/List/{key}")
	public List<String> getList(@PathVariable String key) {
		return redisService.getListValues(key);
	}

}
