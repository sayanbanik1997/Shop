package com.google.sayanbanik1997.shop;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class supCusFrag extends Fragment {
    String subUrl;
    supCusFrag(String subUrl){
        this.subUrl=subUrl;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_sup_cus, container, false);
        EditText supCusEt= (EditText) view.findViewById(R.id.supCusEt);
        RecyclerView supCusReView=(RecyclerView) view.findViewById(R.id.supCusReView);
        TextView textView=new TextView(getContext());
        String[] data={supCusEt.getText().toString()};
        setData(supCusReView, data);
        supCusEt.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}@Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String[] data={supCusEt.getText().toString()};
                setData(supCusReView, data);
            }
        });
        return view;
    }
    public void setData(RecyclerView supCusReView, String[] data){
        String [] tag={"name"};
        VolleyTakeData volleyTakeDat=new VolleyTakeData(getContext(), Info.baseUrl + "get" + subUrl + ".php", tag, data, new AfterTakingData() {
            @Override
            public void doAfterTakingData(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    RecyAdapter recyAdapter=new RecyAdapter(R.layout.cus_sup_id_name_due_sub_layout, jsonArray.length()) {
                        @Override
                        void bind(Vh holder, int position) {
                            String id = "", name="", due="";
                            try {
                                JSONObject jsonObject = (JSONObject) jsonArray.get(position);
                                id = jsonObject.getString("id");
                                name = jsonObject.getString("name");
                                due=jsonObject.getString("due");
                            } catch (JSONException e) {
                                Toast.makeText(getContext(), "Error while json obj", Toast.LENGTH_SHORT).show();
                            }
                            ((TextView) holder.arrView.get(0)).setText(id);
                            ((TextView) holder.arrView.get(1)).setText(name);
                            ((TextView) holder.arrView.get(2)).setText(due);
                            String idd=id, namee=name;
                            ((Button) holder.arrView.get(3)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String supOrCus="1";
                                    if(subUrl.equals("customer")) supOrCus="2";
                                    PayDialog payDialog=new PayDialog(getContext(), idd, namee, ((TextView)holder.arrView.get(2)).getText().toString(), supOrCus){
                                        @Override
                                        void submitBtnClicked(String due) {
                                            ((TextView)holder.arrView.get(2)).setText(due);
                                        }
                                    };
                                }
                            });
                        }
                        @Override
                        Vh onCreate(View view) {
                            return new Vh(view) {
                                @Override
                                void initiateInsideViewHolder(View itemView) {
                                    arrView.add(itemView.findViewById(R.id.id));
                                    arrView.add(itemView.findViewById(R.id.name));
                                    arrView.add(itemView.findViewById(R.id.dueTxt));
                                    arrView.add(itemView.findViewById(R.id.payBtn));
                                }
                            };
                        }
                    };
                    supCusReView.setLayoutManager(new LinearLayoutManager(getContext()));
                    supCusReView.setAdapter(recyAdapter);
                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Error while json array", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}