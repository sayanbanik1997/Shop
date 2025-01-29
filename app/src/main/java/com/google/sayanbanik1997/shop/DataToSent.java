package com.google.sayanbanik1997.shop;

import android.graphics.Bitmap;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.LinkedList;

public class DataToSent {
    LinkedList<DataToSentBuySellFrag> dataToSentBuySellFragArrayList = new LinkedList<>();
}
abstract class DataToSentBuySellFrag{

    Bitmap bitmap;
    BuyOrSellFrag buyOrSellFrag;
    DataToSentBuySellFrag(){
        createConstructor();
    }
    abstract void createConstructor();
}