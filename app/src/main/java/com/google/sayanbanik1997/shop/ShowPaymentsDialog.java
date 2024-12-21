package com.google.sayanbanik1997.shop;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Looper;
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
    ArrayList<String[]> arrayListOfPaymentsArrView = new ArrayList<>(),
            arrayListOfPaymentsArrViewPermanent = new ArrayList<>();
    TextView dateOfPaymentTxt;
    EditText amountEt;
    RecyclerView showPaymentDialogReView;
    ShowPaymentsDialog(Context context, JSONArray payments, String paidEtStr){
        this.context=context;
        dialog= new Dialog(context);
        dialog.setContentView(R.layout.show_payments_dialog);
        dialog.show();

        dateOfPaymentTxt=(TextView) dialog.findViewById(R.id.dateOfPaymentTxt);
        amountEt=(EditText) dialog.findViewById(R.id.amountEt);
        ImageView delImg=(ImageView) dialog.findViewById(R.id.delImg);

        if(payments != null){
            showPaymentDialogReView = (RecyclerView ) dialog.findViewById(R.id.showPaymentDialogReView);
            showPaymentDialogReView.setLayoutManager(new LinearLayoutManager(context));
            showPaymentDialogReView.setAdapter(new RecyAdapter(R.layout.show_payments_dialog_each_sub_layout, payments.length()) {
                @Override
                void bind(Vh holder, int position) {
                    try{
                        arrayListOfPaymentsArrView.add(new String[3]);
                        JSONObject eachPaymentJObj = payments.getJSONObject(position);
                        arrayListOfPaymentsArrView.get(position)[0]= eachPaymentJObj.getString("id");
                        arrayListOfPaymentsArrView.get(position)[1]= eachPaymentJObj.getString("dateOfPayment");
                        arrayListOfPaymentsArrView.get(position)[2]= eachPaymentJObj.getString("amount");
                        bindData(arrayListOfPaymentsArrView.get(position), holder, position);
                        arrayListOfPaymentsArrViewPermanent=arrayListOfPaymentsArrView;
                    } catch (Exception e) {
                        Toast.makeText(context, "json error", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                Vh onCreate(View view) {
                    return new Vh(view) {
                        @Override
                        void initiateInsideViewHolder(View itemView) {
                            initiateViewholder(arrView, itemView);
                        }
                    };
                }
            });
            Thread t = new Thread() {
                public void run() {
                    Looper.prepare();
                    try {
                        this.sleep(100);
                        amountEt.setText(Double.toString(getAmountForEt( Double.parseDouble(paidEtStr))));
                    } catch (Exception ex) {
                        Toast.makeText(context, "error thread", Toast.LENGTH_SHORT).show();
                    }
                }
            };
            t.start();

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

    protected  void bindData(String[] data, Vh holder, int position){
        ((TextView)holder.arrView.get(0)).setText(data[0]);
        ((TextView)holder.arrView.get(1)).setText(data[1]);
        ((EditText)holder.arrView.get(2)).setText(data[2]);
        ((EditText)holder.arrView.get(2)).
                addTextChangedListener(new TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {} @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                    @Override
                    public void afterTextChanged(Editable s) {
                        try {
                            arrayListOfPaymentsArrView.get(position)[2]=Double.toString(Double.parseDouble(((EditText)holder.arrView.get(2)).getText().toString()));
                        } catch (Exception e) {
                            arrayListOfPaymentsArrView.get(position)[2]="0";
                            //Toast.makeText(context, "enetered", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        ((ImageView)holder.arrView.get(3)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayListOfPaymentsArrView.remove(position);//arrayListOfPaymentsArrView.get(position)[1]="";arrayListOfPaymentsArrView.get(position)[2]="";
                showPaymentDialogReView.setAdapter(new RecyAdapter(R.layout.show_payments_dialog_each_sub_layout, arrayListOfPaymentsArrView.size()) {
                    @Override
                    void bind(Vh holder, int position) {
                        bindData(arrayListOfPaymentsArrView.get(position), holder, position);
                    }

                    @Override
                    Vh onCreate(View view) {
                        return new Vh(view) {
                            @Override
                            void initiateInsideViewHolder(View itemView) {
                                initiateViewholder(arrView, itemView);
                            }
                        };
                    }
                });
            }
        });
    }

    protected  void initiateViewholder(ArrayList<View> arrView, View itemView){
        arrView.add(itemView.findViewById(R.id.idTxt));
        arrView.add(itemView.findViewById(R.id.dateOfPaymentTxt));
        arrView.add(itemView.findViewById(R.id.amountEt));
        arrView.add(itemView.findViewById(R.id.delImg));
    }
    protected double getAmountForEt( Double totalPaid){
        for (int i=0; i<arrayListOfPaymentsArrView.size(); i++){
            totalPaid -= Double.parseDouble(arrayListOfPaymentsArrView.get(i)[2]);
            //Toast.makeText(context, Double.toString(totalPaid), Toast.LENGTH_SHORT).show();
//            try {
//                JSONObject eachPaymentJObj = payments.getJSONObject(i);
//                totalPaid -= Double.parseDouble(eachPaymentJObj.getString("amount"));
//            } catch (JSONException e) {
//                Toast.makeText(context, "Json error", Toast.LENGTH_SHORT).show();
//            }
        }
        return totalPaid;
    }
    abstract void submitBtnClicked();
    abstract void onCancled();
}
