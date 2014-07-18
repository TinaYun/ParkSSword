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

public class ForgetPassword extends ActionBarActivity {

    SqliteHelper sh = new SqliteHelper(ForgetPassword.this);
    Handler handler = null;//注意，要在onCreate中初始化!
    ProgressDialog prodialog = null;
    String content = null;
    boolean ProcessRunning = false;
    String Email="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        handler=new Handler();
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

    public void Button_PWD_Forget(View view) {

        EditText et = (EditText)findViewById(R.id.TextForgetEmail);
        Email = et.getText().toString();

        if(Email.trim().equals("")){
            Toast.makeText(getApplicationContext(),"请输入注册邮箱",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!ProcessRunning) {
            prodialog = new ProgressDialog(ForgetPassword.this);
            prodialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            prodialog.setMessage("正在提交请求...");
            prodialog.setIndeterminate(false);
            prodialog.setCancelable(false);
            prodialog.setButton(-1,"确定", new SureButtonListener());
            prodialog.show();//显示对话框
            new Thread() {
                public void run() {

                    String urlsource = null;

                    try {
                        ProcessRunning = true;

                        URL url = new URL("http://pss.codeeer.com/GetbackAccount.php?Email=" + Email.trim());
                        urlsource = getURLSource(url);

                        if(urlsource.contains("SUCC")){
                            content = "验证邮件已经成功发送至"+Email+"请在24小时内完成操作";
                        }
                        else if(urlsource.trim().contains("NOT Email!")){
                            content = "错误的邮箱格式";
                        }
                        else if(urlsource.trim().contains("Not Activeted!")){
                            content = "失败：邮箱尚未激活帐号，不能用于找回";
                        }
                        else if(urlsource.trim().contains("Not SignUp!")){
                            content = "失败：邮箱尚未绑定帐号，不能用于找回";
                        }
                        else if(urlsource.trim().contains("Failed!")){
                            content = "内部错误";
                        }
                        else{
                            content = "未知错误";
                            Log.e("未知错误", "Source:" + urlsource);
                        }
                        handler.post(runnableUi);


                        prodialog.cancel();

                    } catch (Exception e) {
                        content = "失败：" + e.toString();
                        handler.post(runnableUi);
                        Log.e("synchro", "Source:" + urlsource + " Err:" + e.toString());
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

            if(content.contains("成功")||content.contains("失败")||content.contains("错误")){
                Toast.makeText(getBaseContext(), content, Toast.LENGTH_LONG).show();
                prodialog.cancel();
                ProcessRunning=false;
                finish();
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
