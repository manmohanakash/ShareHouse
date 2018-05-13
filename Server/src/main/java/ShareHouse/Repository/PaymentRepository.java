package ShareHouse.Repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ShareHouse.Models.Payment;

public interface PaymentRepository extends CrudRepository<Payment,Integer> {

	List<Payment> findAllByUserID1(Integer UserID1);

	List<Payment> findAllByUserID2(Integer userID2);

	@Transactional
	@Modifying
	@Query("DELETE FROM Payment WHERE bookingID=?1")
	void deleteByBookingID(Integer bookingID);

	Optional<Payment> findByBookingID(Integer b);

}
