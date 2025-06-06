package com.google.sayanbanik1997.shop;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    NavigationView navView;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    DataToSent dataToSent= new DataToSent();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbar=(androidx.appcompat.widget.Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        drawerLayout=(DrawerLayout) findViewById(R.id.main);
        actionBarDrawerToggle= new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navDrClose, R.string.navDrOpen);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navView=(com.google.android.material.navigation.NavigationView) findViewById(R.id.navView);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.buyProdNavMenu) getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new BuyOrSellFrag("Supplier", 0, dataToSent, MainActivity.this)).commit();
                if(item.getItemId()==R.id.sellProdNavMenu) getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new BuyOrSellFrag("Customer", 0, dataToSent, MainActivity.this)).commit();
                if(item.getItemId()==R.id.billPaymentNavMenu) getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new paymentsFrag(dataToSent, MainActivity.this)).commit();
                if(item.getItemId()==R.id.supplierNavMenu) getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new SupCusFrag("Supplier")).commit();
                if(item.getItemId()==R.id.customerNavMenu) getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new SupCusFrag("Customer")).commit();
                drawerLayout.closeDrawer(GravityCompat.START);
                return false;
            }
        });
        getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, new BuyOrSellFrag("Supplier", 0, dataToSent, MainActivity.this)).commit();
    }
}