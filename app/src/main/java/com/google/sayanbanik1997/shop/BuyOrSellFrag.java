package com.google.sayanbanik1997.shop;

import static com.google.sayanbanik1997.shop.Info.baseUrl;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//im
import java.util.Date;
import java.time.LocalDate;

public class BuyOrSellFrag extends Fragment {
    String subUrl="";
    int billId;
    JSONArray payments, paymentsToSend;
    DataToSent dataToSent;
    Boolean showBuySellInMemClickedBool=false;
    MainActivity mainActivity;
    int purOrSell=0;
    BuyOrSellFrag(String subUrl, int billId, DataToSent dataToSent, MainActivity mainActivity){
        this.subUrl=subUrl;
        this.billId=billId;
        this.dataToSent=dataToSent;
        this.mainActivity=mainActivity;
        if(subUrl.equals("Customer")){
            purOrSell=2;
        }else if(subUrl.equals("Supplier")){
            purOrSell=1;
        }
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
    Button addProdIntoListBtn, savePdfBtn;
    static Context context;

    public TextView totalAmounttTxt ,paidTxt, dueTxt;
    EditText haveToPayEt;
    ShowPaymentsDialog showPaymentsDialog=null;
    HashMap<View, BuySellInfoDialog> vgLlHm;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view==null)view = inflater.inflate(R.layout.fragment_buy_or_sell, container, false);
        else return view;

