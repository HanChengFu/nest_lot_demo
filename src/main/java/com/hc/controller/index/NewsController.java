package com.hc.controller.index;

import java.util.List;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hc.service.TbAreaDynamicsService;
import com.hc.service.TbEmergencyNewsService;
import com.hc.service.TbNoticeService;
import com.hc.service.TbWorkDynamicsService;

@Controller
@RequestMapping("/index/news")
@ResponseBody
public class NewsController {
	
	@Autowired 
	private TbWorkDynamicsService tbWorkDynamicsService;
	@Autowired 
	private TbNoticeService tbNoticeService;
	@Autowired 
	private TbAreaDynamicsService tbAreaDynamicsService;
	@Autowired 
	private TbEmergencyNewsService 	tbEmergencyNewsService; 
	
	@RequestMapping("/everyAreaDynamics")
	public List<T> queryEveryAreaDynamics(@RequestParam("type") String type) throws Exception {
		System.out.println("-*********************************-" + type);
		if(type==null || type=="") {
			return tbWorkDynamicsService.queryWorkDynamics();
		}else {
			if(Integer.valueOf(type)==1) {
				return tbWorkDynamicsService.queryWorkDynamics();
			}else if(Integer.valueOf(type)==2) {
				return tbNoticeService.queryNotice();
			}else if(Integer.valueOf(type)==3) {
				return tbAreaDynamicsService.queryEveryAreaDynamics();
			}else if(Integer.valueOf(type)==4) {
				return tbEmergencyNewsService.queryEmergencyNews(); 
			}
		}
		return null;
		
	}
	

}
