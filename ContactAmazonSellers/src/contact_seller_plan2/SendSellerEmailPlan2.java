package contact_seller_plan2;

import java.io.FileOutputStream;

import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;

import contact_seller.WebDriverFactory;
import helpers.MultiOutputStream;
import models.ContactSellerTask;
import models.Host;
import models.RobotAccount;
import web_brokers.SendEmailWebBroker;

public class SendSellerEmailPlan2 {
	ContactSellerTask task;
	Host host;

	public SendSellerEmailPlan2(ContactSellerTask task) {
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
		while (true) {
			ContactSeller2 c = null;
			WebDriver driver = null;
			try {
				driver = WebDriverFactory.getChromeDriver();
				driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

				CreateRobotAccountForRegister cra = new CreateRobotAccountForRegister();
				RobotAccountForRegister account = cra.create(driver);
				if (account != null) {
					account.setCountry(task.getCountry());
				}
				Register r = new Register(task.getCountry(), driver,account);
				r.run();
				if (r.success) {
					SendEmailWebBroker.saveAccountRegistered(account);
				}
				RobotAccount ra = new RobotAccount(account.getEmail(), account.getPassword());
				c = new ContactSeller2(host, ra, driver, task);
				c.run();
			} catch (Exception e) {
				e.printStackTrace(System.out);
				try {
					Thread.sleep(1000 * 60);
				} catch (InterruptedException e1) {
					e1.printStackTrace(System.out);
				}
			} finally {
				try {
					if (driver != null)
						driver.quit();
				} catch (Exception e) {
					try {
						Thread.sleep(1000 * 60 * 30);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}

	public RobotAccount readAmazonAccount() throws Exception {
		String[] response = SendEmailWebBroker.getAmazonAccount(task.getCountry().toString(), task.getName());
		RobotAccount account = new RobotAccount(response[0], response[1]);
		return account;
	}
}
