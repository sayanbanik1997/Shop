package com.google.sayanbanik1997.shop;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//im
import java.util.Date;
import java.time.Month;
import java.time.LocalDate;

public class BuyFrag extends Fragment {
    View view;
    TextView txt;
    TextView supNameTxt, byingDtTxt;
    Dialog addSupDialog;
    Button  addProdIntoListBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_buy, container, false);
        txt  = (TextView)view.findViewById(R.id.supNameTxt);
        byingDtTxt=(TextView) view.findViewById(R.id.byingDtTxt);
        supNameTxt=(TextView) view.findViewById(R.id.supNameTxt);

        addProdIntoListBtn=(Button) view.findViewById(R.id.addProdIntoListBtn);

        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), "clicked", Toast.LENGTH_SHORT).show();
                addSupDialog=new Dialog(getContext());
                addSupDialog.setContentView(R.layout.add_supplier);
                addSupDialog.show();

                cusSupProdDialogueFunc(addSupDialog, supNameTxt, "Supplier", "http://192.168.111.212/me/createSup.php");

            }
        });

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //HH:mm:ss
        Date date = new Date();
        //Toast.makeText(getContext(), dateFormat.format(date), Toast.LENGTH_SHORT).show();
        LocalDate currentDate = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            currentDate = LocalDate.parse(dateFormat.format(date));
            final int day = currentDate.getDayOfMonth();
            final int month = currentDate.getMonthValue();
            final int year = currentDate.getYear();
            //Toast.makeText(getContext(), Integer.toString(month), Toast.LENGTH_SHORT).show();
            byingDtTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            byingDtTxt.setText(Integer.toString(dayOfMonth)+"-"+
                                    Integer.toString(month)+"-"+Integer.toString(year));
                        }
                    }, year, month-1, day).show();
                }
            });
        }
        addProdIntoListBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                Dialog buyInfoDialogue=new Dialog(getContext());
                buyInfoDialogue.setContentView(R.layout.prod_list_entry);
                buyInfoDialogue.show();

                LinearLayout[] llArr=new LinearLayout[6];
                llArr[0]=(LinearLayout)buyInfoDialogue.findViewById(R.id.prodCountLinLayoutProdEntry);
                llArr[1]=(LinearLayout)buyInfoDialogue.findViewById(R.id.pricePerProdLinLayoutProdEntry);
                llArr[2]=(LinearLayout)buyInfoDialogue.findViewById(R.id.boxQuanLayoutProdEntry);
                llArr[3]=(LinearLayout)buyInfoDialogue.findViewById(R.id.boxPriceLinLayoutProdEntry);
                llArr[4]=(LinearLayout)buyInfoDialogue.findViewById(R.id.itemPerBoxLinLayoutProdEntry);
                llArr[5]=(LinearLayout)buyInfoDialogue.findViewById(R.id.totalAmountDLinLayoutProdEntry);

