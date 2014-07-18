package codeeer.parkssword_android.app;

import android.accounts.Account;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static codeeer.parkssword_android.app.Functions.PasswordFormat;
import static codeeer.parkssword_android.app.HtmlRequest.getURLSource;

public class Login extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(getCurrentFocus()!=null && getCurrentFocus().getWindowToken()!=null){
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }

    public void submit_Password(View view) throws Exception {
        EditText ET;

        ET = (EditText) findViewById(R.id.UserName);
        MainActivity.UserName = ET.getText().toString();

        ET = (EditText) findViewById(R.id.PassWord);
        MainActivity.PassWord = PasswordFormat(ET.getText().toString());

        if(MainActivity.UserName.equals("") || MainActivity.PassWord.equals("")){
            Toast.makeText(getBaseContext(),"用户名或密码不能为空",Toast.LENGTH_LONG).show();
        }
        else {
            setResult(RESULT_OK);
            finish();
        }
    }

    public void Button_SignUp_Pre(View view) {
        Intent intent = new Intent();
        intent.setClass(Login.this,SignUp.class);
        startActivityForResult(intent, 0);
    }

    public void forget_Password(View view) {
        Intent intent = new Intent();
        intent.setClass(Login.this,ForgetPassword.class);
        startActivity(intent);
    }
}
