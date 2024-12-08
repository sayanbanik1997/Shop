package com.google.sayanbanik1997.shop;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.text.DecimalFormat;
import java.util.ArrayList;

abstract public class BuyInfoDialog {

    static DecimalFormat decimalFormat = new DecimalFormat("#.0000000");
    static DecimalFormat decimalFormatToComp = new DecimalFormat("#.00");
    Context context;
    View view;
    Fragment fragment;
    //LayoutInflater inflater;ViewGroup container;Bundle savedInstanceStat;
    EditText[] chooseProdDiEtArr;EditText unitEt;
    TextView chooseProdTxt;
    Dialog buyInfoDialogue;
    BuyInfoDialog(Context context, View view, Fragment fragment) {
        this.context=context;
        this.view=view;
        this.fragment=fragment;
        chooseProdDiEtArr = new EditText[6];

        buyInfoDialogue = new Dialog(context);
        buyInfoDialogue.setContentView(R.layout.prod_list_entry_dilog);
        buyInfoDialogue.show();

        LinearLayout[] llArr = new LinearLayout[6];
        llArr[0] = (LinearLayout) buyInfoDialogue.findViewById(R.id.prodCountLinLayoutProdEntry);
        llArr[1] = (LinearLayout) buyInfoDialogue.findViewById(R.id.pricePerProdLinLayoutProdEntry);
        llArr[2] = (LinearLayout) buyInfoDialogue.findViewById(R.id.boxQuanLayoutProdEntry);
        llArr[3] = (LinearLayout) buyInfoDialogue.findViewById(R.id.boxPriceLinLayoutProdEntry);
        llArr[4] = (LinearLayout) buyInfoDialogue.findViewById(R.id.itemPerBoxLinLayoutProdEntry);
        llArr[5] = (LinearLayout) buyInfoDialogue.findViewById(R.id.totalAmountDLinLayoutProdEntry);

        chooseProdTxt = (TextView) buyInfoDialogue.findViewById(R.id.chooseProdTxt);

        chooseProdTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Dialog chooseProdDialogue = new Dialog(context);
//                chooseProdDialogue.setContentView(R.layout.add_supplier);
//                chooseProdDialogue.show();
//                BuyFrag.cusSupProdDialogueFunc(chooseProdDialogue, chooseProdTxt, "Product");
                new CusSupProdDialogue(context, "Product") {
                    @Override
                    void doAfterBtnClicked() {
                        chooseProdTxt.setText(((EditText) dialog.findViewById(R.id.supNameEt)).getText().toString());
                    }
                };
            }
        });

        //prodCountEt
        chooseProdDiEtArr[0] = (EditText) buyInfoDialogue.findViewById(R.id.prodCountEt);
        //pricePerProdEt
        chooseProdDiEtArr[1] = (EditText) buyInfoDialogue.findViewById(R.id.pricePerProdEt);
        //boxQuanEt
        chooseProdDiEtArr[2] = (EditText) buyInfoDialogue.findViewById(R.id.boxQuanEt);
        //boxPriceEt
        chooseProdDiEtArr[3] = (EditText) buyInfoDialogue.findViewById(R.id.boxPriceEt);
        //itemPerBoxEt
        chooseProdDiEtArr[4] = (EditText) buyInfoDialogue.findViewById(R.id.itemPerBoxEt);
        //totalAmountEt
        chooseProdDiEtArr[5] = (EditText) buyInfoDialogue.findViewById(R.id.totalAmountEt);
        unitEt = (EditText) buyInfoDialogue.findViewById(R.id.unitEt);

        Button[] clearBtnArr = new Button[chooseProdDiEtArr.length];

        for (int i = 0; i < chooseProdDiEtArr.length; i++) {
            EditText e = chooseProdDiEtArr[i];
            chooseProdDiEtArr[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            chooseProdDiEtArr[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                String getStringToCompare;
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (v.hasFocus()) {
                        new Thread() {
                            public void run() {
                                try {
                                    Thread.sleep(10);
                                    getStringToCompare = ((EditText) v).getText().toString();
                                } catch (Exception ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                        }.start();
                    } else {
                        if (!((EditText) v).isFocusableInTouchMode()) {
                            ((EditText) v).setText(getStringToCompare);
                            return;
                        }
                        try{
                            Double.parseDouble(((EditText) v).getText().toString());
                        }catch (Exception e){
                            ((EditText)v).setText(getStringToCompare);
                        }
                        if (!((EditText) v).getText().toString().isEmpty()) {
                            if (getStringToCompare.isEmpty()) {
                                prodInfoDialogInfoAutoAdj((EditText) v, chooseProdDiEtArr, llArr, clearBtnArr);
                            } else {
                                if (!BuyFrag.compDeci(Double.parseDouble(getStringToCompare), Double.parseDouble(((EditText) v).getText().toString()))) {
                                    prodInfoDialogInfoAutoAdj((EditText) v, chooseProdDiEtArr, llArr, clearBtnArr);
                                }
                            }
                        }
                    }
                }
            });
        }

        buyInfoDialogue.findViewById(R.id.submitBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v ) {
                if(((TextView) buyInfoDialogue.findViewById(R.id.chooseProdTxt)).getText().equals(fragment.getResources().getString(R.string.buyInfoDialogueProdName))){
                    Toast.makeText(context, "Select product first", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(((EditText) buyInfoDialogue.findViewById(R.id.prodCountEt)).getText().toString().isEmpty() ||
                        ((EditText) buyInfoDialogue.findViewById(R.id.pricePerProdEt)).getText().toString().isEmpty() ||
                        ((EditText) buyInfoDialogue.findViewById(R.id.totalAmountEt)).getText().toString().isEmpty() ||
                        ((EditText) buyInfoDialogue.findViewById(R.id.unitEt)).getText().toString().isEmpty()
                ){
                    Toast.makeText(context, "Product count, Product price, Total amount, unit need to be set", Toast.LENGTH_SHORT).show();
                    return;
                }
                unitEt.requestFocus();

                for (int i = 0; i < clearBtnArr.length; i++) {
                    if (clearBtnArr[i] != null) {
                        Toast.makeText(context, "Numbers are incorrect", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                subBtnClicked();
            }
        });
    }
    abstract void subBtnClicked();
    protected boolean prodInfoDialogInfoAutoAdj(EditText thisEditText, EditText[] editTextArr, LinearLayout[] llArr, Button[] clearBtnArr) {
        ArrayList<Integer> relation;
        ArrayList<ArrayList<Integer>>[] relationArrList = new ArrayList[6];
        for (int i = 0; i < relationArrList.length; i++) {
            relationArrList[i] = new ArrayList<>(2);
        }

        //0
        relation = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) relation.add(-1);
        relation.set(0, 5);
        relation.set(1, 0);
        relation.set(2, 1);
        relationArrList[0].add(relation);

        relation = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) relation.add(-1);
        relation.set(0, 0);
        relation.set(1, 2);
        relation.set(2, 4);
        relationArrList[0].add(relation);


        //1
        relation = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) relation.add(-1);
        relation.set(0, 5);
        relation.set(1, 0);
        relation.set(2, 1);
        relationArrList[1].add(relation);

        relation = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) relation.add(-1);
        relation.set(0, 3);
        relation.set(1, 1);
        relation.set(2, 4);
        relationArrList[1].add(relation);


        //2
        relation = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) relation.add(-1);
        relation.set(0, 0);
        relation.set(1, 2);
        relation.set(2, 4);
        relationArrList[2].add(relation);

        relation = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) relation.add(-1);
        relation.set(0, 5);
        relation.set(1, 2);
        relation.set(2, 3);
        relationArrList[2].add(relation);


        //3
        relation = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) relation.add(-1);
        relation.set(0, 3);
        relation.set(1, 1);
        relation.set(2, 4);
        relationArrList[3].add(relation);

        relation = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) relation.add(-1);
        relation.set(0, 5);
        relation.set(1, 2);
        relation.set(2, 3);
        relationArrList[3].add(relation);


        //4
        relation = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) relation.add(-1);
        relation.set(0, 0);
        relation.set(1, 2);
        relation.set(2, 4);
        relationArrList[4].add(relation);

        relation = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) relation.add(-1);
        relation.set(0, 3);
        relation.set(1, 1);
        relation.set(2, 4);
        relationArrList[4].add(relation);


        //5
        relation = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) relation.add(-1);
        relation.set(0, 5);
        relation.set(1, 0);
        relation.set(2, 1);
        relationArrList[5].add(relation);

        relation = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) relation.add(-1);
        relation.set(0, 5);
        relation.set(1, 2);
        relation.set(2, 3);
        relationArrList[5].add(relation);

        int noOfThisEt = 0;
        while (thisEditText != editTextArr[noOfThisEt]) {
            noOfThisEt++;
        }
        ArrayList<EditText> callThisFuncOnArrL = new ArrayList<>(2);
        for (int i = 0; i < relationArrList[noOfThisEt].size(); i++) {
            if (!editTextArr[noOfThisEt].getText().toString().isEmpty()) {
                if (relationArrList[noOfThisEt].get(i).indexOf(noOfThisEt) == 0) {
                    if (editTextArr[relationArrList[noOfThisEt].get(i).get(1)].getText().toString().isEmpty() &&
                            (!editTextArr[relationArrList[noOfThisEt].get(i).get(2)].getText().toString().isEmpty())) {

                        editTextArr[relationArrList[noOfThisEt].get(i).get(1)].setText(//Double.toString(
                                decimalFormat.format(Double.parseDouble(editTextArr[relationArrList[noOfThisEt].get(i).get(0)].getText().toString()) /
                                        Double.parseDouble(editTextArr[relationArrList[noOfThisEt].get(i).get(2)].getText().toString()))
                        );
                        callThisFuncOnArrL.add(editTextArr[relationArrList[noOfThisEt].get(i).get(1)]);
                    } else if (editTextArr[relationArrList[noOfThisEt].get(i).get(2)].getText().toString().isEmpty() &&
                            (!editTextArr[relationArrList[noOfThisEt].get(i).get(1)].getText().toString().isEmpty())) {

                        editTextArr[relationArrList[noOfThisEt].get(i).get(2)].setText(//Double.toString(
                                decimalFormat.format(Double.parseDouble(editTextArr[relationArrList[noOfThisEt].get(i).get(0)].getText().toString()) /
                                        Double.parseDouble(editTextArr[relationArrList[noOfThisEt].get(i).get(1)].getText().toString()))
                        );
                        callThisFuncOnArrL.add(editTextArr[relationArrList[noOfThisEt].get(i).get(2)]);
                    } else if ((!editTextArr[relationArrList[noOfThisEt].get(i).get(2)].getText().toString().isEmpty()) &&
                            (!editTextArr[relationArrList[noOfThisEt].get(i).get(1)].getText().toString().isEmpty())) {

                        if (!BuyFrag.compDeci(Double.parseDouble(editTextArr[relationArrList[noOfThisEt].get(i).get(0)].getText().toString()),
                                Double.parseDouble(editTextArr[relationArrList[noOfThisEt].get(i).get(1)].getText().toString()) *
                                        Double.parseDouble(editTextArr[relationArrList[noOfThisEt].get(i).get(2)].getText().toString()))) {

                            for (int j = 0; j < relationArrList[noOfThisEt].get(i).size(); j++) {
                                if (editTextArr[relationArrList[noOfThisEt].get(i).get(j)] != thisEditText) {
                                    //editTextArr[relationArrList[noOfThisEt].get(i).get(j)].setText("");
                                    int k;
                                    for (k = 0; k == j || editTextArr[relationArrList[noOfThisEt].get(i).get(k)] == thisEditText; k++) {
                                    }
                                    clearBtnArr[relationArrList[noOfThisEt].get(i).get(j)] = createButton(relationArrList[noOfThisEt].get(i).get(j), relationArrList[noOfThisEt].get(i).get(k), editTextArr, llArr, clearBtnArr);
                                    llArr[relationArrList[noOfThisEt].get(i).get(j)].addView(
                                            clearBtnArr[relationArrList[noOfThisEt].get(i).get(j)]
                                    );
                                    for (int l = 0; l < editTextArr.length; l++) {
                                        editTextArr[l].setFocusableInTouchMode(false);
                                    }
                                }
                            }
                        }
                    }
                } else if (relationArrList[noOfThisEt].get(i).indexOf(noOfThisEt) == 1) {
                    if (editTextArr[relationArrList[noOfThisEt].get(i).get(0)].getText().toString().isEmpty() &&
                            (!editTextArr[relationArrList[noOfThisEt].get(i).get(2)].getText().toString().isEmpty())) {

                        editTextArr[relationArrList[noOfThisEt].get(i).get(0)].setText(//Double.toString(
                                decimalFormat.format(Double.parseDouble(editTextArr[relationArrList[noOfThisEt].get(i).get(1)].getText().toString()) *
                                        Double.parseDouble(editTextArr[relationArrList[noOfThisEt].get(i).get(2)].getText().toString()))
                        );
                        callThisFuncOnArrL.add(editTextArr[relationArrList[noOfThisEt].get(i).get(0)]);
                    } else if (editTextArr[relationArrList[noOfThisEt].get(i).get(2)].getText().toString().isEmpty() &&
                            (!editTextArr[relationArrList[noOfThisEt].get(i).get(0)].getText().toString().isEmpty())) {

                        editTextArr[relationArrList[noOfThisEt].get(i).get(2)].setText(//Double.toString(
                                decimalFormat.format(Double.parseDouble(editTextArr[relationArrList[noOfThisEt].get(i).get(0)].getText().toString()) /
                                        Double.parseDouble(editTextArr[relationArrList[noOfThisEt].get(i).get(1)].getText().toString()))
                        );
                        callThisFuncOnArrL.add(editTextArr[relationArrList[noOfThisEt].get(i).get(2)]);
                    } else if ((!editTextArr[relationArrList[noOfThisEt].get(i).get(2)].getText().toString().isEmpty()) &&
                            (!editTextArr[relationArrList[noOfThisEt].get(i).get(0)].getText().toString().isEmpty())) {

                        if (!BuyFrag.compDeci(Double.parseDouble(editTextArr[relationArrList[noOfThisEt].get(i).get(0)].getText().toString()),
                                Double.parseDouble(editTextArr[relationArrList[noOfThisEt].get(i).get(1)].getText().toString()) *
                                        Double.parseDouble(editTextArr[relationArrList[noOfThisEt].get(i).get(2)].getText().toString()))) {

                            for (int j = 0; j < relationArrList[noOfThisEt].get(i).size(); j++)
                                if (editTextArr[relationArrList[noOfThisEt].get(i).get(j)] != thisEditText) {
                                    //editTextArr[relationArrList[noOfThisEt].get(i).get(j)].setText("");
                                    int k;
                                    for (k = 0; k == j || editTextArr[relationArrList[noOfThisEt].get(i).get(k)] == thisEditText; k++) {
                                    }
                                    clearBtnArr[relationArrList[noOfThisEt].get(i).get(j)] = createButton(relationArrList[noOfThisEt].get(i).get(j), relationArrList[noOfThisEt].get(i).get(k), editTextArr, llArr, clearBtnArr);
                                    llArr[relationArrList[noOfThisEt].get(i).get(j)].addView(
                                            clearBtnArr[relationArrList[noOfThisEt].get(i).get(j)]
                                    );
                                    for (int l = 0; l < editTextArr.length; l++) {
                                        editTextArr[l].setFocusableInTouchMode(false);
                                    }
                                }
                        }
                    }
                } else {
                    if (editTextArr[relationArrList[noOfThisEt].get(i).get(0)].getText().toString().isEmpty() &&
                            (!editTextArr[relationArrList[noOfThisEt].get(i).get(1)].getText().toString().isEmpty())) {
                        editTextArr[relationArrList[noOfThisEt].get(i).get(0)].setText(//Double.toString(
                                decimalFormat.format(Double.parseDouble(editTextArr[relationArrList[noOfThisEt].get(i).get(1)].getText().toString()) *
                                        Double.parseDouble(editTextArr[relationArrList[noOfThisEt].get(i).get(2)].getText().toString()))
                        );
                        callThisFuncOnArrL.add(editTextArr[relationArrList[noOfThisEt].get(i).get(0)]);
                    } else if (editTextArr[relationArrList[noOfThisEt].get(i).get(1)].getText().toString().isEmpty() &&
                            (!editTextArr[relationArrList[noOfThisEt].get(i).get(0)].getText().toString().isEmpty())) {
                        editTextArr[relationArrList[noOfThisEt].get(i).get(1)].setText(//Double.toString(
                                decimalFormat.format(Double.parseDouble(editTextArr[relationArrList[noOfThisEt].get(i).get(0)].getText().toString()) /
                                        Double.parseDouble(editTextArr[relationArrList[noOfThisEt].get(i).get(2)].getText().toString()))
                        );
                        callThisFuncOnArrL.add(editTextArr[relationArrList[noOfThisEt].get(i).get(1)]);
                    } else if ((!editTextArr[relationArrList[noOfThisEt].get(i).get(0)].getText().toString().isEmpty()) &&
                            (!editTextArr[relationArrList[noOfThisEt].get(i).get(1)].getText().toString().isEmpty())) {

                        if (!BuyFrag.compDeci(Double.parseDouble(editTextArr[relationArrList[noOfThisEt].get(i).get(0)].getText().toString()),
                                Double.parseDouble(editTextArr[relationArrList[noOfThisEt].get(i).get(1)].getText().toString()) *
                                        Double.parseDouble(editTextArr[relationArrList[noOfThisEt].get(i).get(2)].getText().toString()))) {

                            for (int j = 0; j < relationArrList[noOfThisEt].get(i).size(); j++)
                                if (editTextArr[relationArrList[noOfThisEt].get(i).get(j)] != thisEditText) {
                                    //editTextArr[relationArrList[noOfThisEt].get(i).get(j)].setText("");
                                    int k;
                                    for (k = 0; k == j || editTextArr[relationArrList[noOfThisEt].get(i).get(k)] == thisEditText; k++) {
                                    }
                                    clearBtnArr[relationArrList[noOfThisEt].get(i).get(j)] = createButton(relationArrList[noOfThisEt].get(i).get(j), relationArrList[noOfThisEt].get(i).get(k), editTextArr, llArr, clearBtnArr);
                                    llArr[relationArrList[noOfThisEt].get(i).get(j)].addView(
                                            clearBtnArr[relationArrList[noOfThisEt].get(i).get(j)]
                                    );
                                    for (int l = 0; l < editTextArr.length; l++) {
                                        editTextArr[l].setFocusableInTouchMode(false);
                                    }
                                }
                        }
                    }
                }
            } else {

            }
        }
        boolean clearBtnRemained = false;
        for (int j = 0; j < clearBtnArr.length; j++) {
            if (clearBtnArr[j] != null) {
                clearBtnRemained = true;
                break;
            }
        }
        boolean ans = false;
        if (!clearBtnRemained) {
            for (int i = 0; i < callThisFuncOnArrL.size(); i++) {
                ans = prodInfoDialogInfoAutoAdj(callThisFuncOnArrL.get(i), editTextArr, llArr, clearBtnArr);
                if (!ans) break;
            }
            if (callThisFuncOnArrL.size() == 0) ans = true;
        }
        return ans;
    }

    @SuppressLint("ResourceAsColor")
    private Button createButton(int noOfThisBtn, int linkedNo, EditText[] etArr, LinearLayout[] linArr, Button[] btnArr) {
        Button b = new Button(context);
        b.setText("change");
        //b.setBackgroundColor(R.color.red);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etArr[noOfThisBtn].setText("");
                linArr[noOfThisBtn].removeView(btnArr[noOfThisBtn]);
                linArr[linkedNo].removeView(btnArr[linkedNo]);
                btnArr[noOfThisBtn] = null;
                btnArr[linkedNo] = null;
                boolean changBtnRemained = false;
                for (int i = 0; i < etArr.length; i++) {
                    if (btnArr[i] != null) {
                        changBtnRemained = true;
                        break;
                    }
                }
                if (!changBtnRemained) {
                    for (int i = 0; i < etArr.length; i++) {
                        etArr[i].setFocusableInTouchMode(true);
                    }
                }
            }
        });
        return b;
    }
}
