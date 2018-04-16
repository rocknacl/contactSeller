package contact_seller_plan2;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import robot.GuerrillamailScraper;

public class CreateRobotAccountForRegister {
	static String password;
	static String name;
	private String email;

	public RobotAccountForRegister create() {
		WebDriver driver = new ChromeDriver();
		GuerrillamailScraper gs = new GuerrillamailScraper(driver);
		gs.run();
		if (gs.isSuccess()) {
			email = gs.getEmail();
		}
		driver.quit();
		return new RobotAccountForRegister(email, name, password);
	}
	
	public RobotAccountForRegister create(WebDriver driver) {
		GuerrillamailScraper gs = new GuerrillamailScraper(driver);
		gs.run();
		if (gs.isSuccess()) {
			email = gs.getEmail();
		}
		return new RobotAccountForRegister(email, name, password);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	

}
