package ngd.controller.rest.client;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

//@Component
public class RestWebClient {
	

	
	public byte[] downloadImageAsBytes(final String imageUrl) {
		
		RestTemplate restTemplate = new RestTemplate();
		byte[] imgBytes = null;
		try {
			
			ResponseEntity<byte[]> responseEntity = restTemplate.getForEntity(imageUrl, byte[].class);
			if (responseEntity.getStatusCode() == HttpStatus.OK) {
				imgBytes = responseEntity.getBody();
			
			}
		}catch(RestClientException e) {
			System.out.println(e.getMessage());
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		return imgBytes;
		
	}

}