//                LinearLayout v1 = (LinearLayout)buyInfoDialogue.findViewById(R.id.prodCountLinLayoutProdEntry);
//                Button b=new Button(getContext());
//                //b.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);b.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
//                b.setText("change");
//                b.setBackgroundColor(R.color.red);
//                b.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(getContext(), "clicked", Toast.LENGTH_SHORT).show();
//                    }
//                });
//                v1.addView(b);

                TextView chooseProdTxt = (TextView) buyInfoDialogue.findViewById(R.id.chooseProdTxt);

                chooseProdTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog chooseProdDialogue= new Dialog(getContext());
                        chooseProdDialogue.setContentView(R.layout.add_supplier);
                        chooseProdDialogue.show();

                        cusSupProdDialogueFunc(chooseProdDialogue, chooseProdTxt, "Product", "http://192.168.111.212/me/createProd.php");
                    }
                });

                EditText[] chooseProdDiEt= new EditText[6];
                //prodCountEt
                chooseProdDiEt[0]=(EditText) buyInfoDialogue.findViewById(R.id.prodCountEt);
                //pricePerProdEt
                chooseProdDiEt[1]=(EditText) buyInfoDialogue.findViewById(R.id.pricePerProdEt);
                //boxQuanEt
                chooseProdDiEt[2]=(EditText) buyInfoDialogue.findViewById(R.id.boxQuanEt);
                //boxPriceEt
                chooseProdDiEt[3]=(EditText) buyInfoDialogue.findViewById(R.id.boxPriceEt);
                //itemPerBoxEt
                chooseProdDiEt[4]=(EditText) buyInfoDialogue.findViewById(R.id.itemPerBoxEt);
                //totalAmountEt
                chooseProdDiEt[5]=(EditText) buyInfoDialogue.findViewById(R.id.totalAmountEt);
                EditText unitEt=(EditText) buyInfoDialogue.findViewById(R.id.unitEt);

                for (int i=0; i<chooseProdDiEt.length; i++){
                    EditText e=chooseProdDiEt[i];
                    chooseProdDiEt[i].addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}@Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                    chooseProdDiEt[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        String getStringToCompare;
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if(v.hasFocus()) {
                                new Thread(){
                                    public void run(){
                                        try {
                                            Thread.sleep(100);
                                            getStringToCompare = ((EditText) v).getText().toString();
                                        } catch (Exception ex) {
                                            throw new RuntimeException(ex);
                                        }
                                    }
                                }.start();
                            }else{
                                if(!getStringToCompare.equals(((EditText)v).getText().toString())) {
                                    prodInfoDialogInfoAutoAdj((EditText) v, chooseProdDiEt, llArr);
                                }
                            }
                        }
                    });
                }
            }
        });
