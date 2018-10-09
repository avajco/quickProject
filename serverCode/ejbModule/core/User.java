package core;

import misc.CryptoStuff;

public class User {
	private String login;
	private String passwordHash; //hash of user's pw
	private String userName;
	private String role;
		
	public User(String login, String pw, String userName, String role) {
		this.login = login;
		this.userName = userName;
		passwordHash = CryptoStuff.sha256(pw);
		this.role =role;
		
	}

	public String getLogin() {
		return login;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
}
