package contact_seller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import helpers.ConnectionPool;
import models.Amazon_Country;
import models.SellerFeedbackInformation;
import web_brokers.SellerFeedbackWebBroker;

public class SellerFeedbackStatistics {
	Amazon_Country country;
	Connection conn;

	public SellerFeedbackStatistics() throws SQLException {
		super();
		this.conn = ConnectionPool.getConnectionPool().getConnection();
	}

	public void setCountry(Amazon_Country c) {
		this.country = c;
	}

	public ArrayList<String> getSellerFeedbackInformation(WebDriver driver, String sellerID) {
		ArrayList<String> result = new ArrayList<String>();
		System.out.println(sellerID);
		driver.get(country.getDomain() + "sp?_encoding=UTF8&marketplaceID=" + this.country.getMarketplaceID()
				+ "&seller=" + sellerID + "&tab=feedback");
		if (driver.getTitle().contains("Robot Check")) {
			System.out.println("Robot check");
			throw new UnreachableBrowserException("Robot check");
		}
		try {
			// String feeds =
			// driver.findElement(By.id("feedback-summary-table")).getText();
			String feeds = driver.findElement(By.xpath("//table[@class='a-bordered']")).getText();
			System.out.println(feeds);
			String[] lines = feeds.split("\n");
			for (int i = 1; i < lines.length; i++) {
				String line = lines[i];
				String[] words = line.split(" ");
				for (int m = 1; m < words.length; m++) {
					result.add(words[m]);
				}
			}
		} catch (NoSuchElementException e) {
			try {
				driver.findElement(By.id("feedback-no-review"));
				for (int i = 0; i < 16; i++) {
					result.add("0");
				}
				System.out.println("no feedback yet");
			} catch (NoSuchElementException e1) {
				throw e1;
			}
		}
		return result;
	}

	public List<SellerFeedbackInformation> getFeedbackInformationOfMultipleSellers(WebDriver driver, List<String> ids)
			throws SQLException {
		List<SellerFeedbackInformation> result = new ArrayList<SellerFeedbackInformation>();
		for (String id : ids) {

			try {
				ArrayList<String> info = getSellerFeedbackInformation(driver, id);
				SellerFeedbackInformation sfi = new SellerFeedbackInformation();
				sfi.setCountry("US");
				sfi.setSellerID(id);
				sfi.setPositive_month_1(info.get(0));
				sfi.setPositive_month_3(info.get(1));
				sfi.setPositive_year_1(info.get(2));
				sfi.setPositive_lifetime(info.get(3));
				sfi.setNeutral_month_1(info.get(4));
				sfi.setNeutral_month_3(info.get(5));
				sfi.setNeutral_year_1(info.get(6));
				sfi.setNeutral_lifetime(info.get(7));
				sfi.setNegative_month_1(info.get(8));
				sfi.setNegative_month_3(info.get(9));
				sfi.setNegative_year_1(info.get(10));
				sfi.setNegative_lifetime(info.get(11));
				sfi.setCount_month_1(Integer.parseInt(info.get(12).replaceAll(",", "")));
				sfi.setCount_month_3(Integer.parseInt(info.get(13).replaceAll(",", "")));
				sfi.setCount_year_1(Integer.parseInt(info.get(14).replaceAll(",", "")));
				sfi.setCount_lifetime(Integer.parseInt(info.get(15).replaceAll(",", "")));
				result.add(sfi);
			} catch (UnreachableBrowserException e) {
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public void saveToDB(String sellerID, ArrayList<String> data) throws SQLException {
		// Connection conn = ConnectionPool.getConnectionPool().getConnection();
		String sql = "update contactseller_seller_feedback_stastics set  positive_month_1 = ?, positive_month_3= ?, positive_year_1= ?, positive_lifetime= ?, neutral_month_1= ?, neutral_month_3= ?, neutral_year_1= ?, neutral_lifetime= ?, negative_month_1= ?, negative_month_3= ?, negative_year_1= ?, negative_lifetime= ?, count_month_1= ?, count_month_3= ?, count_year_1= ?, count_lifetime= ? where seller_id = ? and country = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);

		for (int i = 0; i < data.size(); i++) {
			pstmt.setString(i + 1, data.get(i).replace(",", ""));
		}
		pstmt.setString(data.size() + 1, sellerID);
		pstmt.setString(data.size() + 2, this.country.toString());
		pstmt.addBatch();
		pstmt.executeBatch();
		// conn.close();
		System.out.println("inserted");

	}

	public ArrayList<String> readSellerIDToGetFeedback() throws SQLException {
		ArrayList<String> ids = new ArrayList<String>();
		// Connection conn = null;
		try {
			// conn = ConnectionPool.getConnectionPool().getConnection();
			conn.setAutoCommit(false);
			String sql = "select seller_id from contactseller_seller_feedback_stastics where positive_month_1 is null and assigned is null and country = '"
					+ this.country.toString() + "' limit 1 ; ";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				ids.add(rs.getString(1));
			}
			rs.close();
			for (String id : ids) {
				System.out.println(id);
				stmt.execute("update contactseller_seller_feedback_stastics set assigned = 2 where seller_id = '" + id
						+ "'");

			}
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("read failed");
		} finally {
			// conn.close();
			conn.setAutoCommit(true);
		}
		return ids;
	}

	public static void main(String[] args) {
		int i = 1;
		while (true) {
			WebDriver driver = WebDriverFactory.getChromeDriver();
			SellerFeedbackStatistics cs = null;
			try {
				cs = new SellerFeedbackStatistics();
				// cs.setCountry(SendEmailSystem.country);
				cs.setCountry(Amazon_Country.valueOf(args[0].toUpperCase()));
			} catch (SQLException e1) {
				e1.printStackTrace();
				break;
			}
			while (true) {
				try {
					// ArrayList<String> ids = cs.readSellerIDToGetFeedback();
					List<String> ids = SellerFeedbackWebBroker.getSellerIDs(cs.country.toString());
					System.out.println("download sellerid : " + ids.size());
					if (ids.isEmpty()) {
						System.out.println("no id to download");
						break;
					}
					SellerFeedbackWebBroker
							.saveFeedbackInformation(cs.getFeedbackInformationOfMultipleSellers(driver, ids));

					Thread.sleep(1000 * 5);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				}
			}
			try {
				Thread.sleep(1000 * 60 * 5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			driver.quit();

			// i++;

		}
	}

}
