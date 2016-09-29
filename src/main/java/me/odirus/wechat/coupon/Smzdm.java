package me.odirus.wechat.coupon;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: huangjing
 * Email: huangjing@tinman.cn
 * Date: 2016/9/26
 * Time: 11:18
 */
public class Smzdm {
	private static Map<String, String[]> dataSource = new HashMap<String, String[]>();//<url, keywordList>
	private static final String defaultHeaderUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36";
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static final SimpleDateFormat sdfForPostTime = new SimpleDateFormat("MM-dd HH:mm");

	public Smzdm() {
		Unirest.setDefaultHeader("User-Agent", defaultHeaderUserAgent);
		String urlA = "http://www.smzdm.com/fenlei/tushuyinxiang/h1c4s183f0t0p1/";
		String[] keywordListA = new String[]{"促销活动"};
		dataSource.put(urlA, keywordListA);
		String urlB = "http://www.smzdm.com/fenlei/liangyoutiaowei/h1c1s0f0t0p1/";
		String[] keywordListB = new String[]{"米", "油"};
		dataSource.put(urlB, keywordListB);
	}

	public List<Coupon> get() {
		List<Coupon> resCouponList = new ArrayList<Coupon>();

		Iterator<String> iterator = dataSource.keySet().iterator();
		while (iterator.hasNext()) {
			String url = iterator.next();
			String[] keywordList = dataSource.get(url);

			try {
				List<Coupon> couponList = new ArrayList<Coupon>();
				String contents = getHtml(url);
				Document doc = parseHtml(contents);
				couponList = getCouponList(doc);
				couponList = filter(couponList, keywordList);
				resCouponList.addAll(couponList);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return resCouponList;
	}

	private List<Coupon> filter(List<Coupon> couponList, String[] keywordList) {
		List<Coupon> resCouponList = new ArrayList<Coupon>();

		for (int i = 0; i < couponList.size(); i++) {
			Coupon coupon = couponList.get(i);
			boolean containKeyword = false;
			for (int j = 0; j < keywordList.length; j++) {
				if (coupon.getTitle().contains(keywordList[j])) {
					containKeyword = true;
				}
			}
			if (containKeyword) {
				resCouponList.add(coupon);
			}
		}

		return resCouponList;
	}

	private String getHtml(String url) throws UnirestException {
		GetRequest getRequest = Unirest.get(url);
		HttpResponse<String> httpResponse = getRequest.asString();

		if (httpResponse.getStatus() != 200) {
			throw new RuntimeException("Http get response status is not 200, status=" + httpResponse.getStatus());
		}

		return httpResponse.getBody();
	}

	private Document parseHtml(String contents) {
		return Jsoup.parse(contents);
	}

	private List<Coupon> getCouponList(Document doc) {
		List<Coupon> couponList = new ArrayList<Coupon>();

		ListIterator<Element> elementListIterator = doc.select("li.feed-row-wide").listIterator();
		while (elementListIterator.hasNext()) {
			Element element = elementListIterator.next();
			Coupon coupon = new Coupon();

			String link = element.select(".feed-block-title a").first().attr("href");
			String pattern = "([0-9]+)";
			Pattern r = Pattern.compile(pattern);
			Matcher matcher = r.matcher(link);
			if (matcher.find()) {
				coupon.setId(CouponProvider.SMZDM.getIdPrefix() + Long.parseLong(matcher.group(0)));
			} else {
				throw new RuntimeException("Cannot get id in link, link: " + link);
			}

			List<TextNode> textNodeList = element.select(".feed-block-title a").first().textNodes();
			String title = textNodeList.get(1).getWholeText().trim();
			coupon.setTitle(title);

			String promotion = element.select(".feed-block-title a span").last().text().trim();
			coupon.setPromotion(promotion);

			coupon.setCouponProvider(CouponProvider.SMZDM);

			List<TextNode> timeContainerNodeList = element
				.select(".feed-block.z-hor-feed span.feed-block-extras")
				.first()
				.textNodes();
			String timeStr = timeContainerNodeList.get(0).getWholeText().trim();
			coupon.setTimestamp(new Timestamp(parseTimeStr(timeStr)));

			coupon.setLink(link);

			couponList.add(coupon);
		}

		return couponList;
	}

	private long parseTimeStr(String timeStr) {
		long currentTime = new Date().getTime();//in milliseconds
		long time;//in milliseconds

		if (timeStr.endsWith("分钟前")) {
			int minutes = Integer.parseInt(timeStr.replace("分钟前", ""));
			time = currentTime - minutes * 60 * 1000;
		} else if (timeStr.endsWith("小时前")) {
			int hours = Integer.parseInt(timeStr.replace("小时前", ""));
			time = currentTime - hours * 3600 * 1000;
		} else if (timeStr.isEmpty()) {
			time = currentTime;
		} else {
			try {
				sdfForPostTime.parse(timeStr);//check if valid time format
				time = sdf.parse(getCurrentYear() + "-" + timeStr).getTime();
			} catch (ParseException pe) {
				pe.printStackTrace();
				time = currentTime;
			}
		}

		return time;
	}

	private int getCurrentYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}
}
