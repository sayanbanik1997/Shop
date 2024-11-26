package com.google.sayanbanik1997.shop;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
    ListView supplierList;
    TextView supNameTxt, byingDtTxt;
    Dialog addSupDialog;
    EditText supNameEt;
    Button addCrSupBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_buy, container, false);
        txt  = (TextView)view.findViewById(R.id.supNameTxt);
        byingDtTxt=(TextView) view.findViewById(R.id.byingDtTxt);
        supNameTxt=(TextView) view.findViewById(R.id.supNameTxt);
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), "clicked", Toast.LENGTH_SHORT).show();
                addSupDialog=new Dialog(getContext());
                addSupDialog.setContentView(R.layout.add_supplier);
                addSupDialog.show();
                String[] tag={"name"};
                String[] data={""};
                setData(tag, data);
                supNameEt=(EditText) addSupDialog.findViewById(R.id.supNameEt);
                //Button addSupBtn=(Button) addSupDialog.findViewById(R.id.addCrSupBtn);
                supplierList=(ListView)addSupDialog.findViewById(R.id.supList);
                addCrSupBtn=(Button) addSupDialog.findViewById(R.id.addCrSupBtn);

                supNameEt.addTextChangedListener(new TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {} @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
                     @Override
                    public void afterTextChanged(Editable s) {
                        String[] tag={"name"};
                         String[] data={supNameEt.getText().toString()};
                         setData(tag, data);
                    }
                });
                addCrSupBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(supNameEt.getText().toString().isEmpty()){
                            Toast.makeText(getContext(), "Supplier name can't be empty", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(addCrSupBtn.getText().toString().equalsIgnoreCase("Add Supplier")){
                            supNameTxt.setText(supNameEt.getText().toString());
                            addSupDialog.dismiss();
                            return;
                        }
                        String[] tag={"name"};
                        String[] data={supNameEt.getText().toString()};
                        new VolleyTakeData(getContext(), "http://192.168.111.212/me/createSup.php", tag, data, new AfterTakingData() {
                            @Override
                            public void doAfterTakingData(String response) {
                                if(response.equals("1")){
                                    supNameTxt.setText( supNameEt.getText().toString());
                                    addSupDialog.dismiss();;
                                }else {
                                    Toast.makeText(getContext(),"Error occurred "+ response, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
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
                    //Toast.makeText(getContext(), "clicked", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return view;
    }
    private void setData(String[] tag, String[] data){
        new VolleyTakeData(getContext(),"http://192.168.111.212/me/getSupplier.php", tag, data, new AfterTakingData() {
            @Override
            public void doAfterTakingData(String response) {
                ArrayList<String> rslt=new ArrayList<>();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject explrObject;
                    if(supNameEt.getText().toString().isEmpty()){
                        addCrSupBtn.setText("Suplier name can't be empty");
                    }else{
                        addCrSupBtn.setText("Create supplier and then add");
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        explrObject = jsonArray.getJSONObject(i);
                        rslt.add(explrObject.getString("name"));
                        if(//   (!addCrSupBtn.getText().equals("Add Supplier")) &&
                            explrObject.getString("name").equals(supNameEt.getText().toString())){
                            addCrSupBtn.setText("Add Supplier");
                        }
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), "error while parsing json", Toast.LENGTH_SHORT).show();
                }
                ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, rslt);
                supplierList.setAdapter(arrayAdapter);
                supplierList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        supNameEt.setText(rslt.get( position));
                    }
                });
            }
        });
    }
}