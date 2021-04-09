package com.example.stu;

import androidx.lifecycle.ViewModel;

import com.example.stu.model.HolidayService;

public class BaseViewModel extends ViewModel {

    private HolidayService holidayService;

    public BaseViewModel(HolidayService holidayService) {
        this.holidayService = holidayService;
    }

    protected final HolidayService getHolidayService() {
        return holidayService;
    }

}
