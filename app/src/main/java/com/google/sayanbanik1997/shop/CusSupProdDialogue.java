package com.google.sayanbanik1997.shop;

import static com.google.sayanbanik1997.shop.Info.baseUrl;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

abstract public class CusSupProdDialogue {
    String subUrl;
    Context context;
    Dialog dialog;
    CusSupProdDialogue(Context context, String subUrl){
        this.subUrl=subUrl;
        this.context=context;
        dialog= new Dialog(context);
        dialog.setContentView(R.layout.add_sup_cus_prod_dilog);
        dialog.show();

        EditText supNameEt = (EditText) dialog.findViewById(R.id.supNameEt);
        ListView supplierList = (ListView) dialog.findViewById(R.id.supList);
        Button addCrSupBtn = (Button) dialog.findViewById(R.id.addCrSupBtn);
        String[] tag = {"name"};
        String[] data = {""};
        setData(tag, data, dialog, supNameEt, supplierList, addCrSupBtn, subUrl);

        supNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String[] tag = {"name"};
                String[] data = {supNameEt.getText().toString()};
                setData(tag, data, dialog, supNameEt, supplierList, addCrSupBtn, subUrl);
            }
        });
        addCrSupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (supNameEt.getText().toString().isEmpty()) {
                    Toast.makeText(context, subUrl + " name can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (addCrSupBtn.getText().toString().equalsIgnoreCase("Add " + subUrl)) {
                    doAfterBtnClicked();
                    dialog.dismiss();
                    return;
                }
                String[] tag = {"name"};
                String[] data = {supNameEt.getText().toString()};
                new VolleyTakeData(context, baseUrl+"create"+subUrl+".php", tag, data, new AfterTakingData() {
                    @Override
                    public void doAfterTakingData(String response) {
                        if (response.equals("1")) {
                            doAfterBtnClicked();
                            dialog.dismiss();
                            ;
                        } else {
                            Toast.makeText(context, "Error occurred " + response, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    protected void setData(String[] tag, String[] data, Dialog addSupDialog, EditText supNameEt, ListView supplierList, Button addCrSupBtn, String subUrl) {

        new VolleyTakeData(context, baseUrl + "get" + subUrl + ".php", tag, data, new AfterTakingData() {
            @Override
            public void doAfterTakingData(String response) {
                ArrayList<String> rslt = new ArrayList<>();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject explrObject;
                    if (supNameEt.getText().toString().isEmpty()) {
                        addCrSupBtn.setText(subUrl + " name can't be empty");
                    } else {
                        addCrSupBtn.setText("Create " + subUrl + " and then add");
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        explrObject = jsonArray.getJSONObject(i);
                        rslt.add(explrObject.getString("name"));
                        if (explrObject.getString("name").equals(supNameEt.getText().toString())) {
                            addCrSupBtn.setText("Add " + subUrl);
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "error while parsing json", Toast.LENGTH_SHORT).show();
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, rslt);
                supplierList.setAdapter(arrayAdapter);
                supplierList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        supNameEt.setText(rslt.get(position));
                    }
                });
            }
        });
    }
    abstract void doAfterBtnClicked();
}
