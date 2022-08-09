package com.android.newhome;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.android.newhome.application.Application;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //隐藏标签栏
        getSupportActionBar().hide();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_map, R.id.navigation_add, R.id.navigation_message, R.id.navigation_myself)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        Application application = new Application();
    }

    protected long exitTime;
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(KeyEvent.KEYCODE_BACK == keyCode){
            if(System.currentTimeMillis() - exitTime > 2000){
                Toast.makeText(this, "再按一次退出程序！", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
                //这里如果不执行return，就会执行下面super.onKeyUp，导致Activity重新加载显示
                //底部导航栏状态会被重置为第一个默认选项卡
                return true;
            } else {
                finish();
                System.exit(0);
            }
        }
        return super.onKeyUp(keyCode, event);
    }
}