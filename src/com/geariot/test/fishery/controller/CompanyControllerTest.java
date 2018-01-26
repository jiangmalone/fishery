package com.geariot.test.fishery.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.PrintingResultHandler;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * @author mxy940127
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations={"classpath:application.xml","classpath:springMVC.xml"})
//@Transactional
public class CompanyControllerTest {
	
	private MockMvc mockMvc;
	
	@Autowired
	private WebApplicationContext context;
	
	@Before
	public void setup(){
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
	}
	
	@Test
	public void addCompanyTest() throws Exception{
		mockMvc.perform(post("/usermanagement/addCompany").
				contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", "南京小易信息")
				.param("phone", "15005185697")
				.param("life", "10")
				.param("mail_address", "841114322@qq.com")
				.param("address", "南京玄武区D206")
				
				)
		.andDo(print()).andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void addWXUserTest() throws Exception{
		mockMvc.perform(post("/usermanagement/addWXUser").
				contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", "张三")
				.param("phone", "15005185697")
				.param("life", "10")
				.param("sex", "男")
				.param("address", "南京玄武区D206")
				.param("headimgurl", "www.baidu.com/image/1.jpg")
				.param("openId", "111111111")
				
				)
		.andDo(print()).andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void deleteCompanyTest() throws Exception{
		mockMvc.perform(post("/usermanagement/delCompany").
				contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("companyIds", "4")
			
				
				)
		.andDo(print()).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void deleteUserTest() throws Exception{
		mockMvc.perform(post("/usermanagement/delWXUser").
				contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("WXUserIds", "1")
			
				
				)
		.andDo(print()).andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void modifyUserTest() throws Exception{
		mockMvc.perform(post("/usermanagement/modifyCompany").
				contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("Id", "5")
				.param("name", "南京小易信息")
				.param("phone", "11111111")
				.param("life", "10")
				.param("mail_address", "841114322@qq.com")
				.param("address", "南京玄武区D206")
				)
		.andDo(print()).andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void modifyWXUserTest() throws Exception{
		mockMvc.perform(post("/usermanagement/modifyWXUser").
				contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("Id", "3")
				.param("name", "张三")
				.param("phone", "15005185697")
				.param("life", "10")
				.param("sex", "女")
				.param("address", "南京玄武区D206")
				.param("headimgurl", "www.baidu.com/image/1.jpg")
				.param("openId", "111211111")
				)
		.andDo(print()).andExpect(status().is2xxSuccessful());
	}
	@Test
	public void queryUserTest() throws Exception{
		mockMvc.perform(get("/usermanagement/queryCompany").
				contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", "小")
				.param("page", "0")
				.param("number", "5")
				)
		.andDo(print()).andExpect(status().is2xxSuccessful());
	}

	
	@Test
	public void queryWXUserTest() throws Exception{
		mockMvc.perform(get("/usermanagement/queryWXUser").
				contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", "小")
				.param("page", "2")
				.param("number", "3")
				)
		.andDo(print()).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void relationTest() throws Exception{
		mockMvc.perform(get("/usermanagement/relationDetail").
				contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("relation", "CO14")
				)
		.andDo(print()).andExpect(status().is2xxSuccessful());
	}
	
}
