package ShareHouse.Controllers;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import ShareHouse.Models.House;
import ShareHouse.Models.SharedBy;
import ShareHouse.Models.User;
import ShareHouse.Services.HouseService;
import ShareHouse.Services.SharedByService;
import ShareHouse.Services.UserService;

@RestController
public class HouseController {
	
	@Value("${sharehouse.imageFolderPath}")
	public String imageFolderPath;
	
	
	@Value("${sharehouse.databaseImagePath}")
	public String databaseImagePath;
	
	@Autowired
	private HttpSession session;
	
	@Autowired
	private UserService UserService;
	
	@Autowired
	private HouseService HouseService;
	
	@Autowired
	private SharedByService SharedByService;
	
	
	//Users all house details 
	@RequestMapping(value="/fetchallmyhouse")
	public String getAllMyHouse(HttpSession session) throws JSONException, JsonProcessingException {
		
		JSONObject response = new JSONObject();

		if (null == session.getAttribute("UserID")) 
		{
			response.put("type","fail");
			response.put("message","Please Login");
			response.put("action","login");
			return response.toString();
		} 
		else 
		{
			//adding user email for dashboard
			Optional<User> username = UserService.getUser(Integer.parseInt(session.getAttribute("UserID").toString()));
			if(username.isPresent()) {
				response.put("useremail", username.get().getEmail());
				response.put("username", username.get().getUserName());
			}else {
				response.put("useremail", "");
				response.put("username", "");
			}
			
			//Get all house ids with user id
			List<Integer> HouseID = SharedByService.findHouseIDByUserID(Integer.parseInt(session.getAttribute("UserID").toString()));
			//Get all house objects with house ids
			List<House> Houses = HouseService.getAllHouseByHouseID(HouseID);
			
	        ObjectWriter ow = new ObjectMapper().writer();
			Iterator itr = Houses.iterator();
			JSONArray house_json = new JSONArray();
			
			while(itr.hasNext()) {				
				House house_here = (House)itr.next();
		        String json = ow.writeValueAsString(house_here);
		        JSONObject jsonObj = new JSONObject(json);
		            house_json.put(jsonObj);
			}
			
			response.put("type","success");
			response.put("message","returned houses if exists");
			response.put("House",house_json);
			response.put("sessionID",session.getId().toString());		
		}
		return response.toString();	
	}

	
	//Add new House
	@RequestMapping(method=RequestMethod.POST,value="/addhouse")
	public String addHouse(HttpSession session,@RequestBody String result) throws JSONException, JsonParseException, JsonMappingException, IOException{
		
		JSONObject response = new JSONObject();
		
		if (null == session.getAttribute("UserID")) 
		{
			response.put("type","fail");
			response.put("message","Please Login");
			response.put("action","login");
			return response.toString();
		} 
		else
		{
			JSONObject obj = (JSONObject) new JSONTokener(result).nextValue();
			String houseString = obj.get("house").toString();
			ObjectMapper mapper = new ObjectMapper();
			House house = mapper.readValue(houseString, House.class);
			String imageHouse = obj.get("houseImage").toString();
			
		
			
			if(house.getHouseName().equals("") || house.getHouseStreet().equals("") || house.getHouseCity().equals("") || house.getHouseNo().equals("") ||
				house.getHouseState().equals("") || house.getDescription().equals("") || house.getHouseCountry().equals("") || house.getLandmark().equals("")){
			response.put("type","fail");
			response.put("message","Fields cannot be Empty!");
			}
			else {
				
				House h1 = HouseService.addHouse(house);
				SharedByService.add(Integer.parseInt(session.getAttribute("UserID").toString()),house.getHouseID(),1);
				
				if(!imageHouse.equals("default"))
				{
					byte image[] = Base64.decodeBase64(imageHouse);
					String path = imageFolderPath+h1.getHouseID()+".jpg";
					FileOutputStream fos = new FileOutputStream(new File(path)); 
					fos.write(image); 
					fos.close();
					h1.setHouseImg(databaseImagePath+h1.getHouseID()+".jpg");
					HouseService.updateHouse(h1);
				}
				else {
					h1.setHouseImg(databaseImagePath+"defaulthouse.jpg");
					HouseService.updateHouse(h1);
				}
							
				response.put("type","success");
				response.put("message","House has been added");
			}		
		}
			
		
		return response.toString();
		
	}
	
	
	//Update House
	@RequestMapping(method=RequestMethod.POST,value="/updatehouse")
	public String updateHouse(HttpSession session,@RequestBody String result) throws JSONException, JsonParseException, JsonMappingException, IOException{
		
		JSONObject response = new JSONObject();
		
		if (null == session.getAttribute("UserID")) 
		{
			response.put("type","fail");
			response.put("message","Please Login");
			response.put("action","Login");
			return response.toString();
		} 
		else
		{		

			JSONObject obj = (JSONObject) new JSONTokener(result).nextValue();
			String houseString = obj.get("house").toString();
			ObjectMapper mapper = new ObjectMapper();
			House house = mapper.readValue(houseString, House.class);
			String imageHouse = obj.get("houseImage").toString();
			
			if(house.getHouseName().equals("") || house.getHouseStreet().equals("") || house.getHouseCity().equals("") || house.getHouseNo().equals("") ||
				house.getHouseState().equals("") || house.getDescription().equals("") || house.getHouseCountry().equals("") || house.getLandmark().equals("")){
			response.put("type","fail");
			response.put("message","Fields cannot be Empty!");
			}
			else {

				if(imageHouse.equals("no")) 
					HouseService.updateHouse(house);
				else {
					
					File file = new File(imageFolderPath+house.getHouseID()+".jpg");  
			        file.delete();
					byte image[] = Base64.decodeBase64(imageHouse);
					String path = imageFolderPath+house.getHouseID()+".jpg";
					FileOutputStream fos = new FileOutputStream(new File(path)); 
					fos.write(image); 
					fos.close();
					house.setHouseImg(databaseImagePath+house.getHouseID()+".jpg");
					HouseService.updateHouse(house);
					
				}
				response.put("type","success");
				response.put("message","Update done!");
			}		
		}
			
		
		return response.toString();
		
	}


