package com.geariot.test.fishery.controller;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
public class AdminControllerTest {
	
	private MockMvc mockMvc;
	
	@Autowired
	private WebApplicationContext context;
	
	@Before
	public void setup(){
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
	}
	
	@Test
	public void addAdminTest() throws Exception{
		mockMvc.perform(post("/admin/add").
				contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("account", "admin")
				.param("password", "test")
				.param("type", "0")
				.param("companyId", "0")
				.param("commment", "测试")
				)
		.andDo(print()).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void loginTest() throws Exception{
		mockMvc.perform(post("/admin/login").
				contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("account", "admin")
				.param("password", "test")
				)
		.andDo(print()).andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void logoutTest() throws Exception{
		mockMvc.perform(get("/admin/logout").
				contentType(MediaType.APPLICATION_FORM_URLENCODED)
				)
		.andDo(print()).andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void modifyAdminTest() throws Exception{
		mockMvc.perform(post("/admin/modify").
				contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("adminId", "1")
				.param("password", "admin")
				)
		.andDo(print()).andExpect(status().is2xxSuccessful());
	}
	
}

