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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.geariot.platform.fishery.dao.TimerDao;
import com.geariot.platform.fishery.entities.Timer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
	@Autowired
	private TimerDao timerDao;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
	}

	@Test
	public void delTest() throws Exception {
		mockMvc.perform(get("/equipment/delEquipments").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("device_sn", "0131380529")
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
				.param("device_sn", "31969520")
		).andDo(print()).andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void dataToday() throws Exception {
		mockMvc.perform(get("/equipment/dataToday").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("device_sn", "31380529").param("way", "1")

		).andDo(print()).andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void data3days() throws Exception {
		mockMvc.perform(get("/equipment/data3days").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("device_sn", "31380529").param("way", "1")

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
			mockMvc.perform(get("/equipment/add").contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.param("device_sn", "0131380529").param("name", "1").param("relation", "1").param("type", "1").param("pondId", "7")).andDo(print())
					.andExpect(status().is2xxSuccessful());
	
		
	}
	@Test
	public void addcontrollerTest() throws Exception {
		mockMvc.perform(post("/equipment/addController")
				.contentType(MediaType.APPLICATION_JSON)
				.content("[{\"device_sn\":\"deptId\",\"pondId\":2222,\"relation\":\"小花\",\"name\":\"name\",\"port\":2222},{\"device_sn\":\"deptId\",\"pondId\":2222,\"relation\":\"小花\",\"name\":\"name\",\"port\":2222},{\"device_sn\":\"deptId\",\"pondId\":2222,\"relation\":\"小花\",\"name\":\"name\",\"port\":2222}]"))
				.andDo(print()).andExpect(status().is2xxSuccessful());

	}
	
	@Test
	public void addSensorTest() throws Exception {
		mockMvc.perform(post("/equipment/addSensor")
				.contentType(MediaType.APPLICATION_JSON)
				.content("[{\"device_sn\":\"56454546\",\"pondId\":1,\"relation\":\"WX14\",\"name\":\"小花\"}]"))
				.andDo(print()).andExpect(status().is2xxSuccessful());

	}
	
	@Test
	public void modifyAioTest() throws Exception {
		mockMvc.perform(post("/equipment/modifyAio")
				.contentType(MediaType.APPLICATION_JSON)
				.content("[{\"id\":15,\"device_sn\":\"56454546\",\"pondId\":1,\"relation\":\"WX14\",\"name\":\"小花15\"}]"))
				.andDo(print()).andExpect(status().is2xxSuccessful());

	}
	
	

	@Test
	public void autosetTest() throws Exception {
		mockMvc.perform(post("/equipment/setTimer")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"startTime\":\"8:30\",\"endTime\":\"8:35\",\"device_sn\":\"0100522\",\"way\":1}"))
				.andDo(print()).andExpect(status().is2xxSuccessful());

	}


	@Test
	public void myEquipmentTest() throws Exception {
		mockMvc.perform(get("/equipment/myEquipment").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("relation", "WX14")).andDo(print()).andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void delEquipmentTest() throws Exception {
		mockMvc.perform(get("/equipment/delEquipments").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("device_sn", "12345")).andDo(print()).andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void adminEquipmentTest() throws Exception {
		mockMvc.perform(get("/equipment/adminFindEquipment").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("page", "1").param("number", "10").param("userName", "杨威")).andDo(print()).andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void companyEquipmentTest() throws Exception {
		mockMvc.perform(get("/equipment/companyFindEquipment").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("page", "1").param("number", "2").param("relation", "CO1")).andDo(print()).andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void dataTest() throws Exception {
		mockMvc.perform(get("/equipment/dataAll").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("device_sn", "0300001")).andDo(print()).andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void queryDataTest() throws Exception {
		mockMvc.perform(get("/equipment/queryAeratorData").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("device_sn", "010001")
				.param("way", "1")).andDo(print()).andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void queryAlarmTest() throws Exception {
		mockMvc.perform(get("/equipment/queryAlarm").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("openId", "11")
				//.param("id", "1")
				//.param("device_sn", "010001")
				//.param("relation", "WX1")
				).andDo(print()).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void triggerTest() throws Exception {
		mockMvc.perform(post("/equipment/triggeractive").contentType(MediaType.APPLICATION_JSON).content("{ \"trigger\" : { \"id\" : 110680, \"threshold\" : 50, \"type\" : \"<\" }, \"current_data\" : [{ \"user_id\" : 134874, \"dev_id\" : \"31538332\", \"ds_id\" : \"temperature\", \"at\" : \"2018-06-01 17:34:04.000\", \"value\" : 1 }] }")).andDo(print()).andExpect(status().is2xxSuccessful());
	}


	@Test
	public void refestTest() throws Exception {
		mockMvc.perform(get("/equipment/refeshcondition").contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("device_sn", "11").param("status","0").param("port","1")
				//.param("id", "1")
				//.param("device_sn", "010001")
				//.param("relation", "WX1")
		).andDo(print()).andExpect(status().is2xxSuccessful());
	}


    @Test
    public void diagno() throws Exception {
        mockMvc.perform(get("/usermanagement/diagnosing").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("relation", "WX17")
                //.param("id", "1")
                //.param("device_sn", "010001")
                //.param("relation", "WX1")
        ).andDo(print()).andExpect(status().is2xxSuccessful());
    }
    
    @Test
    public void testTimer() {
    	/*SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    	Date date = new Date();
    	String s = sdf.format(date);
    	System.out.println(date.getHours());
    	System.out.println(date.getMinutes());
    	System.out.println(s);*/


    	
    	/*List<Timer> lt = timerDao.findAllTimer();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    	try {
			long now = sdf.parse(sdf.format(new Date())).getTime();
			for(Timer timer:lt) {
	        	System.out.println(now);
	    	}
		} catch (ParseException e) {
			e.printStackTrace();
		}*/
    	/*List<Timer> lt = timerDao.findAllTimer();
    	for(Timer timer:lt) {
    		String start = timer.getStartTime();
    		Date startTime = new Date();
    		
//    		startTime.setHours(Integer.parseInt(start.substring(0, 2)));
//    		startTime.setMinutes(minutes);
    		String end = timer.getEndTime();
    		
    	}
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    	try {
			long now = sdf.parse(sdf.format(new Date())).getTime();
			System.out.println(now);
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	System.out.println("123456");*/
    	
    }

}
