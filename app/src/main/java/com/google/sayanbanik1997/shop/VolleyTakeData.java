package com.google.sayanbanik1997.shop;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

abstract class AfterTakingData{
    abstract public void doAfterTakingData(String response);
}
public class VolleyTakeData {
    protected  ArrayList<String> rslt;
    VolleyTakeData(Context c,String url, String[] tag, String[] data, AfterTakingData afterTakingData){
        RequestQueue requestQueue= Volley.newRequestQueue(c);
        StringRequest stringRequest=new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        afterTakingData.doAfterTakingData(response);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(c, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        ){
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                for(int i=0;i<tag.length; i++) {
                    hashMap.put(tag[i], data[i]);
                }
                return hashMap;
            }
        };
        requestQueue.add(stringRequest);
    }
}