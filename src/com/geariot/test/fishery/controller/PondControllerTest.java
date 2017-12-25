/**
 * 
 */
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
import org.springframework.test.web.servlet.result.PrintingResultHandler;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * @author mxy940127
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations={"classpath:application.xml","classpath:springMVC.xml"})
public class PondControllerTest {
	
	private MockMvc mockMvc;
	
	@Autowired
	private WebApplicationContext context;
	
	@Before
	public void setup(){
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
	}
	
	@Test
	public void addPondTest() throws Exception{
		mockMvc.perform(post("/pond/addPond").
				contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("name", "南京小易信息")
				.param("area", "20.0")
				.param("address", "南京小易信息")
				.param("longitude", "75.1234")
				.param("latitude", "10.23548")
				.param("water_source", "changjiang")
				.param("sediment_thickness", "0.5")
				.param("depth", "15.0")
				.param("density", "1.2")
				.param("relation", "WX12")
				.param("name", "南京小易信息")
				.param("fish_categorys", "鲫鱼")
				.param("fish_categorys", "刀鱼")
				)
		.andDo(print()).andExpect(status().is2xxSuccessful());
	}

	
}
