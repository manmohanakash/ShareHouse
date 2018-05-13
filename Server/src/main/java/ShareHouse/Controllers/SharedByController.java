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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import ShareHouse.Models.House;
import ShareHouse.Models.User;
import ShareHouse.Services.SharedByService;
import ShareHouse.Services.UserService;

@RestController
public class SharedByController {
	
	@Autowired
	private UserService UserService;
	
	@Autowired
	private ShareHouse.Services.EmailService EmailService;
	
	@Autowired
	private HttpSession session;
	
	@Autowired
	private SharedByService SharedByService;
	
	//Users all house details 
		@RequestMapping(value="/shareduserlist")
		public String getSharedUserList(HttpSession session,@RequestBody String request) throws JSONException, JsonProcessingException {
			
			JSONObject obj = (JSONObject) new JSONTokener(request).nextValue();
			int HouseID = (int) obj.get("HouseID");
		

			JSONObject response = new JSONObject();
			
			if (session.getAttribute("UserID").toString().equals(null)) 
			{
				response.put("type","fail");
				response.put("message","Please Login");
				response.put("action","Login");
				return response.toString();
			} 
			else 
			{
				
				//Get all user ids with user id
				List<Integer> userID = SharedByService.findUserIDByHouseID(HouseID);
				List<User> u = (List<User>) UserService.getUsersByUserID(userID);
				List<String> user_email = new ArrayList<String>();
				for(User x :u) {
					user_email.add(x.getEmail());
				}
				
				
				response.put("type","success");
				response.put("Name",user_email);
				response.put("UserEmail", session.getAttribute("UserEmail").toString());
				response.put("message","returned shared users if exists");
			}
			return response.toString();	
		}

}
