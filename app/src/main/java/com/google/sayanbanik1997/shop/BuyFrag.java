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
import android.widget.ImageView;
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
    //BuyInfoDialog buyInfoDialog;
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
                new CusSupProdDialogue(context, "Supplier") {
                    @Override
                    void doAfterBtnClicked() {
                        supNameTxt.setText(((EditText) dialog.findViewById(R.id.supNameEt)).getText().toString());
                    }
                };
            }
        });
        TextView soldUnsoldTxt = (TextView) view.findViewById(R.id.soldUnsoldTxt);
        soldUnsoldTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(soldUnsoldTxt.getText().toString().equals("Sold")) soldUnsoldTxt.setText("Unsold");
                else soldUnsoldTxt.setText("Sold");
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
                BuyInfoDialog buyInfoDialog = new BuyInfoDialog(getContext(), view, BuyFrag.this){
                    @Override
                    void subBtnClicked() {
                        submitBtnClicked( this, vgLlHm);
                    }
                };
            }
        });
        ((Button) view.findViewById(R.id.submitProdPurchaseBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(supNameTxt.getText().equals(getResources().getString(R.string.buyerNameBuyFrag))) {
                    Toast.makeText(getContext(), "Buyer's name can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(vgLlHm.size()==0) {
                    Toast.makeText(getContext(), "1st add transaction then submit", Toast.LENGTH_SHORT).show();
                    return;
                }
                int soldOrUnsold=2;
                if(soldUnsoldTxt.getText().toString().equals("Sold")) soldOrUnsold=1;
                String[] tags = {"name", "dateOfPurchase", "purOrSell", "soldOrUnsold", "paid", "due"}, data = {
                    supNameTxt.getText().toString(), byingDtTxt.getText().toString(), "1",
                    Integer.toString(soldOrUnsold), ((EditText) view.findViewById(R.id.paidEt)).getText().toString(),
                    ((TextView) view.findViewById(R.id.dueTxt)).getText().toString()
                };
                new VolleyTakeData(getContext(), baseUrl + "insertProdEntry.php", tags, data, new AfterTakingData() {
                    @Override
                    public void doAfterTakingData(String response) {
                        if (Integer.parseInt(response) > 0) {
                            JSONArray jsonArray = new JSONArray();

                            for (Map.Entry me : vgLlHm.entrySet()) {
                                View eachListItem = (View) me.getKey();
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    //Toast.makeText(getContext(), ((TextView) eachListItem.findViewById(R.id.prodCountTxt)).getText(), Toast.LENGTH_SHORT).show();
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
                            Log.d("kkkk", jsonArray.toString());
                            String[] tags = {"data"};
                            String[] data = {jsonArray.toString()};
                            new VolleyTakeData(getContext(), baseUrl + "insertProdList.php", tags, data, new AfterTakingData() {
                                @SuppressLint("ResourceType")
                                @Override
                                public void doAfterTakingData(String response) {
                                    if (Integer.parseInt(response) > 0) {
                                        Toast.makeText(getContext(), "Successfully inserted", Toast.LENGTH_SHORT).show();
                                        getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new BuyFrag()).commit();
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
        return view;
    }
    private void submitBtnClicked(BuyInfoDialog buyInfoDialog, HashMap<View, BuyInfoDialog> vgLlHm){
        View buyListEach= getLayoutInflater().inflate(R.layout.buy_list_each, null);
        if(!vgLlHm.containsValue(buyInfoDialog)) {
            LinearLayout buyFragLlforDealInfoInsert = (LinearLayout) view.findViewById(R.id.buyFragLlforDealInfoInsert);
            ImageView delImg= (ImageView) buyListEach.findViewById(R.id.delImg);
            final View buyListEachTemp= buyListEach;
            delImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buyFragLlforDealInfoInsert.removeView(buyListEachTemp);
                    vgLlHm.remove(buyListEachTemp);
                }
            });

            buyFragLlforDealInfoInsert.addView(buyListEach);
            vgLlHm.put(buyListEach, buyInfoDialog);
        }else{
            if(vgLlHm.containsValue(buyInfoDialog)) {
                for (View key : vgLlHm.keySet()) {
                    if (vgLlHm.get(key) == buyInfoDialog) {
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

}
