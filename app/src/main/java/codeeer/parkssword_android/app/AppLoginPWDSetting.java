package codeeer.parkssword_android.app;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import codeeer.parkssword_android.app.R;

import static codeeer.parkssword_android.app.Functions.PasswordFormat;

public class AppLoginPWDSetting extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_login_pwdsetting);
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

    public void Button_PWD_SetEmpty(View view) throws Exception {
        HandleDB.Account.Set.Password(new SqliteHelper(getBaseContext()), HandleDB.Account.PassWordType.LoginApplication,PasswordFormat(SqliteHelper.LogoutPassword));
        finish();
    }

    public void Button_PWD_Set(View view) throws Exception {
        EditText et1 = (EditText)findViewById(R.id.TextLoginSetting);
        EditText et2 = (EditText)findViewById(R.id.TextLoginSetting2);

        if(et1.getText().toString().equals(et2.getText().toString())){
            HandleDB.Account.Set.Password(new SqliteHelper(getBaseContext()), HandleDB.Account.PassWordType.LoginApplication,PasswordFormat(et2.getText().toString()));
            Toast.makeText(getBaseContext(),"密码设置成功",Toast.LENGTH_LONG).show();
            finish();
        }
        else
        {
            Toast.makeText(getBaseContext(),"两次输入的密码不一致",Toast.LENGTH_LONG).show();
        }
    }
}
