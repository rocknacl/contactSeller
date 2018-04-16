package contact_seller;

import java.io.File;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

public class WebDriverFactory {
	static ChromeDriverService service;
	static {
//		System.setProperty("webdriver.chrome.driver", SystemConfig.driverFolder + "chromedriver.exe");
		// System.setProperty("webdriver.chrome.driver", "chromedriver");
		// System.setProperty("webdriver.gecko.driver",
		// "file/drivers/geckodriver.exe");
	}

	public static FirefoxDriver getFirefoxDriver() {
		return new FirefoxDriver();
	}

	public static FirefoxDriver getFirefoxDriverWithoutImg() {
		FirefoxProfile firefoxProfile = new FirefoxProfile();
		firefoxProfile.setPreference("permissions.default.image", 2);
		FirefoxDriver driver = new FirefoxDriver(firefoxProfile);
		return driver;
	}

	public static ChromeDriver getChromeDriver() {
		return new ChromeDriver();
	}

	public static ChromeDriver getChromeDriverWithoutImg() {
		Map<String, Object> contentSettings = new HashMap<String, Object>();
		contentSettings.put("images", 2);

		Map<String, Object> preferences = new HashMap<String, Object>();
		preferences.put("profile.default_content_setting_values", contentSettings);

		ChromeOptions options = new ChromeOptions();
		options.setExperimentalOption("prefs", preferences);

		DesiredCapabilities chromeCaps = DesiredCapabilities.chrome();
		chromeCaps.setCapability(ChromeOptions.CAPABILITY, options);

		ChromeDriver driver = new ChromeDriver(chromeCaps);
		return driver;
	}

	public static void buildChromeService() {
		service = new ChromeDriverService.Builder().usingAnyFreePort().build();
		try {
			service.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static ChromeDriver getSocks5ProxifiedChromeDriver(String host, int port) {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--proxy-server=socks5://" + host + ":" + port);
		return new ChromeDriver(options);
	}

//	public static WebDriver getPhantomJSDriver() {
//		PhantomJSDriverService service = PhantomJSDriverService.createDefaultService();
//
//		File file = new File("drivers/phantomjs.exe");
//		File file2 = new File("drivers/ghostdriver");
//		System.setProperty("phantomjs.ghostdriver.path", file2.getAbsolutePath());
//		DesiredCapabilities caps = DesiredCapabilities.phantomjs();
//		caps.setJavascriptEnabled(true);
//		// caps.setCapability("takesScreenshot", true);
//		caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, file.getAbsolutePath());
//		// String[] phantomJsArgs = { "--ignore-ssl-errors=true",
//		// "--ssl-protocol=any" };
//		caps.setCapability(PhantomJSDriverService.PHANTOMJS_GHOSTDRIVER_PATH_PROPERTY, file2.getAbsolutePath());
//		PhantomJSDriver driver = new PhantomJSDriver(service, caps);
//		return driver;
//		// WebDriver driver = new PhantomJSDriver();
//		// driver.manage().window().setSize(new Dimension(1920, 1200));
//		// return driver;
//	}

//	public static WebDriver getHtmlUnitDriver() {
//		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log",
//				"org.apache.commons.logging.impl.NoOpLog");
//		HtmlUnitDriver driver = new HtmlUnitDriver();
//		driver.setJavascriptEnabled(true);
//		return driver;
//	}

	public static ChromeDriver getProfiledChromeDriver(String chromeUserName) {
		ChromeOptions options = new ChromeOptions();
		Map<String, Object> prefs = new HashMap<String, Object>();
		prefs.put("download.default_directory", "D:/chromedownload");
		options.setExperimentalOption("prefs", prefs);
//		options.addArguments("user-data-dir=D:/ChromeData/Users/" + chromeUserName + "/AppData/Local/Google/Chrome/Profile 1");
		options.addArguments("user-data-dir=ChromeData/"+chromeUserName);
		options.addArguments("chrome.switches", "--disable-extensions");
		ChromeDriver driver = new ChromeDriver(options);
		return driver;
	}

	public static WebDriver getWebDriverByType(int browserType) {
		switch (browserType) {
		case 0:
			return new ChromeDriver();
		case 1:
			return new FirefoxDriver();
		case 2:
			return getChromeDriverWithoutImg();
		case 3:
			return getFirefoxDriverWithoutImg();
		case 4:
//			return getPhantomJSDriver();
		case 5: {
//			return getHtmlUnitDriver();
		}
		default:
			return new ChromeDriver();
		}
	}

}
