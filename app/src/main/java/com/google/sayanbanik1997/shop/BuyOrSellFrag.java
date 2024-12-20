package com.google.sayanbanik1997.shop;

import static com.google.sayanbanik1997.shop.Info.baseUrl;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

//im
import java.util.Date;
import java.time.LocalDate;

public class BuyOrSellFrag extends Fragment {
    String subUrl="";
    int billId;
    JSONArray payments;
    BuyOrSellFrag(String subUrl, int billId){
        this.subUrl=subUrl;
        this.billId=billId;
    }
    static DecimalFormat decimalFormat = new DecimalFormat("#.0000000");
    static DecimalFormat decimalFormatToComp = new DecimalFormat("#.00");

    protected static boolean compDeci(Double d1, Double d2) {
        if (decimalFormatToComp.format(d1).equals(decimalFormatToComp.format(d2))) {
            return true;
        }
        return false;
    }

    View view;
    TextView supNameTxt, byingDtTxt;
    Button addProdIntoListBtn;
    static Context context;

    public TextView totalAmounttTxt ,paidTxt, dueTxt;
    EditText haveToPayTxt;
    ShowPaymentsDialog showPaymentsDialog=null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context=getContext();
        try {
            payments = new JSONArray("[]");
        } catch (JSONException e) {
            Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
        }
        view = inflater.inflate(R.layout.fragment_buy_or_sell, container, false);

        supNameTxt = (TextView) view.findViewById(R.id.supNameTxt);
        byingDtTxt = (TextView) view.findViewById(R.id.byingDtTxt);
        TextView soldUnsoldTxt = (TextView) view.findViewById(R.id.soldUnsoldTxt);

        if(subUrl.equals("Supplier")) {
            supNameTxt.setText(R.string.sellerNameBuyFrag);
            soldUnsoldTxt.setText("Bought");
        } else if (subUrl.equals("Customer")) {
            supNameTxt.setText(R.string.buyerNameBuyFrag);
            soldUnsoldTxt.setText("Sold");
        }

        addProdIntoListBtn = (Button) view.findViewById(R.id.addProdIntoListBtn);

        supNameTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CusSupProdDialogue(context, subUrl) {
                    @Override
                    void doAfterBtnClicked() {
                        supNameTxt.setText(((EditText) dialog.findViewById(R.id.supNameEt)).getText().toString());
                    }
                };
            }
        });

        soldUnsoldTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(soldUnsoldTxt.getText().toString().equals("Sold")) soldUnsoldTxt.setText("Unsold");
                else if(soldUnsoldTxt.getText().toString().equals("Unsold")) soldUnsoldTxt.setText("Sold");
                else if(soldUnsoldTxt.getText().toString().equals("Bought")) soldUnsoldTxt.setText("Unbought");
                else soldUnsoldTxt.setText("Bought");
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
                                    Integer.toString(month+1) + "-" + Integer.toString(dayOfMonth));
                        }
                    }, year, month - 1, day).show();
                }
            });
        }

        HashMap<View, BuySellInfoDialog> vgLlHm=new HashMap<>();
        addProdIntoListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuySellInfoDialog buySellInfoDialog = new BuySellInfoDialog(getContext(),  BuyOrSellFrag.this){
                    @Override
                    void subBtnClicked() {
                        addBuySellListEach( this, vgLlHm);
                        new BuySellInfoDialog(getContext(), BuyOrSellFrag.this){
                            @Override
                            void subBtnClicked() {
                                addBuySellListEach(this, vgLlHm);
                            }
                        };
                    }
                };
            }
        });
        totalAmounttTxt = (TextView)view.findViewById(R.id.totalAmounttTxt);
        paidTxt=(TextView) view.findViewById(R.id.paidTxt);
        dueTxt=(TextView) view.findViewById(R.id.dueTxt);
        haveToPayTxt=(EditText) view.findViewById(R.id.haveToPayEt);
        totalAmounttTxt.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }@Override public void onTextChanged(CharSequence s, int start, int before, int count) {  }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    haveToPayTxt.setText(
                        Double.toString(Double.parseDouble(totalAmounttTxt.getText().toString()))
                    );
                }catch (Exception e){
                    haveToPayTxt.setText("");
                }
            }
        });
        haveToPayTxt.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}@Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                //if(billId==0) {
                    try {
                        paidTxt.setText(Double.toString(Double.parseDouble(haveToPayTxt.getText().toString())));
                        dueTxt.setText(
                                Double.toString(Double.parseDouble(haveToPayTxt.getText().toString()) - Double.parseDouble(paidTxt.getText().toString()))
                        );
                    } catch (Exception e) {
                        dueTxt.setText("");
                    }
                //}
            }
        });
        paidTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showPaymentsDialog!=null){
                    showPaymentsDialog.dialog.show();
                    showPaymentsDialog.amountEt.setText(Double.toString(showPaymentsDialog.getAmountForEt(payments ,Double.parseDouble(((TextView)v).getText().toString()))));
                }else {
                    ShowPaymentsDialog showPaymentsDialog = new ShowPaymentsDialog(getContext(), payments, ((TextView) v).getText().toString()) {
                        @Override
                        void submitBtnClicked() {
                            double totalPaid = 0;
                            for (int i = 0; i < this.arrayListOfPaymentsArrView.size(); i++) {
                                totalPaid += Double.parseDouble(this.arrayListOfPaymentsArrView.get(i)[2]);
                                //Toast.makeText(getContext(), arrayListOfPaymentsArrView.get(i)[2], Toast.LENGTH_SHORT).show();
                            }
                            try {
                                totalPaid += Double.parseDouble(this.amountEt.getText().toString());
                            } catch (NumberFormatException e) {
                                //Toast.makeText(getContext(), "error while parsing double", Toast.LENGTH_SHORT).show();
                            }
                            paidTxt.setText(Double.toString(totalPaid));
                            BuyOrSellFrag.this.showPaymentsDialog=this;
                        }
                        @Override
                        void onCancled() {
                            BuyOrSellFrag.this.showPaymentsDialog=null;
                            //setDataToPaidTxt();
                        }
                    };
                    //showPaymentsDialog.amountEt.setText(((TextView)v).getText().toString());
                }
            }
        });
        paidTxt.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}@Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {dueTxt.setText(
                        Double.toString(Double.parseDouble(haveToPayTxt.getText().toString()) - Double.parseDouble(paidTxt.getText().toString()))
                );
                }catch (Exception e){
                    dueTxt.setText("");
                }
            }
        });
        dueTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Double.parseDouble(dueTxt.getText().toString())== Double.parseDouble(haveToPayTxt.getText().toString())-
                        Double.parseDouble(paidTxt.getText().toString())){
                    dueTxt.setText("0");
                    haveToPayTxt.setText(paidTxt.getText().toString());
                }
            }
        });
        ((Button) view.findViewById(R.id.submitProdPurchaseBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(supNameTxt.getText().equals(getResources().getString(R.string.buyerNameBuyFrag)) ||
                        supNameTxt.getText().equals(getResources().getString(R.string.sellerNameBuyFrag))) {
                    Toast.makeText(getContext(), subUrl+" can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(vgLlHm.size()==0) {
                    Toast.makeText(getContext(), "1st add transaction then submit", Toast.LENGTH_SHORT).show();
                    return;
                }
                String paid="";
                try{
                    paid  = Double.toString(Double.parseDouble(paidTxt.getText().toString()));
                }catch (Exception e){
                    Toast.makeText(getContext(), "Paid need to be filled", Toast.LENGTH_SHORT).show();
                    return;
                }
                int soldUnsold=2;
                if(soldUnsoldTxt.getText().toString().equals("Sold") || (soldUnsoldTxt.getText().toString().equals("Bought"))) soldUnsold=1;
                int purOrSell=2;
                if(subUrl.equals("Supplier")) purOrSell=1;
                String[] tags = {"name", "dateOfPurchase", "purOrSell", "soldUnsold", "paid", "haveToPay", "billId", "payments"}, data = {
                    supNameTxt.getText().toString(), byingDtTxt.getText().toString(), Integer.toString(purOrSell),
                    Integer.toString(soldUnsold), paid,
                    ((TextView) view.findViewById(R.id.haveToPayEt)).getText().toString(), Integer.toString(billId),
                    payments.toString()
                };
                new VolleyTakeData(getContext(), baseUrl + "insertProdEntry.php", tags, data, new AfterTakingData() {
                    @Override
                    public void doAfterTakingData(String response) {
                        Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                        Log.d("kkkk", response);
                        if (Integer.parseInt(response) >= 0) {
                            JSONArray jsonArray = new JSONArray();

                            for (Map.Entry me : vgLlHm.entrySet()) {
                                View eachListItem = (View) me.getKey();
                                JSONObject jsonObject = new JSONObject();
                                int responsee=Integer.parseInt(response);
                                if(responsee==0) responsee=billId;
                                try {
                                    jsonObject.put("prodEntryTblId", responsee);
                                    if (responsee==0){
                                        jsonObject.put("id", 1);
                                    }
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
                                        getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new BuyOrSellFrag("Supplier", 0)).commit();
                                    }else{
                                        Toast.makeText(getContext(), "Error parsing 2nd response", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getContext(), "Error getting 1st response", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        if(billId!=0){
            TextView billIdTxt = (TextView) view.findViewById(R.id.billIdTxt);
            billIdTxt.setText(Integer.toString(billId));
            String[] tag={"billId"}, data={Integer.toString(billId)};
            new VolleyTakeData(getContext(), baseUrl + "getBills.php", tag, data, new AfterTakingData() {
                @Override
                public void doAfterTakingData(String response) {
                    Log.d("kkkk", response);
                    try {
                        JSONArray billJsonArr=new JSONArray(response);
                        JSONObject billJsonObj=new JSONObject(String.valueOf(billJsonArr.getJSONObject(0)));

                        if(subUrl.equals("Customer")){
                            supNameTxt.setText(billJsonObj.getString("cusName"));
                            if(billJsonObj.getString("soldUnsold").equals("1")){
                                soldUnsoldTxt.setText("Sold");
                            }else{
                                soldUnsoldTxt.setText("Unsold");
                            }
                        }else {
                            supNameTxt.setText(billJsonObj.getString("supName"));
                            if(billJsonObj.getString("soldUnsold").equals("1")){
                                soldUnsoldTxt.setText("Bought");
                            }else{
                                soldUnsoldTxt.setText("Unbought");
                            }
                        }

                        byingDtTxt.setText(billJsonObj.getString("dateOfPurchase"));
                        JSONArray prodListDtlsJsonArr = new JSONArray(billJsonObj.getString("prodListDtls"));
                        for(int i=0; i<prodListDtlsJsonArr.length(); i++) {
                            JSONObject prodListDtlsJsonObj = new JSONObject(String.valueOf(prodListDtlsJsonArr.getJSONObject(i)));
                            int prodId=Integer.parseInt(prodListDtlsJsonObj.getString("prodId"));
                            String prodName = prodListDtlsJsonObj.getString("prodName");
                            double boxQuan = Double.parseDouble(prodListDtlsJsonObj.getString("boxQuan"));
                            double prodQuan = Double.parseDouble(prodListDtlsJsonObj.getString("prodQuan"));
                            double amount = Double.parseDouble(prodListDtlsJsonObj.getString("totalAmount"));
                            String unit = prodListDtlsJsonObj.getString("unit");
                            int plid= Integer.parseInt(prodListDtlsJsonObj.getString("plid"));

                            BuySellInfoDialog buySellInfoDialog =new BuySellInfoDialog(getContext(), BuyOrSellFrag.this) {
                                @Override
                                void subBtnClicked() {
                                    addBuySellListEach(this, vgLlHm);
                                }
                            };
                            buySellInfoDialog.setData(prodName, prodQuan,boxQuan, amount, unit);
                            addBuySellListEach(buySellInfoDialog, vgLlHm);
                        }
                        payments=new JSONArray(billJsonObj.getString("paymentDtls"));
                        setDataToPaidTxt();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "JSON error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        return view;
    }
    private void setDataToPaidTxt(){
        Double totalPaid=0d;
        for (int i=0; i<payments.length(); i++){
            JSONObject eachPaymentJsonObj = null;
            try {
                eachPaymentJsonObj = new JSONObject(payments.getString(i));
                totalPaid+=eachPaymentJsonObj.getDouble("amount");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        paidTxt.setText(Double.toString(totalPaid));
    }
    private void addBuySellListEach(BuySellInfoDialog buySellInfoDialog, HashMap<View, BuySellInfoDialog> vgLlHm){
        View buyListEach= getLayoutInflater().inflate(R.layout.buy_sell_list_each_sub_layout, null);
        if(!vgLlHm.containsValue(buySellInfoDialog)) {
            LinearLayout buyFragLlforDealInfoInsert = (LinearLayout) view.findViewById(R.id.buyFragLlforDealInfoInsert);
            ImageView delImg= (ImageView) buyListEach.findViewById(R.id.delImg);
            final View buyListEachTemp= buyListEach;
            delImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TextView)view.findViewById(R.id.totalAmounttTxt)).setText(Double.toString(
                    Double.parseDouble(((TextView)view.findViewById(R.id.totalAmounttTxt)).getText().toString())-
                    Double.parseDouble(((TextView)buyListEachTemp.findViewById(R.id.totalAmountTxt)).getText().toString())));
                    buyFragLlforDealInfoInsert.removeView(buyListEachTemp);
                    vgLlHm.remove(buyListEachTemp);
                }
            });
            buyFragLlforDealInfoInsert.addView(buyListEach);
            vgLlHm.put(buyListEach, buySellInfoDialog);
        }else{
            if(vgLlHm.containsValue(buySellInfoDialog)) {
                for (View key : vgLlHm.keySet()) {
                    if (vgLlHm.get(key) == buySellInfoDialog) {
                        buyListEach = key;
                        break;
                    }
                }
            }else{
                Toast.makeText(context, "buyInfoDialog not found in vgLlHm", Toast.LENGTH_SHORT).show();
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

//        JSONObject buySellListEachDtlsJsonObj = new JSONObject();
//        try {
//            buySellListEachDtlsJsonObj.put("prodName", buySellInfoDialog.chooseProdTxt.getText().toString());
//            buySellListEachDtlsJsonObj.put("prodCount", buySellInfoDialog.chooseProdDiEtArr[0].getText().toString());
//            buySellListEachDtlsJsonObj.put("prodPrice", buySellInfoDialog.chooseProdDiEtArr[1].getText().toString());
//            buySellListEachDtlsJsonObj.put("boxCount", buySellInfoDialog.chooseProdDiEtArr[2].getText().toString());
//            buySellListEachDtlsJsonObj.put("boxPrice", buySellInfoDialog.chooseProdDiEtArr[3].getText().toString());
//            buySellListEachDtlsJsonObj.put("itemPerBox", buySellInfoDialog.chooseProdDiEtArr[4].getText().toString());
//            buySellListEachDtlsJsonObj.put("TotalAmount", buySellInfoDialog.chooseProdDiEtArr[5].getText().toString());
//            buySellListEachDtlsJsonObj.put("unit", buySellInfoDialog.unitEt.getText().toString());
//        } catch (JSONException e) {
//            Toast.makeText(context, "json error", Toast.LENGTH_SHORT).show();
//        }

        prodNameTxt.setText(buySellInfoDialog.chooseProdTxt.getText());
        prodCountTxt.setText(buySellInfoDialog.chooseProdDiEtArr[0].getText());
        prodAmountTxt.setText(buySellInfoDialog.chooseProdDiEtArr[1].getText());
        boxCountTxt.setText(buySellInfoDialog.chooseProdDiEtArr[2].getText());
        boxPriceTxt.setText(buySellInfoDialog.chooseProdDiEtArr[3].getText());
        itemPerBoxTxt.setText(buySellInfoDialog.chooseProdDiEtArr[4].getText());
        totalAmountTxt.setText(buySellInfoDialog.chooseProdDiEtArr[5].getText());
        unitTxt.setText(buySellInfoDialog.unitEt.getText());


        Double totalAmount= 0d;
        for (View key : vgLlHm.keySet()) {
            totalAmount+=Double.parseDouble(((TextView)key.findViewById(R.id.totalAmountTxt)).getText().toString());
        }
        ((TextView) view.findViewById(R.id.totalAmounttTxt)).setText(Double.toString(totalAmount));

        buySellInfoDialog.buyInfoDialogue.dismiss();

        buyListEach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vgLlHm.get(v).buyInfoDialogue.show();
            }
        });
    }

}
