package com.google.sayanbanik1997.shop;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public abstract class ShowPaymentsDialog {
    Context context;
    Dialog dialog;
    ArrayList<String[]> arrayListOfPaymentsArrView = new ArrayList<>();
    TextView dateOfPaymentTxt;
    EditText amountEt;
    ShowPaymentsDialog(Context context, JSONArray payments, String paidEtStr){
        this.context=context;
        dialog= new Dialog(context);
        dialog.setContentView(R.layout.show_payments_dialog);
        dialog.show();

        dateOfPaymentTxt=(TextView) dialog.findViewById(R.id.dateOfPaymentTxt);
        amountEt=(EditText) dialog.findViewById(R.id.amountEt);
        ImageView delImg=(ImageView) dialog.findViewById(R.id.delImg);

        if(payments != null){
            RecyclerView showPaymentDialogReView = (RecyclerView ) dialog.findViewById(R.id.showPaymentDialogReView);
            showPaymentDialogReView.setLayoutManager(new LinearLayoutManager(context));
            showPaymentDialogReView.setAdapter(new RecyAdapter(R.layout.show_payments_dialog_each_sub_layout, payments.length()) {
                @Override
                void bind(Vh holder, int position) {
                    try{
                        arrayListOfPaymentsArrView.add(new String[3]);
                        JSONObject eachPaymentJObj = payments.getJSONObject(position);
                        ((TextView)holder.arrView.get(0)).setText(eachPaymentJObj.getString("id"));
                        arrayListOfPaymentsArrView.get(position)[0]= eachPaymentJObj.getString("id");
                        ((TextView)holder.arrView.get(1)).setText(eachPaymentJObj.getString("dateOfPayment"));
                        arrayListOfPaymentsArrView.get(position)[1]= eachPaymentJObj.getString("dateOfPayment");
                        ((EditText)holder.arrView.get(2)).setText(eachPaymentJObj.getString("amount"));
                        arrayListOfPaymentsArrView.get(position)[2]= eachPaymentJObj.getString("amount");
                        ((EditText)holder.arrView.get(2)).
                                addTextChangedListener(new TextWatcher() {
                                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {} @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                                    @Override
                                    public void afterTextChanged(Editable s) {
                                        try {
                                            arrayListOfPaymentsArrView.get(position)[2]=Double.toString(Double.parseDouble(((EditText)holder.arrView.get(2)).getText().toString()));
                                        } catch (Exception e) {
                                            arrayListOfPaymentsArrView.get(position)[2]="0";
                                            Toast.makeText(context, "enetered", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
//                                setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                            @Override
//                            public void onFocusChange(View v, boolean hasFocus) {
//                                if(!hasFocus){
//                                    arrayListOfPaymentsArrView.get(position)[2]=((EditText)v).getText().toString();
////                                    Toast.makeText(context, ((EditText)v).getText().toString(), Toast.LENGTH_SHORT).show();
////                                    Toast.makeText(context, arrayListOfPaymentsArrView.get(position)[2], Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
                    } catch (Exception e) {
                        Toast.makeText(context, "json error", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                Vh onCreate(View view) {
                    return new Vh(view) {
                        @Override
                        void initiateInsideViewHolder(View itemView) {
                            arrView.add(itemView.findViewById(R.id.idTxt));
                            arrView.add(itemView.findViewById(R.id.dateOfPaymentTxt));
                            arrView.add(itemView.findViewById(R.id.amountEt));
                            arrView.add(itemView.findViewById(R.id.delImg));
                        }
                    };
                }
            });
            amountEt.setText(Double.toString(getAmountForEt(payments, Double.parseDouble(paidEtStr))));
        }else{
            amountEt.setText(paidEtStr);
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //HH:mm:ss
        Date date = new Date();
        LocalDate currentDate = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            currentDate = LocalDate.parse(dateFormat.format(date));
            final int day = currentDate.getDayOfMonth();
            final int month = currentDate.getMonthValue();
            final int year = currentDate.getYear();
            dateOfPaymentTxt.setText(year+"-"+month+"-"+day);
            dateOfPaymentTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            dateOfPaymentTxt.setText(Integer.toString(year) + "-" +
                                    Integer.toString(month+1) + "-" + Integer.toString(dayOfMonth));
                        }
                    }, year, month - 1, day).show();
                }
            });
        }

        delImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amountEt.setText("");
            }
        });
        dialog.findViewById(R.id.submitBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitBtnClicked();
                dialog.dismiss();
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                onCancled();
            }
        });
    }

    protected double getAmountForEt(JSONArray payments, Double totalPaid){
        for (int i=0; i<payments.length(); i++){
            try {
                JSONObject eachPaymentJObj = payments.getJSONObject(i);
                totalPaid -= Double.parseDouble(eachPaymentJObj.getString("amount"));
            } catch (JSONException e) {
                Toast.makeText(context, "Json error", Toast.LENGTH_SHORT).show();
            }
        }
        return totalPaid;
    }
    abstract void submitBtnClicked();
    abstract void onCancled();
}
