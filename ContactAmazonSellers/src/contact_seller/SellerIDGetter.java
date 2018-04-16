package contact_seller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import helpers.ConnectionPool;
import helpers.IOHelper;
import models.Amazon_Country;
import sys.SendEmailSystem;

public class SellerIDGetter {
	boolean isFBA;
	WebDriver driver;
	Amazon_Country country = Amazon_Country.US;

	public SellerIDGetter(boolean isFBA, WebDriver driver) {
		super();
		this.isFBA = isFBA;
		this.driver = driver;
	}

	public void setCountry(Amazon_Country c) {
		this.country = c;
	}
	
	

	public void saveSellerNameAndID(Map<String, String> data, String category, String keyword) throws SQLException {
		Connection conn = ConnectionPool.getConnectionPool().getConnection();
		String sql = "insert into contactseller_seller_feedback_stastics(seller_name,seller_id,category,keyword,is_fba,country) values(?,?,?,?,?,?) on duplicate key update is_fba = 1,category = concat(category,',',?),keyword=concat(keyword,',',?)";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		for (String name : data.keySet()) {
			pstmt.setString(1, name);
			pstmt.setString(2, data.get(name));
			pstmt.setString(3, category);
			pstmt.setString(4, keyword);
			pstmt.setInt(5, 1);
			pstmt.setString(6, this.country.toString());
			pstmt.setString(7, category);
			pstmt.setString(8, keyword);
			pstmt.addBatch();
		}

		pstmt.executeBatch();
		conn.close();
	}

	public String getSellerIDFromASINPage(WebDriver driver, String url) {
		driver.get(url);
		String href = driver.findElement(By.xpath("//div[@id='merchant-info']/a")).getAttribute("href");
		String[] parameters = href.split("&");
		for (String parameter : parameters) {
			if (parameter.contains("seller="))
				return parameter.replace("seller=", "");
		}
		return null;
	}

	public static void main(String[] args) {
		ArrayList<String[]> searchConditions = IOHelper.readtxt("categoryAndKeyword.txt");
		WebDriver driver = WebDriverFactory.getFirefoxDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		SellerIDGetter g = new SellerIDGetter(true, driver);
		g.setCountry(SendEmailSystem.country);
		for (String[] condition : searchConditions) {
			g.goToSellersPage(condition[0], condition[1]);
			for (char c = 'A'; c <= 'Z'; c++) {
				System.out.println(c);
				Map<String, String> data = g.getMultipleFirstLetterResult(c + "");
				try {
					g.saveSellerNameAndID(data, condition[0], condition[1]);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			char c = '#';
			System.out.println(c);
			Map<String, String> data = g.getMultipleFirstLetterResult(c + "");
			// Map<String, String> data = g.getMultipleFirstLetterResult(null);
			try {
				g.saveSellerNameAndID(data, condition[0], condition[1]);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		driver.quit();

	}

	public void goToSellersPage(String searchAlias, String keyword) {
		driver.get(this.country.getDomain());
		Select department = new Select(driver.findElement(By.id("searchDropdownBox")));
		department.selectByValue("search-alias=" + searchAlias);
		WebElement searchInput = driver.findElement(By.id("twotabsearchtextbox"));
		searchInput.clear();
		searchInput.sendKeys(keyword);
		// driver.findElement(By.xpath("//input[@value='Go']")).click();
		driver.findElement(By.xpath("//input[@class='nav-input' and @type='submit']")).click();
		if (this.isFBA) {
			WebElement element = driver.findElement(By.id("result_0"));
			driver.findElement(By.xpath("//img[@alt='Prime Eligible']")).click();
			new WebDriverWait(driver, 10).until(ExpectedConditions.stalenessOf(element));
		}
		driver.findElement(By.xpath("//div[@id='leftNavContainer']//a[contains(@href,'pickerToList=enc-merchantbin')]")).click();
//		String url = driver.getCurrentUrl();
//		url = url.replace("pickerToList=lbr_brands_browse-bin", "pickerToList=enc-merchantbin");
//		driver.get(url);
	}

	public HashMap<String, String> getMultipleFirstLetterResult(String firstLetter) {
		HashMap<String, String> result = new HashMap<String, String>();
		if (firstLetter != null) {
			driver.findElement(By.xpath("//div[@id='indexBarHeader']//a[text()='" + firstLetter + "']")).click();
		}

		List<WebElement> brands = driver.findElements(By.xpath("//ul[@class='s-see-all-indexbar-column']/li/span/a"));
		for (WebElement e : brands) {
			String name = e.findElement(By.xpath("./span")).getText();
			String href = e.getAttribute("href");
			int end = href.indexOf("&bbn");
			int start = href.indexOf("%3AA") + 3;
			String id = href.substring(start, end);
			result.put(name, id);
		}
		return result;
	}

}
