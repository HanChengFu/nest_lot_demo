package com.hc.mapper.askRecord;

import java.util.List;

import com.hc.pojo.askRecord.TbAskRecord;

public interface TbAskRecordMapper {
	
	int insertSelective(TbAskRecord ask);
	
	List<TbAskRecord> getAskRecord(TbAskRecord ask);
	
	int getAskRecordCount();
	
}