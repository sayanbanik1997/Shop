package com.google.sayanbanik1997.shop;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class billPaymentFrag extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bill_payment, container, false);
        RecyclerView recyclerView=(RecyclerView) view.findViewById(R.id.billPaymentReView);
        String[] tag={};
        new VolleyTakeData(getContext(), Info.baseUrl + "getBillsAndPayments.php", tag, tag, new AfterTakingData() {
            @Override
            public void doAfterTakingData(String response){
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    RecyAdapter recyAdapter  = new RecyAdapter( R.layout.bill_payment_each, jsonArray.length()){
                        @Override
                        void bind(Vh holder, int position) {
                            try {
                                JSONObject jsonObject = (JSONObject) jsonArray.get(position);
                                String billId = "", paymentId="", cusSupNameTxt="",totalAmountTxt="", paidTxt="", dueTxt="";
                                try {
                                    billId = jsonObject.getString("billId");
                                    paymentId = jsonObject.getString("paymentId");
                                    cusSupNameTxt = jsonObject.getString("cusSupName");
                                    totalAmountTxt = jsonObject.getString("totalAmount");
                                    paidTxt = jsonObject.getString("amount");
                                    dueTxt = jsonObject.getString("due");
                                } catch (Exception e) {}
                                ((TextView) holder.arrView.get(0)).setText(billId.toString());
                                ((TextView) holder.arrView.get(1)).setText(paymentId.toString());
                                ((TextView) holder.arrView.get(2)).setText(cusSupNameTxt.toString());
                                ((TextView) holder.arrView.get(3)).setText(totalAmountTxt.toString());
                                ((TextView) holder.arrView.get(4)).setText(paidTxt.toString());
                                ((TextView) holder.arrView.get(5)).setText(dueTxt.toString());


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