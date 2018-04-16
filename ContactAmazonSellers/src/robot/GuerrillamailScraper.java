package robot;

import org.openqa.selenium.By;

import org.openqa.selenium.WebDriver;

public class GuerrillamailScraper {
	private WebDriver driver;
	private String email;
	private boolean success;

	public GuerrillamailScraper(WebDriver driver) {
		this.driver = driver;
	}

	public void run() {
		try {
			driver.get("http://guerrillamail.com");
			String email = driver.findElement(By.id("email-widget")).getText();
			this.email = email;
			this.success = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public WebDriver getDriver() {
		return driver;
	}

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

}
