package com.google.sayanbanik1997.shop;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;


public class billAndPaymentFrag extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bill_and_payment, container, false);
        RecyclerView recyclerView=(RecyclerView) view.findViewById(R.id.billPaymentReView);
        String[] tag={};
        new VolleyTakeData(getContext(), Info.baseUrl + "getBillsAndPayments.php", tag, tag, new AfterTakingData() {
            @Override
            public void doAfterTakingData(String response){
                //Log.d("kkkk", response);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    RecyAdapter recyAdapter  = new RecyAdapter( R.layout.bill_and_payment_each_sub_layout, jsonArray.length()){
                        @Override
                        void bind(Vh holder, int position) {
                            try {
                                JSONObject jsonObject = (JSONObject) jsonArray.get(position);
                                String billId = "", paymentId="", cusSupNameTxt="",totalAmountTxt="", paidTxt="", dueTxt="";
                                try {
                                    billId = jsonObject.getString("billId");
                                    paymentId = jsonObject.getString("paymentId");
                                    cusSupNameTxt = jsonObject.getString("cusSupName");
                                    totalAmountTxt = jsonObject.getString("haveToPay");
                                    paidTxt = jsonObject.getString("amount");
                                    dueTxt = jsonObject.getString("due");
                                } catch (Exception e) {
                                    Toast.makeText(getContext(), "error json", Toast.LENGTH_SHORT).show();
                                }
                                ((TextView) holder.arrView.get(0)).setText(billId.toString());
                                ((TextView) holder.arrView.get(1)).setText(paymentId.toString());
                                ((TextView) holder.arrView.get(2)).setText(cusSupNameTxt.toString());
                                ((TextView) holder.arrView.get(3)).setText(totalAmountTxt.toString());
                                ((TextView) holder.arrView.get(4)).setText(paidTxt.toString());
                                ((TextView) holder.arrView.get(5)).setText(dueTxt.toString());

                                ((LinearLayout) holder.arrView.get(6)).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String []tag={"billId"}, data={((TextView) holder.arrView.get(0)).getText().toString()};
                                        try {
                                            if(jsonObject.getString("purOrSell").equals("1")){
                                                getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new BuyOrSellFrag("Supplier")).commit();
                                            }else{
                                                getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new BuyOrSellFrag("Customer")).commit();
                                            }
                                        } catch (Exception e) {
                                            Toast.makeText(getContext(), "json error 1", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }catch (Exception e){
                                Toast.makeText(getContext(), "Error converting json obj", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        Vh onCreate(View view) {
                            return new Vh(view) {
                                @Override
                                void initiateInsideViewHolder(View itemView) {
                                    arrView.add(itemView.findViewById(R.id.billIdTxt));
                                    arrView.add(itemView.findViewById(R.id.paymentIdTxt));
                                    arrView.add(itemView.findViewById(R.id.cusSupNameTxt));

                                    arrView.add(itemView.findViewById(R.id.totalAmountTxt));
                                    arrView.add(itemView.findViewById(R.id.paidTxt));
                                    arrView.add(itemView.findViewById(R.id.dueeTxt));

                                    arrView.add(itemView.findViewById(R.id.eachBillAndPaymentLl));
                                }
                            };
                        }
                    };
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(recyAdapter);
                }catch (Exception e){
                    Toast.makeText(getContext(), "error while converting json array", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
}