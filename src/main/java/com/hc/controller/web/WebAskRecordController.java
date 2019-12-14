package com.hc.controller.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.hc.common.exception.CustomException;
import com.hc.common.result.ResultBase;
import com.hc.common.result.ResultQuery;
import com.hc.pojo.askRecord.TbAskRecord;
import com.hc.service.TbAskRecordService;

@Controller
@RequestMapping("/web/askRecord")
@ResponseBody
public class WebAskRecordController {

	@Autowired
	private TbAskRecordService tbAskRecordService;
	
    @RequestMapping("getAskRecord")
	ResultQuery<TbAskRecord> getAskRecord(@RequestBody TbAskRecord ask,HttpServletRequest request) throws Exception,CustomException{
		return tbAskRecordService.getAskRecord(ask, request);
	}
	
    @RequestMapping("insertSelective")
	ResultBase insertSelective(@RequestBody TbAskRecord ask,HttpServletRequest request) throws Exception,CustomException{
		return tbAskRecordService.insertSelective(ask, request);
	}
	
}
