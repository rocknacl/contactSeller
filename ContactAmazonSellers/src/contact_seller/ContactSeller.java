package contact_seller;

import java.io.BufferedReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;

import java.sql.SQLException;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import helpers.ConnectionPool;
import models.ContactSellerTask;
import models.Host;
import models.RobotAccount;
import web_brokers.SendEmailWebBroker;

public class ContactSeller {
	Host host;
	ContactSellerTask task;
	RobotAccount account;
	static Logger log = LogManager.getLogger(ContactSeller.class);
	WebDriver driver;
	boolean blocked;
	ArrayList<String> message;

	public ContactSeller(Host host, RobotAccount account, WebDriver driver, ContactSellerTask task)
			throws SQLException {
		this.account = account;
		this.driver = driver;
		this.task = task;
		this.message = readMessageToSend(task.getTaskDataFolder() + task.getMessageFilePath());
		this.host = host;
	}

	private void contactSeller(String sellerID, ArrayList<String> messages, String fileName) throws Exception {
		driver.get(task.getCountry().getDomain()
				+ "ss/help/contact/writeMessage?writeButton=%E6%8F%90%E4%BA%A4&subject=5&orderID=&sellerID=" + sellerID
				+ "&marketplaceID=" + this.task.getCountry().getMarketplaceID() + "&language=en_US");
		try {
			if (driver.getCurrentUrl().contains("signin")) {
				this.reLogin();
			}

			try {
				driver.findElement(By.id("sendEmailForm"));
			} catch (Exception e2) {
				if (driver.findElements(By.xpath("//div[@class='a-box a-alert a-alert-error']")).size() > 0
						|| driver.findElements(By.id("message_error")).size() > 0
						|| driver.findElements(By.id("ap_captcha_img")).size() > 0)
					;
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
				WebElement e = (new WebDriverWait(driver, 60)).until(new ExpectedCondition<WebElement>() {
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
				WebElement sendButton = driver.findElement(By.xpath("//input[@name='sendEmail']"));
				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(false);", sendButton);
				sendButton.click();
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
		driver.get(this.task.getCountry().getDomain()
				+ "gp/flex/sign-out.html/ref=nav_youraccount_signout?ie=UTF8&action=sign-out&path=%2Fgp%2Fyourstore%2Fhome&signIn=1&useRedirectOnSuccess=1");
		AmazonSignIn signin = new AmazonSignIn(this.account.getEmail(), this.account.getPassword(), driver);
		if (signin.signIn()) {
			int i = 0;
			while (true) {
				if (this.blocked) {
					SendEmailWebBroker.saveAccountBlockTime(this.account.getEmail());
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
		} else {
			// this.deleteAccount(email);
			SendEmailWebBroker.saveAccountBlockTime(this.account.getEmail());
			return;
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
