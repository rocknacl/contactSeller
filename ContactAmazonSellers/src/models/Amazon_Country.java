package models;

import java.util.ArrayList;

public enum Amazon_Country {
	US("www.amazon.com","ATVPDKIKX0DER"),UK("www.amazon.co.uk","A1F83G8C2ARO7P"),JP("www.amazon.co.jp","A1VC38T7YXB528"),ES("www.amazon.es",null),IT("www.amazon.it",null),FR("www.amazon.fr",null),DE("www.amazon.de",null),CA("www.amazon.ca","A2EUQ1WTGCTBG2");
	
	private Amazon_Country(String domain,String marketplaceID) {
		this.domain = domain;
		this.marketplaceID = marketplaceID;
	}

	private String domain;
	private String marketplaceID;
	public String getDomain() {
		return "http://"+domain+"/";
	}
	public String getMarketplaceID() {
		return marketplaceID;
	}
	public static ArrayList<String> stringValues(){
		ArrayList<String> values = new ArrayList<String>();
		for(Amazon_Country c : values()){
			values.add(c.toString());
		}
		return values;
	}


	
}
