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


public class billPaymentFrag extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bill_payment, container, false);
        String[] arr={"12", "33", "55"};

        RecyAdapter recyAdapter  = new RecyAdapter( R.layout.bill_payment_each, arr.length){
            @Override
            void bind(Vh holder, int position) {
                ((TextView)holder.arrView.get(0)).setText(arr[position]);
            }
            @Override
            Vh onCreate(View view) {
                return new Vh(view) {
                    @Override
                    void initiateInsideViewHolder(View itemView) {
                        arrView.add(itemView.findViewById(R.id.demoTxt));
                    }
                };
            }
        };

        RecyclerView billPaymentReView=(RecyclerView) view.findViewById(R.id.billPaymentReView);
        billPaymentReView.setLayoutManager(new LinearLayoutManager(getContext()));
        billPaymentReView.setAdapter(recyAdapter);
        return view;
    }
}