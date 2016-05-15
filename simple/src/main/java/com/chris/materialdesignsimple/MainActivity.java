package com.chris.materialdesignsimple;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chris.materialdesignlibrary.ui.MaterialTextEdit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MaterialTextEdit materialTextEdit;
    private EditText editText;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initEvent();

    }

    public void initView() {
        materialTextEdit = (MaterialTextEdit) findViewById(R.id.material_et);
        submitButton = (Button) findViewById(R.id.submit_bt);
    }

    public void initEvent() {
        submitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_bt:
                String value = materialTextEdit.getText().toString();
                if(TextUtils.isEmpty(value)) {
                    materialTextEdit.setError("User name can not empty.");
                }else {
                    materialTextEdit.setError(null);
                    Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }
}
