package contact_seller_plan2;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import contact_seller.AmazonSignIn;
import helpers.ConnectionPool;
import models.ContactSellerTask;
import models.Host;
import models.RobotAccount;
import web_brokers.SendEmailWebBroker;

public class ContactSeller2 {
	Host host;
	ContactSellerTask task;
	RobotAccount account;
	static Logger log = LogManager.getLogger(ContactSeller2.class);
	WebDriver driver;
	boolean blocked;
	ArrayList<String> message;

	public ContactSeller2(Host host, RobotAccount account, WebDriver driver, ContactSellerTask task)
			throws SQLException {
		this.account = account;
		this.driver = driver;
		this.task = task;
		this.message = readMessageToSend(task.getTaskDataFolder() + task.getMessageFilePath());
		this.host = host;
	}

	private void contactSeller(String sellerID, ArrayList<String> messages, String fileName) throws Exception {
		// driver.get(task.getCountry().getDomain() +
		// "sp?_encoding=UTF8&marketplaceID="
		// + task.getCountry().getMarketplaceID() + "&seller=" + sellerID);
		// Set<String> currentWindows = driver.getWindowHandles();
		// String formerWindow = driver.getWindowHandle();
		// driver.findElement(By.xpath("//span[@id='seller-contact-button']//a")).click();
		//
		// for (String s : driver.getWindowHandles()) {
		// if (!currentWindows.contains(s)) {
		// driver.switchTo().window(s);
		// break;
		// }
		// }
		driver.get(task.getCountry().getDomain()
				+ "ss/help/contact/writeMessage?writeButton=%E6%8F%90%E4%BA%A4&subject=5&orderID=&sellerID=" + sellerID
				+ "&marketplaceID=" + this.task.getCountry().getMarketplaceID() + "&language=en_US");

		// driver.get(task.getCountry().getDomain() +
		// "ss/help/contact/?_encoding=UTF8&marketplaceID="
		// + this.task.getCountry().getMarketplaceID() +
		// "&ref_=v_sp_contact_seller&sellerID="
		// + sellerID);

		try {
			if (driver.getCurrentUrl().contains("signin")) {
				this.reLogin();
			}

			try {
				driver.findElement(By.id("sendEmailForm"));
			} catch (Exception e2) {
				driver.findElement(By.xpath("//h4[@class='a-box a-alert a-alert-error']"));
				this.blocked = true;
				System.out.println("**********account blocked ********");
				return;
			}
			if (fileName != null) {

				WebElement addAttachmentButton = (new WebDriverWait(driver, 15))
						.until(new ExpectedCondition<WebElement>() {
							@Override
							public WebElement apply(WebDriver d) {
								return d.findElement(By.xpath("//div[@id='addAttachmentButtonSection']//button"));
							}
						});
				addAttachmentButton.click();
				driver.findElement(By.xpath("//input[@id='file' and @type='file']"))
						.sendKeys(task.getTaskDataFolder() + fileName);
				WebElement e = (new WebDriverWait(driver, 30)).until(new ExpectedCondition<WebElement>() {
					@Override
					public WebElement apply(WebDriver d) {
						return d.findElement(
								By.xpath("//div[@id='attachmentsSection']/div[contains(text(),'" + fileName + "')]"));
					}
				});
				WebElement messageBox = driver.findElement(By.id("comment"));
				for (String s : messages)
					messageBox.sendKeys(s + "\n");
				// Thread.sleep(1000*60);
				driver.findElement(By.xpath("//input[@name='sendEmail']")).click();
				Thread.sleep(1000);
				try {
					driver.findElement(By.xpath("//div[@class='a-box a-alert a-alert-success']"));
					SendEmailWebBroker.saveSendEmailResult(this.account.getEmail(), sellerID, this.task.getName());

				} catch (Exception e2) {
					this.blocked = true;
					System.out.println("**********account blocked ********");
					return;
				}

			}
		} catch (TimeoutException | ElementNotVisibleException | NoSuchElementException e) {
			throw e;
		} finally {
			// driver.close();
			// driver.switchTo().window(formerWindow);
		}
	}

	private void reLogin() {
		driver.findElement(By.id("ap_email")).clear();
		driver.findElement(By.id("ap_email")).sendKeys(this.account.getEmail());
		driver.findElement(By.id("ap_password")).sendKeys(this.account.getPassword());
		driver.findElement(By.id("signInSubmit")).click();
	}

	private ArrayList<String> readMessageToSend(String path) {
		ArrayList<String> result = new ArrayList<String>();
		String line;

		try {
			BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));

			while ((line = bf.readLine()) != null) {
				result.add(line);
			}
			bf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public void run() throws Exception {
		// driver.get(this.task.getCountry().getDomain());
//		driver.get(this.task.getCountry().getDomain()
//				+ "ap/signin?_encoding=UTF8&openid.assoc_handle=usflex&openid.claimed_id=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.identity=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.mode=checkid_setup&openid.ns=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0&openid.ns.pape=http%3A%2F%2Fspecs.openid.net%2Fextensions%2Fpape%2F1.0&openid.pape.max_auth_age=0&openid.return_to=https%3A%2F%2Fwww.amazon.com%2Fgp%2Fcss%2Fhomepage.html%3Fie%3DUTF8%26%252AVersion%252A%3D1%26%252Aentries%252A%3D0");
//		AmazonSignIn signin = new AmazonSignIn(this.account.getEmail(), this.account.getPassword(), driver);
		
			int i = 0;
			while (true) {
				if (this.blocked) {
//					SendEmailWebBroker.saveAccountBlockTime(this.account.getEmail());
					return;
				}
				String id = null;
				id = SendEmailWebBroker.getSellerID(this.task.getCountry().toString(), this.task.getName());

				if (id == null || id.equals("null")) {
					System.out.println("no id to send");
					Thread.sleep(1000 * 60 * 5);
					return;
				}

				try {
					this.contactSeller(id, message, this.task.getAttachmentFileNames()[i]);

					if (i == task.getAttachmentFileNames().length - 1) {
						i = 0;
					} else {
						i++;
					}
				} catch (Exception e) {
					e.printStackTrace(System.out);
					if (ElementNotVisibleException.class.isInstance(e)) {
						System.out.println("**********need to relogin************");
						return;
					}
					if (NoSuchWindowException.class.isInstance(e)) {
						System.out.println("**********windows crashed**********");
						return;
					}
					if (TimeoutException.class.isInstance(e)) {
						System.out.println("**********just time out ********");
						// SendEmailWebBroker.saveAccountBlockTime(this.account.getEmail());
						return;
					}
					if (UnreachableBrowserException.class.isInstance(e)) {
						System.out.println("**********browser died********");
						return;
					}

				}
				// finally {
				// SendEmailWebBroker.saveAccountBlockTime(this.account.getEmail());
				// }
			}
		
	}

	public static WebElement waitForElement(WebDriver driver, By by, int timeoutSeconds) throws TimeoutException {
		WebElement element = (new WebDriverWait(driver, timeoutSeconds)).until(new ExpectedCondition<WebElement>() {
			@Override
			public WebElement apply(WebDriver d) {
				return d.findElement(by);

			}
		});
		return element;
	}

	private void deleteAccount(String email) {
		try {
			Connection conn = ConnectionPool.getConnectionPool().getConnection();
			PreparedStatement pstmt = conn.prepareStatement("delete from contactseller_robot_account where email=?");
			pstmt.setString(1, email);
			pstmt.addBatch();
			pstmt.executeBatch();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
