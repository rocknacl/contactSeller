package get_asin_rank;


import java.util.ArrayList;
import java.util.TimerTask;
import org.openqa.selenium.WebDriver;
import contact_seller.WebDriverFactory;


public class AsinRankingDownloadTimerTask extends TimerTask{

	@Override
	public void run() {
		WebDriver driver = WebDriverFactory.getFirefoxDriverWithoutImg();
		GetAsinRank g = null;
		try {
			
			g = new GetAsinRank(driver);
			ArrayList<String> asinCountry =  g.getASINs();
			for(String ac: asinCountry){			
				String[] tuple = ac.split(",");
				String asin = tuple[0];
				if(asin.contains(";"))
					continue;
				String country = tuple[1];
				System.out.println(asin+","+country);
				String rank = g.getRankInfo(asin, country);
				g.saveRankingToDB(asin,rank);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			driver.quit();
		}
		
	}

}
