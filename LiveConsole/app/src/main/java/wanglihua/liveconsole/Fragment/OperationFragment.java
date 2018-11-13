package wanglihua.liveconsole.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import wanglihua.liveconsole.Handle.OperationHandle;
import wanglihua.liveconsole.R;
import wanglihua.liveconsole.Utils.Utils;
import wanglihua.liveconsole.view.AddTitleDialog;

/**
 * Created by Administrator on 2018/6/5.
 */

public class OperationFragment extends Fragment implements View.OnClickListener{

    private View mView;
    private int mScreenWidth = 0;
    private Context mContext;
    private SharedPreferences sp = null;
    private SharedPreferences.Editor editor;
    private ExecutorService executorService = null;
    private String mUserID;
    private String mPassword;
    private String mDelayTime;
    private int countDownTime;
    private boolean isCounting = false;
    //VideoView
    private RelativeLayout mVideoLay;
    private TextView mBtnCountdown;


    private boolean needPlay =false;
    private int playFlag = -1;
    private TXCloudVideoView mTxView;
    private TXLivePlayer mLivePlayer;

    //Buttons
    private RelativeLayout mButtonsLay;
    private ImageView mMainLogoBtn, mTitleBtn, mBackupPicBtn, mCornerMarkBtn, mPushBtn, mStartBtn, mCopyBtn, mChangeBtn;
    private Boolean isLogoOn = false, isTitleOn = false, isBackupOn = false, isMarOn = false, isPushing = false, isStarted = false;
    private int mTitleIndex = 0;
    private String mMainLogoString = "", mBackupPicString = "", mCornerMarkString = "", mTitle = "", mReporter = "", mRmtpUrl = "", mM3U8Url = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_operation, container, false);
        init();

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(needPlay){
            mLivePlayer.resume();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mLivePlayer.isPlaying()){
            mLivePlayer.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLivePlayer.stopPlay(true); // true 代表清除最后一帧画面
        mTxView.onDestroy();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            if(mLivePlayer.isPlaying()){
                mLivePlayer.pause();
            }
        } else {
            if(needPlay){
                mLivePlayer.resume();
            }
            if (!isCounting) {
                sp = getActivity().getSharedPreferences("settings_info", 0);
                mDelayTime = sp.getString("delay_time", "5秒");
                mDelayTime = mDelayTime.substring(0, mDelayTime.indexOf("秒"));
                countDownTime = Integer.valueOf(mDelayTime).intValue();
                mBtnCountdown.setText(mDelayTime);
            }
              getState();
        }

    }

    private void init() {
        mContext = this.getActivity();
        mScreenWidth = Utils.getWindowsWidth(getActivity());
        sp = mContext.getSharedPreferences("logininfo", 0);
        mUserID = sp.getString("userID", "");
        mPassword = sp.getString("password", "");
        initVideoView();
        initButtons();
    //    getState();

    }


    private void initVideoView() {
        mVideoLay = mView.findViewById(R.id.lay_video);
        Utils.setSize(mVideoLay, 1, mScreenWidth, 750, 425);


        mTxView =  mView.findViewById(R.id.video_view);
        //创建 player 对象
        mLivePlayer = new TXLivePlayer(getActivity());
        //关键 player 对象与界面 view
        mLivePlayer.setPlayerView(mTxView);
        mLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);
        TXLivePlayConfig mPlayConfig = new TXLivePlayConfig();
        mPlayConfig.setAutoAdjustCacheTime(true);
        mPlayConfig.setMinAutoAdjustCacheTime(1);
        mPlayConfig.setMaxAutoAdjustCacheTime(1);
        mLivePlayer.setConfig(mPlayConfig);
        mLivePlayer.setPlayListener(new ITXLivePlayListener() {
            @Override
            public void onPlayEvent(int i, Bundle bundle) {
                 //    Log.e("WLH","事件ID = "+i);
                if (i == TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {
                    Toast.makeText(getActivity().getApplicationContext()
                            , "网络断开，拉流失败！", Toast.LENGTH_SHORT).show();
                }else if(i==TXLiveConstants.PLAY_EVT_PLAY_END||i==TXLiveConstants.PLAY_ERR_NET_DISCONNECT){
                          needPlay = false;
                          mLivePlayer.stopPlay(false);
                    Toast.makeText(getActivity().getApplicationContext()
                            , "无法播放，服务器已关闭或地址错误！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNetStatus(Bundle bundle) {

            }
        });

        mBtnCountdown = mView.findViewById(R.id.btn_countdown);
        Utils.setSize(mBtnCountdown, 1, mScreenWidth, 125, 125);

    }



    private void initButtons() {
        mButtonsLay = mView.findViewById(R.id.lay_button);
        Utils.setSize(mButtonsLay, 1, mScreenWidth, -1, 177);
        Utils.setMargins(mButtonsLay, 1, mScreenWidth, 20, 20, 20, 0);

        mMainLogoBtn = mView.findViewById(R.id.btn_main_logo);
        Utils.setSize(mMainLogoBtn, 1, mScreenWidth, 100, 60);
        mMainLogoBtn.setOnClickListener(this);

        mTitleBtn = mView.findViewById(R.id.btn_title);
        Utils.setSize(mTitleBtn, 1, mScreenWidth, 100, 60);
        mTitleBtn.setOnClickListener(this);

        mBackupPicBtn = mView.findViewById(R.id.btn_backup_pic);
        Utils.setSize(mBackupPicBtn, 1, mScreenWidth, 100, 60);
        mBackupPicBtn.setOnClickListener(this);

        mCornerMarkBtn = mView.findViewById(R.id.btn_corner_mark);
        Utils.setSize(mCornerMarkBtn, 1, mScreenWidth, 100, 60);
        mCornerMarkBtn.setOnClickListener(this);

        mPushBtn = mView.findViewById(R.id.btn_livepush);
        Utils.setSize(mPushBtn, 2, mScreenWidth, 335, 74);
        mPushBtn.setOnClickListener(this);

        mStartBtn = mView.findViewById(R.id.btn_livestart);
        Utils.setSize(mStartBtn, 2, mScreenWidth, 335, 74);
        mStartBtn.setOnClickListener(this);

        mCopyBtn = mView.findViewById(R.id.btn_copy);
        Utils.setSize(mCopyBtn, 2, mScreenWidth, 335, 74);
        mCopyBtn.setOnClickListener(this);

        mChangeBtn = mView.findViewById(R.id.btn_change);
        Utils.setSize(mChangeBtn, 2, mScreenWidth, 335, 74);
        mChangeBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        sp = mContext.getSharedPreferences("push_state", 0);
        if (id == R.id.btn_main_logo) {

            if (isLogoOn) {
                mMainLogoString = "";
            } else {
                mMainLogoString = sp.getString("logo_video","");
            }
            setMainLogo();
        } else if (id == R.id.btn_title) {
            sp = mContext.getSharedPreferences("push_state", 0);
            if (!sp.getString("title", "").isEmpty()) {
                if (isTitleOn) {
                    mTitle = "";
                    mReporter = "";
                } else {
                    mTitleIndex = sp.getInt("title_index", 0);
                    mTitle = sp.getString("title", "");
                    mReporter = sp.getString("reporter", "");
                }
                setTitle();
            } else {
                Toast.makeText(getActivity().getApplicationContext()
                        , "请先选择一个字幕！", Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.btn_backup_pic) {
            if (isBackupOn) {
                mBackupPicString = "";
            } else {
                mBackupPicString= sp.getString("backup_video","");
            }
            setBackupPic();
        } else if (id == R.id.btn_corner_mark) {
            if (isMarOn) {
                mCornerMarkString = "";
            } else {
                mCornerMarkString= sp.getString("mark_video","");
            }
            setCornerMark();
        } else if (id == R.id.btn_livepush) {
//            if (isPushing) {
//                //                isPushing = false;
//                //                mPushBtn.setImageResource(R.drawable.livepush_btn_n);
//                //    startPush();
//            } else {
                startPush();
   //         }
        } else if (id == R.id.btn_livestart) {
            if (isStarted) {
                //                isStarted = false;
                //                mStartBtn.setImageResource(R.drawable.livestart_btn_n);
            } else {
                mBackupPicString= sp.getString("backup_video","");
                startPushComplete();
            }
        } else if (id == R.id.btn_change) {
            showAddTitleDialog();
        } else if (id == R.id.btn_copy) {
            copy();
        }

    }

    private void showAddTitleDialog() {

        AddTitleDialog editDialog = new AddTitleDialog(mContext, mScreenWidth);
        editDialog.show();
        editDialog.setOnPosNegClickListener(new AddTitleDialog.OnPosNegClickListener() {
            @Override
            public void posClickListener(String value) {
                Log.e("WLH", " value = " + value);
                if (value != null && value.equals("update")) {
                    sp = mContext.getSharedPreferences("push_state", 0);
                    mTitleIndex = sp.getInt("title_index", 0);
                    mTitle = sp.getString("title", "");
                    mReporter = sp.getString("reporter", "");
                    setTitle();
                }
            }

            @Override
            public void negCliclListener(String value) {
            }
        });
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            OperationHandle xmlHandle = new OperationHandle(mContext);
            Bundle b = msg.getData();
            switch (msg.arg1) {

                case 1:

                    String result1 = b.getString("result");
                    InputStream inStream1 = new ByteArrayInputStream(
                            result1.getBytes());
                    String mMsg1 = xmlHandle.readXML(inStream1);
                    Log.e("WLH", "mMsg1 = " + mMsg1);
                    if (mMsg1.trim().equals("直播挂标显示")) {
                        mMainLogoBtn.setBackgroundResource(R.drawable.greenswitch_btn_off);
                        isLogoOn = false;
                    } else if (mMsg1.trim().equals("直播挂标关闭")) {
                        mMainLogoBtn.setBackgroundResource(R.drawable.greenswitch_btn_on);
                        isLogoOn = true;
                    } else {
                        Toast.makeText(getActivity().getApplicationContext()
                                , "通讯异常，请重试！", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    String result2 = b.getString("result");
                    InputStream inStream2 = new ByteArrayInputStream(
                            result2.getBytes());
                    String mMsg2 = xmlHandle.readXML(inStream2);
                    Log.e("WLH", "mMsg2 = " + mMsg2);
                    if (mMsg2.trim().equals("直播图关闭")) {
                        isBackupOn = false;
                        mBackupPicBtn.setBackgroundResource(R.drawable.greenswitch_btn_off);
                    } else if (mMsg2.trim().equals("直播图打开")) {
                        isBackupOn = true;
                        mBackupPicBtn.setBackgroundResource(R.drawable.greenswitch_btn_on);
                    } else {
                        Toast.makeText(getActivity().getApplicationContext()
                                , "通讯异常，请重试！", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case 3:
                    String result3 = b.getString("result");
                    InputStream inStream3 = new ByteArrayInputStream(
                            result3.getBytes());
                    String mMsg3 = xmlHandle.readXML(inStream3);
                    Log.e("WLH", "mMsg3 = " + mMsg3);
                    if (mMsg3.trim().equals("右下角广告关闭")) {
                        isMarOn = false;
                        mCornerMarkBtn.setBackgroundResource(R.drawable.greenswitch_btn_off);
                    } else if (mMsg3.trim().equals("右下角广告打开")) {
                        isMarOn = true;
                        mCornerMarkBtn.setBackgroundResource(R.drawable.greenswitch_btn_on);
                    } else {
                        Toast.makeText(getActivity().getApplicationContext()
                                , "通讯异常，请重试！", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case 4:
                    String result4 = b.getString("result");
                    InputStream inStream4 = new ByteArrayInputStream(
                            result4.getBytes());
                    String mMsg4 = xmlHandle.readXML(inStream4);
                    Log.e("WLH", "mMsg4 = " + mMsg4);
                    if (mMsg4.trim().equals("字幕关闭")) {
                        isTitleOn = false;
                        mTitleBtn.setBackgroundResource(R.drawable.greenswitch_btn_off);
                    } else if (mMsg4.trim().equals("字幕打开")) {
                        isTitleOn = true;
                        mTitleBtn.setBackgroundResource(R.drawable.greenswitch_btn_on);
                    } else {
                        Toast.makeText(getActivity().getApplicationContext()
                                , "通讯异常，请重试！", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case 5:
                    String result5 = b.getString("result");
                    InputStream inStream5 = new ByteArrayInputStream(
                            result5.getBytes());
                    String mMsg5 = xmlHandle.readMsgXML(inStream5);
                    String[] temp = mMsg5.split("\\|");
                    mRmtpUrl = temp[0].trim();
                    mM3U8Url = temp[1];
                    Log.e("WLH", mRmtpUrl);
                    Log.e("WLH", mM3U8Url);
                    isPushing = true;
                    mPushBtn.setImageResource(R.drawable.livepush_btn_h);
                    playFlag = 0;
                    showPlayDialog();
                    break;
                case 6:
                    String result6 = b.getString("result");
                    InputStream inStream6 = new ByteArrayInputStream(
                            result6.getBytes());
                    String mMsg6 = xmlHandle.readMsgXML(inStream6);
                    String[] temp2 = mMsg6.split("\\|");
                    mRmtpUrl = temp2[0].trim();
                    mM3U8Url = temp2[1];
                    isStarted = true;
                    mStartBtn.setImageResource(R.drawable.livestart_btn_h);
                    playFlag = 1;
                    showPlayDialog();
                    break;
                case 7:
                    if (countDownTime > 0) {
                        android.os.Message newmsg = mHandler.obtainMessage();
                        newmsg.arg1 = 7;
                        mHandler.sendMessageDelayed(newmsg, 1000);
                        countDownTime--;
                        mBtnCountdown.setText("" + countDownTime);
                    } else {
                        isCounting = false;
                        mBtnCountdown.setVisibility(View.INVISIBLE);
                    }
                    break;
                case 8:
                    String result8 = b.getString("result");
                    InputStream inStream8 = new ByteArrayInputStream(
                            result8.getBytes());
                    String mMsg8 = xmlHandle.readMsgXML(inStream8);
                    String[] temp8 = mMsg8.split("\\|");

//                    for(int i= 0;i<temp8.length;i++){
//                        Log.e("WLH", "temp8["+i+"] = "+temp8[i]);
//                    }
                    syncState(temp8);
                    break;
            }
        }
    };

    private void syncState(String[] flag){
        sp = mContext.getSharedPreferences("push_state", 0);
        if (sp.getBoolean("need_update", true)) {

            if(flag[0].trim().equals(mUserID)){
                if (flag[3].equals("1")) {
                    mMainLogoString = sp.getString("logo_video","");
                } else {
                    mMainLogoString = "";
                }
                setMainLogo();


                if (flag[4].equals("1")) {
                    mTitleIndex = sp.getInt("title_index", 0);
                    mTitle = sp.getString("title", "");
                    mReporter = sp.getString("reporter", "");
                } else {
                    mTitle = "";
                    mReporter = "";
                }
                setTitle();

                if (flag[2].equals("1")) {
                    mBackupPicString= sp.getString("backup_video","");
                } else {
                    mBackupPicString = "";
                }
                setBackupPic();


                if (flag[1].equals("1")) {
                    mCornerMarkString= sp.getString("mark_video","");
                } else {
                    mCornerMarkString = "";
                }
                setCornerMark();

//                if (flag[5].equals("1")) {
//                    startPush();
//                    isPushing = true;
//                    mPushBtn.setImageResource(R.drawable.livepush_btn_h);
//                } else if(flag[5].equals("0")){
//                    isPushing = false;
//                    mPushBtn.setImageResource(R.drawable.livepush_btn_n);
//                }

            }


            editor = sp.edit();
            editor.putBoolean("need_update", false);
            editor.commit();
        }
    }

    private void showPlayDialog() {
        Dialog mDialog = new AlertDialog.Builder(mContext,
                AlertDialog.THEME_HOLO_DARK)
                // .setTitle("网络异常")
                .setMessage("是否播放视频流")
                .setPositiveButton("是",
                        new DialogInterface.OnClickListener() {
                            @SuppressLint("NewApi")
                            public void onClick(DialogInterface dialog,int which) {

                                if(playFlag == 0){
//                                    isPushing = true;
//                                    mPushBtn.setImageResource(R.drawable.livepush_btn_h);
                                }else if(playFlag == 1){
//                                    isStarted = true;
//                                    mStartBtn.setImageResource(R.drawable.livestart_btn_h);
                                    if (countDownTime > 0) {
                                        isCounting = true;
                                        mBtnCountdown.setVisibility(View.VISIBLE);
                                        android.os.Message newmsg = mHandler.obtainMessage();
                                        newmsg.arg1 = 7;
                                        mHandler.sendMessageDelayed(newmsg, 1000);
                                        mBtnCountdown.setText("" + countDownTime);
                                    } else {
                                        mBtnCountdown.setVisibility(View.INVISIBLE);
                                    }
                                }

                                if(needPlay){
                                    mLivePlayer.stopPlay(true);
                                }
                                needPlay = true;

                                mLivePlayer.startPlay(mRmtpUrl,TXLivePlayer.PLAY_TYPE_LIVE_RTMP_ACC);
                            }

                        })
                .setNegativeButton("否",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        }).create();
        mDialog.show();
    }

    private void setMainLogo() {

        if (Utils.isConnect(mContext)) {

            if (executorService == null) {
                executorService = Executors.newCachedThreadPool();

            }
             Log.e("WLH","mMainLogoString = "+mMainLogoString);
            executorService.submit(new Runnable() {
                public void run() {
                    SoapObject so = new SoapObject("http://tempuri.org/",
                            "MiniMaster_Logo");
                    so.addProperty("picString", mMainLogoString);
                    so.addProperty("_userID", mUserID);
                    so.addProperty("_password", mPassword);
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                            SoapEnvelope.VER12);
                    envelope.bodyOut = so;
                    envelope.dotNet = true;
                    HttpTransportSE ht = new HttpTransportSE(
                            "http://m1.lzgd.com.cn/test/report_WebService.asmx");
                    try {
                        ht.call(null, envelope);
                        String result = envelope.getResponse().toString();
                        // Log.e("WLH", "result = " + result);
                        android.os.Message msg = mHandler.obtainMessage();
                        msg.arg1 = 1;
                        Bundle b = new Bundle();
                        b.putString("result", result);
                        msg.setData(b);
                        msg.sendToTarget();
                    } catch (Exception ex) {
                        Log.e("WLH", ex.toString());
                        Toast.makeText(getActivity().getApplicationContext()
                                , "通讯异常，请重试！", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        } else {
            showNoNetDialog();
        }

    }

    private void setTitle() {

        if (Utils.isConnect(mContext)) {

            if (executorService == null) {
                executorService = Executors.newCachedThreadPool();

            }
            Log.e("WLH", "mTitleIndex = " + mTitleIndex);
            Log.e("WLH", "mTitle = " + mTitle);
            Log.e("WLH", "mReporter = " + mReporter);
            executorService.submit(new Runnable() {
                public void run() {
                    SoapObject so = new SoapObject("http://tempuri.org/",
                            "MiniMaster_Sub");
                    so.addProperty("sub_style", mTitleIndex);
                    so.addProperty("sub_title", mTitle);
                    so.addProperty("sub_pep", mReporter);
                    so.addProperty("_userID", mUserID);
                    so.addProperty("_password", mPassword);
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                            SoapEnvelope.VER12);
                    envelope.bodyOut = so;
                    envelope.dotNet = true;
                    HttpTransportSE ht = new HttpTransportSE(
                            "http://m1.lzgd.com.cn/test/report_WebService.asmx");
                    try {
                        ht.call(null, envelope);
                        String result = envelope.getResponse().toString();
                        Log.e("WLH", "result = " + result);
                        android.os.Message msg = mHandler.obtainMessage();
                        msg.arg1 = 4;
                        Bundle b = new Bundle();
                        b.putString("result", result);
                        msg.setData(b);
                        msg.sendToTarget();
                    } catch (Exception ex) {
                        Log.e("WLH", ex.toString());
                        Toast.makeText(getActivity().getApplicationContext()
                                , "通讯异常，请重试！", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        } else {
            showNoNetDialog();
        }

    }

    private void setBackupPic() {

        if (Utils.isConnect(mContext)) {

            if (executorService == null) {
                executorService = Executors.newCachedThreadPool();

            }
              Log.e("WLH","mBackupPicString = "+mBackupPicString);
            executorService.submit(new Runnable() {
                public void run() {
                    SoapObject so = new SoapObject("http://tempuri.org/",
                            "MiniMaster_LivePicString");
                    so.addProperty("picString", mBackupPicString);
                    so.addProperty("_userID", mUserID);
                    so.addProperty("_password", mPassword);
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                            SoapEnvelope.VER12);
                    envelope.bodyOut = so;
                    envelope.dotNet = true;
                    HttpTransportSE ht = new HttpTransportSE(
                            "http://m1.lzgd.com.cn/test/report_WebService.asmx");
                    try {
                        ht.call(null, envelope);
                        String result = envelope.getResponse().toString();
                        Log.e("WLH", "result = " + result);
                        android.os.Message msg = mHandler.obtainMessage();
                        msg.arg1 = 2;
                        Bundle b = new Bundle();
                        b.putString("result", result);
                        msg.setData(b);
                        msg.sendToTarget();
                    } catch (Exception ex) {
                        Log.e("WLH", ex.toString());
                        Toast.makeText(getActivity().getApplicationContext()
                                , "通讯异常，请重试！", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        } else {
            showNoNetDialog();
        }

    }

    private void setCornerMark() {

        if (Utils.isConnect(mContext)) {

            if (executorService == null) {
                executorService = Executors.newCachedThreadPool();

            }
              Log.e("WLH","mCornerMarkString = "+mCornerMarkString);
            executorService.submit(new Runnable() {
                public void run() {
                    SoapObject so = new SoapObject("http://tempuri.org/",
                            "MiniMaster_ADAD_PicString");
                    so.addProperty("media_url", mCornerMarkString);
                    so.addProperty("_userID", mUserID);
                    so.addProperty("_password", mPassword);
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                            SoapEnvelope.VER12);
                    envelope.bodyOut = so;
                    envelope.dotNet = true;
                    HttpTransportSE ht = new HttpTransportSE(
                            "http://m1.lzgd.com.cn/test/report_WebService.asmx");
                    try {
                        ht.call(null, envelope);
                        String result = envelope.getResponse().toString();
                        Log.e("WLH", "result = " + result);
                        android.os.Message msg = mHandler.obtainMessage();
                        msg.arg1 = 3;
                        Bundle b = new Bundle();
                        b.putString("result", result);
                        msg.setData(b);
                        msg.sendToTarget();
                    } catch (Exception ex) {
                        Log.e("WLH", ex.toString());
                        Toast.makeText(getActivity().getApplicationContext()
                                , "通讯异常，请重试！", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        } else {
            showNoNetDialog();
        }

    }

    private void getState() {

        if (Utils.isConnect(mContext)) {

            if (executorService == null) {
                executorService = Executors.newCachedThreadPool();

            }
            executorService.submit(new Runnable() {
                public void run() {
                    SoapObject so = new SoapObject("http://tempuri.org/",
                            "MiniMaster_State_Sub");
                    so.addProperty("_userID", mUserID);
                    so.addProperty("_password", mPassword);
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                            SoapEnvelope.VER12);
                    envelope.bodyOut = so;
                    envelope.dotNet = true;
                    HttpTransportSE ht = new HttpTransportSE(
                            "http://m1.lzgd.com.cn/test/report_WebService.asmx");
                    try {
                        ht.call(null, envelope);
                        String result = envelope.getResponse().toString();
                    //    Log.e("WLH", "result = " + result);
                        android.os.Message msg = mHandler.obtainMessage();
                        msg.arg1 = 8;
                        Bundle b = new Bundle();
                        b.putString("result", result);
                        msg.setData(b);
                        msg.sendToTarget();
                    } catch (Exception ex) {
                        Log.e("WLH", ex.toString());
                        Toast.makeText(getActivity().getApplicationContext()
                                , "通讯异常，请重试！", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        } else {
            showNoNetDialog();
        }

    }

    private void startPush() {

        if (Utils.isConnect(mContext)) {

            if (executorService == null) {
                executorService = Executors.newCachedThreadPool();

            }
            executorService.submit(new Runnable() {
                public void run() {
                    SoapObject so = new SoapObject("http://tempuri.org/",
                            "MiniMaster_LivePush");
                    so.addProperty("_userID", mUserID);
                    so.addProperty("_password", mPassword);
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                            SoapEnvelope.VER12);
                    envelope.bodyOut = so;
                    envelope.dotNet = true;
                    HttpTransportSE ht = new HttpTransportSE(
                            "http://m1.lzgd.com.cn/test/report_WebService.asmx");
                    try {
                        ht.call(null, envelope);
                        String result = envelope.getResponse().toString();
                        Log.e("WLH", "result = " + result);
                        android.os.Message msg = mHandler.obtainMessage();
                        msg.arg1 = 5;
                        Bundle b = new Bundle();
                        b.putString("result", result);
                        msg.setData(b);
                        msg.sendToTarget();
                    } catch (Exception ex) {
                        Log.e("WLH", ex.toString());
                        Toast.makeText(getActivity().getApplicationContext()
                                , "通讯异常，请重试！", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        } else {
            showNoNetDialog();
        }

    }

    private void startPushComplete() {

        if (Utils.isConnect(mContext)) {

            if (executorService == null) {
                executorService = Executors.newCachedThreadPool();

            }
            Log.e("WLH","bak_play = "+mBackupPicString);
            Log.e("WLH","delay_time = "+mDelayTime);

            executorService.submit(new Runnable() {
                public void run() {
                    SoapObject so = new SoapObject("http://tempuri.org/",
                            "MiniMaster_LivePush");

                    so.addProperty("bak_play", mBackupPicString);
                    so.addProperty("delay_time", mDelayTime);
                    so.addProperty("_userID", mUserID);
                    so.addProperty("_password", mPassword);
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                            SoapEnvelope.VER12);
                    envelope.bodyOut = so;
                    envelope.dotNet = true;
                    HttpTransportSE ht = new HttpTransportSE(
                            "http://m1.lzgd.com.cn/test/report_WebService.asmx");
                    try {
                        ht.call(null, envelope);
                        String result = envelope.getResponse().toString();
                        Log.e("WLH", "result = " + result);
                        android.os.Message msg = mHandler.obtainMessage();
                        msg.arg1 = 6;
                        Bundle b = new Bundle();
                        b.putString("result", result);
                        msg.setData(b);
                        msg.sendToTarget();
                    } catch (Exception ex) {
                        Log.e("WLH", ex.toString());
                        Toast.makeText(getActivity().getApplicationContext()
                                , "通讯异常，请重试！", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        } else {
            showNoNetDialog();
        }

    }

    private void showNoNetDialog() {
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

    private void copy() {
        if (mRmtpUrl.equals("")) {
            Toast.makeText(getActivity().getApplicationContext()
                    , " 尚未获取到流地址！", Toast.LENGTH_SHORT).show();
        } else {
            //获取剪贴板管理器：
            ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("txt", "RMTP地址： " + mRmtpUrl + "\nM3U8地址： " + mM3U8Url);
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
            Toast.makeText(getActivity().getApplicationContext()
                    , "已将流地址复制到剪切板！", Toast.LENGTH_SHORT).show();
        }

    }

}
