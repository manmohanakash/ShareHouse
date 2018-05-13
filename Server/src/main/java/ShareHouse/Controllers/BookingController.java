package ShareHouse.Controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import ShareHouse.Services.HouseService;
import ShareHouse.Services.PaymentService;
import ShareHouse.Services.SharedByService;
import ShareHouse.Services.UserService;
import ShareHouse.Models.Booking;
import ShareHouse.Models.House;
import ShareHouse.Models.Payment;
import ShareHouse.Services.BookingService;

@RestController
public class BookingController {

	
	@Autowired
	private HttpSession session;
	
	@Autowired
	private UserService UserService;
	
	@Autowired
	private HouseService HouseService;
	
	@Autowired
	private BookingService BookingService;
	
	@Autowired
	private SharedByService SharedByService;
	
	@Autowired
	private PaymentService PaymentService;

	@RequestMapping(value="/fetchbookingforhouse")
	public String getAllMyHouse(HttpSession session,@RequestBody String result) throws JSONException, JsonProcessingException {
		
		
		JSONObject response = new JSONObject();
		
		JSONObject obj = (JSONObject) new JSONTokener(result).nextValue();
		int HouseID = Integer.parseInt(obj.get("HouseID").toString());
		Integer UserID = Integer.parseInt(session.getAttribute("UserID").toString());
		
		List<Booking> booked = BookingService.findByHouseID(HouseID);
		
		List<Integer> weeksBooked = new ArrayList<Integer>();
		
		Iterator itr = booked.iterator();
		while(itr.hasNext()) {
			Booking b = (Booking) itr.next();
			weeksBooked.add(b.getBookingWeek());
		}
		
		response.put("type","success");
		response.put("booking",weeksBooked);
		return response.toString();
	}
	
	@RequestMapping(value="/addnewbooking")
	public String addNewBooking(HttpSession session,@RequestBody String result) throws JSONException, JsonProcessingException {	

		JSONObject response = new JSONObject();
		
		if (null == session.getAttribute("UserID"))  {
			response.put("type","fail");
			response.put("message","Please Login");
			response.put("action","login");
			return response.toString();
		} 
		else 
		{
		
		JSONObject obj = (JSONObject) new JSONTokener(result).nextValue();
		int HouseID = Integer.parseInt(obj.get("HouseID").toString());
		Integer UserID = Integer.parseInt(session.getAttribute("UserID").toString());
		List<Integer> weeks =  new ArrayList<Integer>();
		JSONArray weekArray = obj.getJSONArray("weekids");
		
        if (weekArray != null) {
            for (int i=0;i<weekArray.length();i++){
                weeks.add(Integer.parseInt(weekArray.getString(i)));
                }
        }
        
        
        Iterator itr = weeks.iterator();
        
        while(itr.hasNext()){
        	Booking book = new Booking();
        	book.setUserID(UserID);
        	book.setHouseID(HouseID);
        	book.setBookingWeek((Integer) itr.next());
        	if(BookingService.checkbooking(book)) {
        		
        		int numberbooked = BookingService.getTotalBookings(HouseID,UserID);
        		Optional<House> house = HouseService.findByHouseID(HouseID);
        		int freeAllowed = 99;
        		if(house.isPresent()) {freeAllowed=house.get().getMaxWeeks();}
        		
            	Booking b = BookingService.addnewbooking(book);
          
        		if(numberbooked>=freeAllowed) {
        			List<Integer> shareByUsers = SharedByService.findUserIDByHouseID(HouseID);
        			if(shareByUsers.size()>0) {
        			int eachPerson = house.get().getWeekRate()/(shareByUsers.size()-1);
        			for(int i=0;i<shareByUsers.size();i++) {
        				if(UserID!=shareByUsers.get(i))
        				PaymentService.addPayment(b.getBookingID(),UserID,shareByUsers.get(i),eachPerson);
        			}
        			}
        		}
        		
        		response.put("type","success");
        		response.put("message","Booking Success");

        	}
        	else {
        		response.put("type","fail");
        		response.put("message","few weeks got booked before yours");
        		return response.toString();
        	}
        }

		return response.toString();
		}
	}
	
	@RequestMapping(value="/fetchmybookingforhouse")
	public String fetchMyBooking(HttpSession session,@RequestBody String result) throws JSONException, JsonProcessingException {	
		JSONObject response = new JSONObject();
		
		JSONObject obj = (JSONObject) new JSONTokener(result).nextValue();
		int HouseID = Integer.parseInt(obj.get("HouseID").toString());
		Integer UserID = Integer.parseInt(session.getAttribute("UserID").toString());
		List<Integer> weeksBooked = BookingService.findMyBookingsByHouseID(HouseID,UserID);
				
		response.put("type","success");
		response.put("booking",weeksBooked);
		return response.toString();
	}
	
	
	@RequestMapping(value="/removemybooking")
	public String removeMyBooking(HttpSession session,@RequestBody String result) throws JSONException, JsonProcessingException {	
		JSONObject response = new JSONObject();
		
		JSONObject obj = (JSONObject) new JSONTokener(result).nextValue();
		int HouseID = Integer.parseInt(obj.get("HouseID").toString());
		Integer UserID = Integer.parseInt(session.getAttribute("UserID").toString());
		Integer BookingWeek = Integer.parseInt(obj.get("WeekID").toString());
		
		Booking b = BookingService.getBookingByAll(HouseID,UserID,BookingWeek);
		BookingService.deleteMyBooking(HouseID,UserID,BookingWeek);
		PaymentService.deleteByBookingID(b.getBookingID());
		
		response.put("type","success");
		response.put("message","Booking Deleted");
		return response.toString();
	}
	
}
