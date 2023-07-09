package ngd.controller;


import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


public class CommonObject {
	
	public static ConcurrentMap<String, SseEmitter> sseUsers = new ConcurrentHashMap<>();
	
	public static ConcurrentMap<String, List<Integer>> seenViews = new ConcurrentHashMap<>();
	
	public static ConcurrentMap<String, List<Integer>> msgSent = new ConcurrentHashMap<>();
	

}
