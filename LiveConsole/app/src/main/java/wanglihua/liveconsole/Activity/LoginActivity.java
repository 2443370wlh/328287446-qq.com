package wanglihua.liveconsole.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

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
import wanglihua.liveconsole.R;
import wanglihua.liveconsole.Utils.Utils;


public class LoginActivity extends Activity {

    private SurfaceView mVideoView;
    private SurfaceHolder holder;
    private int mScreenWidth = 0;
    private LoginActivity mActivity;
    private Context mContext;
    private MediaPlayer mPlayer;
    private SharedPreferences sp = null;
    private SharedPreferences.Editor editor;
    private String mName, mPwd;
    private ExecutorService executorService = null;
    private String result;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        sp = getSharedPreferences("logininfo", 0);
        mActivity = this;
        mContext = this;
        mScreenWidth = Utils.getWindowsWidth(this);
        mVideoView =  findViewById(R.id.videoView);
        Utils.setSize(mVideoView,2,mScreenWidth,750,1000);
        holder = mVideoView.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                // 设置需要播放的视频
                try
                {
                    Uri mUri = Uri.parse("android.resource://" + getPackageName() + "/"+ R.raw.login);
                    mPlayer.setDataSource(mContext, mUri);
                    mPlayer.prepare();
                }
                catch (Exception e1)
                {
                    e1.printStackTrace();
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });
        mPlayer = new MediaPlayer();


        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mPlayer.setDisplay(holder);
                mPlayer.start();

            }
        });
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                if (sp.getBoolean("isRemember", false)) {
                    mName = sp.getString("name", "");
                    mPwd = sp.getString("pwd", "");
                    login();
                } else {
                    Intent intent = new Intent(mActivity, AccountActivity.class);
                    mActivity.startActivity(intent);
                    mActivity.finish();
                }

            }
        });

    }

    private void login() {
        if (!mName.equals("") && !mPwd.equals("")) {
            if (Utils.isConnect(mContext)) {

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
                            // Log.e("WLH", "result = " + result);
                            android.os.Message msg = loginHandler.obtainMessage();
                            msg.arg1 = 1;
                            msg.sendToTarget();

                        } catch (Exception ex) {
                            Intent intent = new Intent(mActivity, AccountActivity.class);
                            mActivity.startActivity(intent);
                            mActivity.finish();
                        }

                    }
                });
            } else {
                Intent intent = new Intent(mActivity, AccountActivity.class);
                mActivity.startActivity(intent);
                mActivity.finish();
            }
        }

    }

    private LoginInfo mLoginInfo;
    @SuppressLint("HandlerLeak")
    private Handler loginHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.arg1) {
                case 1:
                    InputStream inStream = new ByteArrayInputStream(
                            result.getBytes());
                    LoginHandle xmlHandle = new LoginHandle(mContext);
                    mLoginInfo = xmlHandle.readXML(inStream);
                    if (mLoginInfo != null) {
                        editor = sp.edit();

                        editor.putString("userID", "" + mLoginInfo.userID);
                        editor.putString("password", "" + mPwd + mLoginInfo.login_hash);
                        Log.e("WLH", "userID = " + mLoginInfo.userID);
                        Log.e("WLH","password = "+ mPwd + mLoginInfo.login_hash);
                        editor.commit();

                        Intent intent = new Intent(mActivity, MainActivity.class);
                        mActivity.startActivity(intent);
                        mActivity.finish();

                    } else {
                        Intent intent = new Intent(mActivity, AccountActivity.class);
                        mActivity.startActivity(intent);
                        mActivity.finish();
                    }
                    break;
            }
        }
    };
}
