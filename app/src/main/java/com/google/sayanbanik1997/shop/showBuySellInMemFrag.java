package com.google.sayanbanik1997.shop;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class showBuySellInMemFrag extends Fragment {
    RecyclerView showBuySellInMemRecyView;
    DataToSent dataToSent;
    showBuySellInMemFrag(DataToSent dataToSent){
        this.dataToSent=dataToSent;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view   = inflater.inflate(R.layout.fragment_show_buy_sell_in_mem, container, false);
        showBuySellInMemRecyView = view.findViewById(R.id.showBuySellInMemRecyView);
        if(dataToSent.dataToSentBuySellFragArrayList.size()==0){
            Toast.makeText(getContext(), "Nothing to show", Toast.LENGTH_SHORT).show();
        }else {
            addBuySellInRecyAdapter();
        }
        return view;
    }
    private void addBuySellInRecyAdapter(){
        RecyAdapter recyAdapter = new RecyAdapter(R.layout.each_buysell_img_in_mem_sub_layout, dataToSent.dataToSentBuySellFragArrayList.size()) {
            @Override
            void bind(Vh holder, int position) {
                ((LinearLayout)holder.arrView.get(0)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(getContext(), Integer.toString(position), Toast.LENGTH_SHORT).show();
                        Log.d("kkkk", Integer.toString(position));
                        getParentFragmentManager().beginTransaction().replace(R.id.frameLayout,
                                dataToSent.dataToSentBuySellFragArrayList.get(position).buyOrSellFrag).commit();
                    }
                });
                ((ImageView) holder.arrView.get(1)).setImageBitmap(
                        dataToSent.dataToSentBuySellFragArrayList.get(position).bitmap
                );
                ((ImageView) holder.arrView.get(2)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(getContext(), "deleted", Toast.LENGTH_SHORT).show();
                        dataToSent.dataToSentBuySellFragArrayList.remove(
                                dataToSent.dataToSentBuySellFragArrayList.get(position)
                        );
                        addBuySellInRecyAdapter();
                    }
                });
            }

            @Override
            Vh onCreate(View view) {
                return new Vh(view) {
                    @Override
                    void initiateInsideViewHolder(View itemView) {
                        arrView.add(itemView.findViewById(R.id.eachBuySellLl));
                        arrView.add(itemView.findViewById(R.id.imgView));
                        arrView.add(itemView.findViewById(R.id.delImg));
                    }
                };
            }
        };
        showBuySellInMemRecyView.setLayoutManager(new LinearLayoutManager(getContext()));
        showBuySellInMemRecyView.setAdapter(recyAdapter);
    }
}