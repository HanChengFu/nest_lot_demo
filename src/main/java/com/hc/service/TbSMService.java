package com.hc.service;

import javax.servlet.http.HttpServletRequest;

import com.hc.common.exception.CustomException;
import com.hc.common.result.ResultBase;
import com.hc.pojo.email.TbEmail;

public interface TbSMService {

	ResultBase sendSM(TbEmail tbEmail,HttpServletRequest request) throws Exception,CustomException;
	
}