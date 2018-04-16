package sys;

import contact_seller.SendSellerEmail;
import contact_seller_plan2.SendSellerEmailPlan2;
import models.Amazon_Country;
import models.ContactSellerTask;
import models.ContactSellerTaskFactory;

public class SendEmailSystem {
	// public static String host = "104.203.181.98";
	public static String host = "47.89.185.130";
	// public static String host = "localhost";
	public static Amazon_Country country = Amazon_Country.US;
	public static int i = 0;
	public static boolean captchaUnsolved = false;
	public static boolean useProfiledChromeDriver = false;
	public static void main(String[] args) {
		if (args.length > 0) {
			host = args[0];
		}
		if (args.length > 1) {
			String c = args[1];
			country = Amazon_Country.valueOf(c);
		}
		if (args.length > 2) {
			String mode = args[2];
			int m = Integer.parseInt(mode);
			if (m <2) {
				i = m;
			}
			if(m==2) {
				useProfiledChromeDriver = true;
			}
		}
		System.out.println("host set to " + host);
		System.out.println("country set to " + country);
		while (true) {
			if (captchaUnsolved) {
				try {
					System.out.println("take a rest for 10 mins");
					captchaUnsolved = false;
					Thread.sleep(1000 * 60 * 10);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			try {
				System.out.println("send email start");
				ContactSellerTask task = ContactSellerTaskFactory.getTaskOfJerry(country);
				SendSellerEmailPlan2 sse = new SendSellerEmailPlan2(task);
				sse.run();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				i++;
			}
		}
	}
}
