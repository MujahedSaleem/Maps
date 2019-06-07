package com.example.MapDetector.ui.main;


import com.example.MapDetector.state;

import java.util.Comparator;

public class roads implements Comparator<roads> {
    public Integer roadNumber;
    public state roadState;

    public roads() {
    }

    public roads(Integer roadNumber, state roadState) {
        this.roadNumber = roadNumber;
        this.roadState = roadState;
    }

    @Override
    public int compare(roads o1, roads o2) {
        if(o1.roadState.distance==o2.roadState.distance){
            return 0;
        }else if(o1.roadState.distance>o2.roadState.distance){
            return 1;
        }
        else
            return -1;
    }
}