        context=getContext();
        try {
            payments = new JSONArray("[]");
            //paymentsToSend= payments;
        } catch (JSONException e) {
            Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
        }

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
        supNameTxt.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }@Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                String tableName = "customerr_tbl";
                if(purOrSell==1){
                    tableName = "supplier_tbl";
                }
                //Toast.makeText(mainActivity, supNameTxt.getText().toString(), Toast.LENGTH_SHORT).show();
                String[] tags = {"tableName", "whereClause"}, data = {tableName, "where name='" + supNameTxt.getText().toString() +"'"};
                new VolleyTakeData(getContext(), baseUrl + "getDirectQryRslt.php", tags, data, new AfterTakingData() {
                    @Override
                    public void doAfterTakingData(String response) {
                        String cusId="0";
                        try {
                            cusId = new JSONArray(response).getJSONObject(0).getString("id");
                        } catch (Exception e) {
                            Toast.makeText(mainActivity, "error46378", Toast.LENGTH_SHORT).show();
                        }

//                        String[] tags = {"qry", "tableId"},
//                                data = {"select * from prod_entry_tbl where purOrSell = "+ Integer.toString(purOrSell) +
//                                                " and cusId="+ cusId +" and id != "+billId, "3"};
                        String[] tags = {"tableName", "whereClause"},
                                data = {"prod_entry_tbl", "where purOrSell = "+ Integer.toString(purOrSell) +
                                        " and cusId="+ cusId +" and id != "+billId};
                        final double[] totalHaveToPayExcludingThisBill = {0};
                        String finalCusId = cusId;
                        new VolleyTakeData(getContext(), baseUrl + "getDirectQryRslt.php", tags, data, new AfterTakingData() {
                            @Override
                            public void doAfterTakingData(String response) {
                                try {
                                    JSONArray billsJsonArr = new JSONArray(response);
                                    for (int i=0; i< billsJsonArr.length(); i++) {
                                        JSONObject billsJsonObj = billsJsonArr.getJSONObject(i);
                                        totalHaveToPayExcludingThisBill[0] += Double.parseDouble(billsJsonObj.getString("haveToPay"));
                                    }
                                } catch (Exception e) {
                                    Toast.makeText(mainActivity, "error 107652098", Toast.LENGTH_SHORT).show();
                                }
                                final double[] totalPaymentsExcludingThisBill={0};
                                String qry="";
                                if(purOrSell==1){
                                    qry=
                                            //"select * from payment_tbl " +
                                            "where supId="+ finalCusId;
                                }else if(purOrSell==2){
                                    qry=
                                            //"select * from payment_tbl " +
                                                    "where cusId="+ finalCusId;
                                }
                                qry += " and billId != "+ billId;
                                //String[] tags = {"qry", "tableId"}, data = {qry, "2"};
                                String[] tags = {"tableName", "whereClause"}, data = {"payment_tbl", qry};
                                new VolleyTakeData(getContext(), baseUrl + "getDirectQryRslt.php", tags, data, new AfterTakingData() {
                                    @Override
                                    public void doAfterTakingData(String response) {
                                        try {
                                            JSONArray billsJsonArr = new JSONArray(response);
                                            for (int i=0; i< billsJsonArr.length(); i++) {
                                                JSONObject billsJsonObj = billsJsonArr.getJSONObject(i);
                                                totalPaymentsExcludingThisBill[0] += Double.parseDouble(billsJsonObj.getString("amount"));
                                            }
                                        } catch (Exception e) {
                                            Toast.makeText(mainActivity, "error85701", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                });

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

        vgLlHm=new HashMap<>();
        addProdIntoListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuySellInfoDialog buySellInfoDialog = new BuySellInfoDialog(getContext(),  BuyOrSellFrag.this){
                    @Override
                    void subBtnClicked() {
                        addBuySellListEach( this, vgLlHm);
                        onClick(v);
                    }
                };
            }
        });
        totalAmounttTxt = (TextView)view.findViewById(R.id.totalAmounttTxt);
        paidTxt=(TextView) view.findViewById(R.id.paidTxt);
        dueTxt=(TextView) view.findViewById(R.id.dueTxt);
        haveToPayEt=(EditText) view.findViewById(R.id.haveToPayEt);
        totalAmounttTxt.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }@Override public void onTextChanged(CharSequence s, int start, int before, int count) {  }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    haveToPayEt.setText(
                        Double.toString(Double.parseDouble(totalAmounttTxt.getText().toString()))
                    );
                }catch (Exception e){
                    haveToPayEt.setText("");
                }
            }
        });
        haveToPayEt.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}@Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if(billId==0) {
                        paidTxt.setText(Double.toString(Double.parseDouble(haveToPayEt.getText().toString())));
                    }
                    dueTxt.setText(
                            Double.toString(Double.parseDouble(haveToPayEt.getText().toString()) - Double.parseDouble(paidTxt.getText().toString()))
                    );
                } catch (Exception e) {
                    paidTxt.setText("0");
                    dueTxt.setText("0");
                }
            }
        });
        paidTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showPaymentsDialog!=null){
                    showPaymentsDialog.dialog.show();
                    showPaymentsDialog.amountEt.setText(Double.toString(showPaymentsDialog.getAmountForEt(Double.parseDouble(((TextView)v).getText().toString()))));
                }else {

                    ShowPaymentsDialog showPaymentsDialog = new ShowPaymentsDialog(getContext(), payments, ((TextView) v).getText().toString()) {
                        @Override
                        void submitBtnClicked(ArrayList<String[]> arrayListOfPaymentsArrView,
                                String dateOfPayment, String amount) {
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
                            paymentsToSend = new JSONArray();
                            for (int i=0;i <arrayListOfPaymentsArrView.size(); i++){
                                JSONObject eachPaymentJsonObj= new JSONObject();
                                try {
                                    eachPaymentJsonObj.put("id", arrayListOfPaymentsArrView.get(i)[0]);
                                    eachPaymentJsonObj.put("dateOfPayment", arrayListOfPaymentsArrView.get(i)[1]);
                                    eachPaymentJsonObj.put("amount", arrayListOfPaymentsArrView.get(i)[2]);
                                } catch (JSONException e) {
                                    Toast.makeText(context, "eroro36748er5", Toast.LENGTH_SHORT).show();
                                }
                                paymentsToSend.put(eachPaymentJsonObj);
                            }
                            JSONObject eachPaymentJsonObj= new JSONObject();
                            try {
                                eachPaymentJsonObj.put("id", "");
                                eachPaymentJsonObj.put("dateOfPayment", dateOfPayment);
                                eachPaymentJsonObj.put("amount", amount);
                                //Toast.makeText(context, amount, Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                Toast.makeText(context, "eroro367485", Toast.LENGTH_SHORT).show();
                            }
                            paymentsToSend.put(eachPaymentJsonObj);
                            BuyOrSellFrag.this.showPaymentsDialog=this;
                        }
                        @Override
                        void onCancled() {
                            BuyOrSellFrag.this.showPaymentsDialog=null;
                            if(billId!=0){
                                setDataToPaidTxt();
                            }
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
                        Double.toString(Double.parseDouble(haveToPayEt.getText().toString()) - Double.parseDouble(paidTxt.getText().toString()))
                );
                }catch (Exception e){
                    dueTxt.setText("");
                }
            }
        });
        dueTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Double.parseDouble(dueTxt.getText().toString())== Double.parseDouble(haveToPayEt.getText().toString())-
                        Double.parseDouble(paidTxt.getText().toString())){
                    dueTxt.setText("0");
                    haveToPayEt.setText(paidTxt.getText().toString());
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
                String[] tags = {"name", "dateOfPurchase", "purOrSell", "soldUnsold", "paid", "haveToPay", "billId", "payments"},
                        data = {
                            supNameTxt.getText().toString(), byingDtTxt.getText().toString(), Integer.toString(purOrSell),
                            Integer.toString(soldUnsold), paid,
                            ((EditText) view.findViewById(R.id.haveToPayEt)).getText().toString(), Integer.toString(billId),
                                paymentsToSend.toString()
                        };
                new VolleyTakeData(getContext(), baseUrl + "insertProdEntry.php", tags, data, new AfterTakingData() {
                    @Override
                    public void doAfterTakingData(String response) {
                       //Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                        //Log.d("kkkk", response);
                        if (Integer.parseInt(response) > 0) {
                            JSONArray jsonArray = new JSONArray();

                            for (Map.Entry me : vgLlHm.entrySet()) {
                                View eachListItem = (View) me.getKey();
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("prodEntryTblId", response);
                                    jsonObject.put("id", ((TextView) eachListItem.findViewById(R.id.idTxt)).getText());
                                    jsonObject.put("prodName", ((TextView) eachListItem.findViewById(R.id.prodNameTxt)).getText());
                                    jsonObject.put("prodQuantity", ((TextView) eachListItem.findViewById(R.id.prodCountTxt)).getText());
                                    jsonObject.put("boxQuantity", ((TextView) eachListItem.findViewById(R.id.boxCountTxt)).getText());
                                    jsonObject.put("totalAmount", ((TextView) eachListItem.findViewById(R.id.totalAmountTxt)).getText());
                                    jsonObject.put("unit", ((TextView) eachListItem.findViewById(R.id.unitTxt)).getText());
                                    jsonArray.put(jsonObject);
                                } catch (Exception e) {
                                    Toast.makeText(getContext(), "error putting json", Toast.LENGTH_SHORT).show();
                                }
                            }
                            String[] tags = { "billId", "data" };
                            String[] data = { Integer.toString(billId) ,jsonArray.toString() };
                            new VolleyTakeData(getContext(), baseUrl + "insertProdList.php", tags, data, new AfterTakingData() {
                                @SuppressLint("ResourceType")
                                @Override
                                public void doAfterTakingData(String response) {
                                    //Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                                    //Log.d("kkkk", response);
                                    if (Integer.parseInt(response) > 0) {
                                        Toast.makeText(getContext(), "Successfully inserted", Toast.LENGTH_SHORT).show();
                                        getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new BuyOrSellFrag("Supplier", 0, new DataToSent(), mainActivity)).commit();
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

        ((Button) view.findViewById(R.id.otherBuySellsBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAndAddBuySellFragInMem();
                showBuySellInMemClickedBool=true;
                getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new ShowBuySellInMemFrag(dataToSent)).commit();
            }
        });

        ((Button)view.findViewById(R.id.savePdfBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new PdfFrag(BuyOrSellFrag.this)).commit();
                //createAndSavePdf(view);
            }
        });

//        String buyerOrSellerNameBuyFragString;
//        if(purOrSell==1){
//            buyerOrSellerNameBuyFragString = getResources().getString(R.string.sellerNameBuyFrag);
//        }else{
//            buyerOrSellerNameBuyFragString = getResources().getString(R.string.buyerNameBuyFrag);
//        }
//                Toast.makeText(mainActivity, supNameTxt.getText().toString(), Toast.LENGTH_SHORT).show();
//        Toast.makeText(mainActivity,buyerOrSellerNameBuyFragString, Toast.LENGTH_SHORT).show();
        if(billId!=0
                //&& supNameTxt.getText().toString().equals(buyerOrSellerNameBuyFragString)
            ){
            Toast.makeText(mainActivity, "entered", Toast.LENGTH_SHORT).show();
            TextView billIdTxt = (TextView) view.findViewById(R.id.billIdTxt);
            billIdTxt.setText(Integer.toString(billId));
            String[] tag={"billId"}, data={Integer.toString(billId)};
            new VolleyTakeData(getContext(), baseUrl + "getBills.php", tag, data, new AfterTakingData() {
                @Override
                public void doAfterTakingData(String response) {
                    //Log.d("kkkk", response);
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
                            //int id=Integer.parseInt(prodListDtlsJsonObj.getString("plId"));
                            int prodId=Integer.parseInt(prodListDtlsJsonObj.getString("prodId"));
                            String prodName = prodListDtlsJsonObj.getString("prodName");
                            double boxQuantity = Double.parseDouble(prodListDtlsJsonObj.getString("boxQuantity"));
                            double prodQuantity = Double.parseDouble(prodListDtlsJsonObj.getString("prodQuantity"));
                            double amount = Double.parseDouble(prodListDtlsJsonObj.getString("totalAmount"));
                            String unit = prodListDtlsJsonObj.getString("unit");
                            int plid= Integer.parseInt(prodListDtlsJsonObj.getString("plid"));

                            BuySellInfoDialog buySellInfoDialog =new BuySellInfoDialog(getContext(), BuyOrSellFrag.this) {
                                @Override
                                void subBtnClicked() {
                                    addBuySellListEach(this, vgLlHm);
                                }
                            };
                            buySellInfoDialog.setData(plid, prodName, prodQuantity,boxQuantity, amount, unit);
                            addBuySellListEach(buySellInfoDialog, vgLlHm);
                        }
                        haveToPayEt.setText(billJsonObj.getString("haveToPay"));
                        payments=new JSONArray(billJsonObj.getString("paymentDtls"));
//                        for(int i=0; i<payments.length(); i++){
//                            String amount = "";
//                            try{
//                                amount  =  payments.getJSONObject(i).getString("amount");
//                            } catch (Exception e) {
//                                Toast.makeText(context, "eror998676", Toast.LENGTH_SHORT).show();
//                            }
//                            Toast.makeText(context, amount, Toast.LENGTH_SHORT).show();
//                        }
                        paymentsToSend=new JSONArray(payments.toString());

                        int day=0 ,month=0, year=0;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            LocalDate currentDate = LocalDate.parse(dateFormat.format(date));
                            day = currentDate.getDayOfMonth();
                            month = currentDate.getMonthValue();
                            year = currentDate.getYear();
                        }

                        JSONObject eachPaymentJsonObj= new JSONObject();
                        eachPaymentJsonObj.put("id", "");
                        eachPaymentJsonObj.put("dateOfPayment", year + "-" + month + "-" + day);
                        eachPaymentJsonObj.put("amount", "0");
                        paymentsToSend.put(eachPaymentJsonObj);

                        setDataToPaidTxt();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "JSON error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            paymentsToSend = payments;
        }
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(showBuySellInMemClickedBool) {
            showBuySellInMemClickedBool=false;
        }else {
            removeAndAddBuySellFragInMem();
        }
        //Toast.makeText(context, "destroyed", Toast.LENGTH_SHORT).show();
    }

    private void createAndSavePdf(View view) {
        int pageWidth =0
                , pageHeight =0;
        Canvas canvas=null;
        PdfDocument pdfDocument=null;
        PdfDocument.Page page=null;
        try {
            // Create a PdfDocument
            pdfDocument = new PdfDocument();
            pageWidth = view.getWidth(); //1500
            pageHeight = view.getHeight(); // 2750;
            // Define the page size
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();

            // Start a page
            page = pdfDocument.startPage(pageInfo);
            canvas = page.getCanvas();

            // Draw something on the page
//        Paint paint = new Paint();
//        paint.setTextSize(16);
        } catch (Exception e) {
            Toast.makeText(getContext(), Integer.toString(pageHeight) + Integer.toString(pageWidth), Toast.LENGTH_SHORT).show();
        }
        try {
            Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);

            Canvas canvass = new Canvas(bitmap);
            view.draw(canvass);
            Bitmap scaledBitmap = scaleBitmapToFit(bitmap, pageWidth, pageHeight);
            canvas.drawBitmap(bitmap, 0, 0, null);
            //canvas.drawText("Hello, this is a sample PDF!", 10, 25, paint);

            // Finish the page
            pdfDocument.finishPage(page);
        }catch (Exception e){
            Toast.makeText(getContext() , "error 593278", Toast.LENGTH_SHORT).show();
        }

        // Save the PDF
        try {
            File pdfFile;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Scoped Storage for Android 10+
                ContentValues values = new ContentValues();
                values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, "a.pdf");
                values.put(MediaStore.Files.FileColumns.MIME_TYPE, "application/pdf");
                values.put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                Uri uri = mainActivity.getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
                if (uri != null) {
                    try (OutputStream outputStream = mainActivity.getContentResolver().openOutputStream(uri)) {
                        pdfDocument.writeTo(outputStream);
                    }
                }
                pdfFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "a.pdf");
            } else {
                // Legacy storage
                pdfFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "a.pdf");
                pdfDocument.writeTo(new FileOutputStream(pdfFile));
            }

            Toast.makeText(getContext(), "PDF saved to Downloads", Toast.LENGTH_SHORT).show();

            // Open the PDF
            //    openPdf(this, pdfFile);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error saving PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(getContext(), "error205250", Toast.LENGTH_SHORT).show();
        } finally{
            pdfDocument.close();
        }
    }
    private Bitmap scaleBitmapToFit(Bitmap bitmap, int width, int height) {
        // Calculate the scaling factor
        float aspectRatio = (float) bitmap.getWidth() / bitmap.getHeight();
        int scaledWidth = width;
        int scaledHeight = (int) (width / aspectRatio);

        if (scaledHeight > height) {
            // Adjust width and height to fit within the page
            scaledHeight = height;
            scaledWidth = (int) (height * aspectRatio);
        }

        // Scale the bitmap
        return Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
    }
    private void removeAndAddBuySellFragInMem(){
        Iterator iterator = dataToSent.dataToSentBuySellFragArrayList.iterator();
        //Toast.makeText(context, "entered in method", Toast.LENGTH_SHORT).show();
        int count =0;
        while (iterator.hasNext()){
            //Toast.makeText(context, "iterator has value", Toast.LENGTH_SHORT).show();
            if(((DataToSentBuySellFrag)iterator.next()).buyOrSellFrag==BuyOrSellFrag.this){
                //Toast.makeText(context, "BuysellFrag deleted ", Toast.LENGTH_SHORT).show();
                dataToSent.dataToSentBuySellFragArrayList.remove(
                        dataToSent.dataToSentBuySellFragArrayList.get(count)
                        //BuyOrSellFrag.this
                );
                break;
            }
            count++;
            //Log.d("kkkk", iterator.next().toString());
        }
        dataToSent.dataToSentBuySellFragArrayList.addFirst(new DataToSentBuySellFrag() {
            @Override
            void createConstructor() {
                // take screenshot
                bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                view.draw(canvas);
                buyOrSellFrag = BuyOrSellFrag.this;
            }
        });
    }
    private void setDataToPaidTxt(){
        Double totalPaid=0d;
        for (int i=0; i<payments.length(); i++){
            JSONObject eachPaymentJsonObj = null;
            try {
                eachPaymentJsonObj = new JSONObject(payments.getString(i));
                totalPaid+=eachPaymentJsonObj.getDouble("amount");
            } catch (JSONException e) {
                Toast.makeText(context, "error 242898654", Toast.LENGTH_SHORT).show();
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
            //if(vgLlHm.containsValue(buySellInfoDialog)) {
                for (View key : vgLlHm.keySet()) {
                    if (vgLlHm.get(key) == buySellInfoDialog) {
                        buyListEach = key;
                        break;
                    }
                }
//            }else{
//                Toast.makeText(context, "buyInfoDialog not found in vgLlHm", Toast.LENGTH_SHORT).show();
//            }
        }

        //buyListEach.setBackgroundColor(R.color.white);
        TextView idTxt = (TextView) buyListEach.findViewById(R.id.idTxt);
        TextView prodNameTxt = (TextView) buyListEach.findViewById(R.id.prodNameTxt);
        TextView prodCountTxt = (TextView) buyListEach.findViewById(R.id.prodCountTxt);
        TextView prodAmountTxt = (TextView) buyListEach.findViewById(R.id.prodAmountTxt);
        TextView boxCountTxt = (TextView) buyListEach.findViewById(R.id.boxCountTxt);
        TextView boxPriceTxt = (TextView) buyListEach.findViewById(R.id.boxPriceTxt);
        TextView itemPerBoxTxt = (TextView) buyListEach.findViewById(R.id.itemPerBoxTxt);
        TextView totalAmountTxt = (TextView) buyListEach.findViewById(R.id.totalAmountTxt);
        TextView unitTxt = (TextView) buyListEach.findViewById(R.id.unitTxt);

        idTxt.setText(buySellInfoDialog.idTxt.getText());
        prodNameTxt.setText(buySellInfoDialog.chooseProdTxt.getText());
        prodCountTxt.setText(buySellInfoDialog.chooseProdDiEtArr[0].getText());
        prodAmountTxt.setText(buySellInfoDialog.chooseProdDiEtArr[1].getText());
        boxCountTxt.setText(buySellInfoDialog.chooseProdDiEtArr[2].getText());
        boxPriceTxt.setText(buySellInfoDialog.chooseProdDiEtArr[3].getText());
        itemPerBoxTxt.setText(buySellInfoDialog.chooseProdDiEtArr[4].getText());
        totalAmountTxt.setText(buySellInfoDialog.chooseProdDiEtArr[5].getText());
        unitTxt.setText(buySellInfoDialog.unitEt.getText());

        buyListEach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vgLlHm.get(v).buyInfoDialogue.show();
            }
        });

        Double totalAmount= 0d;
        for (View key : vgLlHm.keySet()) {
            totalAmount+=Double.parseDouble(((TextView)key.findViewById(R.id.totalAmountTxt)).getText().toString());
        }
        ((TextView) view.findViewById(R.id.totalAmounttTxt)).setText(Double.toString(totalAmount));

        buySellInfoDialog.buyInfoDialogue.dismiss();
    }
    private void openPdf(Context context, File file) {
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "No PDF viewer found", Toast.LENGTH_SHORT).show();
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                createAndSavePdf(view);
//            } else {
//                Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
}
