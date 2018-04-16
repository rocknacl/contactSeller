package helpers;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SeleniumHelper {
	
	public static WebElement waitForElement(WebDriver driver, By by, int timeoutSeconds) throws TimeoutException {
		WebElement element = (new WebDriverWait(driver, timeoutSeconds)).until(new ExpectedCondition<WebElement>() {
			@Override
			public WebElement apply(WebDriver d) {
				return d.findElement(by);

			}
		});
		return element;
	}

}
