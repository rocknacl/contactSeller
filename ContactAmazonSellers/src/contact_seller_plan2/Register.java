package contact_seller_plan2;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import models.Amazon_Country;

public class Register {
	Amazon_Country country;
	RobotAccountForRegister ri;
	boolean success;
	WebDriver driver;

	public Register(Amazon_Country c, WebDriver d,RobotAccountForRegister ri) {
		this.country = c;
		this.driver = d;
		this.ri = ri;
	}

	public void run() {
		driver.get(country.getDomain());
		String href = driver.findElement(By.xpath("//div[@id='nav-flyout-ya-newCust']/a")).getAttribute("href");
		driver.navigate().to(href);
		WebElement nameInput = driver.findElement(By.id("ap_customer_name"));
		WebElement emailInput = driver.findElement(By.id("ap_email"));
		WebElement passwordInput = driver.findElement(By.id("ap_password"));
		WebElement passwordReEnterInput = driver.findElement(By.id("ap_password_check"));
		nameInput.clear();
		nameInput.sendKeys(ri.getName());
		emailInput.clear();
		emailInput.sendKeys(ri.getEmail());
		passwordInput.clear();
		passwordInput.sendKeys(ri.getPassword());
		passwordReEnterInput.clear();
		passwordReEnterInput.sendKeys(ri.getPassword());

		driver.findElement(By.id("continue")).click();;
		if (!driver.getCurrentUrl().contains("ap/register")) {
			this.success = true;
		}
	}

}
