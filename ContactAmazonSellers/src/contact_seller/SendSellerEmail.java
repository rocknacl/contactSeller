package contact_seller;

import java.io.FileOutputStream;

import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;

import helpers.MultiOutputStream;
import models.ContactSellerTask;
import models.Host;
import models.RobotAccount;
import sys.SendEmailSystem;
import web_brokers.SendEmailWebBroker;

public class SendSellerEmail {
	ContactSellerTask task;
	Host host;

	public SendSellerEmail(ContactSellerTask task) {
		super();
		this.task = task;
		this.host = new Host();
	}

	public void run() {
		FileOutputStream propFile = null;
		MultiOutputStream multi = null;

		try {
			propFile = new FileOutputStream("consoleOutput.txt");
			multi = new MultiOutputStream(new PrintStream(propFile), System.out);
			System.setOut(new PrintStream(multi));
		} catch (Exception e) {
			e.printStackTrace(System.out);
		} finally {
		}

		WebDriver driver = null;
		if (SendEmailSystem.useProfiledChromeDriver) {
			driver = WebDriverFactory.getProfiledChromeDriver("Rock");
		} else {
			if (SendEmailSystem.i % 2 == 0) {
				driver = WebDriverFactory.getChromeDriver();
			} else {
				driver = WebDriverFactory.getFirefoxDriver();
			}
		}

		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		while (true) {
			ContactSeller c = null;

			try {

				RobotAccount account = readAmazonAccount();
				if (account == null) {
					System.out.println("no account available");
					return;
				}
				c = new ContactSeller(host, account, driver, task);
				c.run();
			} catch (Exception e) {
				e.printStackTrace(System.out);
				try {
					Thread.sleep(1000 * 60);
				} catch (InterruptedException e1) {
					e1.printStackTrace(System.out);
				}
			} finally {
				if (SendEmailSystem.captchaUnsolved) {
					driver.quit();
					return;
				}

				// try {
				// if (driver != null)
				// driver.quit();
				// } catch (Exception e) {
				// try {
				// Thread.sleep(1000 * 60 * 30);
				// } catch (InterruptedException e1) {
				// e1.printStackTrace();
				// }
				// }
			}
		}
	}

	public RobotAccount readAmazonAccount() throws Exception {
		String[] response = SendEmailWebBroker.getAmazonAccount(task.getCountry().toString(), task.getName());
		RobotAccount account = new RobotAccount(response[0], response[1]);
		return account;
	}
}
