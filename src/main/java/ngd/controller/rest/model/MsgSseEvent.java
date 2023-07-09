package ngd.controller.rest.model;

public class MsgSseEvent implements SseEvent{

	@Override
	public SseEventType getSseEventType() {
		
		return SseEvent.SseEventType.MSG_SSE;
	}

}
