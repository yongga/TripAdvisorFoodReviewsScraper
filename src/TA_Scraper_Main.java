import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.javascript.host.dom.Node;
import com.opencsv.CSVWriter;

public class TA_Scraper_Main {
	
	private List<String> urlsToCrawl;
	
	public TA_Scraper_Main() {
		
	}
	
	public List<String> getUrlsToCrawl() {
		return urlsToCrawl;
	}

	public void setUrlsToCrawl(List<String> urlsToCrawl) {
		this.urlsToCrawl = urlsToCrawl;
	}
	
	final static String lookup_url = "https://www.tripadvisor.com.sg/Restaurants-g294265-Singapore.html";

	final static String outFile = "TA_Scraper_Data.csv";
	public static void main(String[] args) throws InterruptedException {
		
		
		// TODO Auto-generated method stub
		java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
		
		TA_Scraper_Main scraper = new TA_Scraper_Main();
		
		scraper.write_headers(outFile);
		
		scraper.setUrlsToCrawl(scraper.getFoodPlaces());
		
		List<String> urls = scraper.getUrlsToCrawl();
		//List<String> urls = new ArrayList();
		//urls.add("https://www.tripadvisor.com.sg/Restaurant_Review-g294265-d1193730-Reviews-or10-Entre_Nous_creperie-Singapore.html");
		WebClient webClient = new WebClient(BrowserVersion.CHROME);
		
		for(String url : urls) {

			HtmlPage currPage;
			try {
				
				currPage = webClient.getPage(url);

				WebResponse resp = currPage.getWebResponse();
				webClient.waitForBackgroundJavaScript(1000);

				int reviewsIndex = url.indexOf("Reviews"); // the index of beginning of "Reviews" string

				int endReviewsIdx = reviewsIndex + 7; // inclusive of '-' char

				String aftReviews = url.substring(endReviewsIdx + 1); // eg. Alma_By_Juan_Amador-Singapore.html

				String befReviews = url.substring(0, endReviewsIdx);
				
				DomElement node = (DomElement) currPage.getByXPath("//a[@class='pageNum last taLnk ']").get(0);
				
				int lastPage = Integer.parseInt(node.getAttribute("data-page-number"));
				
				String restaurantName = ((DomElement) currPage.getByXPath("//h1[@class='ui_header h1']").get(0)).getTextContent();
				
				String restaurantAddr = ((DomElement) currPage.getByXPath("//span[@class='restaurants-detail-overview-cards-LocationOverviewCard__detailLinkText--co3ei']").get(0)).getTextContent();
				
				// limit to only top 10 pages for each restaurant
				lastPage = lastPage > 20? 20 : lastPage;
				
				// iterate through every page
				for(int i=0;i<=lastPage;i++) {
					
					if (i == 5) break;
					
					List<Review> scrapedReviews = new ArrayList<Review>();
					
					// get a reference of all "more" links
					List<HtmlSpan> more_btns = currPage.getByXPath("//span[@class='taLnk ulBlueLinks']");
					
					HtmlPage p = null;
					
					List<DomElement> es = currPage.getByXPath("//span[@class='taLnk ulBlueLinks']");
						
					if (es != null && !es.isEmpty()) {
						HtmlSpan e = (HtmlSpan) es.get(0);
						long startTime = System.currentTimeMillis();
						while (e.asText().contains("more") || e.asText().contains("More")) {
							System.out.println("I am in: " + e.asText());
						
							webClient.waitForBackgroundJavaScript(2000);
							p = es.get(0).click();
							webClient.waitForBackgroundJavaScript(2000);

							e = ((HtmlSpan) p.getByXPath("//span[@class='taLnk ulBlueLinks']").get(0));
							System.out.println(e.asText());
							long currentTime = System.currentTimeMillis();
							if ((currentTime - startTime)/1000 > 20) {
								System.out.println("Current: " + currentTime);
								System.out.println("Start: " + startTime);
								System.out.println((currentTime - startTime)/1000);
								break;
							}
						}
					}
					
					List<DomElement> revs = currPage.getByXPath("//div[@class='reviewSelector']");
					int availRevs = revs.size();
					
					for(int j=0;j<availRevs;j++) {
						Review review = new Review();
						String reviewID = revs.get(j).getAttribute("id");
						String elePrefix = "//*[@id='"+reviewID+"']";
						System.out.println("ElePrefix = " + elePrefix);
						
						String reviewTitle = ((DomElement) currPage.getByXPath("//a[@class='title ']").get(j)).asText();
						
						String reviewer = ((DomElement) currPage.getByXPath("//div[@class='member_info']").get(j)).getFirstElementChild().getAttribute("id");
						
						DomElement loc =  ((DomElement) currPage.getByXPath("//div[@class='member_info']").get(j)).getFirstChild().getChildNodes().get(1).getFirstChild().getNextElementSibling();
						
						//System.out.println("Next Review");
						
						String reviewContent = p.getByXPath(elePrefix+"/div/div[2]/div[2]/div/p/text()").toString();
						//System.out.println("Review Content: \n" + reviewContent);
						
						String reviewerLoc = "";
						if (loc != null)
							reviewerLoc = loc.getTextContent();
						
						int numPhotos = 0;
						
						//DomElement summary = ((DomElement) currPage.getByXPath(elePrefix+"/div/div[2]/div[2]").get(0));
						
						String visitDate = ((DomElement) currPage.getByXPath("//*[@class='stay_date_label']").get(j)).getNextSibling().asText();
						
						String reviewDate = ((DomElement) currPage.getByXPath("//*[@class='ratingDate']").get(j)).getAttribute("title");
						
						String reviewRating = ((DomElement) currPage.getByXPath(elePrefix+"/div/div[2]/span[1]").get(0)).getAttribute("class");
						
						review.setRestaurantName(restaurantName);
						review.setRestaurantAddr(restaurantAddr);
						review.setReviewID(reviewID);
						review.setReviewTitle(reviewTitle);
						review.setReviewContent(reviewContent);
						review.setReviewer(reviewer);
						review.setReviewerCountry(reviewerLoc);
						review.setReviewRating(review.extractRating(reviewRating));
						review.setReviewDate(reviewDate);
						review.setVisitDate(visitDate);
						
						scrapedReviews.add(review);
					}
					
					scraper.append_to_csv(outFile, scrapedReviews);
					
					if (i == lastPage)
						break;
					
					String nextUrl = befReviews + "-or" + String.valueOf(i + 1) + "0-" + aftReviews;

					currPage = webClient.getPage(nextUrl);

					System.out.println(currPage.getUrl().toString());
				}
				
				
			} catch (FailingHttpStatusCodeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public List<String> getFoodPlaces() {
		
		int minPage = (2 - 1) * 30;
		
		int maxPage = (20 - 1) * 30;
		
		final int limit = 100; // only crawl top 100 restaurants
		
		ArrayList<String> lookup_urls = new ArrayList<String>();
		try (final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {

			HtmlPage page = webClient.getPage(lookup_url);
			
			Set<String> urlSet = new HashSet<String>();
			
			int currentNum = 1;
			
			for(int i=2;i<=2;i++) {
				urlSet.add("https://www.tripadvisor.com.sg/Restaurants-g294265-oa"+String.valueOf((i-1)*30)+"-Singapore.html#EATERY_OVERVIEW_BOX");
			}
			
			List<DomElement> restaurant_urls = page.getByXPath("//a[@class='restaurants-list-ListCell__restaurantName--2aSdo']");
			
			restaurant_urls.remove(0); // exclude "sponsored" restaurant
			
			for(DomElement url : restaurant_urls) {
				System.out.println(url.getAttribute("href"));
				lookup_urls.add("https://www.tripadvisor.com.sg" + url.getAttribute("href"));
				currentNum++;
			}
			
			for(String s : urlSet) {
				page = webClient.getPage(s);
				// loop through restaurants on each page
				restaurant_urls = page.getByXPath("//a[@class='restaurants-list-ListCell__restaurantName--2aSdo']");
				
				restaurant_urls.remove(0); // exclude "sponsored" restaurant
				
				for(DomElement url : restaurant_urls) {
					if (currentNum == limit) break;
					System.out.println(url.getAttribute("href"));
					lookup_urls.add("https://www.tripadvisor.com.sg"+url.getAttribute("href"));
					currentNum++;
				}
				if (currentNum == limit) break;
			}
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return lookup_urls;
	}
	
	public void write_headers(String filePath) {
		File outFile = new File(filePath);

		// always delete file before writing to it
		if (outFile.exists())
			outFile.delete();

		try {
			FileWriter outputFile = new FileWriter(outFile);

			CSVWriter writer = new CSVWriter(outputFile);

			// create table headers
			String[] header = { "reviewID", "ReviewTitle", "reviewContent", "reviewer", "reviewerLoc", 
								"reviewDate", "visitDate", "reviewRating", "restaurantName", "restaurantAddr" };
			writer.writeNext(header);

			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void append_to_csv(String filePath, List<Review> scrapedReviews) {
		File outFile = new File(filePath);

		try {
			FileWriter outputFile = new FileWriter(outFile, true);

			CSVWriter writer = new CSVWriter(outputFile);
			
			if (!scrapedReviews.isEmpty()) {
				for(Review r : scrapedReviews) {
					String[] entry = { r.getReviewID(), r.getReviewTitle(), r.getReviewContent(), r.getReviewer(), r.getReviewerCountry(), r.getReviewDate(),
							r.getVisitDate(), r.getReviewRating(), r.getRestaurantName(), r.getRestaurantAddr()};
					writer.writeNext(entry);
				}
			}

			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
