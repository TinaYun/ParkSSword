package codeeer.parkssword_android.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import static codeeer.parkssword_android.app.Functions.PasswordFormat;

public class AppLoginPWD extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_login_pwd);

        try {
            if(HandleDB.Account.Get.Password(new SqliteHelper(getBaseContext()), HandleDB.Account.PassWordType.LoginApplication).equals(PasswordFormat(SqliteHelper.LogoutPassword))){
                Intent intent = new Intent();
                intent.setClass(AppLoginPWD.this,MainActivity.class);
                startActivity(intent);
                finish();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    public void Button_Exit(View view) {
        finish();
    }

    public void Button_LoginCheck(View view) throws Exception {
        EditText et = (EditText)findViewById(R.id.TextLoginCheck);
        if(HandleDB.Account.Get.Password(new SqliteHelper(getBaseContext()), HandleDB.Account.PassWordType.LoginApplication).equals(PasswordFormat(et.getText().toString()))){
            Intent intent = new Intent();
            intent.setClass(AppLoginPWD.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            et.setText("");
            Toast.makeText(getBaseContext(),"密码错误",Toast.LENGTH_SHORT).show();
        }
    }

    public void Button_LogOut(View view) throws Exception {
        HandleDB.Account.Logout(new SqliteHelper(getBaseContext()));
        finish();
    }
}
