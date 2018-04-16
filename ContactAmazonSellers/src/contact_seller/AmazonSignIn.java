package contact_seller;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import helpers.SeleniumHelper;
import helpers.SimpleCaptchaProcesser;
import ruokuaidati.DaAn;
import ruokuaidati.Ruokuai;
import sys.SendEmailSystem;

public class AmazonSignIn {
	String email;
	String password;
	WebDriver driver;

	public AmazonSignIn(String email, String password, WebDriver driver) {
		this.email = email;
		this.password = password;
		this.driver = driver;
	}

	public boolean signIn() throws Exception {

		// driver.findElement(By.id("nav-link-accountList")).click();
		driver.findElement(By.id("ap_email")).sendKeys(email);
		driver.findElement(By.id("ap_password")).sendKeys(password);
		driver.findElement(By.id("signInSubmit")).click();

		int count = 0;
		while (driver.getCurrentUrl().contains("ap/signin") && count < 6) {
			count++;
			try {
				SeleniumHelper.waitForElement(driver, By.id("image-captcha-section"), 5);
				System.out.println("captcha!");
				if (count < 3) {
					this.captchaDealing();
				} else {
					this.captchaDealingRuokuai();
				}
				continue;
			} catch (TimeoutException e) {
			}
			try {
				SeleniumHelper.waitForElement(driver, By.id("auth-error-message-box"), 5);
				System.out.println("auth-error");
				return false;
			} catch (TimeoutException e) {

			}

			try {
				SeleniumHelper.waitForElement(driver, By.id("dcq_submit"), 5);
				System.out.println("fail to sign in");
				if (driver.findElements(By.id("image-captcha-section")).size() > 0) {
					SendEmailSystem.captchaUnsolved = true;
				}
				return false;
			} catch (TimeoutException e) {
			}

		}

		if (driver.getCurrentUrl().contains("ap/signin")) {
			SendEmailSystem.captchaUnsolved = true;
			return false;
		}
		return true;
	}

	private void captchaDealing() {

		try {
			String src = driver.findElement(By.id("auth-captcha-image")).getAttribute("src");
			SimpleCaptchaProcesser scd = new SimpleCaptchaProcesser();
			scd.downloadCaptchaImg(src);
			String captcha = scd.recognize();
			if (captcha != null) {
				WebElement pwEle = driver.findElement(By.id("ap_password"));
				for (int i = 0; i < password.length(); i++) {
					pwEle.sendKeys(String.valueOf(password.charAt(i)));
					Thread.sleep(300);
				}
				WebElement ele = driver.findElement(By.id("auth-captcha-guess"));
				ele.clear();
				for (int i = 0; i < captcha.length(); i++) {
					ele.sendKeys(String.valueOf(captcha.charAt(i)));
					Thread.sleep(3000);
				}
				driver.findElement(By.id("signInSubmit")).click();
			} else {
				driver.findElement(By.id("auth-captcha-refresh-link")).click();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void captchaDealingRuokuai() {

		try {
			String src = driver.findElement(By.id("auth-captcha-image")).getAttribute("src");
			DaAn da = Ruokuai.deal(src);

			if (da != null) {
				String captcha = da.getAnswer();
				WebElement pwEle = driver.findElement(By.id("ap_password"));
				for (int i = 0; i < password.length(); i++) {
					pwEle.sendKeys(String.valueOf(password.charAt(i)));
					Thread.sleep(1 * 1000);
				}
				WebElement ele = driver.findElement(By.id("auth-captcha-guess"));
				ele.clear();
				for (int i = 0; i < captcha.length(); i++) {
					ele.sendKeys(String.valueOf(captcha.charAt(i)));
					Thread.sleep(1 * 1000);
				}
				driver.findElement(By.id("signInSubmit")).click();
				if (driver.getCurrentUrl().contains("/ap/signin")) {
					if (driver.findElement(By.xpath("//div[@id='auth-error-message-box']//span")).getText()
							.contains("characters")) {
						System.out.println(Ruokuai.reportMistake(da.getId()));
					}
				}
			} else {
				driver.findElement(By.id("auth-captcha-refresh-link")).click();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
