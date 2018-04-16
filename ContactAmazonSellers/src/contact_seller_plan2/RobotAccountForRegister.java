package contact_seller_plan2;

import models.Amazon_Country;
import models.RobotAccount;

public class RobotAccountForRegister {
	private Amazon_Country country;
	private String email;
	private String password;
	private String name;

	public RobotAccountForRegister(String email, String name, String password) {
		this.email = email;
		this.password = password;
		this.name = name;
	}

	public Amazon_Country getCountry() {
		return country;
	}

	public void setCountry(Amazon_Country country) {
		this.country = country;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
