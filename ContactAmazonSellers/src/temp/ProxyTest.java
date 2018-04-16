	package temp;


import java.io.File;

import java.io.IOException;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;


import sys.SystemConfig;

public class ProxyTest {
	String proxyIp = "localhost";
	String proxyPort = "1080";

	public FirefoxDriver getProxifiedFirefoxDriver(FirefoxProfile profile) {
		profile.setPreference("network.proxy.type", 1);
//		profile.setPreference("network.proxy.socks", "127.0.0.1");
//		profile.setPreference("network.proxy.socks_port", 1080);
		profile.setPreference("network.proxy.http", "23.95.204.244");
		profile.setPreference("network.proxy.http_port", 2252);
//		profile.setPreference(key, value);
//		profile.setPreference("network.proxy.https", "127.0.0.1");
//		profile.setPreference("network.proxy.https_port", 1080);
		profile.setPreference("network.proxy.socks_remote_dns", true);
		profile.setPreference("signon.autologin.proxy", true);
//		profile.setPreference("network.http.phishy-userpass-length", 255);
//		profile.setPreference("network.automatic-ntlm-auth.trusted-uris", "google.com");
		FirefoxDriver driver = new FirefoxDriver(profile);
		return driver;
	}
	
	public ChromeDriver getProxifiedChromeDriver(){
		System.setProperty("webdriver.chrome.driver",SystemConfig.driverFolder+"chromedriver.exe");
//		Proxy proxy = new Proxy();
//		proxy.setSocksProxy(proxyIp+":"+proxyPort);
//		DesiredCapabilities cap = DesiredCapabilities.chrome();
//		cap.setCapability("proxy", proxy);
//		
//
//		ChromeDriver driver = new ChromeDriver(cap);
		ChromeOptions options = new ChromeOptions();
	    options.addArguments("--proxy-server=socks5://" + proxyIp + ":" + proxyPort);
//		 options.addArguments("--proxy-server=http://23.95.204.244:2252:rocknacl:rocknacl");

	    ChromeDriver driver = new ChromeDriver(options);
		return driver;
	}
	
	public FirefoxProfile getFirefoxDriverWithProfile(String profileName){
		ProfilesIni init = new ProfilesIni();
		FirefoxProfile profile = init.getProfile(profileName);
		return profile;
	}
	
	public FirefoxProfile setPlugin(FirefoxProfile profile){
		try {
			profile.addExtension(new File("D://closeproxyauth.vaka@gmail.com.xpi"));
			profile.setPreference("extensions.closeproxyauth.authtoken", "cm9ja25hY2w6cm9ja25hY2w=");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return profile;
	}
	
	public static void main(String[] args){
		ProxyTest test = new ProxyTest();
		FirefoxProfile profile = test.getFirefoxDriverWithProfile("rock");
		profile = test.setPlugin(profile);
		FirefoxDriver driver = test.getProxifiedFirefoxDriver(profile);
//		ChromeDriver driver = test.getProxifiedChromeDriver();

		
//		driver.get("http://@google.com");
	}

}
