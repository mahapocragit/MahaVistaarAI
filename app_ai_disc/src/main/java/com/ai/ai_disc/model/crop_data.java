package com.ai.ai_disc.model;

import com.google.gson.annotations.SerializedName;

public class crop_data {

    @SerializedName("crop_id")
    String crop_id;
    @SerializedName("image_number")
    String image_number;
    @SerializedName("crop_name")
    String crop_name;

    public String getCrop_id() {
        return crop_id;
    }

    public void setCrop_id(String crop_id) {
        this.crop_id = crop_id;
    }

    public String getImage_number() {
        return image_number;
    }

    public void setImage_number(String image_number) {
        this.image_number = image_number;
    }

    public String getCrop_name() {
        return crop_name;
    }

    public void setCrop_name(String crop_name) {
        this.crop_name = crop_name;
    }
}
