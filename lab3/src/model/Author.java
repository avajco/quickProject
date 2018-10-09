package model;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import lab2.db.AppException;
import lab2.db.AuthorTableGateway;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Author {

	private SimpleObjectProperty<LocalDate> dob;
	private int id;
	private SimpleStringProperty firstName;
	private SimpleStringProperty lastName;
	private SimpleStringProperty gender;
	private SimpleStringProperty webSite;
	private AuthorTableGateway gateway;
	boolean valid = false;
	private String title;
	private String feedback1;
	private String feedback2;
	

	private LocalDateTime lastModified;

	public Author(String firstName) {
		this.firstName = new SimpleStringProperty(firstName);
		this.lastName = new SimpleStringProperty("N?");
		this.gender = new SimpleStringProperty("N?");
		this.webSite = new SimpleStringProperty("N?");
		this.dob = new SimpleObjectProperty<LocalDate>();
		id = 0;
	}

	public Author() {
		this("");
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getGender() {
		return gender.get();
	}

	public void setGender(String gender) {
		this.gender.set(gender);
		;
	}

	public String getLastName() {
		return lastName.get();
	}

	public void setLastName(String lastName) {
		this.lastName.set(lastName);
	}

	public String getWebSite() {
		return webSite.get();
	}

	public void setWebSite(String webSite) {
		this.webSite.set(webSite);
	}

	public LocalDate getDob() {
		return this.dob.getValue();
	}

	@Override
	public String toString() {
		return firstName.get();
	}

	public void setDob(LocalDate dob) {
		this.dob.setValue(dob);
	}

	public String getfirstName() {
		return firstName.get();
	}

	public void setfirstName(String firstName) {
		this.firstName.set(firstName);
	}

	public AuthorTableGateway getGateway() {
		return gateway;
	}

	public void setGateway(AuthorTableGateway gateway) {
		this.gateway = gateway;
	}

	public boolean updateAuthor() throws AppException, SQLException {

		if (validation()) {
			gateway.updateAuthor(this);
		} else {
			feedback2 = " Author not updated. Try again.";
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle(title);
			alert.setContentText(feedback1 + feedback2);
			alert.showAndWait();
		}
		return valid;
	}

	public void saveAuthor() throws SQLException {
		if (validation()) {
			gateway.insertAuthor(this);
		} else {
			feedback2 = " Author not saved. Try again.";
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle(title);
			alert.setContentText(feedback1 + feedback2);
			alert.showAndWait();
		}

	}

	public void deleteAuthor() {
		gateway.deleteAuthor(this);
	}

	public SimpleStringProperty firstNameProperty() {
		return firstName;
	}

	public SimpleStringProperty lastNameProperty() {
		return lastName;
	}

	public SimpleStringProperty genderProperty() {
		return gender;
	}

	public SimpleStringProperty websiteProperty() {
		return webSite;
	}

	public SimpleObjectProperty<LocalDate> dobProperty() {
		return dob;
	}

	public boolean validationFirstName() {
		if (firstName.get().equals("") || firstName.get().length() > 100) {
			if (firstName.get().length() > 100) {
				feedback1 = "First Name field is invalid. Longer than 100 characters.";
				valid = false;
			} else if (firstName.get().equals("")) {
				feedback1 = "First Name field must not be blank.";
				valid = true;
			} else {
				valid = true;
			}
		}
		return valid;
	}

	public boolean validateGender() {
		if (gender.get().equals("M") || gender.get().equals("F") || gender.get().equals("U")) {
			valid = true;
		} else {
			valid = false;
			feedback1 = "plase enter name M,F or U for male female or unknown.";
		}
		return valid;
	}

	public boolean validateWebsite() {
		if (webSite.get().length() > 100) {
			valid = false;
			title = "Website Field";
			feedback1 = "Website field is longer than 100 characters.";
		} else {
			valid = true;
		}
		return valid;
	}

	public boolean validlastName() {
		if (lastName.get().equals("") || lastName.get().length() > 100) {
			title = "Last Name Field";
			if (lastName.get().length() > 100) {
				feedback1 = "Last Name field is invalid. Longer than 100 characters.";
			} else {
				feedback1 = "Last Name field must not be blank.";
			}
			valid = false;
		} else {

			valid = true;

		}
		return valid;
	}

	public boolean validation() {
		if (this.validateGender() == true && this.validationFirstName() == true && this.validlastName() == true
				&& this.validateWebsite() == true) {
			return true;
		} else {
			return false;
		}
	}
	public LocalDateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
	}
}
