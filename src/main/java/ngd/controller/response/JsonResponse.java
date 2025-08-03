package ngd.controller.response;

import java.io.Serializable;

public class JsonResponse implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6094306912603705651L;

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
