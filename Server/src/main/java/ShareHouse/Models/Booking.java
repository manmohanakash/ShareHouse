package ShareHouse.Models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Booking {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer bookingID;
	private Integer houseID;
	private Integer userID;
	private Integer bookingWeek;

	public Booking() {}

	public Booking(Integer bookingID, Integer houseID, Integer userID, Integer bookingWeek) {
		super();
		this.bookingID = bookingID;
		this.houseID = houseID;
		this.userID = userID;
		this.bookingWeek = bookingWeek;
	}

	public Integer getBookingID() {
		return bookingID;
	}

	public void setBookingID(Integer bookingID) {
		this.bookingID = bookingID;
	}

	public Integer getHouseID() {
		return houseID;
	}

	public void setHouseID(Integer houseID) {
		this.houseID = houseID;
	}

	public Integer getUserID() {
		return userID;
	}

	public void setUserID(Integer userID) {
		this.userID = userID;
	}

	public Integer getBookingWeek() {
		return bookingWeek;
	}

	public void setBookingWeek(Integer bookingWeek) {
		this.bookingWeek = bookingWeek;
	}
	
	
}
