package ShareHouse.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ShareHouse.Models.House;
import ShareHouse.Repository.HouseRepository;


@Service
public class HouseService {
	
		@Autowired
		private HouseRepository HouseRepository;
		
		public List<House> getAllHouse(){
			List<House> house = new ArrayList<House>();
			HouseRepository.findAll().forEach(house::add);
			return house;
		}

		public List<House> getAllHouseByHouseID(List<Integer> houses) {
			return (List<House>) HouseRepository.findAllById(houses);
		}

		public House addHouse(House house) {
			return HouseRepository.save(house);
		}

		public Optional<House> findByHouseID(int houseID) {
			return HouseRepository.findById(houseID);
		}

		public void updateHouse(House house) {
			HouseRepository.save(house);
		}

		public void deleteHouse(int houseid) {
			HouseRepository.deleteById(houseid);
		}

		
}
