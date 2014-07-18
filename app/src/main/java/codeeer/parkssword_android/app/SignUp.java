package codeeer.parkssword_android.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URL;

import codeeer.parkssword_android.app.R;

import static codeeer.parkssword_android.app.Functions.PasswordFormat;
import static codeeer.parkssword_android.app.HtmlRequest.getURLSource;

public class SignUp extends ActionBarActivity {

    SqliteHelper sh = new SqliteHelper(SignUp.this);
    Handler handler = null;//注意，要在onCreate中初始化!
    ProgressDialog prodialog = null;
    String content = null;
    boolean ProcessRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        handler=new Handler();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    public void Button_SignUp(View view) {
        EditText et_email = (EditText) findViewById(R.id.Text_Input_Email);
        EditText et_pwd = (EditText) findViewById(R.id.Text_Input_PassWord);
        EditText et_pwd2 = (EditText) findViewById(R.id.Text_Input_PassWord2);

        if(et_email.getText().toString().equals("")||et_pwd.getText().toString().equals("")||et_pwd2.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "邮箱或密码不能为空", Toast.LENGTH_SHORT).show();
        }
        else if(et_pwd.getText().toString().equals(et_pwd2.getText().toString())){
            //提交注册
            SubmitSignUp(et_email.getText().toString(),et_pwd2.getText().toString());
        }
        else{
            Toast.makeText(getApplicationContext(),"两次输入的密码不一致",Toast.LENGTH_SHORT).show();
        }
    }

    //提交注册请求
    public void SubmitSignUp(final String Email, final String PassWord){

        if(!ProcessRunning) {
            prodialog = new ProgressDialog(SignUp.this);
            prodialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            prodialog.setMessage("正在连接服务器...");
            prodialog.setIndeterminate(false);
            prodialog.setCancelable(false);
            prodialog.setButton(-1,"确定", new SureButtonListener());
            prodialog.show();//显示对话框
            new Thread() {
                public void run() {

                    String urlsource = null;

                    try {
                        ProcessRunning = true;

                        content = "正在注册帐号...";
                        handler.post(runnableUi);

                        URL url = new URL("http://parkssword.sinaapp.com/SignUp.php?Email=" + Email.trim() + "&PassWord=" + PasswordFormat(PassWord));
                        urlsource = getURLSource(url);

                        if(urlsource.indexOf("SUCC")!=-1){
                            content = "注册成功。\n验证邮件已经发送至"+Email+"\n请在24小时内完成激活";
                            finish();
                        }
                        else if(urlsource.trim().contains("NOT Email!")){
                            content = "错误的邮箱格式";
                        }
                        else if(urlsource.trim().contains("IP Limited!")){
                            content = "注册失败：您的IP操作太频繁,请稍后再试";
                        }
                        else if(urlsource.trim().contains("Exist Email!")){
                            content = "注册失败：该邮箱已存在，请更换";
                        }
                        else if(urlsource.trim().contains("IP Limited!")){
                            content = "注册失败：您的IP操作太频繁,请稍后再试";
                        }
                        else if(urlsource.trim().contains("Failed!")){
                            content = "内部错误";
                        }
                        else{
                            content = "未知错误";
                        }
                        handler.post(runnableUi);


                        prodialog.cancel();

                    } catch (Exception e) {
                        content = "失败：" + e.toString();
                        handler.post(runnableUi);
                        Log.e("synchro", "Source:"+urlsource+" Err:"+e.toString());
                    }
                }
            }.start();
        }
        else{
            prodialog.show();
        }
    }

    //构建Runnable对象，在runnable中更新界面
    Runnable runnableUi = new Runnable(){
        @Override
        public void run() {

            if (content.contains("用户名或密码错误")) {
                try {
                    HandleDB.Account.Logout(sh);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if(content.contains("成功")||content.contains("失败")||content.contains("错误")){
                Toast.makeText(getBaseContext(),content,Toast.LENGTH_LONG).show();
                prodialog.cancel();
                ProcessRunning=false;
            }
            else{
                prodialog.setMessage(content);
            }
        }
    };

    private class SureButtonListener implements android.content.DialogInterface.OnClickListener{

        public void onClick(DialogInterface dialog, int which) {
            //关闭对话框
            dialog.dismiss();
        }
    }
}