	//Add new User for house
	@RequestMapping(method=RequestMethod.POST,value="/adduserforhouse")
	public String addUserForHouse(HttpSession session,@RequestBody String result) throws JSONException {
		
		JSONObject obj = (JSONObject) new JSONTokener(result).nextValue();
		int HouseID = Integer.parseInt(obj.get("HouseID").toString());
		String UserEmail = obj.getString("UserEmail");
		int makeAdmin = Integer.parseInt(obj.getString("makeAdmin"));
		
		JSONObject response = new JSONObject();
		
		
		Integer c_user = Integer.parseInt(session.getAttribute("UserID").toString());
		
		if(SharedByService.isAdmin(c_user, HouseID) == 1) {
			List<SharedBy> s = SharedByService.findAllByHouseID(HouseID);
			
			Iterator itr = s.iterator();
			while(itr.hasNext()) {
					SharedBy i =(SharedBy) itr.next();
				if(UserService.getUser(i.getUserID()).get().getEmail().equals(UserEmail)) {
						response.put("type", "fail");
						response.put("message","Already in sharedlist");
						return response.toString();
				}
			}
			
			Optional<User> get_user = UserService.getUserByEmail(UserEmail);
			
			if(get_user.isPresent()) {
				if(makeAdmin==1)
					SharedByService.add(get_user.get().getUserID(),HouseID , 1);
				else
					SharedByService.add(get_user.get().getUserID(),HouseID , 0);
				
				Optional<House> h = HouseService.findByHouseID(HouseID);
				if(h.isPresent()) {
					Integer counter = h.get().getSharedCounter();
					h.get().setSharedCounter(counter+1);
					HouseService.addHouse(h.get());
				}
				response.put("type", "success");
				response.put("message","User was added");
			}
			else {
				response.put("type", "fail");
				response.put("message","User with Email not Found");
			}
		}
		else {
			response.put("type", "fail");
			response.put("message","Not admin of House");
		}
		return response.toString();

	}


