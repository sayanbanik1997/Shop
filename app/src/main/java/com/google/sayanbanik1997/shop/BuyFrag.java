package com.google.sayanbanik1997.shop;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
            @Override
            public void onClick(View v) {
                Dialog buyInfoDialogue=new Dialog(getContext());
                buyInfoDialogue.setContentView(R.layout.prod_list_entry);
                buyInfoDialogue.show();

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
                            //prodInfoDialogInfo(e, chooseProdDiEt);
                        }
                    });
                    chooseProdDiEt[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            //Toast.makeText(getContext(), Boolean.toString(v.hasFocus()), Toast.LENGTH_SHORT).show();
                            prodInfoDialogInfo((EditText) v, chooseProdDiEt);
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

    protected  void prodInfoDialogInfo(EditText thisEditText, EditText[] editTextArr){
        float prodCountFloat=0,pricePerProdFloat=0,boxQuanFloat=0,boxPriceFloat=0,itemPerBoxFloat=0, totalAmountFloat=0;
        if(!editTextArr[0].getText().toString().isEmpty()){
            prodCountFloat=Float.parseFloat( editTextArr[0].getText().toString());
        } if (!editTextArr[1].getText().toString().isEmpty()){
            pricePerProdFloat=Float.parseFloat( editTextArr[1].getText().toString());
        } if (!editTextArr[2].getText().toString().isEmpty()){
            boxQuanFloat=Float.parseFloat( editTextArr[2].getText().toString());
        } if (!editTextArr[3].getText().toString().isEmpty()){
            boxPriceFloat=Float.parseFloat( editTextArr[3].getText().toString());
        } if (!editTextArr[4].getText().toString().isEmpty()) {
            itemPerBoxFloat = Float.parseFloat(editTextArr[4].getText().toString());
        }if (!editTextArr[5].getText().toString().isEmpty()) {
            totalAmountFloat = Float.parseFloat(editTextArr[5].getText().toString());
        }
        if(thisEditText == editTextArr[0] ){
            if(!editTextArr[0].getText().toString().isEmpty()) {
                if (pricePerProdFloat!=0) {
                    editTextArr[5].setText(Float.toString(prodCountFloat * pricePerProdFloat));
                }else if (!editTextArr[5].getText().toString().isEmpty()) {
                    editTextArr[1].setText(Float.toString(totalAmountFloat / prodCountFloat));
                }
                if (!editTextArr[2].getText().toString().isEmpty()) {
                    editTextArr[4].setText(Float.toString(prodCountFloat / boxQuanFloat));
                }else if (!editTextArr[4].getText().toString().isEmpty()) {
                    editTextArr[2].setText(Float.toString(prodCountFloat / pricePerProdFloat));
                }
            }else{
//                editTextArr[5].setText("");
//                editTextArr[4].setText("");
//                editTextArr[2].setText("");
//                editTextArr[1].setText("");
            }
        }
        Toast.makeText(getContext(), thisEditText.getText().toString(), Toast.LENGTH_SHORT).show();
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