package com.cat.xml;

import android.app.AlertDialog;
import android.xml2axml.Layout;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    public Context instance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        applyForMultiplePermissions();

        instance = this;
        EditText txt = (EditText) findViewById(R.id.edit);
        Button bt = (Button) findViewById(R.id.run);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = txt.getText().toString();
                try {
                    HashMap<String,View> ids = new HashMap<String,View>();
                    View view = Layout.loadXml(instance,path,ids);
                    StringBuilder sb = new StringBuilder("解析为布局：" + view + "\n");
                    AlertDialog.Builder builder = new AlertDialog.Builder(instance);
                    builder.setView(view).setTitle("布局预览").create().show();
                    sb.append("\n");
                    sb.append("ID表").append(ids);
                    ((TextView)findViewById(R.id.results)).setText(sb.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private final String[] PM_MULTIPLE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.WRITE_MEDIA_STORAGE",
            "android.permission.FOREGROUND_SERVICE",
            "android.permission.SYSTEM_ALERT_WINDOW",
    };

    //申请多个权限
    public void applyForMultiplePermissions() {
        try {
            //如果操作系统SDK级别在23之上（android6.0），就进行动态权限申请
            if (Build.VERSION.SDK_INT >= 23) {
                ArrayList<String> pmList = new ArrayList<>();
                //获取当前未授权的权限列表
                for (String permission : PM_MULTIPLE) {
                    int nRet = ContextCompat.checkSelfPermission(this, permission);
                    if (nRet != PackageManager.PERMISSION_GRANTED) {
                        pmList.add(permission);
                    }
                }

                if (!pmList.isEmpty()) {
                    String[] sList = pmList.toArray(new String[0]);
                    ActivityCompat.requestPermissions(this, sList, 10000);
                } else {
                    showToast("全部权限都已授权");
                }
            }

        } catch (Exception ignored) {
        }
    }

    public void showToast(CharSequence csq) {
        Toast.makeText(this.getApplicationContext(), csq, Toast.LENGTH_SHORT).show();
    }
}