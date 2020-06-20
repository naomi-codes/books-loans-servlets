package cart;

import java.util.ArrayList;

public class Cart {
	private ArrayList<Book> books;

	public ArrayList<Book> getBooks() {
		return books;
	}

	public void setBooks(ArrayList<Book> books) {
		this.books = books;
	}

	public Cart() {
		books = new ArrayList<Book>();
	}

	public void addBooks(String[] books) {

		for (String bookDetails : books) {
			String[] arrOfBookInfo = bookDetails.split("_", 2);

			String title = arrOfBookInfo[0];
			String author = arrOfBookInfo[1];
			Book newBook = new Book(author, title);
			addBook(newBook);
		}
	}

	public void addBook(Book book) {
		if (!(containsBook(book))) {
			books.add(book);
		}
	}

	public void removeBook(Book book) {
		if (this.books.contains(book))
			this.books.remove(book);
	}

	public void removeBooks(String[] books) {

		for (String bookDetails : books) {
			String[] arrOfBookInfo = bookDetails.split("_", 2);

			String title = arrOfBookInfo[0];
			String author = arrOfBookInfo[1];
			Book book = new Book(author, title);
			removeBook(book);
		}
	}

	public boolean containsBook(Book book) {
		boolean containsBook = false;

		for (Book bookInCart : this.books) {
			if (book.equals(bookInCart))
				containsBook = true;
		}

		return containsBook;
	}

}