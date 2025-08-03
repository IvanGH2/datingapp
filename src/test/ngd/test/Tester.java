package ngd.test;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;

import java.util.LinkedHashMap;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ngd.controller.display.UserMessageDisplay;
import ngd.controller.response.JsonResponse;

@SpringBootTest
@AutoConfigureMockMvc
public class Tester {
	
	private  final RestTemplate restTemplate = new RestTemplate();
	
	private final  String url = "http://localhost:8080/";
	
	private static final HttpHeaders headers = new HttpHeaders();
	
	@Autowired
	private MockMvc mockMvc;
	
	/*@BeforeAll
	public static void setup() {
		
		String userCredentials = "user1:user1Pass";                     
		final String encodedUserCredentials= Base64.getMimeEncoder().encodeToString(userCredentials.getBytes());        
		headers.set( "Authorization", "Basic " + encodedUserCredentials );
		//headers.setContentType( MediaType.TEXT_PLAIN );
		headers.setAccept( Arrays.asList( MediaType.APPLICATION_JSON ) );
	
	}*/
	
	@Test
	public void simulateLogin() throws Exception {
		
		mockMvc.perform(formLogin("/login").user("Maya").password("Ma_graz22?"))
	       .andExpect(authenticated().withUsername("Maya"));
	
	}
	
	//@Test
	public void getTargetMessages() {
		
		HttpEntity<String> request = new HttpEntity<>( headers ); 
		String target = "?target=Maya&offsetMsg=0";
		
		ResponseEntity<JsonResponse> responseEntity = restTemplate.exchange( url + "targetMessages" + target, 
				HttpMethod.GET, request, JsonResponse.class );
		
		if (responseEntity.getStatusCode() == HttpStatus.OK  ) {
			
			final JsonResponse response = responseEntity.getBody( );
			
			if( response.getStatus() == JsonResponse.ResponseStatus.SUCCESS ) {
				
				@SuppressWarnings("unchecked")
				List<UserMessageDisplay> msgDisplay = 
						convertToList( (List<LinkedHashMap<String, UserMessageDisplay>>)response.getResult());
			}
		}
	}
	
	private List<UserMessageDisplay> convertToList( List<LinkedHashMap<String, UserMessageDisplay>> lhm ){
		final ObjectMapper mapper = new  ObjectMapper();
		final List<UserMessageDisplay> list = mapper.convertValue(lhm, new TypeReference<List<UserMessageDisplay>>() {});
		return list;
	}
}
