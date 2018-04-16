package models;

public class ContactSellerTaskFactory {
	public static ContactSellerTask getTaskOfJerry(Amazon_Country c) {
		ContactSellerTask task = new ContactSellerTask("Arrela",
				"JerryTask\\", "SEO Service.txt", new String[] { "Contact_1.jpg" },
				c);
			return task;
	}

	public static ContactSellerTask getTaskOfBens() {
		ContactSellerTask task = new ContactSellerTask("Stellar","BensTask\\", "Stellar Service.txt",
				new String[] { "Contacta.JPG", "Contactb.JPG", "Contactc.JPG" }, Amazon_Country.US);
		return task;
	}

}
