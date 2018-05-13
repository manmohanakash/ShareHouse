package ShareHouse.Models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Payment {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer paymentID;
	private Integer bookingID;
	private Integer userID1;
	private Integer userID2;
	private Integer amount;
	
	public Payment() {}
	
	public Payment(Integer paymentID, Integer bookingID,Integer userID1, Integer userID2, Integer amount) {
		super();
		this.paymentID = paymentID;
		this.bookingID = bookingID;
		this.userID1 = userID1;
		this.userID2 = userID2;
		this.amount = amount;
	}

	public Integer getPaymentID() {
		return paymentID;
	}

	public void setPaymentID(Integer paymentID) {
		this.paymentID = paymentID;
	}
	
	public Integer getBookingID() {
		return bookingID;
	}

	public void setBookingID(Integer bookingID) {
		this.bookingID = bookingID;
	}

	public Integer getUserID1() {
		return userID1;
	}

	public void setUserID1(Integer userID1) {
		this.userID1 = userID1;
	}

	public Integer getUserID2() {
		return userID2;
	}

	public void setUserID2(Integer userID2) {
		this.userID2 = userID2;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	
	
	
}
