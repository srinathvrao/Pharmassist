package com.example.pharmassist;

import java.io.Serializable;

public class Tablet implements Serializable {

    public String name, dateregd;
    public int count, noofdays, nopoints,perday,m,l,n;

    Tablet() {
        m=0;
        l=0;
        n=0;
        name="0";
        dateregd = "0";
        count = 0;
        noofdays = 0;
        nopoints = 0;
        perday = 0;

    }


}
