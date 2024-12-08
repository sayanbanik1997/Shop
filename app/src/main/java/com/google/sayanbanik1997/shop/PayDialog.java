package com.google.sayanbanik1997.shop;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public abstract class PayDialog {
    PayDialog(Context context, String id, String name, String due){
        Dialog dialog=new Dialog(context);
        dialog.setContentView(R.layout.pay_dilog);
        dialog.show();
        TextView cusSupIdTxt = (TextView) dialog.findViewById(R.id.cusSupIdTxt);
        TextView cusSupNameTxt = (TextView) dialog.findViewById(R.id.cusSupNameTxt);
        EditText billIdEt=(EditText) dialog.findViewById(R.id.billIdEt);
        TextView dueTxt = (TextView) dialog.findViewById(R.id.dueTxt);
        EditText amountEt=(EditText) dialog.findViewById(R.id.amountEt);
        TextView dueAfterPaymentTxt=(TextView) dialog.findViewById(R.id.dueAfterPaymentTxt);

        cusSupIdTxt.setText(id);
        cusSupNameTxt.setText(name);
        dueTxt.setText(due);
        billIdEt.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}@Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        dueTxt.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}@Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        amountEt.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}@Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                try{
                    dueAfterPaymentTxt.setText(
                            Double.toString(Double.parseDouble(dueTxt.getText().toString()) - Double.parseDouble(amountEt.getText().toString()))
                    );
                } catch (Exception e) {
                    dueAfterPaymentTxt.setText("");
                }
            }
        });
        ((Button)dialog.findViewById(R.id.submitBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitBtnClicked(due);
            }
        });
    }
    abstract void submitBtnClicked(String due);
}
