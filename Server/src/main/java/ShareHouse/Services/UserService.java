package ShareHouse.Services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;

import ShareHouse.Models.User;
import ShareHouse.Repository.UserRepository;


@Service
public class UserService {
	
	@Autowired
	private UserRepository UserRepository;
	
	public List<User> getAllUser(){
		List<User> users = new ArrayList<User>();
		UserRepository.findAll().forEach(users::add);
		return users;
	}
	
	public User addUser(User user) {
		try {
			return UserRepository.save(user);
		}
		catch (JpaSystemException e) {
			System.out.println(e.getMessage());
			return null;
		}
		
	}
	
	public Optional<User> getUser(int id) {
		return UserRepository.findById(id);
	}
	
	public Optional<User> getUserByUserName(String username) {
		return UserRepository.getUserByUserName(username);
	}
	
	public Optional<User> getUserByEmail(String email) {
		return UserRepository.getUserByEmail(email);
	}
	
	
	public void updateUser(User user) {
		UserRepository.save(user);
	}
	
	public void deleteUser(String id) {
		UserRepository.deleteById(Integer.parseInt(id));
	}

	public Iterable<User> getUsersByUserID(List<Integer> userID) {		
			return UserRepository.findAllById(userID);
		}

	public void editUser(User user) {
		UserRepository.save(user);
	}
	
	public String getUserEmailByUserID(Integer UserID) {
		Optional<User> u= UserRepository.findById(UserID);
		if(u.isPresent()) {
			return u.get().getEmail();
		}
		return null;
	}


}
