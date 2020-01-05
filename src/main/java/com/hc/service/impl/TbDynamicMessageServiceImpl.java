package com.hc.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.hc.common.code.StatusCode;
import com.hc.common.result.ResultBase;
import com.hc.common.result.ResultData;
import com.hc.mapper.tbAreaDynamics.TbDynamicMessageInfoMapper;
import com.hc.pojo.base.PageUtilBean;
import com.hc.pojo.entity.TbDynamicMessageInfo;
import com.hc.pojo.reqBean.DynamicMessageGetInfoReqBean;
import com.hc.pojo.resBean.ResGetTyphoonWarningsBean;
import com.hc.pojo.resBean.ResOneDayDetailsWeatherBean;
import com.hc.pojo.resBean.ResOneDayWeatherBean;
import com.hc.pojo.resBean.ResSevenDaysWeatherBean;
import com.hc.service.TbDynamicMessageService;
import com.hc.test.GetJson;
import com.hc.utils.result.ResultUtil;


@Service("TbDynamicMessageService")
public class TbDynamicMessageServiceImpl implements TbDynamicMessageService{
	@Autowired
	private TbDynamicMessageInfoMapper tbDynamicMessageInfoMapper;
	
	//增加动态消息
	@Override
	public ResultBase addInfo(TbDynamicMessageInfo bean) throws Exception {
		bean.setTbId(null);
		int i = tbDynamicMessageInfoMapper.insertSelective(bean);
		if (i>0) {
			return ResultUtil.getResultBase(true, StatusCode.SUCCESS,"增加成功！");
		}
		return ResultUtil.getResultBase(false, StatusCode.FAIL,"增加失败！");
	}
	//删除动态消息
	@Override
	public ResultBase delInfo(Map<String, Object> map) throws Exception {
		@SuppressWarnings("unchecked")
		List<Integer> li = (List<Integer>)map.get("listid");
		int size = tbDynamicMessageInfoMapper.deleDynamicMessage(li);
		if (size>0) {
			return ResultUtil.getResultBase(true, StatusCode.SUCCESS,"删除成功！");
		}
		return ResultUtil.getResultBase(false, StatusCode.FAIL,"删除失败！");
	}
	//修改动态消息
	@Override
	public ResultBase updateInfo(TbDynamicMessageInfo bean)  throws Exception  {
		int i = tbDynamicMessageInfoMapper.updateByPrimaryKeySelective(bean);
		if (i>0) {
			return ResultUtil.getResultBase(true, StatusCode.SUCCESS,"修改成功！");
		}
		return ResultUtil.getResultBase(false, StatusCode.FAIL,"修改失败！");
	}
	//查询动态消息
	@Override
	public ResultData<PageUtilBean> getAllInfo(DynamicMessageGetInfoReqBean bean)  throws Exception {
		//查询总条数
		int totalCount = tbDynamicMessageInfoMapper.selectDynamicMessageAllCount(bean.getTbType());
		PageUtilBean pages = new PageUtilBean(bean.getPageSize(), totalCount, bean.getPage());
		//查询案件
		List<TbDynamicMessageInfo> list = tbDynamicMessageInfoMapper.selectDynamicMessageAll(bean.getTbType(), pages.limitsTart(),pages.limitsEnd());
		pages.setResults(list);
		return ResultUtil.getResultData(true, StatusCode.SUCCESS, "操作成功！", pages);
	}
	//查询动态消息详情通过ID
	@Override
	public ResultData<TbDynamicMessageInfo> getOneInfoById(Integer tbId)  throws Exception {
		if(null == tbId){
			return ResultUtil.getResultData(false, StatusCode.FAIL, "id不能为空！", new TbDynamicMessageInfo());
		}
		TbDynamicMessageInfo messageInfo = tbDynamicMessageInfoMapper.selectByPrimaryKey(tbId);
		if (messageInfo == null) {
			return ResultUtil.getResultData(false, StatusCode.FAIL, "未找到该数据！", new TbDynamicMessageInfo());
		}
		return ResultUtil.getResultData(true, StatusCode.SUCCESS, "操作成功！", messageInfo );
	}
	//返回天气预报详情
	@Override
	public ResultData<ResSevenDaysWeatherBean> getWeatherForecastDetails() throws Exception {
		return ResultUtil.getResultData(true, StatusCode.SUCCESS, "操作成功！", getWeatherForecastDetailsCrawler());
	}
	//爬虫获取天气预报详情
	public ResSevenDaysWeatherBean getWeatherForecastDetailsCrawler() throws Exception {
		ResSevenDaysWeatherBean bean = new ResSevenDaysWeatherBean();
		List<ResOneDayWeatherBean> beanList = new ArrayList<ResOneDayWeatherBean>();
		String emergencyNewsJsonString = new GetJson().getHttpJson2("http://www.nmc.cn/publish/forecast/AGD/shenzhen.html", 1);
		org.jsoup.nodes.Document emergencyNewsDocument = Jsoup.parse(emergencyNewsJsonString);
		Element forecast = ((Element) emergencyNewsDocument).getElementById("forecast");//获取每条路径
		Elements list_detail = forecast.getElementsByClass("detail");
		list_detail = list_detail != null ? list_detail : null;
		for (int i = 0; i < list_detail.size(); i++) {
			/**
			 * 七天天气预报
			 * ***/
			ResOneDayWeatherBean dayWeatherBean = new ResOneDayWeatherBean();
			Elements list_tr = list_detail.get(i).getElementsByTag("tr");
			Elements list_td_tr0 = list_tr.get(0).getElementsByTag("td");
			Elements list_p_td0_tr0 = list_td_tr0.get(0).getElementsByTag("p");
			if(list_p_td0_tr0.size()==1&&"".equals(list_p_td0_tr0.get(0).text())){
				//日期
				dayWeatherBean.setDate(list_td_tr0.get(0).text());
				//星期
				dayWeatherBean.setWeek(list_td_tr0.get(1).text());
				//日期名称
				dayWeatherBean.setDateName("");
			}else{
				//日期名称
				dayWeatherBean.setDateName(list_p_td0_tr0.get(0).text());
				//星期
				dayWeatherBean.setWeek(list_p_td0_tr0.get(1).text());
				//日期
				dayWeatherBean.setDate(list_td_tr0.get(1).text());
			}
			//天气现象img
			Elements list_td_tr1 = list_tr.get(1).getElementsByTag("td");
			dayWeatherBean.setWicon(list_td_tr1.get(0).attr("src"));
			if(list_td_tr1.size()>1){
				dayWeatherBean.setWicon2(list_td_tr1.get(1).attr("src"));
			}
			//天气现象
			Elements list_td_tr2 = list_tr.get(2).getElementsByTag("td");
			dayWeatherBean.setWdesc(list_td_tr2.get(0).text());
			if(list_td_tr2.size()>1){
				dayWeatherBean.setWdesc2(list_td_tr2.get(1).text());
			}
			//气温
			Elements list_td_tr3 = list_tr.get(3).getElementsByTag("td");
			dayWeatherBean.setTemp(list_td_tr3.get(0).text());
			if(list_td_tr3.size()>1){
				dayWeatherBean.setTemp2(list_td_tr3.get(1).text());
			}
			//风向
			Elements list_td_tr4 = list_tr.get(4).getElementsByTag("td");
			dayWeatherBean.setDirect(list_td_tr4.get(0).text());
			if(list_td_tr4.size()>1){
				dayWeatherBean.setDirect2(list_td_tr4.get(1).text());
			}
			//风度
			Elements list_td_tr5 = list_tr.get(5).getElementsByTag("td");
			dayWeatherBean.setWind(list_td_tr5.get(0).text());
			if(list_td_tr5.size()>1){
				dayWeatherBean.setWind2(list_td_tr5.get(1).text());
			}
			
			/**
			 * 精细预报
			 * ***/
			ResOneDayDetailsWeatherBean weatherBean = new ResOneDayDetailsWeatherBean();
			Element hour3 = ((Element) emergencyNewsDocument).getElementById("hour3");
			Element hour3_day = hour3.getElementById("day"+i);
			//精细预报标题
			Elements hour3_day_first = hour3_day.getElementsByClass("row first");
			Elements hour3_day_first_divs = hour3_day_first.get(0).getElementsByTag("div");
			for (int j = 1; j < hour3_day_first_divs.size(); j++) {
				weatherBean.getForecast()[j-1] = hour3_day_first_divs.get(j).text();
			}
			//天气现象
			Elements hour3_day_secondTqxx = hour3_day.getElementsByClass("row second tqxx");
			Elements hour3_day_secondTqxx_divs = hour3_day_secondTqxx.get(0).getElementsByTag("div");
			for (int j = 1; j < hour3_day_secondTqxx_divs.size(); j++) {
				String img = hour3_day_secondTqxx_divs.get(j).getElementsByTag("img").attr("src");
				if(null!=img&&!"".equals(img)){
					weatherBean.getWeatherPhenomenon()[j-1] = img;
				}else{
					weatherBean.getWeatherPhenomenon()[j-1] = hour3_day_secondTqxx_divs.get(j).text();
				}
			}
			//气温
			Elements hour3_day_wd = hour3_day.getElementsByClass("row wd");
			Elements hour3_day_wd_divs = hour3_day_wd.get(0).getElementsByTag("div");
			for (int j = 1; j < hour3_day_wd_divs.size(); j++) {
				weatherBean.getTemperature()[j-1] = hour3_day_wd_divs.get(j).text();
			}
			//降水
			Elements hour3_day_js = hour3_day.getElementsByClass("row js");
			Elements hour3_day_js_divs = hour3_day_js.get(0).getElementsByTag("div");
			for (int j = 1; j < hour3_day_js_divs.size(); j++) {
				weatherBean.getPrecipitation()[j-1] = hour3_day_js_divs.get(j).text();
			}
			//风速
			Elements hour3_day_winds = hour3_day.getElementsByClass("row winds");
			Elements hour3_day_winds_divs = hour3_day_winds.get(0).getElementsByTag("div");
			for (int j = 1; j < hour3_day_winds_divs.size(); j++) {
				weatherBean.getWindSpeed()[j-1] = hour3_day_winds_divs.get(j).text();
			}
			//风向
			Elements hour3_day_windd = hour3_day.getElementsByClass("row windd");
			Elements hour3_day_windd_divs = hour3_day_windd.get(0).getElementsByTag("div");
			for (int j = 1; j < hour3_day_windd_divs.size(); j++) {
				weatherBean.getDirectionOfTheWind()[j-1] = hour3_day_windd_divs.get(j).text();
			}
			//气压
			Elements hour3_day_qy = hour3_day.getElementsByClass("row qy");
			Elements hour3_day_qy_divs = hour3_day_qy.get(0).getElementsByTag("div");
			for (int j = 1; j < hour3_day_qy_divs.size(); j++) {
				weatherBean.getAirPressure()[j-1] = hour3_day_qy_divs.get(j).text();
			}
			//相对湿度
			Elements hour3_day_xdsd = hour3_day.getElementsByClass("row xdsd");
			Elements hour3_day_xdsd_divs = hour3_day_xdsd.get(0).getElementsByTag("div");
			for (int j = 1; j < hour3_day_xdsd_divs.size(); j++) {
				weatherBean.getRelativeHumidity()[j-1] = hour3_day_xdsd_divs.get(j).text();
			}
			//云量
			Elements hour3_day_yl = hour3_day.getElementsByClass("row yl");
			Elements hour3_day_yl_divs = hour3_day_yl.get(0).getElementsByTag("div");
			for (int j = 1; j < hour3_day_yl_divs.size(); j++) {
				weatherBean.getCloudCover()[j-1] = hour3_day_yl_divs.get(j).text();
			}
			//能见度
			Elements hour3_day_njd = hour3_day.getElementsByClass("row njd");
			Elements hour3_day_njd_divs = hour3_day_njd.get(0).getElementsByTag("div");
			for (int j = 1; j < hour3_day_njd_divs.size()-1; j++) {
				weatherBean.getVisibility()[j-1] = hour3_day_njd_divs.get(j+1).text();
			}
			dayWeatherBean.setWeatherBean(weatherBean);
			beanList.add(dayWeatherBean);
		}
		bean.setBeanList(beanList);
		System.out.println(JSON.toJSONString(bean));
		return bean;
	}
	@Override
	public ResultData<ResGetTyphoonWarningsBean> getTyphoonWarning() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
