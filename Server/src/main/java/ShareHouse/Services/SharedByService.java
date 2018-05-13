package ShareHouse.Services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ShareHouse.Models.SharedBy;
import ShareHouse.Repository.SharedByRepository;


@Service
public class SharedByService {
	
	@Autowired
	private SharedByRepository SharedByRepository;
	
	public List<Integer> findHouseIDByUserID(Integer UserID){
		
		List<SharedBy> shared = SharedByRepository.findByUserID(UserID);
		List<Integer> housesId = new ArrayList<Integer>();
		
		for(SharedBy x:shared){
			housesId.add(x.getHouseID());
		}
		return housesId;
	}
	
	public void add(Integer userid,Integer houseid, int admin) {
		
		SharedBy s = new SharedBy(null,userid,houseid,admin);
		SharedByRepository.save(s);
	}

	public int isAdmin(Integer UserID,Integer HouseID) {
		return SharedByRepository.isAdmin(UserID,HouseID);
	}

	public List<SharedBy> findAllByHouseID(int HouseID) {
		
		return SharedByRepository.findAllByHouseID(HouseID);
	}

	public void delete(Integer userID, int houseID) {
		SharedByRepository.deleteByUserIDHouseID(userID,houseID);
	}

	public List<Integer> findHouseIDByUserIDandAdmin(int UserID) {
		
		List<SharedBy> shared = SharedByRepository.findByUserID(UserID);
		List<Integer> housesId = new ArrayList<Integer>();
		
		for(SharedBy x:shared){
			if(x.getIsAdmin()==1)
			housesId.add(x.getHouseID());
		}
		return housesId;
	}

	public List<Integer> findUserIDByHouseID(int houseID) {

		List<SharedBy> shared = SharedByRepository.findByHouseID(houseID);
		List<Integer> userID = new ArrayList<Integer>();
		
		for(SharedBy x:shared){
			userID.add(x.getUserID());
		}
		return userID;
	}


}