	//Remove shared user for house
	@RequestMapping(method=RequestMethod.POST,value="/removeuserforhouse")
	public String removeUserForHouse(HttpSession session,@RequestBody String request) throws JSONException {
		
		JSONObject obj = (JSONObject) new JSONTokener(request).nextValue();
		int HouseID = (int) obj.get("HouseID");
		String UserEmail = obj.getString("UserEmail");
		Integer c_user = Integer.parseInt(session.getAttribute("UserID").toString());
		JSONObject response = new JSONObject();
		
		if(SharedByService.isAdmin(c_user, HouseID) == 1) {
			Optional<User> u = UserService.getUserByEmail(UserEmail);
			if(u.get().getUserID()==c_user) {
				response.put("type", "fail");
				response.put("message","Cannot Remove admin");
				return response.toString();
			}
			
			if(u.isPresent()) {
				
				List<SharedBy> s = SharedByService.findAllByHouseID(HouseID);
				
				Iterator itr = s.iterator();
				while(itr.hasNext()) {
					SharedBy i =(SharedBy) itr.next();
					if(UserService.getUser(i.getUserID()).get().getEmail().equals(UserEmail)) {
						SharedByService.delete(u.get().getUserID(), HouseID);
						Optional<House> h = HouseService.findByHouseID(HouseID);
						if(h.isPresent()) {
							Integer counter = h.get().getSharedCounter();
							h.get().setSharedCounter(counter-1);
							HouseService.addHouse(h.get());
						}
						response.put("type", "success");
						response.put("message","Removed user");
						return response.toString();
					}
				}
				response.put("type", "fail");
				response.put("message","User doesnot share house");
				return response.toString();
			}
			response.put("type", "fail");
			response.put("message","User not found");
		}
		else {
			response.put("type", "fail");
			response.put("message","Not admin of House");
		}
		return response.toString();

	}



	//Admin all house details 
	@RequestMapping(value="/fetchalladminhouse")
	public String getAllAdminHouse(HttpSession session) throws JSONException, JsonProcessingException {
		
		JSONObject response = new JSONObject();

		if (null == session.getAttribute("UserID")) 
		{
			response.put("type","fail");
			response.put("message","Please Login");
			response.put("action","login");
		} 
		else 
		{
			
			
			//Get all house ids with user id
			List<Integer> HouseID = SharedByService.findHouseIDByUserIDandAdmin(Integer.parseInt(session.getAttribute("UserID").toString()));
			
			//Get all house objects with house ids
			List<House> Houses = HouseService.getAllHouseByHouseID(HouseID);
			
	        ObjectWriter ow = new ObjectMapper().writer();
			Iterator itr = Houses.iterator();
			JSONArray house_json = new JSONArray();
			
			while(itr.hasNext()) {				
				House house_here = (House)itr.next();
		        String json = ow.writeValueAsString(house_here);
		        JSONObject jsonObj = new JSONObject(json);
		            house_json.put(jsonObj);
			}
			
			response.put("type","success");
			response.put("message","returned houses for admin if exists");
			response.put("House",house_json);
			response.put("sessionID",session.getId().toString());		
		}
		return response.toString();	
	}
	
	//Delete House
	@RequestMapping(method=RequestMethod.POST,value="/deletehouse")
	public String deleteHouse(HttpSession session,@RequestBody String result) throws JSONException{
		
		JSONObject response = new JSONObject();
		
		JSONObject obj = (JSONObject) new JSONTokener(result).nextValue();
		int houseid = Integer.parseInt(obj.get("HouseID").toString());
		
		if (session == null) 
		{
			response.put("type","fail");
			response.put("message","Please Login");
			response.put("action","Login");
			return response.toString();
		} 
		else
		{
			HouseService.deleteHouse(houseid);
			
			File file = new File(imageFolderPath+houseid+".jpg");  
	        file.delete();
			response.put("type","success");
			response.put("message","House Deleted");
		
		}
			
		
		return response.toString();
		
	}
	
	
	
}
