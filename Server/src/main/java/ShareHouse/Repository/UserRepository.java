package ShareHouse.Repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import ShareHouse.Models.User;


public interface UserRepository extends CrudRepository<User,Integer>{
	
	public Optional<User> getUserByUserName(String UserName);
	public Optional<User> getUserByEmail(String Email);
}
