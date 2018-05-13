package ShareHouse.Models;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class SharedBy {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer sharedID;
	private Integer userID;
	private Integer houseID;
	private Integer isAdmin;
	
	public SharedBy() {}
	
	public SharedBy(Integer sharedID, Integer userID, Integer houseID, Integer isAdmin) {
		super();
		this.sharedID = sharedID;
		this.userID = userID;
		this.houseID = houseID;
		this.isAdmin = isAdmin;
	}

	public Integer getSharedID() {
		return sharedID;
	}

	public void setSharedID(Integer sharedID) {
		this.sharedID = sharedID;
	}

	public Integer getUserID() {
		return userID;
	}

	public void setUserID(Integer userID) {
		this.userID = userID;
	}

	public Integer getHouseID() {
		return houseID;
	}

	public void setHouseID(Integer houseID) {
		this.houseID = houseID;
	}

	public Integer getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(Integer isAdmin) {
		this.isAdmin = isAdmin;
	}
	
	
	
	
}
	