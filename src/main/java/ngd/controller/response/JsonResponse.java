package ngd.controller.response;



public class JsonResponse {
	private ResponseStatus status;
	private Object result;

	public enum ResponseStatus {
		SUCCESS,
		FAIL;
	}

	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "JsonResponse [status=" + status + ", result=" + result + "]";
	}

}
