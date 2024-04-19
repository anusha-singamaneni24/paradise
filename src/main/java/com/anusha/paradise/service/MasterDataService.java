package com.anusha.paradise.service;

import com.anusha.paradise.model.MasterData;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MasterDataService {
    MasterData data;
    public MasterData fetchMasterData() {
        try {
            data = new MasterData();
            data.setLocations(List.of("Dublin 1","Dublin 2","Dublin 3","Dublin 4",
                    "Dublin 5","Dublin 6","Dublin 7","Dublin 8","Dublin 9","Dublin 10",
                    "Dublin 11","Dublin 12","Dublin 13","Dublin 14","Dublin 15","Dublin 16",
                    "Dublin 17","Dublin 18","Dublin 19","Dublin 20","Dublin 21","Dublin 22"));
            data.setAmenities(List.of("Washing Machine","Gated Security","CCTV","Heater",
                    "Lawn","Gym","Library","Bar","Parking"));
            data.setEnergyRatings(List.of("A1","A2","A3","B1","B2","B3","C1","C2",
                    "C3","D1","D2","E1","E2","F","G"));
            data.setPropertyType(List.of("Studio","Apartment","Bungalow","House", "Flat", "Penthouse"));
        } catch (Exception ex) {
            System.out.println(ex.getLocalizedMessage());
        }
        return data;
    }
}
