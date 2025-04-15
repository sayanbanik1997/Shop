package com.google.sayanbanik1997.shop;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;


public class paymentsFrag extends Fragment {
    DataToSent dataToSent;
    MainActivity mainActivity;
    paymentsFrag(DataToSent dataToSent, MainActivity mainActivity){
        this.dataToSent=dataToSent;
        this.mainActivity=mainActivity;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payments, container, false);
        RecyclerView recyclerView=(RecyclerView) view.findViewById(R.id.billPaymentReView);
        String[] tag={};

        new VolleyTakeData(getContext(), Info.baseUrl + "getPayments.php", tag, tag, new AfterTakingData() {
            @Override
            public void doAfterTakingData(String response){
                //Log.d("kkkk", response);
                try {
                    //Toast.makeText(mainActivity, response, Toast.LENGTH_SHORT).show();
                    JSONArray jsonArray = new JSONArray(response);
                    RecyAdapter recyAdapter  = new RecyAdapter( R.layout.payments_each_sub_layout, jsonArray.length()){
                        @Override
                        void bind(Vh holder, int position) {
                            try {
                                JSONObject jsonObject = (JSONObject) jsonArray.get(position);
                                String billId = "", paymentId="", cusSupNameTxt="",haveToPayTxt="",
                                        paidTxt="", dueTxt="", dateOfPayment="", dateTimeOfPaymentEntry="",
                                        totalHaveToPayToThisCusOrSup="";
                                try {
                                    billId = jsonObject.getString("billId");
                                    paymentId = jsonObject.getString("paymentId");
                                    cusSupNameTxt = jsonObject.getString("cusSupName");
                                    haveToPayTxt = jsonObject.getString("haveToPay");
                                    paidTxt = jsonObject.getString("amount");
                                    dueTxt = Double.toString(Double.parseDouble(jsonObject.getString("totalHaveToPayToThisCusOrSup"))
                                            - Double.parseDouble(jsonObject.getString("totalPaidToThisCusOrSup")));
                                    dateOfPayment = jsonObject.getString("dateOfPayment");
                                    dateTimeOfPaymentEntry = jsonObject.getString("dateTimeOfPaymentEntry");
                                    totalHaveToPayToThisCusOrSup = jsonObject.getString("totalHaveToPayToThisCusOrSup");
                                } catch (Exception e) {
                                    Toast.makeText(getContext(), "error json", Toast.LENGTH_SHORT).show();
                                }
                                ((TextView) holder.arrView.get(0)).setText(billId.toString());
                                ((TextView) holder.arrView.get(1)).setText(paymentId.toString());
                                ((TextView) holder.arrView.get(2)).setText(cusSupNameTxt.toString());
                                ((TextView) holder.arrView.get(3)).setText(haveToPayTxt.toString());
                                ((TextView) holder.arrView.get(4)).setText(paidTxt.toString());
                                ((TextView) holder.arrView.get(5)).setText(dueTxt.toString());

                                ((TextView) holder.arrView.get(7)).setText(dateOfPayment.toString());
                                ((TextView) holder.arrView.get(8)).setText(dateTimeOfPaymentEntry.toString());

                                ((TextView) holder.arrView.get(9)).setText(totalHaveToPayToThisCusOrSup.toString());

                                String billIdd=billId;
                                ((LinearLayout) holder.arrView.get(6)).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String []tag={"billId"}, data={((TextView) holder.arrView.get(0)).getText().toString()};
                                        if(!billIdd.equals("0")) {
                                            try {
                                                if (jsonObject.getString("purOrSell").equals("1")) {
                                                    getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new BuyOrSellFrag("Supplier", Integer.parseInt(billIdd), dataToSent, mainActivity)).commit();
                                                } else {
                                                    getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new BuyOrSellFrag("Customer", Integer.parseInt(billIdd), dataToSent, mainActivity)).commit();
                                                }
                                            } catch (Exception e) {
                                                Toast.makeText(getContext(), "json error 1", Toast.LENGTH_SHORT).show();
                                            }
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
                                    arrView.add(itemView.findViewById(R.id.billIdTxt)); //0
                                    arrView.add(itemView.findViewById(R.id.paymentIdTxt));
                                    arrView.add(itemView.findViewById(R.id.cusSupNameTxt));

                                    arrView.add(itemView.findViewById(R.id.AmountHaveToPayToThisBillTxt));    //3
                                    arrView.add(itemView.findViewById(R.id.paidTxt));
                                    arrView.add(itemView.findViewById(R.id.dueeTxt));

                                    arrView.add(itemView.findViewById(R.id.eachBillAndPaymentLl));  //6

                                    arrView.add(itemView.findViewById(R.id.paymentDateTxt));
                                    arrView.add(itemView.findViewById(R.id.entryDateTimeTxt));  //8

                                    arrView.add(itemView.findViewById(R.id.totalHaveToPayToThisCusOrSupTxt));
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