//        View v= getLayoutInflater().inflate(R.layout.buy_list_each, null);
//        LinearLayout frameLayout=(LinearLayout) view.findViewById(R.id.buyFragFrLayout);
//        frameLayout.addView(v);

        return view;
    }

    @SuppressLint("ResourceAsColor")
    private Button createButton(int noOfThisBtn, int linkedNo, EditText[] etArr, LinearLayout[] linArr, Button[] btnArr){
        Button b=new Button(getContext());
        b.setText("change");
        //b.setBackgroundColor(R.color.red);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etArr[noOfThisBtn].setText("");
                linArr[noOfThisBtn].removeView(btnArr[noOfThisBtn]);linArr[linkedNo].removeView(btnArr[linkedNo]);
                for(int i=0;i<etArr.length; i++){
                    etArr[i].setFocusableInTouchMode(true);
                }
            }
        });
        return b;
    }
    protected  void prodInfoDialogInfoAutoAdj(EditText thisEditText, EditText[] editTextArr, LinearLayout[] llArr){
        ArrayList<Integer> relation;
        ArrayList<ArrayList<Integer>>[] relationArrList = new ArrayList[6];
        for(int i=0; i<relationArrList.length; i++){
            relationArrList[i]=new ArrayList<>(2);
        }
        
        //0
        relation = new ArrayList<>(3);
        for(int i=0;i<3; i++) relation.add(-1);
        relation.set(0, 5); relation.set(1, 0); relation.set(2, 1);
        relationArrList[0].add(relation);

        relation = new ArrayList<>(3);
        for(int i=0;i<3; i++) relation.add(-1);
        relation.set(0,0); relation.set(1,2); relation.set(2,4);
        relationArrList[0].add(relation);


        //1
        relation = new ArrayList<>(3);
        for(int i=0;i<3; i++) relation.add(-1);
        relation.set(0,5); relation.set(1,0); relation.set(2,1);
        relationArrList[1].add(relation);

        relation = new ArrayList<>(3);
        for(int i=0;i<3; i++) relation.add(-1);
        relation.set(0,3); relation.set(1,1); relation.set(2,4);
        relationArrList[1].add(relation);


        //2
        relation = new ArrayList<>(3);
        for(int i=0;i<3; i++) relation.add(-1);
        relation.set(0,0); relation.set(1,2); relation.set(2,4);
        relationArrList[2].add(relation);

        relation = new ArrayList<>(3);
        for(int i=0;i<3; i++) relation.add(-1);
        relation.set(0,5); relation.set(1,2); relation.set(2,3);
        relationArrList[2].add(relation);


        //3
        relation = new ArrayList<>(3);
        for(int i=0;i<3; i++) relation.add(-1);
        relation.set(0,3); relation.set(1,1); relation.set(2,4);
        relationArrList[3].add(relation);

        relation = new ArrayList<>(3);
        for(int i=0;i<3; i++) relation.add(-1);
        relation.set(0,5); relation.set(1,2); relation.set(2,3);
        relationArrList[3].add(relation);


        //4
        relation = new ArrayList<>(3);
        for(int i=0;i<3; i++) relation.add(-1);
        relation.set(0,0); relation.set(1,2); relation.set(2,4);
        relationArrList[4].add(relation);

        relation = new ArrayList<>(3);
        for(int i=0;i<3; i++) relation.add(-1);
        relation.set(0,3); relation.set(1,1); relation.set(2,4);
        relationArrList[4].add(relation);


        //5
        relation = new ArrayList<>(3);
        for(int i=0;i<3; i++) relation.add(-1);
        relation.set(0,5); relation.set(1,0); relation.set(2,1);
        relationArrList[5].add(relation);

        relation = new ArrayList<>(3);
        for(int i=0;i<3; i++) relation.add(-1);
        relation.set(0,5); relation.set(1,2); relation.set(2,3);
        relationArrList[5].add(relation);

        int noOfThisEt=0;
        while(thisEditText!=editTextArr[noOfThisEt]){
            noOfThisEt++;
        }

        Button[] clearBtnArr=new Button[editTextArr.length];

        ArrayList<EditText> callThisFuncOnArrL= new ArrayList<>(2);
        for(int i=0; i<relationArrList[noOfThisEt].size(); i++){
            if(!editTextArr[noOfThisEt].getText().toString().isEmpty()){
                //Toast.makeText(getContext(), Integer.toString( relationArrList[noOfThisEt].get(i).indexOf(noOfThisEt)), Toast.LENGTH_SHORT).show();
                if(relationArrList[noOfThisEt].get(i).indexOf(noOfThisEt)==0){
                    if(editTextArr[ relationArrList[noOfThisEt].get(i).get(1)].getText().toString().isEmpty() &&
                            (!editTextArr[ relationArrList[noOfThisEt].get(i).get(2)].getText().toString().isEmpty())){
                        editTextArr[ relationArrList[noOfThisEt].get(i).get(1)].setText(Float.toString(
                                Float.parseFloat( editTextArr[ relationArrList[noOfThisEt].get(i).get(0)].getText().toString())/
                                    Float.parseFloat( editTextArr[ relationArrList[noOfThisEt].get(i).get(2)].getText().toString())
                                )
                        );
                        callThisFuncOnArrL.add(editTextArr[ relationArrList[noOfThisEt].get(i).get(1)]);
                    } else if(editTextArr[ relationArrList[noOfThisEt].get(i).get(2)].getText().toString().isEmpty() &&
                            (!editTextArr[ relationArrList[noOfThisEt].get(i).get(1)].getText().toString().isEmpty())){
                        editTextArr[ relationArrList[noOfThisEt].get(i).get(2)].setText(Float.toString(
                                        Float.parseFloat( editTextArr[ relationArrList[noOfThisEt].get(i).get(0)].getText().toString())/
                                                Float.parseFloat( editTextArr[ relationArrList[noOfThisEt].get(i).get(1)].getText().toString())
                                )
                        );
                        callThisFuncOnArrL.add(editTextArr[ relationArrList[noOfThisEt].get(i).get(2)]);
                    }else if((!editTextArr[ relationArrList[noOfThisEt].get(i).get(2)].getText().toString().isEmpty()) &&
                            (!editTextArr[ relationArrList[noOfThisEt].get(i).get(1)].getText().toString().isEmpty())) {

                        if (Float.parseFloat(editTextArr[relationArrList[noOfThisEt].get(i).get(0)].getText().toString())!=
                                Float.parseFloat(editTextArr[relationArrList[noOfThisEt].get(i).get(1)].getText().toString())*
                                Float.parseFloat(editTextArr[relationArrList[noOfThisEt].get(i).get(2)].getText().toString())){

//                            Log.d("kkkk", "1 "+
//                                    Float.toString( Float.parseFloat(editTextArr[relationArrList[noOfThisEt].get(i).get(0)].getText().toString())) +" "+
//                                    Float.toString(Float.parseFloat(editTextArr[relationArrList[noOfThisEt].get(i).get(1)].getText().toString())*
//                                            Float.parseFloat(editTextArr[relationArrList[noOfThisEt].get(i).get(2)].getText().toString())));

                            for (int j = 0; j < relationArrList[noOfThisEt].get(i).size(); j++) {
                                if (editTextArr[relationArrList[noOfThisEt].get(i).get(j)] != thisEditText) {
                                    //editTextArr[relationArrList[noOfThisEt].get(i).get(j)].setText("");
                                    int k;
                                    for (k = 0; k == j || editTextArr[relationArrList[noOfThisEt].get(i).get(k)] == thisEditText; k++){}
                                    clearBtnArr[relationArrList[noOfThisEt].get(i).get(j)]=createButton(relationArrList[noOfThisEt].get(i).get(j), relationArrList[noOfThisEt].get(i).get(k), editTextArr, llArr, clearBtnArr);
                                    llArr[relationArrList[noOfThisEt].get(i).get(j)].addView(
                                            clearBtnArr[relationArrList[noOfThisEt].get(i).get(j)]
                                    );
                                    for(int l=0;l<editTextArr.length; l++){
                                        editTextArr[l].setFocusable(false);
                                    }
                                }
                            }
                        }
                    }
                }else if(relationArrList[noOfThisEt].get(i).indexOf(noOfThisEt)==1){
                    if(editTextArr[ relationArrList[noOfThisEt].get(i).get(0)].getText().toString().isEmpty() &&
                            (!editTextArr[ relationArrList[noOfThisEt].get(i).get(2)].getText().toString().isEmpty())){
                        editTextArr[ relationArrList[noOfThisEt].get(i).get(0)].setText(Float.toString(
                                        Float.parseFloat( editTextArr[ relationArrList[noOfThisEt].get(i).get(1)].getText().toString())*
                                                Float.parseFloat( editTextArr[ relationArrList[noOfThisEt].get(i).get(2)].getText().toString())
                                )
                        );
                        callThisFuncOnArrL.add(editTextArr[ relationArrList[noOfThisEt].get(i).get(0)]);
                    } else if(editTextArr[ relationArrList[noOfThisEt].get(i).get(2)].getText().toString().isEmpty() &&
                            (!editTextArr[ relationArrList[noOfThisEt].get(i).get(0)].getText().toString().isEmpty())){
                        editTextArr[ relationArrList[noOfThisEt].get(i).get(2)].setText(Float.toString(
                                        Float.parseFloat( editTextArr[ relationArrList[noOfThisEt].get(i).get(0)].getText().toString())/
                                                Float.parseFloat( editTextArr[ relationArrList[noOfThisEt].get(i).get(1)].getText().toString())
                                )
                        );
                        callThisFuncOnArrL.add(editTextArr[ relationArrList[noOfThisEt].get(i).get(2)]);
                    }else if((!editTextArr[ relationArrList[noOfThisEt].get(i).get(2)].getText().toString().isEmpty()) &&
                            (!editTextArr[ relationArrList[noOfThisEt].get(i).get(0)].getText().toString().isEmpty())){

                      if (Float.parseFloat(editTextArr[relationArrList[noOfThisEt].get(i).get(0)].getText().toString())!=
                                Float.parseFloat(editTextArr[relationArrList[noOfThisEt].get(i).get(1)].getText().toString())*
                                        Float.parseFloat(editTextArr[relationArrList[noOfThisEt].get(i).get(2)].getText().toString())) {
//                          Log.d("kkkk", "2 "+
//                                  Float.toString( Float.parseFloat(editTextArr[relationArrList[noOfThisEt].get(i).get(0)].getText().toString())) +" "+
//                                  Float.toString(Float.parseFloat(editTextArr[relationArrList[noOfThisEt].get(i).get(1)].getText().toString())*
//                                          Float.parseFloat(editTextArr[relationArrList[noOfThisEt].get(i).get(2)].getText().toString())));
                            for (int j = 0; j < relationArrList[noOfThisEt].get(i).size(); j++)
                                if (editTextArr[relationArrList[noOfThisEt].get(i).get(j)] != thisEditText) {
                                    //editTextArr[relationArrList[noOfThisEt].get(i).get(j)].setText("");
                                    int k;
                                    for (k = 0; k == j || editTextArr[relationArrList[noOfThisEt].get(i).get(k)] == thisEditText; k++){}
                                    clearBtnArr[relationArrList[noOfThisEt].get(i).get(j)]=createButton(relationArrList[noOfThisEt].get(i).get(j), relationArrList[noOfThisEt].get(i).get(k), editTextArr, llArr, clearBtnArr);
                                    llArr[relationArrList[noOfThisEt].get(i).get(j)].addView(
                                            clearBtnArr[relationArrList[noOfThisEt].get(i).get(j)]
                                    );
                                    for(int l=0;l<editTextArr.length; l++){
                                        editTextArr[l].setFocusable(false);
                                    }
                                }
                        }
                    }
                }
                else{
                    if(editTextArr[ relationArrList[noOfThisEt].get(i).get(0)].getText().toString().isEmpty() &&
                            (!editTextArr[ relationArrList[noOfThisEt].get(i).get(1)].getText().toString().isEmpty())){
                        editTextArr[ relationArrList[noOfThisEt].get(i).get(0)].setText(Float.toString(
                                        Float.parseFloat( editTextArr[ relationArrList[noOfThisEt].get(i).get(1)].getText().toString())*
                                                Float.parseFloat( editTextArr[ relationArrList[noOfThisEt].get(i).get(2)].getText().toString())
                                )
                        );
                        callThisFuncOnArrL.add(editTextArr[ relationArrList[noOfThisEt].get(i).get(0)]);
                    } else if(editTextArr[ relationArrList[noOfThisEt].get(i).get(1)].getText().toString().isEmpty() &&
                            (!editTextArr[ relationArrList[noOfThisEt].get(i).get(0)].getText().toString().isEmpty())){
                        editTextArr[ relationArrList[noOfThisEt].get(i).get(1)].setText(Float.toString(
                                        Float.parseFloat( editTextArr[ relationArrList[noOfThisEt].get(i).get(0)].getText().toString())/
                                                Float.parseFloat( editTextArr[ relationArrList[noOfThisEt].get(i).get(2)].getText().toString())
                                )
                        );
                        callThisFuncOnArrL.add(editTextArr[ relationArrList[noOfThisEt].get(i).get(1)]);
                    }else if((!editTextArr[ relationArrList[noOfThisEt].get(i).get(0)].getText().toString().isEmpty()) &&
                            (!editTextArr[ relationArrList[noOfThisEt].get(i).get(1)].getText().toString().isEmpty())){

                       if (Float.parseFloat(editTextArr[relationArrList[noOfThisEt].get(i).get(0)].getText().toString())!=
                                Float.parseFloat(editTextArr[relationArrList[noOfThisEt].get(i).get(1)].getText().toString())*
                                Float.parseFloat(editTextArr[relationArrList[noOfThisEt].get(i).get(2)].getText().toString())) {
//                           Log.d("kkkk", "3 "+
//                                   Float.toString( Float.parseFloat(editTextArr[relationArrList[noOfThisEt].get(i).get(0)].getText().toString())) +" "+
//                                   Float.toString(Float.parseFloat(editTextArr[relationArrList[noOfThisEt].get(i).get(1)].getText().toString())*
//                                           Float.parseFloat(editTextArr[relationArrList[noOfThisEt].get(i).get(2)].getText().toString())));
                            for (int j = 0; j < relationArrList[noOfThisEt].get(i).size(); j++)
                                if (editTextArr[relationArrList[noOfThisEt].get(i).get(j)] != thisEditText) {
                                    //editTextArr[relationArrList[noOfThisEt].get(i).get(j)].setText("");
                                    int k;
                                    for (k = 0; k == j || editTextArr[relationArrList[noOfThisEt].get(i).get(k)] == thisEditText; k++){}
                                    clearBtnArr[relationArrList[noOfThisEt].get(i).get(j)]=createButton(relationArrList[noOfThisEt].get(i).get(j), relationArrList[noOfThisEt].get(i).get(k), editTextArr, llArr, clearBtnArr);
                                    llArr[relationArrList[noOfThisEt].get(i).get(j)].addView(
                                            clearBtnArr[relationArrList[noOfThisEt].get(i).get(j)]
                                    );
                                    for(int l=0;l<editTextArr.length; l++){
                                        editTextArr[l].setFocusable(false);
                                    }
                                }
                        }
                    }
                }
            }else{

            }
        }
//        String s=noOfThisEt+" ";
//        for(int i=0; i<editTextArr.length; i++){
//            s+=editTextArr[i].getText().toString()+" ";
//        }
        //Log.d("kkkk", s);

        for(int i=0;i< callThisFuncOnArrL.size();i++){
            prodInfoDialogInfoAutoAdj(callThisFuncOnArrL.get(i), editTextArr,  llArr);
        }
    }
    protected void cusSupProdDialogueFunc(Dialog dialog, TextView ultimateTextSetTextview, String subUrl, String fullUrl){
        EditText supNameEt=(EditText) dialog.findViewById(R.id.supNameEt);
        ListView supplierList=(ListView)dialog.findViewById(R.id.supList);
        Button addCrSupBtn=(Button) dialog.findViewById(R.id.addCrSupBtn);
        String[] tag={"name"};
        String[] data={""};
        setData(tag, data, dialog, supNameEt, supplierList, addCrSupBtn, subUrl);

        supNameEt.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {} @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                String[] tag={"name"};
                String[] data={supNameEt.getText().toString()};
                setData(tag, data,  dialog, supNameEt, supplierList, addCrSupBtn, subUrl);
            }
        });
        addCrSupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(supNameEt.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), subUrl+ " name can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(addCrSupBtn.getText().toString().equalsIgnoreCase("Add "+subUrl)){
                    ultimateTextSetTextview.setText(supNameEt.getText().toString());
                    dialog.dismiss();
                    return;
                }
                String[] tag={"name"};
                String[] data={supNameEt.getText().toString()};
                new VolleyTakeData(getContext(), fullUrl, tag, data, new AfterTakingData() {
                    @Override
                    public void doAfterTakingData(String response) {
                        if(response.equals("1")){
                            ultimateTextSetTextview.setText(supNameEt.getText().toString());
                            dialog.dismiss();;
                        }else {
                            Toast.makeText(getContext(),"Error occurred "+ response, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
    protected void setData(String[] tag, String[] data, Dialog addSupDialog, EditText supNameEt, ListView supplierList, Button addCrSupBtn, String subUrl){
        new VolleyTakeData(getContext(),"http://192.168.111.212/me/get"+ subUrl +".php", tag, data, new AfterTakingData() {
            @Override
            public void doAfterTakingData(String response) {
                ArrayList<String> rslt=new ArrayList<>();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject explrObject;
                    if(supNameEt.getText().toString().isEmpty()){
                        addCrSupBtn.setText(subUrl+ " name can't be empty");
                    }else{
                        addCrSupBtn.setText("Create "+ subUrl +" and then add");
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        explrObject = jsonArray.getJSONObject(i);
                        rslt.add(explrObject.getString("name"));
                        if(//   (!addCrSupBtn.getText().equals("Add Supplier")) &&
                                explrObject.getString("name").equals(supNameEt.getText().toString())){
                            addCrSupBtn.setText("Add "+ subUrl);
                        }
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), "error while parsing json", Toast.LENGTH_SHORT).show();
                }
                //ArrayAdapter<String > arrayAdapter= new ArrayAdapter<>(getContext(), an)
                ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, rslt);
                supplierList.setAdapter(arrayAdapter);
                supplierList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        supNameEt.setText(rslt.get(position));
                    }
                });
            }
        });
    }
}