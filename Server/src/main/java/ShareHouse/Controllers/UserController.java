package ShareHouse.Controllers;


import java.util.Optional;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import ShareHouse.Models.User;
import ShareHouse.Models.hashing;
import ShareHouse.Services.UserService;

@RestController
public class UserController {
		
	
	@Autowired
	private UserService UserService;
	
	@Autowired
	private ShareHouse.Services.EmailService EmailService;
	
	@Autowired
	private HttpSession session;
	
	
	//Login
	@RequestMapping(value="/login")
	public String loginUser(HttpServletRequest request,@RequestBody User user) throws JSONException {
		
		JSONObject response = new JSONObject();
		Optional<User> user_details = UserService.getUserByUserName(user.getUserName());	
		
		if(user_details.isPresent()) {
			if(user_details.get().getPassword().equals(user.getPassword())) {
					session = request.getSession();
					session.setMaxInactiveInterval(10*60);
					response.put("type","success");
					session.setAttribute("UserID", user_details.get().getUserID());
					session.setAttribute("UserEmail", user_details.get().getEmail());
					response.put("message","Loged In.");				
			}
			else{
				response.put("type","fail");
				response.put("message","Invalid Password");
			}
		}
		else {
			response.put("type","fail");
			response.put("message","Username doesnot exist");
		}
		return response.toString();
	}
	
	
	//Register	
	@RequestMapping(method=RequestMethod.POST,value="/register")
	public String registerUser(@RequestBody User user) throws JSONException {
		
		JSONObject response = new JSONObject();
		
		if(user.getFirstName().equals("") || user.getLastName().equals("") || user.getEmail().equals("") || user.getUserName().equals("") || user.getPassword().equals("")) {	
			response.put("type","fail");
			response.put("message","Cannot leave fields empty");
		}else if(!user.getEmail().matches("^(.+)@(.+)$")) {	
			response.put("type","fail");
			response.put("message","Please enter Valid email");
		}else if(!user.getUserName().matches("^[A-Za-z_][A-Za-z\\d_]*$")) {	
			response.put("type","fail");
			response.put("message","Username should be alphabets");
		}
		else if(UserService.getUserByEmail(user.getEmail()).isPresent()) {
			response.put("type","fail");
			response.put("message","Email already exists");
		}
		else if(UserService.getUserByUserName(user.getUserName()).isPresent()) {
			response.put("type","fail");
			response.put("message","Username already exists");
		}	
		else {
			if(UserService.addUser(user)==null) {
				response.put("type","Fail");
				response.put("message", "Unable to create account");
			}
			else {
				response.put("type","success");
				response.put("message","Account created");
				try {
					EmailService.welcomemail(user);
					}catch(MailException e) {System.out.print("Email not sent"+e.getMessage());}
			}
		}		
		return response.toString();
	}
	
	
	//Profile 
	@RequestMapping(value="/user")
	public String getUser(HttpSession session) throws JSONException {
			
		JSONObject response = new JSONObject();
				
		if (null == session.getAttribute("UserID"))  {
			response.put("type","fail");
			response.put("message","Please Login");
			response.put("action","login");
			return response.toString();
		} 
		else 
		{		
			Optional<User> user_details = UserService.getUser(Integer.parseInt(session.getAttribute("UserID").toString()));
		
			if(user_details.isPresent()) {
				String json = null;
				ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();			
				try{
					json = ow.writeValueAsString(user_details.get());	
				}catch (JsonProcessingException e){
					e.printStackTrace();
					}
				response.put("User",json.toString());
				response.put("type","success");			
			}
			else
			{
				response.put("type","fail");
				response.put("message","failed to fetch user details");
				response.put("action", "login");
			}
			return response.toString();
		}
		
	}
	
	
	//Forgot Password 
	@RequestMapping(value="/requestnewpassword")
	public String requestNewPassword(@RequestBody String username) throws JSONException {
		
		JSONObject response = new JSONObject();
		String username_new = username.substring(9);
		
		//check by username
		Optional<User> user_details = UserService.getUserByUserName(username_new);
		
		//check by email
		if(!user_details.isPresent()) {
		username = username_new.replace("%40", "@");
		user_details = UserService.getUserByEmail(username);
		}
		
		//update password and email
		if(user_details.isPresent()) {
			
			//Generate random password
			Random rand = new Random(); 
			int value = rand.nextInt(500000)+99999;
			String password = Integer.toString(value);
			
			//Send email with new password
			User user = user_details.get();
			user.setPassword(password);
			EmailService.passwordchangemail(user);
			
			//hash and update datase with new password
			password = hashing.hashString(password);
			user.setPassword(password);
			UserService.updateUser(user);
			
			response.put("type","success");
			response.put("message","New Password Emailed");
		}
		else {
			response.put("type","fail");
			response.put("message","Username/Email not Found");
			}			
		return response.toString();
		
	}
	
	
	//Logout Activity
	@RequestMapping(value="/logout")
	public String logout(HttpSession session) throws JSONException {
			
		//if(session)
		JSONObject response = new JSONObject();
				
		if (null == session.getAttribute("UserID"))  {
			response.put("type","fail");
			response.put("message","Please Login first");
			response.put("action","login");
		}
		else {
			session.invalidate();
			response.put("type","success");
			response.put("message","Logged Out");
			response.put("action","login");			
		}
		return response.toString();		
	}
	
	
	//Change Password
	@RequestMapping(method=RequestMethod.POST,value="/changePassword")
	public String changePassword(@RequestBody String passwords) throws JSONException {
		
		JSONObject response = new JSONObject();
		
		//Get password details
		String oldPassword = passwords.substring(passwords.indexOf("oldPassword=")+12,passwords.indexOf("newPassword=")-1);
		String newPassword = passwords.substring(passwords.indexOf("newPassword=")+12);
		
		//Check if session exist
		if (null == session.getAttribute("UserID")) {
			response.put("type","fail");
			response.put("message","Please Login first");
			response.put("action","login");
		}
		else {
			
			Optional<User> user = UserService.getUser(Integer.parseInt(session.getAttribute("UserID").toString()));	
			
			if(user.isPresent() && user.get().getPassword().equals(oldPassword)) {
				user.get().setPassword(newPassword);
				UserService.updateUser(user.get());
				response.put("type","success");
				response.put("message","Password Changed");
			}
			else {
				response.put("type","fail");
				response.put("message","Incorrect Old Password");
			}
		}		
		return response.toString();
	}
	
	//Edit Profile
	@RequestMapping(method=RequestMethod.POST,value="/editprofile")
	public String editProfile(HttpSession session,@RequestBody User user) throws JSONException {
		
		JSONObject response = new JSONObject();
		
		//Check if session exist
		if (null == session.getAttribute("UserID")) {
			response.put("type","fail");
			response.put("message","Please Login first");
			response.put("action","login");
		}
		else {

			int userId = Integer.parseInt(session.getAttribute("UserID").toString());
			Optional<User> u1 = UserService.getUserByEmail(user.getEmail());
			Optional<User> u2 = UserService.getUserByUserName(user.getUserName());
			if(u1.isPresent() && u1.get().getUserID()!=userId) {
			response.put("type","fail");
			response.put("message","Email already exists");
			}
			else if(u2.isPresent() && u2.get().getUserID()!=userId) {
			response.put("type","fail");
			response.put("message","Username already exists");
			}	
			else {
			UserService.editUser(user);
			response.put("type","success");
			response.put("message","Profile updated");
			}		
		}
		return response.toString();
	}

}

