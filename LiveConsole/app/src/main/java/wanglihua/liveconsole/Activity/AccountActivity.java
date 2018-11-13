package wanglihua.liveconsole.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import wanglihua.liveconsole.Handle.LoginHandle;
import wanglihua.liveconsole.Model.LoginInfo;
import wanglihua.liveconsole.Model.TitleRepo;
import wanglihua.liveconsole.R;
import wanglihua.liveconsole.Utils.Utils;
import wanglihua.liveconsole.view.MessageDialog;

/**
 * Created by Administrator on 2018/4/2.
 */
public class AccountActivity extends Activity {

    private Context mContext;
    private AccountActivity mActivity;

    private LinearLayout mMainLay;
    private EditText mNameTxt, mPwdTxt;
    private String mName, mPwd;
    private LinearLayout mRemLay;
    private ImageView mRemBtn;
    private boolean isRemember =false;
    private TextView mRemTxt;
    private Button mLoginBtn;
    private int mScreenWidth = 0;
    private String  oldUserID = "";

    private ExecutorService executorService = null;
    private String result;
    private SharedPreferences sp = null;
    private SharedPreferences.Editor editor;
    private LoginInfo mLoginInfo;
    @SuppressLint("HandlerLeak")
    private  Handler loginHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.arg1) {
                case 1:

                    InputStream inStream = new ByteArrayInputStream(
                            result.getBytes());
                    LoginHandle xmlHandle = new LoginHandle(mContext);
                    mLoginInfo = xmlHandle.readXML(inStream);
                    if (mLoginInfo != null) {
                        editor = sp.edit();

                        if (isRemember) {
                            editor.putBoolean("isRemember", true);
                        } else {
                            editor.putBoolean("isRemember", false);
                        }

                        editor.putString("userID", "" + mLoginInfo.userID);
                        editor.putString("name", "" + mName);
                        editor.putString("pwd", "" + mPwd);
                        editor.putString("password", "" + mPwd + mLoginInfo.login_hash);
                        editor.commit();


                        if(!oldUserID.equals(mLoginInfo.userID)){
                            sp = mContext.getSharedPreferences("settings_info", 0);
                            editor = sp.edit();
                            editor.clear();
                            editor.commit();

                            sp = mContext.getSharedPreferences("push_state", 0);
                            editor = sp.edit();
                            editor.clear();
                            editor.commit();

                            TitleRepo  repo = new TitleRepo(mContext);
                            repo.delList();
                        }

                        Intent intent = new Intent(mActivity, MainActivity.class);
                        mActivity.startActivity(intent);
                        mActivity.finish();

                    } else {

                    }
                    MessageDialog.dismiss();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_account);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
    }

    private void init() {
        sp = getSharedPreferences("logininfo", 0);
        oldUserID = sp.getString("userID", "");
        mContext = this;
        mActivity = this;
        mScreenWidth = Utils.getWindowsWidth(mActivity);

        mMainLay =  findViewById(R.id.lay_main);
        Utils.setMargins(mMainLay,1,mScreenWidth,0,600,0,0);

        mNameTxt =  findViewById(R.id.login_username_txt);
        Utils.setSize(mNameTxt,1,mScreenWidth,500,88);

        mName = sp.getString("name", "");
        if (!mName.equals("")) {
            mNameTxt.setText(mName);
        }

        mPwdTxt = findViewById(R.id.login_userpassword_txt);
        Utils.setSize(mPwdTxt,1,mScreenWidth,500,88);

        mRemLay = findViewById(R.id.lay_remember);
        Utils.setSize(mRemLay,1,mScreenWidth,500,88);
        mRemLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRemember){
                    isRemember = false;
                    mRemBtn.setBackgroundResource(R.drawable.remember);
                }else{
                    isRemember = true;
                    mRemBtn.setBackgroundResource(R.drawable.remember_checked);
                }
            }
        });

        mRemBtn = findViewById(R.id.btn_remember);
        Utils.setSize(mRemBtn,1,mScreenWidth,60,60);

        mRemTxt = findViewById(R.id.txt_remember);
        Utils.setMargins(mRemTxt,1,mScreenWidth,10,0,0,0);
        mRemTxt.setTextSize(Utils.px2sp(mContext,30,mScreenWidth));
        mLoginBtn =  findViewById(R.id.login_btn);
        Utils.setSize(mLoginBtn,1,mScreenWidth,300,88);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mName = mNameTxt.getText().toString().trim();
                mPwd = Utils.Md5((mPwdTxt.getText().toString().trim()));
                login();
            }
        });


        if (sp.getBoolean("isAutoLogin", false)) {
            mName =  sp.getString("name","");
            mPwd = sp.getString("pwd","");
            login();
            return;
        }
    }

    private void login() {

//        Log.e("WLH", "mName = " + mName);
//        Log.e("WLH", "mPwd = " + mPwd);
        if (mName.equals("")) {
            Utils.AlertDialog("提示", "请输入账号!", mContext);
        } else if (mPwd.equals("")) {
            Utils.AlertDialog("提示", "请输入密码!", mContext);
        } else if (!mName.equals("") && !mPwd.equals("")) {
            if (Utils.isConnect(mContext)) {
                //prDialog.show();
                MessageDialog.show(mContext, "正在通信......");

                if (executorService == null) {
                    executorService = Executors.newCachedThreadPool();

                }

                executorService.submit(new Runnable() {
                    public void run() {
                        SoapObject so = new SoapObject("http://tempuri.org/",
                                "MiniMaster_User_Login");
                        so.addProperty("param", mName);
                        so.addProperty("password", mPwd);
                        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                                SoapEnvelope.VER12);
                        envelope.bodyOut = so;
                        envelope.dotNet = true;
                        HttpTransportSE ht = new HttpTransportSE(
                                "http://m1.lzgd.com.cn/test/report_WebService.asmx");
                        try {
                            ht.call(null, envelope);
                            result = envelope.getResponse().toString();
                           //  Log.e("WLH", "result = " + result);
                            android.os.Message msg = loginHandler.obtainMessage();
                            msg.arg1 = 1;
                            msg.sendToTarget();

                        } catch (Exception ex) {
                            Toast.makeText(getApplicationContext()
                                    , "通讯异常，请重试！", Toast.LENGTH_SHORT).show();
                            Log.e("WLH", ex.toString());
                            MessageDialog.dismiss();
                        }

                    }
                });
            } else {
                Dialog dialog = new AlertDialog.Builder(mContext, AlertDialog.THEME_HOLO_LIGHT)
                        .setTitle("网络异常")
                        .setMessage("没有可用网络")
                        // 设置内容
                        .setPositiveButton("设置网络",// 设置确定按钮
                                new DialogInterface.OnClickListener() {

                                    @SuppressLint("NewApi")
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        if (android.os.Build.VERSION.SDK_INT > 10) {
                                            startActivity(new Intent(
                                                    android.provider.Settings.ACTION_SETTINGS));
                                        } else {
                                            startActivity(new Intent(
                                                    android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                                        }

                                    }

                                })
                        .setNegativeButton("重试",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                    }
                                }).create();// 创建
                // 显示对话框
                dialog.show();
            }
        }

    }



}
