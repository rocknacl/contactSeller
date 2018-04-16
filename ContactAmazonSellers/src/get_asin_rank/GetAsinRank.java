package get_asin_rank;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


import models.Amazon_Country;
import web_brokers.AsinRankingWebBroker;

public class GetAsinRank {
	WebDriver driver;
	

	public GetAsinRank(WebDriver driver) throws SQLException {
		super();
		this.driver = driver;
	}

	public String getRankInfo(String asin, String country) {
		String result;
		Amazon_Country ac = Amazon_Country.valueOf(country);
		driver.get(ac.getDomain() + "dp/" + asin);
		if (driver.getTitle().contains("Not Found")) {
			return "error";
		}
		WebElement we = null;
		try {
			we = driver.findElement(By.xpath(
					"//table[@id='productDetails_detailBullets_sections1']/tbody//th[contains(text(),'Rank')]/following::td"));
		} catch (NoSuchElementException e) {
			try {
				we = driver.findElement(By.id("SalesRank"));
			} catch (NoSuchElementException e1) {
				System.out.println("empty");
				return "empty";
			}
		}
		result = we.getText();
		return result;
	}
	
	public ArrayList<String> getASINs() throws Exception{
		String s = AsinRankingWebBroker.getSEOASIN();
		String[] asins = s.split("\t");
		ArrayList<String> asinCountry = new ArrayList<String>(Arrays.asList(asins));
		return asinCountry;
	}

	public void saveRankingToDB(String asin, String rank) {
		try {
			AsinRankingWebBroker.saveRankInformation(asin, rank);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
