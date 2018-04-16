package contact_seller;

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
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import helpers.ConnectionPool;

public class ContactSellerBrowserOperation {
	String attachmentPath = "D:\\Contact.jpg";
	String email;
	String password;
	WebDriver driver;
	boolean blocked;

	public ContactSellerBrowserOperation(String email, String password, WebDriver driver) {
		super();
		this.email = email;
		this.password = password;
		this.driver = driver;
	}

	public static ArrayList<String> readSellerIDToGSendMessages() throws SQLException {
		ArrayList<String> ids = new ArrayList<String>();
		Connection conn = ConnectionPool.getConnectionPool().getConnection();
		String sql = "select seller_id from contactseller_seller_feedback_stastics where count_lifetime<100 and send_time is null and send_assigned is null order by rand() limit 20";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			ids.add(rs.getString(1));
			System.out.println(rs.getString(1));
			Statement stmt1 = conn.createStatement();
			stmt1.execute("update contactseller_seller_feedback_stastics set send_assigned = 1 where seller_id = '"
					+ rs.getString(1) + "'");
		}
		conn.close();
		return ids;
	}

	public static String[] readAmazonAccount() throws SQLException {
		String[] emailAndPword = new String[2];
		Connection conn = ConnectionPool.getConnectionPool().getConnection();
		String sql = "select email,password from contactseller_robot_account where block_time is null or datediff(now(),block_time)>0 order by rand() limit 1";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		rs.next();
		emailAndPword[0] = rs.getString("email");
		emailAndPword[1] = rs.getString("password");
		conn.close();
		return emailAndPword;
	}

	public void sendMessages(WebDriver driver, String path, List<String> ids) {

		ArrayList<String> message = readMessageToSend(path);
		for (String id : ids) {
			if (this.blocked) {
				try {
					ContactSellerBrowserOperation.saveAccountStatus(email);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return;
			}
			try {

				contactSeller(id, message);

				Connection conn = ConnectionPool.getConnectionPool().getConnection();
				PreparedStatement pstmt = conn.prepareStatement(
						"update contactseller_seller_feedback_stastics set send_time = ? where seller_id=?");
				pstmt.setString(1, (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()));
				pstmt.setString(2, id);
				pstmt.addBatch();
				pstmt.executeBatch();
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();

			}

		}
	}

	private void login() {
		driver.get("https://www.amazon.com/");
		driver.findElement(By.id("nav-link-yourAccount")).click();
		driver.findElement(By.xpath("//input[@type='email']")).sendKeys(email);
		driver.findElement(By.xpath("//input[@type='password']")).sendKeys(password);
		driver.findElement(By.id("signInSubmit")).click();
	}

	// you must have logged in before calling this procedure.
	private void contactSeller(String sellerID, ArrayList<String> messages) {
		driver.get("https://www.amazon.com/s?me=" + sellerID);
		driver.findElement(By.xpath("//div[@id='merchantFooter']//td/a")).click();
		Set<String> currentWindows = driver.getWindowHandles();
		String formerWindow = driver.getWindowHandle();
		driver.findElement(By.xpath("//span[@id='seller-contact-button']//a")).click();

		for (String s : driver.getWindowHandles()) {
			if (!currentWindows.contains(s)) {
				driver.switchTo().window(s);
				break;
			}
		}
		if (driver.getCurrentUrl().contains("signin")) {
			this.reLogin();
		}
		try {
			Select subjectSelect = new Select(driver.findElement(By.xpath("//select[@name='preOrderSubject']")));
			List<WebElement> ops = subjectSelect.getOptions();
			subjectSelect.selectByIndex(ops.size() - 1);

			driver.findElement(By.xpath("//div[@id='step3']//input[@name='writeButton']")).click();
		} catch (NoSuchElementException e) {
			try {
				driver.findElement(By.xpath("//h4[@class='a-alert-heading']"));
				blocked = true;
			} catch (NoSuchElementException e1) {
				e1.printStackTrace();
			}
		}
		if (attachmentPath != null) {
			driver.findElement(By.xpath("//div[@id='addAttachmentButtonSection']//button")).click();
			driver.findElement(By.xpath("//input[@id='file' and @type='file']")).sendKeys(attachmentPath);
			try {
				WebElement e = (new WebDriverWait(driver, 60)).until(new ExpectedCondition<WebElement>() {
					@Override
					public WebElement apply(WebDriver d) {
						return d.findElement(
								By.xpath("//div[@id='attachmentsSection']/div[contains(text(),'Contact.jpg')]"));
					}
				});
			} catch (TimeoutException e) {
				driver.close();
				driver.switchTo().window(formerWindow);
			}
		}
		for (String s : messages)
			driver.findElement(By.id("comment")).sendKeys(s + "\n");

		driver.findElement(By.xpath("//input[@name='sendEmail']")).click();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		driver.close();
		driver.switchTo().window(formerWindow);
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

	private void reLogin() {
		driver.findElement(By.id("ap_email")).clear();
		driver.findElement(By.id("ap_email")).sendKeys(email);
		driver.findElement(By.id("ap_password")).sendKeys(password);
		driver.findElement(By.id("signInSubmit")).click();
	}

	public static void main(String[] args) {
		while (true) {
			ArrayList<String> ids = null;
			WebDriver driver = null;
			try {
				ids = ContactSellerBrowserOperation.readSellerIDToGSendMessages();
			} catch (SQLException e) {

				e.printStackTrace();
				break;
			}
			if (ids.isEmpty()) {
				System.out.println("no seller id to operate");
				break;
			}
			try {

				String[] account = ContactSellerBrowserOperation.readAmazonAccount();
				driver = new FirefoxDriver();
				ContactSellerBrowserOperation cs = new ContactSellerBrowserOperation(account[0], account[1], driver);
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				cs.login();
				cs.sendMessages(driver, "SEO Service.txt", ids);

			} catch (Exception e1) {
				e1.printStackTrace();
				driver.quit();
				continue;
			} finally {
				Connection conn;
				try {
					conn = ConnectionPool.getConnectionPool().getConnection();
					PreparedStatement pstmt = conn.prepareStatement(
							"update contactseller_seller_feedback_stastics set send_assigned = null where seller_id = ?");
					for (String id : ids) {
						pstmt.setString(1, id);
						pstmt.addBatch();
					}
					pstmt.executeBatch();
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				driver.quit();
			}
		}

	}

	public static void saveAccountStatus(String email) throws SQLException {
		Connection conn = ConnectionPool.getConnectionPool().getConnection();
		PreparedStatement pstmt = conn
				.prepareStatement("update contactseller_robot_account set block_time = ? where email=?");
		pstmt.setString(1, (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()));
		pstmt.setString(2, email);
		pstmt.addBatch();
		pstmt.executeBatch();
		conn.close();
	}
	
	public static void main1(String[] args){
		
	}
}
