package com.hc.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.formula.functions.T;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hc.common.exception.CustomException;
import com.hc.common.tools.DownloadImage;
import com.hc.common.tools.Tools;
import com.hc.mapper.tbAreaDynamics.TbAreaDynamicsMapper;
import com.hc.mapper.tbAreaDynamics.TbEmergencyNewsMapper;
import com.hc.mapper.tbAreaDynamics.TbNoticeMapper;
import com.hc.mapper.tbAreaDynamics.TbWorkDynamicsMapper;
import com.hc.pojo.callCenter.CallCenter;
import com.hc.pojo.entity.TbWorkDynamics;
import com.hc.service.TbWorkDynamicsService;
import com.hc.test.GetJson;

@Service("tbWorkDynamicsService")
public class TbWorkDynamicsServiceImpl implements TbWorkDynamicsService{
	
	private static String workDynamicsUrl = "http://yjgl.sz.gov.cn/zwgk/xxgkmu/qt/gzdt";  //工作动态
	private static String noticesUrl = "http://yjgl.sz.gov.cn/zwgk/xxgkmu/qt/tzgg/";  //通知公告
	private static String everyAreaDynamicsUrl = "http://yjgl.sz.gov.cn/zwgk/xxgkmu/qt/gqdt/";  //各区动态
	private static String emergencyNewsUrl = "http://yjgl.sz.gov.cn/zwgk/xxgkmu/qt/yjyw/";  //应急要闻
	
	
	@Autowired
	private TbWorkDynamicsMapper tbWorkDynamicsMapper;//工作动态
	@Autowired
	private TbNoticeMapper  tbNoticeMapper;//通知公告
	@Autowired
	private TbAreaDynamicsMapper tbAreaDynamicsMapper;//各区动态
	@Autowired
	private TbEmergencyNewsMapper tbEmergencyNewsMapper;//应急要闻
	
	
	@Override
	public List<T> queryWorkDynamics() {
		return tbWorkDynamicsMapper.queryWorkDynamics();
	}
	
	
	
	/**
	 * 爬虫 (工作动态、通知公告、各区动态、应急要闻) 的数据
	 */
	
