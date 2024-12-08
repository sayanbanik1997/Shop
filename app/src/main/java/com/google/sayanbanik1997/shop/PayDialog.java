package com.google.sayanbanik1997.shop;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public abstract class PayDialog {
    PayDialog(Context context, String id, String name, String due, String supOrCus){
        Dialog dialog=new Dialog(context);
        dialog.setContentView(R.layout.pay_dilog);
        dialog.show();
        TextView cusSupIdTxt = (TextView) dialog.findViewById(R.id.cusSupIdTxt);
        TextView cusSupNameTxt = (TextView) dialog.findViewById(R.id.cusSupNameTxt);
        EditText billIdEt=(EditText) dialog.findViewById(R.id.billIdEt);
        TextView dueTxt = (TextView) dialog.findViewById(R.id.dueTxt);
        EditText amountEt=(EditText) dialog.findViewById(R.id.amountEt);
        TextView dueAfterPaymentTxt=(TextView) dialog.findViewById(R.id.dueAfterPaymentTxt);
        TextView paymentDateTxt = (TextView ) dialog.findViewById(R.id.paymentDateTxt);

        cusSupIdTxt.setText(id);
        cusSupNameTxt.setText(name);
        dueTxt.setText(due);
        billIdEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) return;
                if(((EditText)v).getText().toString().isEmpty()) {
                    dueTxt.setText(due); return;
                }
                String[] tag={"billId"}, data = {billIdEt.getText().toString()};
                new VolleyTakeData(context, Info.baseUrl + "getPayBillAgainstBillId.php", tag, data, new AfterTakingData() {
                    @Override
                    public void doAfterTakingData(String response) {
                        try{
                            Integer.parseInt(response);
                            Toast.makeText(context, "Bill id not found", Toast.LENGTH_SHORT).show();
                            return;
                        } catch (Exception e) {}
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            //Log.d("kkkk", cusSupIdTxt);
                            if(!(jsonObject.getString("id").equals(cusSupIdTxt.getText().toString()) && jsonObject.getString("purOrSell").equals(supOrCus))){
                                if(supOrCus.equals("1")){
                                    Toast.makeText(context, "Bill is not of this supplier", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(context, "Bill is not of this customer", Toast.LENGTH_SHORT).show();
                                }
                                billIdEt.setText("");
                                dueTxt.setText(due);
//                                dueAfterPaymentTxt.setText(Double.toString(Double.parseDouble(amountEt.getText().toString()) -
//                                        Double.parseDouble(dueTxt.getText().toString())));
                                return;
                            }
                            dueTxt.setText(jsonObject.getString("due"));
//                            dueAfterPaymentTxt.setText(Double.toString(Double.parseDouble(amountEt.getText().toString()) -
//                                Double.parseDouble(dueTxt.getText().toString())));
                        }catch (Exception e){
                            Toast.makeText(context, "Json obj error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        dueTxt.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}@Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    dueAfterPaymentTxt.setText(Double.toString(Double.parseDouble(dueTxt.getText().toString()) -
                                    Double.parseDouble(amountEt.getText().toString())
                            ));
                } catch (Exception e) {
                    dueAfterPaymentTxt.setText("");
                }
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
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //HH:mm:ss
        Date date = new Date();
        LocalDate currentDate = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            currentDate = LocalDate.parse(dateFormat.format(date));
            final int day = currentDate.getDayOfMonth();
            final int month = currentDate.getMonthValue();
            final int year = currentDate.getYear();
            paymentDateTxt.setText(year+"-"+month+"-"+day);
            paymentDateTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            paymentDateTxt.setText(Integer.toString(year) + "-" +
                                    Integer.toString(month+1) + "-" + Integer.toString(dayOfMonth));
                        }
                    }, year, month -1, day).show();
                }
            });
        }

        ((Button)dialog.findViewById(R.id.submitBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                submitBtnClicked(dueAfterPaymentTxt.getText().toString());
            }
        });
    }
    abstract void submitBtnClicked(String due);
}
