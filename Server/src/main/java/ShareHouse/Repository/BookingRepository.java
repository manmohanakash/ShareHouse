package ShareHouse.Repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ShareHouse.Models.Booking;

public interface BookingRepository extends CrudRepository<Booking,Integer> {


	public List<Booking> findByHouseID(int houseID);

	 @Query("SELECT bookingWeek FROM Booking WHERE houseID=?1 AND userID=?2")
	public List<Integer> findMyBookingByHouseID(int houseID,int userID);

	@Transactional
	@Modifying
	@Query("DELETE FROM Booking WHERE houseID=?1 AND userID=?2 AND bookingWeek=?3")
	public void deleteRecord(int houseID, Integer userID, Integer bookingWeek);

	@Query("SELECT bookingID FROM Booking WHERE houseID=?1 AND bookingWeek=?2")
	public Optional<Booking> findAnyBooking(Integer houseID,Integer bookingWeek);

	@Query("SELECT bookingID FROM Booking WHERE houseID=?1 AND userID=?2")
	public List<Booking> getTotalBookings(int houseID, Integer userID);

	@Query("SELECT c FROM Booking AS c WHERE c.houseID=?1 AND c.userID=?2 AND c.bookingWeek=?3")
	public Booking getBookingByAll(int houseID, Integer userID, Integer bookingWeek);

}
