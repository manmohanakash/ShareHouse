package ShareHouse.Repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


import ShareHouse.Models.SharedBy;


public interface SharedByRepository extends CrudRepository<SharedBy,Integer>{

	public List<SharedBy> findByUserID(Integer UserID);

	public Integer findIsAdminByHouseID(Integer HouseID);

	 @Query("SELECT isAdmin FROM SharedBy WHERE UserID=?1 AND HouseID=?2")
	public Integer isAdmin(Integer UserID,Integer HouseID);

	public List<SharedBy> findAllByHouseID(int houseID);

	@Transactional
	@Modifying
	@Query("DELETE FROM SharedBy WHERE UserID=?1 AND HouseID=?2")
	public void deleteByUserIDHouseID(Integer userID, Integer houseID);

	public List<SharedBy> findByHouseID(int houseID);
	
}
