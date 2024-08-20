package in.co.appinventor.services_api.app_util;

import android.graphics.Color;

import java.util.Random;

/* renamed from: in.co.appinventor.services_api.app_util.AppinventorColor */
public class AppinventorColor {
    private static final AppinventorColor ourInstance = new AppinventorColor();
    public String[] mColors = {"#39add1", "#3079ab", "#c25975", "#e15258", "#f9845b", "#838cc7", "#7d669e", "#53bbb4", "#51b46d", "#e0ab18", "#637a91", "#f092b0", "#8b9ba7"};

    public static AppinventorColor getInstance() {
        return ourInstance;
    }

    public int getColor() {
        return Color.parseColor(this.mColors[new Random().nextInt(this.mColors.length)]);
    }

    public int getPredefineRandomColor() {
        return Color.parseColor(this.mColors[new Random().nextInt(this.mColors.length)]);
    }
}
