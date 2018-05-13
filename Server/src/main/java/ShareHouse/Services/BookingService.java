package ShareHouse.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ShareHouse.Models.Booking;
import ShareHouse.Repository.BookingRepository;

@Service
public class BookingService {
	
	@Autowired
	private BookingRepository BookingRepository;

	public List<Booking> findByHouseID(int houseID) {
		return BookingRepository.findByHouseID(houseID);
	}

	public Booking addnewbooking(Booking book) {
		return BookingRepository.save(book);
	}

	public List<Integer> findMyBookingsByHouseID(int houseID, Integer userID) {
		return BookingRepository.findMyBookingByHouseID(houseID,userID);
	}

	public void deleteMyBooking(int houseID, Integer userID, Integer bookingWeek) {
		BookingRepository.deleteRecord(houseID,userID,bookingWeek);
	}

	public boolean checkbooking(Booking book) {
		Optional<Booking> b = BookingRepository.findAnyBooking(book.getHouseID(),book.getBookingWeek());
		
		if(b.isPresent()) {
			return false;
		}
		return true;
		
	}

	public int getTotalBookings(int houseID, Integer userID) {
		List<Booking> ls = BookingRepository.getTotalBookings(houseID,userID);
		return ls.size();
	}

	public Booking getBookingByAll(int houseID, Integer userID, Integer bookingWeek) {
		return BookingRepository.getBookingByAll(houseID,userID,bookingWeek);
	}

}
