package com.google.sayanbanik1997.shop;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
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
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class ShowPaymentsDialog {
    Dialog dialog;
    ShowPaymentsDialog(Context context, Fragment fragment, JSONArray payments){
        dialog= new Dialog(context);
        dialog.setContentView(R.layout.show_payments_dialog);
        dialog.show();
        if(payments != null){
            RecyclerView showPaymentDialogReView = (RecyclerView ) dialog.findViewById(R.id.showPaymentDialogReView);
            showPaymentDialogReView.setLayoutManager(new LinearLayoutManager(context));
            showPaymentDialogReView.setAdapter(new RecyAdapter(R.layout.show_payments_dialog_each_sub_layout, payments.length()) {
                @Override
                void bind(Vh holder, int position) {
                    try{
                        JSONObject eachPaymentJObj = payments.getJSONObject(position);
                        ((TextView)holder.arrView.get(0)).setText(eachPaymentJObj.getString("id"));
                        ((TextView)holder.arrView.get(1)).setText(eachPaymentJObj.getString("dateOfPayment"));
                        ((EditText)holder.arrView.get(2)).setText(eachPaymentJObj.getString("amount"));
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
        }
        TextView dateOfPaymentTxt=(TextView) dialog.findViewById(R.id.dateOfPaymentTxt);
        EditText amountEt=(EditText) dialog.findViewById(R.id.amountEt);
        ImageView delImg=(ImageView) dialog.findViewById(R.id.delImg);

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
    }
}