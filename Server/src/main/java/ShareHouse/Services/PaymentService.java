package ShareHouse.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ShareHouse.Models.Payment;
import ShareHouse.Repository.PaymentRepository;

@Service
public class PaymentService {

	@Autowired
	private PaymentRepository PaymentRepository;

	public List<Payment> findPaymentsByUserID1(Integer UserID1) {
		List<Payment> ls = new ArrayList<Payment>();
		ls = PaymentRepository.findAllByUserID1(UserID1);
		return ls;
	}
	
	public List<Payment> findPaymentsByUserID2(Integer UserID2) {
		List<Payment> ls = new ArrayList<Payment>();	
		ls = PaymentRepository.findAllByUserID2(UserID2);
		return ls;
	}

	public void addPayment(Integer bookingID, Integer userID, Integer nextID, int eachPerson) {
		Payment p=new Payment();
		p.setBookingID(bookingID);
		p.setUserID1(userID);
		p.setUserID2(nextID);
		p.setAmount(eachPerson);
		PaymentRepository.save(p);
	}

	public void deleteByBookingID(Integer bookingID) {
		PaymentRepository.deleteByBookingID(bookingID);
	}

	public String deleteByPaymentID(Integer userID, int paymentID) {
		Optional<Payment> p = PaymentRepository.findById(paymentID);
		if(p.isPresent()) {
			if(p.get().getUserID2()==userID) {
				PaymentRepository.delete(p.get());
				return "Payment Deleted";
			}
			else {
				return "Only ower can delete a payment";
			}
		
		}
		else {
			return "Payment not found";
		}
	}
	
	public Optional<Payment> findPaymentsByBookingID(Integer b){
		return PaymentRepository.findByBookingID(b);
	}

}
