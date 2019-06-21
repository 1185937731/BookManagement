package com.ccnu.bookmanagement.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ccnu.bookmanagement.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn_login = (Button)this.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
    }

//    private void initView() {
//        et_username = (EditText) this.findViewById(R.id.et_username);
//        et_pwd = (EditText) this.findViewById(R.id.et_pwd);
//        tv_admin_register = (TextView) this.findViewById(R.id.tv_admin_register);
//        tv_register = (TextView) this.findViewById(R.id.tv_register);
//        btn_login = (Button) this.findViewById(R.id.btn_login);
//        UserInfo userInfo = UserManage.getInstance().getUserInfo(LoginActivity.this);
//        et_username.setText(userInfo.getUserName());
//        et_pwd.setText(userInfo.getPassword());
//        //有用户和密码
//        if (UserManage.getInstance().hasUserInfo(LoginActivity.this)) {
//            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        }
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                System.out.println("登录");
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }
}
