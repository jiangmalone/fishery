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

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations={"classpath:application.xml","classpath:springMVC.xml"})
public class BindControllerTest {
	
	private MockMvc mockMvc;
	
	@Autowired
	private WebApplicationContext context;
	
	@Before
	public void setup(){
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
	}
	
	@Test
	public void delBindTest() throws Exception{
		mockMvc.perform(get("/bind/delSensorOrAIOBind").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("device_sn", "0300001")
				.param("type", "1")
				)
		.andDo(print()).andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void delBindSensorControllerTest() throws Exception{
		mockMvc.perform(get("/bind/delSensorControllerBind").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("sensorId", "1")
				.param("sensor_port", "2")
				)
		.andDo(print()).andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void bindSensorControllerTest() throws Exception{
		mockMvc.perform(get("/bind/bindSensorController").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("sensorId", "1")
				.param("sensor_port", "2")
				.param("controllerId", "1")
				.param("controller_port", "3")
				)
		.andDo(print()).andExpect(status().is2xxSuccessful());
	}
}
