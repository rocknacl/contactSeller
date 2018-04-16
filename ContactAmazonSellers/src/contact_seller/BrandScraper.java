package contact_seller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;

import java.util.List;

import org.apache.commons.io.IOUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import models.Amazon_Country;

public class BrandScraper {
	WebDriver driver;
	Amazon_Country country;
	
	public BrandScraper(WebDriver driver,Amazon_Country country){
		this.driver = driver;
		this.country = country;
	}

	public void goToBrandsPage(String node) {
		driver.get(
				country.getDomain() + "gp/search/other?ie=UTF8&pickerToList=brandtextbin&rh=n%3A" + node + "&page=1");
	}

	public List<String> readNodes(File f) throws FileNotFoundException, IOException {
		List<String> nodes = IOUtils.readLines(new FileInputStream(f));
		return nodes;
	}

	public void writeToFile(File f, List<String> result) throws FileNotFoundException, IOException {
		FileOutputStream ops = new FileOutputStream(f);
		IOUtils.writeLines(result, "\n", ops);
		ops.close();

	}

	public List<String> getMultipleFirstLetterResult(String firstLetter) {
		List<String> names = new ArrayList<String>();
		if (firstLetter != null) {
			driver.findElement(By.xpath("//div[@id='indexBarHeader']//a[text()='" + firstLetter + "']")).click();
		}

		List<WebElement> brands = driver.findElements(By.xpath("//ul[@class='s-see-all-indexbar-column']/li/span/a"));
		for (WebElement e : brands) {
			String name = e.findElement(By.xpath("./span")).getText();
			System.out.println(name);
			names.add(name);
			// String href = e.getAttribute("href");
			// int end = href.indexOf("&bbn");
			// int start = href.indexOf("%3AA") + 3;
			// String id = href.substring(start, end);
			// result.put(name, id);
		}
		return names;
	}

	public static void main(String[] args) {
		WebDriver driver = WebDriverFactory.getChromeDriver();
		BrandScraper b = new BrandScraper(driver,Amazon_Country.US);
		String fileName = args[0];
		try {
			List<String> nodes = b.readNodes(new File(fileName));
			List<String> brands = new ArrayList<String>();
			for (String node : nodes) {
				b.goToBrandsPage(node);
				for (char c = 'A'; c <= 'Z'; c++) {
					System.out.println(c);
					brands.addAll(b.getMultipleFirstLetterResult(c + ""));
				}
				char c = '#';
				System.out.println(c);
				brands.addAll(b.getMultipleFirstLetterResult(c + ""));

				b.writeToFile(new File(node + ".txt"), brands);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			driver.quit();
		}
	}

}
