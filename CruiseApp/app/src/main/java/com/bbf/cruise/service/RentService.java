package com.bbf.cruise.service;

import java.time.LocalDateTime;
import java.util.Date;

import model.Rent;

public class RentService {

    public Rent createRent(String carRegNumber, String userId) {
        Rent r = new Rent();
        r.setDate_created(new Date());
        r.setCar_reg_number(carRegNumber);
        r.setUser_id(userId);
        return r;
    }
}
