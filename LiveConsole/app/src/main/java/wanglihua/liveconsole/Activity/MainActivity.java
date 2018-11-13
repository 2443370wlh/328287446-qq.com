package wanglihua.liveconsole.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import wanglihua.liveconsole.Fragment.OperationFragment;
import wanglihua.liveconsole.Fragment.PreviewFragment;
import wanglihua.liveconsole.Fragment.SettingsFragment;
import wanglihua.liveconsole.R;
import wanglihua.liveconsole.Utils.Utils;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private Context mContext;
    private int mScreenWidth = 0;
    private FragmentManager manager;
    private LinearLayout mBottomLay;
    private LinearLayout mainLy;
    private RelativeLayout mSettingsLy;
    private ImageView previewImg, operationImg, settingsImg;
    private Fragment currentFragment, settingsFragment, operationFragment, previewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            manager = getSupportFragmentManager();//重新创建Manager，防止此对象为空
            manager.popBackStackImmediate(null, 1);//弹出所有fragment
        }
        super.onCreate(savedInstanceState);
        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.    FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mContext = this;
        mScreenWidth = Utils.getWindowsWidth(this);
        init();
        initTab();
        initFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void init() {
        mainLy = findViewById(R.id.content_layout);
        mBottomLay = findViewById(R.id.ll_bottom_tab);
        Utils.setSize(mBottomLay,2,mScreenWidth,-1,98);
        previewImg = findViewById(R.id.iv_preview);
        previewImg.setOnClickListener(this);
        operationImg = findViewById(R.id.iv_operation);
        operationImg.setOnClickListener(this);
        mSettingsLy = findViewById(R.id.rl_settings);
        settingsImg = findViewById(R.id.iv_settings);
        Utils.setSize(settingsImg,2,mScreenWidth,98,98);
        settingsImg.setOnClickListener(this);
    }

    private void initTab() {
        if (previewFragment == null) {
            previewFragment = new PreviewFragment();
        }

        if (!previewFragment.isAdded()) {
            // 提交事务
            getSupportFragmentManager().beginTransaction().add(R.id.content_layout, previewFragment).commit();

            // 记录当前Fragment
            currentFragment = previewFragment;
            // 设置图片文本的变化
            previewImg.setImageResource(R.drawable.bottom_nav_previewbtn_h);
            operationImg.setImageResource(R.drawable.bottom_nav_operationbtn_n);
            settingsImg.setImageResource(R.drawable.bottom_nav_settingbtn_n);

        }

    }

    private void initFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (operationFragment == null) {
            operationFragment = new OperationFragment();
            transaction.add(R.id.content_layout, operationFragment).hide(operationFragment);
        }

        if (settingsFragment == null) {
            settingsFragment = new SettingsFragment();
            transaction.add(R.id.content_layout, settingsFragment).hide(settingsFragment);
        }

        if (previewFragment == null) {
            previewFragment = new PreviewFragment();
            transaction.add(R.id.content_layout, previewFragment).hide(previewFragment);
        }

        transaction.commit();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_preview) {
            if (previewFragment == null) {
                previewFragment = new PreviewFragment();
            }
            addOrShowFragment(getSupportFragmentManager().beginTransaction(), previewFragment);
            previewImg.setImageResource(R.drawable.bottom_nav_previewbtn_h);
            operationImg.setImageResource(R.drawable.bottom_nav_operationbtn_n);
            settingsImg.setImageResource(R.drawable.bottom_nav_settingbtn_n);

        } else if (view.getId() == R.id.iv_operation) {
            if (operationFragment == null) {
                operationFragment = new OperationFragment();
            }
            addOrShowFragment(getSupportFragmentManager().beginTransaction(), operationFragment);
            previewImg.setImageResource(R.drawable.bottom_nav_previewbtn_n);
            operationImg.setImageResource(R.drawable.bottom_nav_operationbtn_h);
            settingsImg.setImageResource(R.drawable.bottom_nav_settingbtn_n);

        } else if (view.getId() == R.id.iv_settings) {
            if (settingsFragment == null) {
                settingsFragment = new SettingsFragment();
            }
            addOrShowFragment(getSupportFragmentManager().beginTransaction(), settingsFragment);
            previewImg.setImageResource(R.drawable.bottom_nav_previewbtn_n);
            operationImg.setImageResource(R.drawable.bottom_nav_operationbtn_n);
            settingsImg.setImageResource(R.drawable.bottom_nav_settingbtn_h);

        }

    }

    private void addOrShowFragment(FragmentTransaction transaction,
                                   Fragment fragment) {
        if (currentFragment == fragment)
            return;

        if (!fragment.isAdded()) { // 如果当前fragment未被添加，则添加到Fragment管理器中
            transaction.hide(currentFragment)
                    .add(R.id.content_layout, fragment).commit();
        } else {
            transaction.hide(currentFragment).show(fragment).commit();
        }

        currentFragment = fragment;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showExitDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void showExitDialog() {
        Dialog dialog = new AlertDialog.Builder(mContext,
                AlertDialog.THEME_HOLO_DARK)
                .setTitle("提示")
                .setMessage("是否退出移动控制台？")
                // 设置内容
                .setPositiveButton("是",// 设置确定按钮
                        new DialogInterface.OnClickListener() {
                            @SuppressLint("NewApi")
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                finish();
                            }

                        })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }).create();// 创建
        // 显示对话框
        dialog.show();
    }
}
