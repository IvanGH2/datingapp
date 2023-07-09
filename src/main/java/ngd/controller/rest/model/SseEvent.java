package ngd.controller.rest.model;


public interface SseEvent {
	
	public enum SseEventType { MSG_SSE, VIEW_SSE };
	
	public SseEventType getSseEventType(); 
		
}
