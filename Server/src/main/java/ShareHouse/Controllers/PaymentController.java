package ShareHouse.Controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import ShareHouse.Models.House;
import ShareHouse.Models.Payment;
import ShareHouse.Models.User;
import ShareHouse.Services.HouseService;
import ShareHouse.Services.SharedByService;
import ShareHouse.Services.UserService;
import ShareHouse.Services.PaymentService;

@RestController
public class PaymentController {

	@Autowired
	private HttpSession session;

	@Autowired
	private UserService UserService;

	@Autowired
	private HouseService HouseService;

	@Autowired
	private SharedByService SharedByService;

	@Autowired
	private PaymentService PaymentService;

	@RequestMapping(value = "/fetchmypayments")
	public String fetchMyPayments(HttpSession session) throws JSONException, JsonProcessingException {

		JSONObject response = new JSONObject();
		ObjectWriter ow = new ObjectMapper().writer();

		if (null == session.getAttribute("UserID")) {
			response.put("type", "fail");
			response.put("message", "Please Login");
			response.put("action", "login");
			return response.toString();
		} else {
			int UserID = Integer.parseInt(session.getAttribute("UserID").toString());

			List<Payment> paymentspostive = PaymentService.findPaymentsByUserID1(UserID);
			List<Payment> paymentsnegative = PaymentService.findPaymentsByUserID2(UserID);
			paymentspostive.addAll(paymentsnegative);

			// Get user id
			HashSet<Integer> hs = new HashSet<Integer>();
			for (int i = 0; i < paymentspostive.size(); i++) {
				hs.add(paymentspostive.get(i).getUserID1());
				hs.add(paymentspostive.get(i).getUserID2());
			}

			// Get user email
			List<User> users = new ArrayList<User>();
			Iterator hsitr = hs.iterator();
			while (hsitr.hasNext()) {
				int i = (int) hsitr.next();
				Optional<User> temp = UserService.getUser(i);
				if (temp.isPresent()) {
					temp.get().setPassword("");temp.get().setUserName("");
					users.add(temp.get());
				}
			}

			// write user email to json
			JSONArray user_json = new JSONArray();
			for (int i = 0; i < users.size(); i++) {
				User here = users.get(i);
				String json = ow.writeValueAsString(here);
				JSONObject jsonObj = new JSONObject(json);
				user_json.put(jsonObj);
			}
			
			
			List<Payment> cummulative = new ArrayList<Payment>();
			for(int j=0;j<paymentspostive.size();j++) {
				int flag= 0;
				for(int x = 0; x<cummulative.size();x++) {
					if(paymentspostive.get(j).getUserID1() == cummulative.get(x).getUserID1() && paymentspostive.get(j).getUserID2() == cummulative.get(x).getUserID2()) {
						cummulative.get(x).setAmount(paymentspostive.get(j).getAmount()+cummulative.get(x).getAmount());
						flag=1;
						break;
					}
				}
				if(flag==0) {
					cummulative.add(paymentspostive.get(j));
				}
			}
			
			List<Payment> paymentslist = new ArrayList<Payment>();
			for(int y = 0; y<cummulative.size();y++) {
				int flag= 0;
				for(int x = 0; x<paymentslist.size();x++) {
					if(cummulative.get(y).getUserID1() == paymentslist.get(x).getUserID2() && cummulative.get(y).getUserID2() == paymentslist.get(x).getUserID1()) {
						if(cummulative.get(y).getAmount()>paymentslist.get(x).getAmount()) {
							int temp = paymentslist.get(x).getUserID1();
							paymentslist.get(x).setUserID1(paymentslist.get(x).getUserID2());
							paymentslist.get(x).setUserID2(temp);
							paymentslist.get(x).setAmount(cummulative.get(y).getAmount()-paymentslist.get(x).getAmount());

							flag=1;
						}
						else {
							paymentslist.get(x).setAmount(paymentslist.get(x).getAmount()-cummulative.get(y).getAmount());
							flag=1;
						}
						break;
					}
				}
				if(flag==0) {
					paymentslist.add(cummulative.get(y));
				}
			}
			
			for(int z = 0;z<paymentslist.size();z++) {
				if(paymentslist.get(z).getAmount() == 0) {
					paymentslist.remove(z);
				}
			}
			
			Iterator itr = paymentslist.iterator();
			JSONArray payment_json = new JSONArray();
			while (itr.hasNext()) {
				Payment here = (Payment) itr.next();
				String json = ow.writeValueAsString(here);
				JSONObject jsonObj = new JSONObject(json);
				payment_json.put(jsonObj);
			}

			response.put("Names", user_json);
			response.put("UserID", UserID);
			response.put("payments", payment_json);
			response.put("type", "success");
		}
		return response.toString();
	}

	@RequestMapping(value="/deletepayment")
	public String deletePayment(HttpSession session,@RequestBody String result) throws JSONException, JsonProcessingException {
	
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
			int user1 = Integer.parseInt(obj.get("userID1").toString());
			int user2 = Integer.parseInt(obj.get("userID2").toString());
			int amt = Integer.parseInt(obj.get("amt").toString());
			Integer UserID = Integer.parseInt(session.getAttribute("UserID").toString());
			
			if(UserID==user2) {		
			//String message =PaymentService.deleteByPaymentID(UserID,PaymentID);
				PaymentService.addPayment(0, user2, user1, amt);
				response.put("type","success");
				response.put("message", "Payment Recorded");
				return response.toString();
			}
			else {
				response.put("type","success");
				response.put("message", "You can record only your payments");
				return response.toString();
			}
		
		}
	}
}
