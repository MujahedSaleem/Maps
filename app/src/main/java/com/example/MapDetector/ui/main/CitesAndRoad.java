package com.example.MapDetector.ui.main;

import com.example.MapDetector.ui.main.roads;

import java.util.List;

public class CitesAndRoad {
    public String CityName ;
    public List<roads> numberRoad;

    public CitesAndRoad() {
    }

    public CitesAndRoad(String cityName, List<roads> numberRoad) {
        CityName = cityName;
        this.numberRoad = numberRoad;
    }
}
