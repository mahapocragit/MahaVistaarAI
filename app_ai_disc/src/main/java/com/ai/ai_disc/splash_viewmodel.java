package com.ai.ai_disc;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ai.ai_disc.model.splash_model;

public class splash_viewmodel extends ViewModel {

   public  MutableLiveData<splash_model> liveData;

    public LiveData<splash_model> get() {
        // if (list == null) {
        liveData = new MutableLiveData<splash_model>();
        get_data();

        return liveData;
    }
    public void get_data(){

        splash_model a = new splash_model();
        try {
            Thread.sleep(3000);
            liveData.postValue(a);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
