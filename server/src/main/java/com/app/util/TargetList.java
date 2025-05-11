package com.app.util;

import java.util.ArrayList;

public class TargetList extends ArrayList<Integer>{
    public TargetList() {
        super();
    }

    @Override
    public String toString() {
        String str = "";
        if(this.isEmpty()) {
            return "-";
        }else{
            str = String.valueOf(this.get(0));
            for(int i= 1; i<this.size(); i++) {
                str = str + " " + String.valueOf(this.get(i));
            }
        }
        
        return str;
    }
}