package cart;

public class Book {

	private String author;
	private String title;
	private int availableCopy;

	public Book(String author, String title) {
		this.author = author;
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getAvailableCopy() {
		return availableCopy;
	}

	public void setAvailableCopy(int availableCopy) {
		this.availableCopy = availableCopy;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;

		if (o instanceof Book) {
			Book book = (Book) o;

			if (book.getAuthor().equals(this.getAuthor()) && book.getTitle().contentEquals(this.getTitle()))
				return true;
		}

		return false;
	}
}
