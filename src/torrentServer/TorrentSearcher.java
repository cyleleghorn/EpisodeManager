package torrentServer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.logging.LogFactory;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

public class TorrentSearcher 
{
	private WebClient webClient;

	public TorrentSearcher()
	{
		webClient = this.init();
	}
	private WebClient init()
	{
		WebClient webclient = new WebClient(BrowserVersion.CHROME);
		// Disable annoying output about CSS.
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
		java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF); 
		java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);

		return webclient;
	}
	public String searchKat(String title, String season, String episode) throws Exception
	{
		String query=title.toLowerCase().replace(" ", "%20")+"%20s"+season+"e"+episode;
		HtmlPage resultsPage = webClient.getPage("http://www.kat.cr/usearch/" + query);
		//List<?> resultsList = resultsPage.getByXPath("//tr[@class='odd']//div[@class='torrentname'] | //tr[@class='even']");
		List<?> resultsList = resultsPage.getByXPath("//div[@class='markeredBlock torType filmType']/a/@href");
		String matchingUrl = null;
		for(int i=0; i<resultsList.size(); i++)
		{	
			if(
					((DomAttr) resultsList.get(i)).getValue().contains("s"+season+"e"+episode) && 
					((DomAttr) resultsList.get(i)).getValue().contains("1080p") && 
					((DomAttr) resultsList.get(i)).getValue().contains(title.toLowerCase()))
			{
				matchingUrl = "https://kat.cr"+((DomAttr) resultsList.get(i)).getValue();
				break;
			}
		}
		return matchingUrl;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<List<String>> searchTPB(String query)
	{
		query = query.toLowerCase().replace(" ", "%20");
		List<List<String>> finalList = new ArrayList<List<String>>();

		try 
		{
			webClient.getOptions().setJavaScriptEnabled(false);
			HtmlPage resultsPage = webClient.getPage("https://thepiratebay.org/search/"+query+"/0/99/0");

			//Need to get all the tr's inside tbody.
			List<HtmlTableRow> resultsList = (List<HtmlTableRow>) resultsPage.getByXPath("//tbody/tr");
			List<HtmlAnchor> hrefList = (List<HtmlAnchor>) resultsPage.getByXPath("//a[@title='Download this torrent using magnet']");

			//Create String lists to hold the various values.
			List<String> titles = new ArrayList<String>();
			List<String> seeders = new ArrayList<String>();
			List<String> leechers = new ArrayList<String>();
			List<String> magnets = new ArrayList<String>();
			
			
			//Extract only the important shit from page 1 of results.
			for(int i=0; i<resultsList.size(); i++){
				titles.add(resultsList.get(i).getCells().get(1).getChildNodes().get(1).asText());
				seeders.add(resultsList.get(i).getCells().get(2).getChildNodes().get(0).asText());
				leechers.add(resultsList.get(i).getCells().get(3).getChildNodes().get(0).asText());
				magnets.add(hrefList.get(i).getHrefAttribute());
			}
			
			
			/*
			for(int k=0; k<titles.size(); k++)
			{
				System.out.println("Result number " + (k+1) + ":\n");
				System.out.println("Title: " + titles.get(k));
				System.out.println("Seeders: " + seeders.get(k));
				System.out.println("Leechers: " + leechers.get(k));
				System.out.println("Magnet: " + magnets.get(k) + "\n\n");
			}
			*/
			
			
			//Compile final list for use and return.
			//List<List<String>> finalList = new ArrayList<List<String>>();  This is really created outside the try block.
			finalList.add(titles);
			finalList.add(seeders);
			finalList.add(leechers);
			finalList.add(magnets);
			
			
		} 
		catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			System.out.println("Obscure exception regarding Http Status Code.");
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			System.out.println("Malformed URL Exception.");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error with internet connectivity or getting results page.");
			e.printStackTrace();
		}
		return finalList;
	}
}