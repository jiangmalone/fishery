/**
 * 
 */
package com.geariot.test.fishery.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author mxy940127
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations={"classpath:application.xml","classpath:springMVC.xml"})
public class WebServiceControllerTest {

private MockMvc mockMvc;
	
	@Autowired
	private WebApplicationContext context;
	
	@Before
	public void setup(){
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
	}
	
/*	@Test
	public void weatherTest() throws Exception{
		
		mockMvc.perform(get("/webService/weather").
				contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("city", "320100"))
		.andDo(print()).andExpect(status().is2xxSuccessful());
	}*/
	@Test
	public void weatherTest() throws Exception{
		
		mockMvc.perform(get("/webService/weather").
				contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("lon", "118.87474").param("lat", "32.13955"))
		.andDo(print()).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void infoTest() throws Exception{
		mockMvc.perform(get("/webService/getuserinfo").
				contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("code", "023oRal426IqLL0BTxm42Lsbl42oRalk"))
				.andDo(print()).andExpect(status().is2xxSuccessful());
	}


}
