package com.google.sayanbanik1997.shop;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public class BuyFrag extends Fragment {
    View view;
    TextView txt;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_buy, container, false);
        txt  = (TextView)view.findViewById(R.id.supNameTxt);
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), "clicked", Toast.LENGTH_SHORT).show();
                Dialog addSupDialog=new Dialog(getContext());
                addSupDialog.setContentView(R.layout.add_supplier);
                addSupDialog.show();
                EditText supNameEt=(EditText) addSupDialog.findViewById(R.id.supNameEt);
                Button addSupBtn=(Button) addSupDialog.findViewById(R.id.addSupBtn);
                supNameEt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        getData("name", supNameEt.getText().toString());
                    }
                });
            }
        });
        return view;
    }
    private void getData(String tag, String data){
        RequestQueue requestQueue= Volley.newRequestQueue(getContext());
        StringRequest stringRequest=new StringRequest(
            Request.Method.POST,
            "http://192.168.111.212/me/getSuplier.php",
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                }
            },
            new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                }
            }
        ){
             protected Map<String, String> getParams() throws AuthFailureError{
                 HashMap<String, String> hashMap= new HashMap<>();
                 hashMap.put(tag, data);
                 return hashMap;
             }
        };
        requestQueue.add(stringRequest);
    }
}