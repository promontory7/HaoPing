package pageprocessor;

import java.util.ArrayList;
import java.util.HashMap;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import model.NewDetail;
import model.News;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import utils.SessionFactoryUtil;

public class NBANewsProcessor implements PageProcessor {
	// 列表页的匹配
	public static final String URL_LIST = "http://voice\\.hupu\\.com/nba/\\d+";
	// 详情页的匹配
	public static final String URL_DETAILS = "http://voice\\.hupu\\.com/nba/\\d+\\.html";

	public static String url = "http://voice.hupu.com/nba/1";
	public static String teString = "http://voice.hupu.com/nba/2022173.html";
	private boolean isFirst = true;
	
	//哈希链表容器
	HashMap  hashmap =new HashMap();

	@Override
	public void process(Page page) {
		// TODO Auto-generated method stub

		// 第一次进来执行
		if (isFirst) {
			System.out.println("添加所有列表链接");
			ArrayList<String> urls = new ArrayList<String>();
			for (int i = 2; i < 20; i++) {
				urls.add("http://voice.hupu.com/nba/" + i);
			}
			page.addTargetRequests(urls);
			isFirst = false;
		}
		// 所有链接都会执行
		Document doc = Jsoup.parse(page.getHtml().toString());

		
		// 如果链接属于列表页
		if (page.getUrl().regex(URL_LIST).match()) {

			// 提取页面的新闻节点
			Elements lis = doc.getElementsByAttributeValue("class", "news-list").select("ul").select("li");
			System.out.println("这个列表页一共有 " + lis.size() + "条新闻");

			// 创建新闻一个实体类
			News news = new News();
			String title;
			String content;
			String from;
			String url;

			for (Element li : lis) {
				title = li.getElementsByAttributeValue("class", "list-hd").text();
				content = li.getElementsByAttributeValue("class", "list-content").text();
				from = li.getElementsByAttributeValue("class", "other-left").text();
				url = li.getElementsByAttributeValue("class", "list-hd").select("h4").select("a").attr("href");

				System.out.println("标题：" + title);
				System.out.println("内容：" + content);
				System.out.println("来源：" + from);
				System.out.println("详情链接：" + url);
				page.addTargetRequest(url);

				// --------------------存入数据库------------------------------
				news.setTitle(title);
				news.setContent(content);
				news.setSource(from);
				news.setUrl(url);
				// 开始存入数据库
				System.out.println("开始存入数据库");
				SessionFactory sf = SessionFactoryUtil.getInstance();
				Session s = null;
				Transaction t = null;

				try {
					s = sf.openSession();
					t = s.beginTransaction();
					s.save(news);
					t.commit();
				} catch (Exception err) {
					t.rollback();
					err.printStackTrace();
				} finally {
					s.close();
				}
				System.out.println("----------------------------------------------------------------------------------");
				// -----------------------------------------------
			}
		}

		// 如果链接属于详情页
		if (page.getUrl().regex(URL_DETAILS).match()) {

			String title = doc.getElementsByAttributeValue("class", "headline").text();
			String image = doc.getElementsByAttributeValue("class", "artical-importantPic").select("img").attr("src");
			String content = doc.getElementsByAttributeValue("class", "artical-main-content").text();

			System.out.println("标题：" + title);
			System.out.println("图片：" + image);
			System.out.println("内容：" + content);

			// ---------------------------------------------------------
			NewDetail detail = new NewDetail();
			detail.setTitle(title);
			detail.setImage(image);
			detail.setContent(content);
			detail.setUrl(page.getUrl().toString());

			SessionFactory sf2 = SessionFactoryUtil.getInstance();
			Session s2 = null;
			Transaction t2 = null;

			try {
				s2 = sf2.openSession();
				t2 = s2.beginTransaction();
				s2.save(detail);
				t2.commit();
			} catch (Exception err) {
				t2.rollback();
				err.printStackTrace();
			} finally {
				s2.close();
			}
			System.out.println("----------------------------------------------------------------------------------");
			// ---------------------------------------------------
		}
	}

	// 返回策略
	@Override
	public Site getSite() {
		// TODO Auto-generated method stub
		return Site.me().setRetryTimes(3).setSleepTime(1000);
	}

}
