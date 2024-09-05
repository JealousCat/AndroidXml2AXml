package com.cat.xml;

import android.PrintStack;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.xml2axml.Loader;
import android.xml2axml.util.FileUtils;
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
    public int checkedID = R.id.XMLView;

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
        EditText editText = (EditText) findViewById(R.id.edit);
        Button bt = (Button) findViewById(R.id.run);
        RadioGroup xml = (RadioGroup) findViewById(R.id.xmlGroup);

        xml.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
              checkedID = checkedId;
            }
        });

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = editText.getText().toString();
                try {
                    if(checkedID==R.id.XMLView) {
                        HashMap<String, View> ids = new HashMap<String, View>();
                        View view = Loader.loadXmlView(instance, path, ids);
                        showAlert(view, "View预览");
                        String sb = "解析为布局：" + view + "\n\n" + "ID表" + ids;

                        ((TextView) findViewById(R.id.results)).setText(sb);
                    } else if (checkedID==R.id.XMLVector) {
                        Drawable drawable = Loader.loadXmlDrawable(instance, path);
                        View view = LayoutInflater.from(instance).inflate(R.layout.image_dialog,(ViewGroup) null);
                        view.setBackground(drawable);
                        showAlert(view, "Drawable预览");
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    String err = String.valueOf(new PrintStack(e));
                    if(FileUtils.error!=null){
                        err+="\n\n"+ new PrintStack(FileUtils.error);
                    }
                    TextView txt = new TextView(instance);
                    txt.setText(err);
                    showAlert(txt, "错误！");

                    ((TextView) findViewById(R.id.results)).setText(err);

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

    public void showAlert(View view, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(instance);
        builder.setView(view).setTitle(title).setNegativeButton("取消",null).create().show();
    }
}