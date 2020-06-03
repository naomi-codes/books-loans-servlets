package user;

public class LoggedInBorrower {
	private String borrowerNo;
	private String name;
	private String department;

	public LoggedInBorrower(String borrowerNo, String name, String department) {
		setBorrowerNo(borrowerNo);
		setName(name);
		setDepartment(department);
	}

	public String getBorrowerNo() {
		return borrowerNo;
	}

	public void setBorrowerNo(String borrowerNo) {
		this.borrowerNo = borrowerNo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

}
