package codeeer.parkssword_android.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.lang.reflect.Field;
import java.net.URL;
import static codeeer.parkssword_android.app.Functions.PasswordFormat;
import static codeeer.parkssword_android.app.HtmlRequest.Source2Sqlite;
import static codeeer.parkssword_android.app.HtmlRequest.getURLSource;

public class MainActivity extends Activity implements TextWatcher {

    public static String UserName;
    public static String PassWord;

    SqliteHelper sh = new SqliteHelper(MainActivity.this);
    Handler handler = null;
    ProgressDialog prodialog = null;
    String content = null;
    boolean ProcessRunning = false;
    Vibrator vibrator;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onClickResult(View view){

        Functions.LoadBaseInfo(sh);
        final AutoCompleteTextView textInput = (AutoCompleteTextView) findViewById(R.id.pssInput);

        //计算密码
        String Input = textInput.getText().toString().trim();
        String calcResult = Functions.CreatCore(Input);//接收计算结果

        //复制结果
        ClipboardManager clipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        ClipData textCd = ClipData.newPlainText("PSS", calcResult);
        clipboard.setPrimaryClip(textCd);

        //记录到数据库
        HandleDB.Record.Add(sh,Input);

        //生成对话框
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.abc_ic_go)
                .setTitle("密码已经复制")
                .setMessage(calcResult)
                .setPositiveButton("返回", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //
                    }
                })
                .setNeutralButton("清空", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                textInput.setText("");
            }
        })
                .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                }).show();
    }

    private AutoCompleteTextView autoview;
    private AutoTextViewAdapter adapter;
    private final static String[] dominEnum = new String[]{".com", ".com.cn", ".cn", ".net" , ".org"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //创建属于主线程的handler
        handler=new Handler();

        //振动
        vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);

        //设置溢出菜单
        try {
            ViewConfiguration mconfig = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(mconfig, false);
            }
        } catch (Exception ignored) {
        }

        //设置默认英文键盘
        EditText et = (EditText) findViewById(R.id.pssInput);
        et.setInputType(InputType.TYPE_TEXT_VARIATION_URI);

        //以findViewById()取得AutoCompleteTextView对象
        autoview = (AutoCompleteTextView) findViewById(R.id.pssInput);
        adapter = new AutoTextViewAdapter(this);
        autoview.setAdapter(adapter);
        autoview.setThreshold(1);//输入1个字符时就开始检测，默认为2个
        autoview.addTextChangedListener(this);//监听autoview的变化

        //绑定ListView菜单并加载数据
        final ListView lst = (ListView) findViewById(R.id.listHistory);
        lst.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, HandleDB.Record.Read(sh)));
        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                AutoCompleteTextView textInput = (AutoCompleteTextView) findViewById(R.id.pssInput);
                textInput.setText(lst.getItemAtPosition(arg2).toString());

                //收回输入法
                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(getCurrentFocus()!=null && getCurrentFocus().getWindowToken()!=null){
                    manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
        lst.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id) {

                //TODO：长按复制密码 -> 进入设置
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,ManualPassword.class);
                Bundle bundle = new Bundle();
                bundle.putString("domin", lst.getItemAtPosition(position).toString());
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
                vibrator.vibrate(10);

                /*
                Functions.LoadBaseInfo(sh);

                //计算密码
                String Input = lst.getItemAtPosition(position).toString();
                String calcResult = Functions.CreatCore(Input);//接收计算结果

                //复制结果
                ClipboardManager clipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                ClipData textCd = ClipData.newPlainText("PSS", calcResult);
                clipboard.setPrimaryClip(textCd);
                Toast.makeText(getApplicationContext(),"已复制"+Input+"的密码",Toast.LENGTH_SHORT).show();
                vibrator.vibrate(500);

                finish();

                //记录到数据库
                HandleDB.Record.Add(sh,Input);

                */
                return false;
            }

        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        SQLiteDatabase db = sh.getWritableDatabase();

        switch (item.getItemId()) {
            case R.id.action_synchrodata:
                DoSynchroData();
            break;
            case R.id.action_cleanup:
                db.execSQL("DELETE FROM Recent");
                ListView lst = (ListView) findViewById(R.id.listHistory);
                lst.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, HandleDB.Record.Read(sh)));
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) { //resultCode为回传的标记，回传的是RESULT_OK
            case RESULT_OK:
                DoSynchroData();
                break;
            default:
                break;
        }
    }

    //执行同步
    public void DoSynchroData(){

        if(!ProcessRunning) {

            if(UserName==null)UserName= HandleDB.Account.Get.UserName(sh);
            if(PassWord==null)PassWord=HandleDB.Account.Get.Password(sh, HandleDB.Account.PassWordType.LoginAccount);

            if(PassWord.equals(SqliteHelper.LogoutPassword)||UserName.equals("")||PassWord.equals("")){
                //提示输入密码
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,Login.class);
                startActivityForResult(intent, 0);
            }
            else {
                prodialog = new ProgressDialog(MainActivity.this);
                prodialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                prodialog.setMessage("正在连接服务器...");
                prodialog.setIndeterminate(false);
                prodialog.setCancelable(false);
                prodialog.setButton(-1,"确定", new SureButtonListener());
                prodialog.show();//显示对话框
                new Thread() {
                    public void run() {
                        try {
                            ProcessRunning = true;

                            String PhoneID = android.provider.Settings.System.getString(getContentResolver(), "android_id");

                            content = "正在登录服务器...";
                            handler.post(runnableUi);

                            URL url = new URL("http://pss.codeeer.com/Xml2Sql.php?Email=" + UserName + "&PassWord=" + PassWord + "&PhoneID=" + PhoneID);
                            String urlsource = getURLSource(url);

                            content = "正在处理数据...";
                            handler.post(runnableUi);

                            content = Source2Sqlite(urlsource, sh);
                            handler.post(runnableUi);

                            prodialog.cancel();

                        } catch (Exception e) {
                            content = "数据加载失败";
                            handler.post(runnableUi);
                            Log.e("synchro", e.toString());
                        }
                    }
                }.start();
            }
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
                PassWord = null;
            }

            if(content.contains("成功")||content.contains("失败")||content.contains("错误")){
                Toast.makeText(getBaseContext(),content,Toast.LENGTH_LONG).show();
                prodialog.cancel();
                ProcessRunning=false;
                if(content.contains("成功")){
                    //设置登录密码
                    try {
                        if(HandleDB.Account.Get.Password(new SqliteHelper(getBaseContext()), HandleDB.Account.PassWordType.LoginApplication).equals(PasswordFormat(SqliteHelper.LogoutPassword))) {
                            Intent intent = new Intent();
                            intent.setClass(MainActivity.this, AppLoginPWDSetting.class);
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            else{
                prodialog.setMessage(content);
            }
        }
    };

    //注销账号
    public void Logoff(MenuItem item) throws Exception {
        HandleDB.Account.Logout(sh);
        Toast.makeText(getBaseContext(),"注销成功",Toast.LENGTH_LONG).show();
    }

    private class SureButtonListener implements android.content.DialogInterface.OnClickListener{

        public void onClick(DialogInterface dialog, int which) {
            //关闭对话框
            dialog.dismiss();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String input = editable.toString();
        adapter.mList.clear();
        if(!input.contains(".")) {
            autoAddDomins(input);
            adapter.notifyDataSetChanged();
            autoview.showDropDown();
        }
    }

    /**
     * 自动填充邮箱列表
     */
    private void autoAddDomins(String input) {
        input=input.trim();
        if (input.length() > 0) {
            for (String DominEnum : dominEnum) {
                adapter.mList.add(input + DominEnum);
            }
        }
    }
}