	@Override 
	public int insertReptileData(TbWorkDynamics tbWorkDynamics) throws Exception,CustomException{
		
		//工作动态
		String workDynamicsJsonString = new GetJson().getHttpJson2(workDynamicsUrl, 1);
		org.jsoup.nodes.Document workDynamicsDocument = Jsoup.parse(workDynamicsJsonString);
		List<String> workDynamicsList = allCaseListItem(workDynamicsDocument);
		Integer index = 0;
		for (int i = 0; i < workDynamicsList.size(); i++) {
			String[] hrefAndtimes = workDynamicsList.get(i).split("@Time");
//          System.out.println("key= " + key + " and value= " + workDynamicsMap.get(key));
			TbWorkDynamics wd = new TbWorkDynamics();
//			String address = "http://yjgl.sz.gov.cn/zwgk/xxgkmu/qt/gzdt/201912/t20191217_18936590.htm";
			String address = workDynamicsUrl+hrefAndtimes[0];
			String dayLine = new GetJson().getHttpJson2(address, 1);
			org.jsoup.nodes.Document dayLineDocument = Jsoup.parse(dayLine);
			wd = solvePageData(dayLineDocument,wd);
			wd.setCreateTime(Tools.getAPIresponseDateTime());//当前时间
			wd.setDataTime(hrefAndtimes[1]);//时间
//			index = tbWorkDynamicsMapper.insertWorkDynamics(wd);
			//把图片保存到本地
			
			String[] tempImgUrls = wd.getTempImgUrl().split("@img");
			if(tempImgUrls!=null && tempImgUrls.length>0) {
				for (int j = 0; j < tempImgUrls.length; j++) {
					String filename = address.substring(0,address.lastIndexOf("/")+1);
					DownloadImage.download(filename+tempImgUrls[j], "D:\\upload\\workDynamics");
				}
			}
			System.out.println(index+"===========================");
		}
		
		//通知公告
		String noticesJsonString = new GetJson().getHttpJson2(noticesUrl, 1);
		org.jsoup.nodes.Document noticesDocument = Jsoup.parse(noticesJsonString);
		List<String> noticesList = allCaseListItem(noticesDocument);
		for (int i = 0; i < noticesList.size(); i++) {
			String[] hrefAndtimes = noticesList.get(i).split("@Time");
			TbWorkDynamics wd = new TbWorkDynamics();
			String address = noticesUrl+hrefAndtimes[0] ;
			String dayLine = new GetJson().getHttpJson2(address, 1);
			org.jsoup.nodes.Document dayLineDocument = Jsoup.parse(dayLine);
			wd = solvePageData(dayLineDocument,wd);
			wd.setCreateTime(Tools.getAPIresponseDateTime());//当前时间
			wd.setDataTime(hrefAndtimes[1]);//时间
			index = tbNoticeMapper.insertNotice(wd);
			System.out.println(index+"===========================");
			if(wd.getTempImgUrl()!=null && !"".equals(wd.getTempImgUrl())) {
				String[] tempImgUrls = wd.getTempImgUrl().split("@img");
				for (int j = 0; j < tempImgUrls.length; j++) {
					String filename = address.substring(0,address.lastIndexOf("/")+1);
					DownloadImage.download(filename+tempImgUrls[j], "D:\\upload\\notices");
				}
			}
		}
		
		//各区动态
		String everyAreaDynamicsJsonString = new GetJson().getHttpJson2(everyAreaDynamicsUrl, 1);
		org.jsoup.nodes.Document everyAreaDynamics = Jsoup.parse(everyAreaDynamicsJsonString);
		List<String> everyAreaDynamicsList = allCaseListItem(everyAreaDynamics);
		for (int i = 0; i < everyAreaDynamicsList.size(); i++) {
			String[] hrefAndtimes = everyAreaDynamicsList.get(i).split("@Time");
			TbWorkDynamics wd = new TbWorkDynamics();
			String address = everyAreaDynamicsUrl+hrefAndtimes[0] ;
			String dayLine = new GetJson().getHttpJson2(address, 1);
			org.jsoup.nodes.Document dayLineDocument = Jsoup.parse(dayLine);
			wd = solvePageData(dayLineDocument,wd);
			wd.setCreateTime(Tools.getAPIresponseDateTime());//当前时间
			wd.setDataTime(hrefAndtimes[1]);//时间
			index = tbAreaDynamicsMapper.insertAreaDynamics(wd);
			System.out.println(index+"===========================");
			if(wd.getTempImgUrl()!=null && !"".equals(wd.getTempImgUrl())) {
				String[] tempImgUrls = wd.getTempImgUrl().split("@img");
				for (int j = 0; j < tempImgUrls.length; j++) {
					String filename = address.substring(0,address.lastIndexOf("/")+1);
					DownloadImage.download(filename+tempImgUrls[j], "D:\\upload\\everyAreaDynamics");
				}
			}
		}
		
		//应急要闻
		String emergencyNewsJsonString = new GetJson().getHttpJson2(emergencyNewsUrl, 1);
		org.jsoup.nodes.Document emergencyNewsDocument = Jsoup.parse(emergencyNewsJsonString);
		List<String> emergencyNewsList = allCaseListItem(emergencyNewsDocument);
		for (int i = 0; i < emergencyNewsList.size(); i++) {
			String[] hrefAndtimes = emergencyNewsList.get(i).split("@Time");
			TbWorkDynamics wd = new TbWorkDynamics();
			String address = emergencyNewsUrl+hrefAndtimes[0] ;
			String dayLine = new GetJson().getHttpJson2(address, 1);
			org.jsoup.nodes.Document dayLineDocument = Jsoup.parse(dayLine);
			wd = solvePageData(dayLineDocument,wd);
			wd.setCreateTime(Tools.getAPIresponseDateTime());//当前时间
			wd.setDataTime(hrefAndtimes[1]);//时间
			index = tbEmergencyNewsMapper.insertEmergencyNews(wd);
			System.out.println(index+"===========================");
			if(wd.getTempImgUrl()!=null && !"".equals(wd.getTempImgUrl())) {
				String[] tempImgUrls = wd.getTempImgUrl().split("@img");
				for (int j = 0; j < tempImgUrls.length; j++) {
					String filename = address.substring(0,address.lastIndexOf("/")+1);
					DownloadImage.download(filename+tempImgUrls[j], "D:\\upload\\emergencyNews");
				}
			}
		}

        return index;
	}

	
	/**
	 * 获取每个动态中的每一条数据   (工作动态、通知公告、各区动态、应急要闻)
	 * @param leakListDoc
	 * @param tbWorkDynamics
	 * @return
	 */
	public static TbWorkDynamics solvePageData(Document leakListDoc,TbWorkDynamics tbWorkDynamics) {
	 Elements ulElements = ((Element) leakListDoc).getElementsByClass("view_title");
	 String str = new String();
	 String strImgUrl = new String ();
	 Elements TRS_Editor_ulElements = ((Element) leakListDoc).getElementsByClass("TRS_Editor");
	 List<Node> node = TRS_Editor_ulElements != null ? TRS_Editor_ulElements.get(0).childNodes():null;
	 if(node!=null) {
		 for(int j=0;j<node.size();j++) {
				Node d = node.get(j) ;
//				String dd = d.nodeName();
				if(d.nodeName().equals("p")) {
					Element r = (Element) node.get(j);
					String oldsrc = r.getElementsByTag("img").attr("OLDSRC");//获取图片
					String text = r.text();//获取内容
//					System.out.println(oldsrc+"==="+text);
					if(oldsrc!=null && !"".equals(oldsrc)) {
						str = str + "@img "+ oldsrc + "@imgText";
						strImgUrl = strImgUrl + oldsrc + "@img";
					}else {
						str = str + text+"@imgText";
					}
				}
			}
	 }
//	 System.out.println(str);
	 tbWorkDynamics.setTbTitle(ulElements.text());
	 tbWorkDynamics.setTbContent(str);
	 tbWorkDynamics.setTempImgUrl(strImgUrl);
	 
//	 String dd = TRS_Editor_ulElements.text();
	 return tbWorkDynamics;
	}
	
	/**
	 * 获取每条动态的信息 (工作动态、通知公告、各区动态、应急要闻)
	 * @param leakListDoc
	 * @return
	 */
	public static List<String> allCaseListItem(Document leakListDoc){
		
		Elements list_boxElements = ((Element) leakListDoc).getElementsByClass("list_item");//获取每条路径
		list_boxElements = list_boxElements != null ? list_boxElements : null;
//		String ddd = list_boxElements.attr("href");
//		Map<String,String> m1 =new HashMap<String,String>();
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < list_boxElements.size(); i++) {
			String href = list_boxElements.get(i).attr("href");
			String  time = list_boxElements.get(i).getElementsByTag("font").text();
			if(href!=null && !"".equals(href) && (time!=null && !"".equals(time))) {
				String hrefAndtimes = (href == null ? "" : href.substring(1)) +"@Time" + (time == null ? "" : time);
				list.add(hrefAndtimes);
//				System.out.println(href.substring(1)+"===="+time);
//				m1.put(href.substring(1), time);
			}
		}
		return list;
	}  
	
}
