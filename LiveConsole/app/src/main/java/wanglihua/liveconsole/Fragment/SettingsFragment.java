package wanglihua.liveconsole.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import wanglihua.liveconsole.Activity.AccountActivity;
import wanglihua.liveconsole.Handle.LoginHandle;
import wanglihua.liveconsole.R;
import wanglihua.liveconsole.Utils.Utils;
import wanglihua.liveconsole.view.SpinerPopWindow;

/**
 * Created by Administrator on 2018/6/5.
 */

public class SettingsFragment extends Fragment implements View.OnClickListener{
    private View mView;
    private Context mContext;
    private SharedPreferences sp = null;
    private SharedPreferences.Editor editor;

    private TextView timeTxt;
    private LinearLayout timeLay;
    private SpinerPopWindow<String> mSpinerPopWindow;
    private List<String> mList;

    private Button exitBtn;

    private int mScreenWidth = 0 ;

    private ExecutorService executorService = null;
    private String result;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_settings, container, false);
        init();
        return mView;
    }

    private void init() {
        mScreenWidth = Utils.getWindowsWidth(getActivity());
        mContext = this.getActivity();

        LinearLayout mCountDownLay = mView.findViewById(R.id.lay_count_down);
        Utils.setSize(mCountDownLay,1,mScreenWidth,-1,110);
        Utils.setMargins(mCountDownLay,1,mScreenWidth,12,0,12,0);

        ImageView mCountDownImg = mView.findViewById(R.id.img_count_down);
        Utils.setSize(mCountDownImg,1,mScreenWidth,50,54);
        Utils.setMargins(mCountDownImg,1,mScreenWidth,0,0,12,0);


        timeTxt = mView.findViewById(R.id.txt_time);
        Drawable drawable=getResources().getDrawable(R.drawable.menu_down_icon);
        drawable.setBounds(0,0,18,10);
        timeTxt.setCompoundDrawables(null,null,drawable,null);
        sp = getActivity().getSharedPreferences("settings_info", 0);
        timeTxt.setText(sp.getString("delay_time", "5秒"));

        timeLay = mView.findViewById(R.id.lay_time);
        Utils.setSize(timeLay,1,mScreenWidth,150,60);
        timeLay.setOnClickListener(this);

        initData();
        mSpinerPopWindow = new SpinerPopWindow<String>(this.getContext(), mList,itemClickListener,mScreenWidth);
        mSpinerPopWindow.setOnDismissListener(dismissListener);

        exitBtn = mView.findViewById(R.id.btn_exit);
        Utils.setSize(exitBtn,1,mScreenWidth,335,74);
        exitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case  R.id.lay_time:
                mSpinerPopWindow.setWidth(timeLay.getWidth());
                mSpinerPopWindow.showAsDropDown(timeLay);
                break;
            case R.id.btn_exit:
                exitBtn.setClickable(false);
                logout();
                break;
        }

    }

    private void logout() {

            if (Utils.isConnect(mContext)) {

                if (executorService == null) {
                    executorService = Executors.newCachedThreadPool();
                }

                executorService.submit(new Runnable() {
                    public void run() {
                        SoapObject so = new SoapObject("http://tempuri.org/",
                                "MiniMaster_User_Logout");
                        sp = getActivity().getSharedPreferences("logininfo", 0);
                        String userID = sp.getString("userID", "");
                        String password = sp.getString("password", "");
                        so.addProperty("_userID", userID);
                        so.addProperty("_password", password);
                        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                                SoapEnvelope.VER12);
                        envelope.bodyOut = so;
                        envelope.dotNet = true;
                        HttpTransportSE ht = new HttpTransportSE(
                                "http://m1.lzgd.com.cn/test/report_WebService.asmx");
                        try {
                            ht.call(null, envelope);
                            result = envelope.getResponse().toString();
                         //    Log.e("WLH", "result = " + result);
                            android.os.Message msg = logoutHandler.obtainMessage();
                            msg.arg1 = 1;
                            msg.sendToTarget();

                        } catch (Exception ex) {
                            exitBtn.setClickable(true);
                        }

                    }
                });
            } else {
                exitBtn.setClickable(true);
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
    @SuppressLint("HandlerLeak")
    private Handler logoutHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.arg1) {
                case 1:
                    InputStream inStream = new ByteArrayInputStream(
                            result.getBytes());
                    LoginHandle xmlHandle = new LoginHandle(mContext);
                     String mMsg =  xmlHandle.readOUTXML(inStream);
                    //Log.e("WLH","logout msg = "+mMsg);
                    if(mMsg.trim().equals("用户已登出")){
                        sp = getActivity().getSharedPreferences("logininfo", 0);
                        editor = sp.edit();
                        editor.putBoolean("isRemember", false);
                        editor.commit();
                        Intent intent = new Intent(mContext, AccountActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                        getActivity().finish();
                    }else{
                        exitBtn.setClickable(true);
                        Toast.makeText(getActivity().getApplicationContext()
                                , "出错啦，请重试！", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };



    private void initData() {
        mList = new ArrayList<String>();
        mList.add("5秒" );
        mList.add("10秒" );
        mList.add("30秒" );
        mList.add("60秒" );
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mSpinerPopWindow.dismiss();
            timeTxt.setText(mList.get(position));
            sp = getActivity().getSharedPreferences("settings_info", 0);
            editor = sp.edit();
            editor.putString("delay_time",mList.get(position));
            editor.commit();
        }
    };

    private PopupWindow.OnDismissListener  dismissListener=new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {

        }
    };
}
