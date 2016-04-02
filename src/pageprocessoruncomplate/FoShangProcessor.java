package pageprocessoruncomplate;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

public class FoShangProcessor implements PageProcessor {
	
	public static final String URL_LIST = "http://www\\.fsggzy\\.cn/gcjy/gc_zbxx/gc_zbsz/index*";
	public static final String URL_DETAILS = "http://www\\.fsggzy\\.cn/gcjy/gc_zbxx/gc_zbsz/\\d+/t\\d+_\\d+\\.html";

	//市值
	public static String url = "http://www.fsggzy.cn/gcjy/gc_zbxx/gc_zbsz/index.html";
	//各区
	public static String url1 = "http://www.fsggzy.cn/gcjy/gc_zbxx/jygg_gq/index.html";
	
	public static String test = "http://www.fsggzy.cn/gcjy/gc_zbxx/gc_zbsz/201603/t20160325_5552037.html";

	private Site site = Site.me().setRetryTimes(3).setSleepTime(300);
	private static boolean isFirst = true;


	
	
	@Override
	public void process(Page page) {
		// TODO Auto-generated method stub
		Document doc = Jsoup.parse(page.getHtml().toString());
		
		if(isFirst){
			System.out.println("添加所有列表链接");
			ArrayList<String> urls =new ArrayList<String>();
			for(int i=1;i<50;i++){
				urls.add("http://www.fsggzy.cn/gcjy/gc_zbxx/gc_zbsz/index_"+i+".html");
				urls.add("http://www.fsggzy.cn/gcjy/gc_zbxx/jygg_gq/index_"+i+".html");
			}
			urls.add("http://www.fsggzy.cn/gcjy/gc_zbxx/jygg_gq/index.html");
			System.out.println("url的总数是："+urls.size());
			page.addTargetRequests(urls);
			isFirst=false;
		}	
		
		if(page.getUrl().regex(URL_LIST).match()){
			System.out.println("获取列表数据");

			List<String> urls = page.getHtml().xpath("//a[@id=\"title_link\"]").links().regex(URL_DETAILS).all();
			System.out.println(urls.size());
			if(urls!=null&&urls.size()>0){
				page.addTargetRequests(urls);
			}
		}
		if(page.getUrl().regex(URL_DETAILS).match()){
			System.out.println(doc.getElementsByAttributeValue("class", "contenttitle2").text());
			System.out.println(doc.getElementsByAttributeValue("class", "content2").text());
		}
		
	
		
	}

	@Override
	public Site getSite() {
		// TODO Auto-generated method stub
		return site;
	}

}