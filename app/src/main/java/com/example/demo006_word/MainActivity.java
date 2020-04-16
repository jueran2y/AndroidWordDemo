package com.example.demo006_word;

import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {
    private NavController controller;

    /*
    * Android学习的一个Demo 运用了Android3.6一些新特性
    * Room RecyclerView Fragment Navigation
    *
    *
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        controller = Navigation.findNavController(findViewById(R.id.fragment));
        NavigationUI.setupActionBarWithNavController(this, controller);
    }

    @Override
    public boolean onSupportNavigateUp() {
        controller.navigateUp();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(findViewById(R.id.fragment).getWindowToken(), 0);
        return super.onSupportNavigateUp();
    }
}
