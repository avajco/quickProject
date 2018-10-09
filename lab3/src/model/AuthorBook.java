package model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleIntegerProperty;

public class AuthorBook {
	private Author author;
	private Book book;
	private SimpleIntegerProperty royalty;
	private boolean newRecord;
  public AuthorBook(Book book, Author author, int royalty) {
	  this.book = book;
	  this.author = author;
	  this.royalty = new SimpleIntegerProperty();
	  this.newRecord = true;
  }
  public AuthorBook() {
	  this(null, null, 0);
  }
public Author getAuthor() {
	return author;
}
public void setAuthor(Author author) {
	this.author = author;
}
public Book getBook() {
	return book;
}
public void setBook(Book book) {
	this.book = book;
}
public int getRoyalty() {
	return this.royalty.get();
}
public void setRoyalty(int royalty) {
	this.royalty.set(royalty);;
}
public boolean isNewRecord() {
	return newRecord;
}
public void setNewRecord(boolean newRecord) {
	this.newRecord = newRecord;
}
public SimpleIntegerProperty royaltyProperty() {
	return this.royalty;
}
public String toString(){
	return this.getAuthor().getfirstName() + " " + this.getBook().getTitle();
}
}
