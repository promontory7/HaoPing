import pageprocessor.NBANewsProcessor;
import us.codecraft.webmagic.Spider;

public class MainCrawler {

	public static void main(String[] args) {
		Spider.create(new NBANewsProcessor())
		.addUrl(NBANewsProcessor.url)
		.thread(5)
		.run();
	}
}
