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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BuyFrag extends Fragment {
    View view;
    TextView txt;
    ListView supplierList;
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
                supplierList=(ListView)addSupDialog.findViewById(R.id.supList);

                supNameEt.addTextChangedListener(new TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {} @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
                     @Override
                    public void afterTextChanged(Editable s) {
                        getData("name", supNameEt.getText().toString());
                    }
                });
                getData("name", "");
            }
        });
        return view;
    }
    String s[]={"a", "b"};
    private void getData(String tag, String data){
        RequestQueue requestQueue= Volley.newRequestQueue(getContext());
        StringRequest stringRequest=new StringRequest(
            Request.Method.POST,
            "http://192.168.111.212/me/getSuplier.php",
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                    ArrayList<String> rslt=new ArrayList<>();
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject explrObject;

                        for (int i = 0; i < jsonArray.length(); i++) {
                            explrObject = jsonArray.getJSONObject(i);
                            rslt.add(explrObject.getString("name"));
                        }
                    }catch (Exception e){
                        Toast.makeText(getContext(), "error while parsing json", Toast.LENGTH_SHORT).show();
                    }
                    ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, rslt);
                    supplierList.setAdapter(arrayAdapter);
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