package com.google.sayanbanik1997.shop;

import static com.google.sayanbanik1997.shop.Info.baseUrl;

import static java.security.AccessController.getContext;

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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//im
import java.util.Date;
import java.time.Month;
import java.time.LocalDate;

public class BuyFrag extends Fragment {
    static DecimalFormat decimalFormat = new DecimalFormat("#.0000000");
    static DecimalFormat decimalFormatToComp = new DecimalFormat("#.00");

    protected static boolean compDeci(Double d1, Double d2) {
        if (decimalFormatToComp.format(d1).equals(decimalFormatToComp.format(d2))) {
            return true;
        }
        return false;
    }

    View view;
    TextView txt;
    TextView supNameTxt, byingDtTxt;
    Dialog addSupDialog;
    Button addProdIntoListBtn;
    BuyInfoDialog buyInfoDialog;
    static Context context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context=getContext();
        view = inflater.inflate(R.layout.fragment_buy, container, false);
        supNameTxt = (TextView) view.findViewById(R.id.supNameTxt);
        byingDtTxt = (TextView) view.findViewById(R.id.byingDtTxt);

        addProdIntoListBtn = (Button) view.findViewById(R.id.addProdIntoListBtn);

        supNameTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSupDialog = new Dialog(getContext());
                addSupDialog.setContentView(R.layout.add_supplier);
                addSupDialog.show();
                cusSupProdDialogueFunc(addSupDialog, supNameTxt, "Supplier");
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
            byingDtTxt.setText(year+"-"+month+"-"+day);
            byingDtTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            byingDtTxt.setText(Integer.toString(year) + "-" +
                                    Integer.toString(month) + "-" + Integer.toString(dayOfMonth));
                        }
                    }, year, month - 1, day).show();
                }
            });
        }

        HashMap<View, BuyInfoDialog> vgLlHm=new HashMap<>();
        addProdIntoListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyInfoDialog = new BuyInfoDialog(getContext(), view, BuyFrag.this){
                    @Override
                    void subBtnClicked() {
                        submitBtnClicked( buyInfoDialog, vgLlHm);
                    }
                };
            }
        });
        ((Button) view.findViewById(R.id.submitProdPurchaseBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!supNameTxt.getText().equals(getResources().getString(R.string.buyerNameBuyFrag))) {
                    String[] tags = {"name", "dateOfPurchase"}, data = {
                        supNameTxt.getText().toString(), byingDtTxt.getText().toString()
                    };
                    new VolleyTakeData(getContext(), baseUrl + "insertProdEntry.php", tags, data, new AfterTakingData() {
                        @Override
                        public void doAfterTakingData(String response) {
                            if (Integer.parseInt(response) > 0) {
                                JSONArray jsonArray = new JSONArray();
                                JSONObject jsonObject = new JSONObject();

                                for (Map.Entry me : vgLlHm.entrySet()) {
                                    View eachListItem = (View) me.getKey();
                                    try {
                                        jsonObject.put("prodEntryTblId", Integer.parseInt(response));
                                        jsonObject.put("prodName", ((TextView) eachListItem.findViewById(R.id.prodNameTxt)).getText());
                                        jsonObject.put("prodQuan", ((TextView) eachListItem.findViewById(R.id.prodCountTxt)).getText());
                                        jsonObject.put("boxQuan", ((TextView) eachListItem.findViewById(R.id.boxCountTxt)).getText());
                                        jsonObject.put("totalAmount", ((TextView) eachListItem.findViewById(R.id.totalAmountTxt)).getText());
                                        jsonObject.put("unit", ((TextView) eachListItem.findViewById(R.id.unitTxt)).getText());
                                        jsonArray.put(jsonObject);
                                    } catch (Exception e) {
                                        Toast.makeText(getContext(), "error putting json", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                String[] tags = {"data"};
                                String[] data = {jsonArray.toString()};
                                new VolleyTakeData(getContext(), baseUrl + "insertProdList.php", tags, data, new AfterTakingData() {
                                    @SuppressLint("ResourceType")
                                    @Override
                                    public void doAfterTakingData(String response) {
                                        if (Integer.parseInt(response) > 0) {
                                            Toast.makeText(getContext(), "Successfully inserted", Toast.LENGTH_SHORT).show();
                                            getParentFragmentManager().beginTransaction().replace(R.layout.fragment_buy, new BuyFrag()).commit();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(getContext(), "error getting 1st response", Toast.LENGTH_SHORT).show();
                                //Log.d("kkkk", response);
                            }
                        }
                    });
                }
            }
        });
        return view;
    }
    private  void submitBtnClicked( BuyInfoDialog buyInfoDialog, HashMap<View, BuyInfoDialog> vgLlHm){
        View buyListEach= getLayoutInflater().inflate(R.layout.buy_list_each, null);
        if(!vgLlHm.containsValue(buyInfoDialog)) {
            LinearLayout buyFragLlforDealInfoInsert = (LinearLayout) view.findViewById(R.id.buyFragLlforDealInfoInsert);
            buyFragLlforDealInfoInsert.addView(buyListEach);
            vgLlHm.put(buyListEach, buyInfoDialog);
        }else{
            for ( View key : vgLlHm.keySet() ) {
                if(vgLlHm.get(key)==buyInfoDialog) {
                    buyListEach=key;
                    break;
                }
            }
        }

        //buyListEach.setBackgroundColor(R.color.white);
        TextView prodNameTxt = (TextView) buyListEach.findViewById(R.id.prodNameTxt);
        TextView prodCountTxt = (TextView) buyListEach.findViewById(R.id.prodCountTxt);
        TextView prodAmountTxt = (TextView) buyListEach.findViewById(R.id.prodAmountTxt);
        TextView boxCountTxt = (TextView) buyListEach.findViewById(R.id.boxCountTxt);
        TextView boxPriceTxt = (TextView) buyListEach.findViewById(R.id.boxPriceTxt);
        TextView itemPerBoxTxt = (TextView) buyListEach.findViewById(R.id.itemPerBoxTxt);
        TextView totalAmountTxt = (TextView) buyListEach.findViewById(R.id.totalAmountTxt);
        TextView unitTxt = (TextView) buyListEach.findViewById(R.id.unitTxt);
        prodNameTxt.setText(buyInfoDialog.chooseProdTxt.getText());
        prodCountTxt.setText(buyInfoDialog.chooseProdDiEtArr[0].getText());
        prodAmountTxt.setText(buyInfoDialog.chooseProdDiEtArr[1].getText());
        boxCountTxt.setText(buyInfoDialog.chooseProdDiEtArr[2].getText());
        boxPriceTxt.setText(buyInfoDialog.chooseProdDiEtArr[3].getText());
        itemPerBoxTxt.setText(buyInfoDialog.chooseProdDiEtArr[4].getText());
        totalAmountTxt.setText(buyInfoDialog.chooseProdDiEtArr[5].getText());
        unitTxt.setText(buyInfoDialog.unitEt.getText());

        buyInfoDialog.buyInfoDialogue.dismiss();
        buyInfoDialog = new BuyInfoDialog(getContext(), view, BuyFrag.this){
            @Override
            void subBtnClicked() {
                submitBtnClicked(this, vgLlHm);
            }
        };
        buyListEach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vgLlHm.get(v).buyInfoDialogue.show();
            }
        });
    }
    protected static void cusSupProdDialogueFunc(Dialog dialog, TextView ultimateTextSetTextview, String subUrl) {
        EditText supNameEt = (EditText) dialog.findViewById(R.id.supNameEt);
        ListView supplierList = (ListView) dialog.findViewById(R.id.supList);
        Button addCrSupBtn = (Button) dialog.findViewById(R.id.addCrSupBtn);
        String[] tag = {"name"};
        String[] data = {""};
        setData(tag, data, dialog, supNameEt, supplierList, addCrSupBtn, subUrl);

        supNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String[] tag = {"name"};
                String[] data = {supNameEt.getText().toString()};
                setData(tag, data, dialog, supNameEt, supplierList, addCrSupBtn, subUrl);
            }
        });
        addCrSupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (supNameEt.getText().toString().isEmpty()) {
                    Toast.makeText(context, subUrl + " name can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (addCrSupBtn.getText().toString().equalsIgnoreCase("Add " + subUrl)) {
                    ultimateTextSetTextview.setText(supNameEt.getText().toString());
                    dialog.dismiss();
                    return;
                }
                String[] tag = {"name"};
                String[] data = {supNameEt.getText().toString()};
                new VolleyTakeData(context, baseUrl+"create"+subUrl+".php", tag, data, new AfterTakingData() {
                    @Override
                    public void doAfterTakingData(String response) {
                        if (response.equals("1")) {
                            ultimateTextSetTextview.setText(supNameEt.getText().toString());
                            dialog.dismiss();
                            ;
                        } else {
                            Toast.makeText(context, "Error occurred " + response, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
    protected static void setData(String[] tag, String[] data, Dialog addSupDialog, EditText supNameEt, ListView supplierList, Button addCrSupBtn, String subUrl) {
        new VolleyTakeData(context, baseUrl + "get" + subUrl + ".php", tag, data, new AfterTakingData() {
            @Override
            public void doAfterTakingData(String response) {
                ArrayList<String> rslt = new ArrayList<>();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject explrObject;
                    if (supNameEt.getText().toString().isEmpty()) {
                        addCrSupBtn.setText(subUrl + " name can't be empty");
                    } else {
                        addCrSupBtn.setText("Create " + subUrl + " and then add");
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        explrObject = jsonArray.getJSONObject(i);
                        rslt.add(explrObject.getString("name"));
                        if (explrObject.getString("name").equals(supNameEt.getText().toString())) {
                            addCrSupBtn.setText("Add " + subUrl);
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "error while parsing json", Toast.LENGTH_SHORT).show();
                }
                //ArrayAdapter<String > arrayAdapter= new ArrayAdapter<>(getContext(), an)
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, rslt);
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
abstract class BuyInfoDialog {

    static DecimalFormat decimalFormat = new DecimalFormat("#.0000000");
    static DecimalFormat decimalFormatToComp = new DecimalFormat("#.00");
    Context context;View view;Fragment fragment;
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
        buyInfoDialogue.setContentView(R.layout.prod_list_entry);
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
                Dialog chooseProdDialogue = new Dialog(context);
                chooseProdDialogue.setContentView(R.layout.add_supplier);
                chooseProdDialogue.show();
                BuyFrag.cusSupProdDialogueFunc(chooseProdDialogue, chooseProdTxt, "Product");
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
                if(((TextView) buyInfoDialogue.findViewById(R.id.chooseProdTxt)).getText().equals(R.string.buyInfoDialogueProdName) ||
                        ((EditText) buyInfoDialogue.findViewById(R.id.prodCountEt)).getText().toString().isEmpty() ||
                        ((EditText) buyInfoDialogue.findViewById(R.id.pricePerProdEt)).getText().toString().isEmpty() ||
                        ((EditText) buyInfoDialogue.findViewById(R.id.totalAmountEt)).getText().toString().isEmpty() ||
                        ((EditText) buyInfoDialogue.findViewById(R.id.unitEt)).getText().toString().isEmpty() 
                ){
                    Toast.makeText(context, "Prduct name, Product count, Product price, Total amount, unit need to be set", Toast.LENGTH_SHORT).show();
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
