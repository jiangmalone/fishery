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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * @author mxy940127
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:application.xml", "classpath:springMVC.xml" })
public class EquipmentControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext context;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
	}

	@Test
	public void delTest() throws Exception {
		mockMvc.perform(post("/equipment/delEquipments").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("device_sns", "0102030405").param("device_sns", "0202030405")

		).andDo(print()).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void setLimitTest() throws Exception {
		mockMvc.perform(post("/equipment/limit").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("device_sn", "1507375").param("low_limit", "1.1").param("up_limit", "9.1")
				.param("high_limit", "11.1")

		).andDo(print()).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void setTimerTest() throws Exception {
		mockMvc.perform(post("/equipment/timer").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("device_sn", "032222220").param("startTime", "8:30").param("endTime", "16:30")

		).andDo(print()).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void excelExportTest() throws Exception {
		mockMvc.perform(get("/equipment/exportData").contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
				.param("device_sn", "032222220").param("startTime", "2017-12-27 14:03:13")
				.param("endTime", "2017-12-27 14:03:14")

		).andDo(print()).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void realTimeData() throws Exception {
		mockMvc.perform(get("/equipment/realTimeData").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("device_sn", "032222220")

		).andDo(print()).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void queryTest() throws Exception {
		mockMvc.perform(get("/equipment/query").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("device_sn", "0402030405").param("relation", "1").param("name", "1").param("page", "0")
				.param("number", "3")

		).andDo(print()).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void addTest() throws Exception {
		int i = 0;
		while (i < 10) {
			mockMvc.perform(post("/equipment/add").contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.param("device_sn", String.format("0", i)).param("name", "1").param("relation", "1")).andDo(print())
					.andExpect(status().is2xxSuccessful());
			i++;
		}
	}
	
	@Test
	public void myEquipmentTest() throws Exception {
		mockMvc.perform(get("/equipment/myEquipment").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("relation", "WX4")).andDo(print()).andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void adminEquipmentTest() throws Exception {
		mockMvc.perform(get("/equipment/adminFindEquipment").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("page", "1").param("number", "2").param("device_sn", "0001").param("companyName", "小易")).andDo(print()).andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void companyEquipmentTest() throws Exception {
		mockMvc.perform(get("/equipment/companyFindEquipment").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("page", "1").param("number", "2").param("relationId", "CO1")).andDo(print()).andExpect(status().is2xxSuccessful());
	}
}
