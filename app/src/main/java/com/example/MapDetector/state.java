package com.example.MapDetector;

import java.util.List;

public class state {
    public Integer distance;
    public List<String> barrierName;

    public state(Integer distance, List<String> barrierName) {
        this.distance = distance;
        this.barrierName = barrierName;
    }

    public state() {
    }
}