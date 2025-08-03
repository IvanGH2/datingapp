package ngd.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.LinkedHashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ngd.controller.display.UserMessageDisplay;
import ngd.controller.response.JsonResponse;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Tester {
		
	
	private MockHttpSession session;
	
	//private static final HttpHeaders headers = new HttpHeaders();	
	@Autowired
	private  MockMvc mockMvc;
	
	@BeforeAll
	public  void setup() throws Exception {
		
		session = (MockHttpSession) mockMvc.perform(formLogin("/letsclick").user("Petra").password("Petrapsw2?"))
			    .andExpect(status().is3xxRedirection())
			    .andReturn()
			    .getRequest()
			    .getSession();
	}
	
	/*@BeforeAll
	public void setup() {
		
		String userCredentials = "user1:user1Pass";                     
		final String encodedUserCredentials= Base64.getMimeEncoder().encodeToString(userCredentials.getBytes());        
		headers.set( "Authorization", "Basic " + encodedUserCredentials );
		//headers.setContentType( MediaType.TEXT_PLAIN );
		headers.setAccept( Arrays.asList( MediaType.APPLICATION_JSON ) );
	
	}*/
	
	//@Test
	public void simulateLogin() throws Exception {
		
		mockMvc.perform( formLogin("/letsclick").user("Petra").password("Petrapsw2?") )
	       .andExpect(authenticated().withUsername("Petra"));
		
	}
	@Test
	public void getPing() throws Exception {
		
		mockMvc.perform( get( "/ping").session(session)).
		andExpect( content().string("pong") );

	}
	@SuppressWarnings("unchecked")
	@Test
	public void getTargetMessages() throws Exception {
		
		//HttpEntity<String> request = new HttpEntity<>( headers ); 
		String target = "/targetMessages?target=Marko_zg&offsetMsg=0";
	
		MvcResult result = mockMvc.perform( get( target ).session( session )).andReturn();
		
		String response = result.getResponse().getContentAsString();
		//System.out.println(response);
		final JsonResponse jsonResp = convert(response);
		final List<UserMessageDisplay> listMsg;
		if( jsonResp.getStatus() == JsonResponse.ResponseStatus.SUCCESS ) {
			
			listMsg = 
					convertToList( (List< LinkedHashMap<String, UserMessageDisplay>>) jsonResp.getResult() );
					
			UserMessageDisplay userMsg = listMsg.get(0);
			
			assertEquals("sorry, Ive been busy, I get back to you later", userMsg.getMsgContent());
			
			userMsg = listMsg.get(1);
			
			assertEquals("yeah, i know, ", userMsg.getMsgContent());
			
		}
		
	}
	@Test
	public void changeLanguage() throws Exception {
		
		mockMvc.perform( get("/letsclick?lang=hr"))
			.andExpect( MockMvcResultMatchers.view().name("pages/letsclick") );
			
		MvcResult result = mockMvc.perform( get("/letsclick?lang=en"))
		.andExpect( MockMvcResultMatchers.view().name("pages/letsclick") )
		.andReturn();		
		
		assertEquals("lang=en", result.getRequest().getQueryString() );
		
	}
	private JsonResponse convert(String json) throws JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		JsonResponse response = mapper.readValue(json, JsonResponse.class);
		return response;
		
	}
	
	private List<UserMessageDisplay> convertToList( List<LinkedHashMap<String, UserMessageDisplay>> lhm ){
		final ObjectMapper mapper = new  ObjectMapper();
		final List<UserMessageDisplay> list = mapper.convertValue(lhm, new TypeReference<List<UserMessageDisplay>>() {});
		return list;
	}
}

