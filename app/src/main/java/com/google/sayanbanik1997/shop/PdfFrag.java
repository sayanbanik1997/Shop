package com.google.sayanbanik1997.shop;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class PdfFrag extends Fragment {
    BuyOrSellFrag buyOrSellFrag;
    RecyclerView pdfEntryRecyView;
    View[] eachListItemArr;
    PdfFrag(BuyOrSellFrag buyOrSellFrag){
        this.buyOrSellFrag=buyOrSellFrag;
    }

    //TextView dateTxt;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_pdf, container, false);
        new Thread() {
            public void run() {
                Looper.prepare();
                try {
                    Thread.sleep(100);
                    createAndSavePdf(view);
                } catch (Exception ex) {
                    Log.d("err", ex.getMessage().toString());
                }
            }
        }.start();
        ((TextView)view.findViewById(R.id.dateTxt)).setText(buyOrSellFrag.byingDtTxt.getText().toString());
        ((TextView)view.findViewById(R.id.billIdTxt)).setText(Integer.toString(buyOrSellFrag.billId));

        eachListItemArr = new View[buyOrSellFrag.vgLlHm.size()];
        int count=0;
        for (Map.Entry me : buyOrSellFrag.vgLlHm.entrySet()) {
            eachListItemArr[count] = (View) me.getKey();
            count++;
        }
        RecyAdapter recyAdapter  = new RecyAdapter( R.layout.each_entry_of_pdf, buyOrSellFrag.vgLlHm.size()){
            @Override
            void bind(Vh holder, int position) {
                    ((TextView) holder.arrView.get(0)).setText(((TextView) eachListItemArr[position].findViewById(R.id.prodNameTxt)).getText());
                    ((TextView) holder.arrView.get(1)).setText(((TextView) eachListItemArr[position].findViewById(R.id.prodCountTxt)).getText());
                    ((TextView) holder.arrView.get(2)).setText(((TextView) eachListItemArr[position].findViewById(R.id.unitTxt)).getText());
                    ((TextView) holder.arrView.get(3)).setText(((TextView) eachListItemArr[position].findViewById(R.id.totalAmountTxt)).getText());
            }
            @Override
            Vh onCreate(View view) {
                return new Vh(view) {
                    @Override
                    void initiateInsideViewHolder(View itemView) {
                        arrView.add(itemView.findViewById(R.id.prodNameTxt));
                        arrView.add(itemView.findViewById(R.id.quantityTxt));
                        arrView.add(itemView.findViewById(R.id.unitTxt));
                        arrView.add(itemView.findViewById(R.id.totalTxt));
                    }
                };
            }
        };
        pdfEntryRecyView = (RecyclerView)view.findViewById(R.id.pdfEntryRecyView);
        pdfEntryRecyView.setLayoutManager(new LinearLayoutManager(getContext()));
        pdfEntryRecyView.setAdapter(recyAdapter);


        //createAndSavePdf(view);
//        ((Button)view.findViewById(R.id.savePdfBtn)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                createAndSavePdf(view);
//            }
//        });
        return view;
    }

    //int PERMISSION_REQUEST_CODE=1;
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

                Uri uri = buyOrSellFrag.mainActivity.getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
                if (uri != null) {
                    try (OutputStream outputStream = buyOrSellFrag.mainActivity.getContentResolver().openOutputStream(uri)) {
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
}