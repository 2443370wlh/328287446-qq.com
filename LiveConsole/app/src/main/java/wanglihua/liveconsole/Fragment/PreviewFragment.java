package wanglihua.liveconsole.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import wanglihua.liveconsole.Handle.ConfigureHandle;
import wanglihua.liveconsole.Model.MiniMasterInfo;
import wanglihua.liveconsole.R;
import wanglihua.liveconsole.Utils.Utils;
import wanglihua.liveconsole.view.AddTitleDialog;
import wanglihua.liveconsole.view.ColumnHorizontalScrollView;
import wanglihua.liveconsole.view.RecyclerImageView;

/**
 * Created by Administrator on 2018/6/5.
 */

public class PreviewFragment extends Fragment implements View.OnClickListener {
    private View mView;
    private SharedPreferences sp = null;
    private SharedPreferences.Editor editor;
    private int mScreenWidth = 0;
    private Context mContext;
    private ExecutorService executorService = null;
    private String mUserID;
    private String mPassword;
    private String result;
    private ArrayList<MiniMasterInfo> mMiniMasterInfoList = new ArrayList<MiniMasterInfo>();
    //预览界面
    private RelativeLayout mPreviewLay;
    private ImageView mPreview_backuppic;
    private ImageView mPreview_mainlogo;
    private ImageView mUpdateBtn;
    private RelativeLayout mPreviewBottom;
    private LinearLayout mPreview_title;
    private ImageView mPreview_cornermark;
    private TextView mTitleTxt;
    private TextView mReporterTxt;
    //主LOGO
   // private ImageView mMainLogoBtn;
    private LinearLayout mMainLogoLayout;
    private View mMainLogoSelect;
    private ColumnHorizontalScrollView mMainLogoScrollView;
    private int mMainLogoIndex = 0;
    private LinearLayout mMainLogoGroup;
    private ArrayList<MiniMasterInfo> mMainLogoInfoList = new ArrayList<>();
    private String mMainLogoMediaURL = "";
    private String mMainLogoVideoURL = "";
    //字幕面板
   // private ImageView mTitleBtn;
    private ImageView mAddTitleBtn;
    private LinearLayout mTitleLayout;
    private View mTitleSelect;
    private ColumnHorizontalScrollView mTitleScrollView;
    private int mTitleIndex = 0;
    private LinearLayout mTitleGroup;
    private ArrayList<MiniMasterInfo> mTitleInfoList = new ArrayList<>();

    //备播图片
   // private ImageView mBackupPicBtn;
    private LinearLayout mBackupPicLayout;
    private View mBackupPicSelect;
    private ColumnHorizontalScrollView mBackupPicScrollView;
    private int mBackupPicIndex = 0;
    private LinearLayout mBackupPicGroup;
    private ArrayList<MiniMasterInfo> mBackupPicInfoList = new ArrayList<>();
    private String mBackupPicMediaURL = "";
    private String mBackupPicVideoURL = "";
    //角标
  //  private ImageView mCornerMarkBtn;
    private LinearLayout mCornerMarkLayout;
    private View mCornerMarkSelect;
    private ColumnHorizontalScrollView mCornerMarkScrollView;
    private int mCornerMarkIndex = 0;
    private LinearLayout mCornerMarkGroup;
    private ArrayList<MiniMasterInfo> mCornerMarkInfoList = new ArrayList<>();
    private String mCornerMarkMediaURL = "";
    private String mCornerMarkVideoURL = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_preview, container, false);
        init();
        return mView;
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {

        } else {
            if(mTitleIndex>=1&&mTitleIndex<=6){
                sp = mContext.getSharedPreferences("push_state", 0);
                mTitleTxt.setText(sp.getString("title",""));
                mReporterTxt.setText(sp.getString("reporter",""));
            }

        }
    }
    private void init() {
        mContext = this.getActivity();
        mScreenWidth = Utils.getWindowsWidth(getActivity());
//        sp = mContext.getSharedPreferences("push_state", 0);
//        editor = sp.edit();
//        editor.putBoolean("need_update",true);
//        editor.commit();

       getMiniMasterInfo();
    }


    private void initPreview() {
        mPreviewLay = mView.findViewById(R.id.lay_preview);
        Utils.setSize(mPreviewLay,1,mScreenWidth,750,425);

        mPreview_backuppic = mView.findViewById(R.id.img_preview);

        mPreview_mainlogo = mView.findViewById(R.id.img_main_logo);
        Utils.setSize(mPreview_mainlogo,2,mScreenWidth,98,48);
        Utils.setMargins(mPreview_mainlogo,2,mScreenWidth,36,30,0,0);

        mUpdateBtn = mView.findViewById(R.id.btn_update);
        Utils.setSize(mUpdateBtn,1,mScreenWidth,200,66);
        mUpdateBtn.setOnClickListener(this);

        mPreviewBottom = mView.findViewById(R.id.lay_bottom);
        Utils.setMargins(mPreviewBottom,2,mScreenWidth,36,0,36,36);

        mPreview_title = mView.findViewById(R.id.lay_title);

        mPreview_cornermark = mView.findViewById(R.id.img_corner_mark);
        Utils.setSize(mPreview_cornermark,2,mScreenWidth,119,119);
        Utils.setMargins(mPreview_cornermark,2,mScreenWidth,36,0,0,0);

    }

    private void updatePreview() {
        sp = mContext.getSharedPreferences("push_state", 0);

        mPreview_mainlogo.setVisibility(sp.getInt("logo_state",0 ));
        if(mMainLogoInfoList.size()>0){
            mMainLogoIndex = sp.getInt("logo_index",Integer.parseInt(mMainLogoInfoList.get(0).id.trim()));
        }else{
            mMainLogoIndex = sp.getInt("logo_index",0);
        }

        mPreview_title.setVisibility(sp.getInt("title_state",0 ));
        if(mTitleInfoList.size()>0){
            mTitleIndex = sp.getInt("title_index",Integer.parseInt(mTitleInfoList.get(0).id.trim()) );
        }else{
            mTitleIndex = sp.getInt("title_index",1 );
        }

        String type = "";
        if(mTitleIndex ==1){
            type= Utils.HONG_HEI;
        }else if(mTitleIndex == 2){
            type= Utils.FEN_HEI;
        }else if(mTitleIndex == 3){
            type= Utils.HUANG;
        }else if(mTitleIndex == 4){
            type= Utils.LAN_BAI;
        }else if(mTitleIndex == 5){
            type= Utils.LV_BAI;
        }else if(mTitleIndex == 6){
            type= Utils.LV_QIN;
        }else{
            type= Utils.OTHER;
        }
        mPreview_title.addView(addTitleView(type));
//        if(!sp.getString("title","").isEmpty()){
//            mPreview_title.setVisibility(View.VISIBLE);
//        }else {
//            mPreview_title.setVisibility(View.GONE);
//        }

        mPreview_backuppic.setVisibility(sp.getInt("backup_state",0 ));
        if(mBackupPicInfoList.size()>0){
            mBackupPicIndex = sp.getInt("backup_index",Integer.parseInt(mBackupPicInfoList.get(0).id.trim()) );
        }else{
            mBackupPicIndex = sp.getInt("backup_index",0 );
        }



        mPreview_cornermark.setVisibility(sp.getInt("mark_state",0 ));
        if(mCornerMarkInfoList.size()>0){
            mCornerMarkIndex = sp.getInt("mark_index",Integer.parseInt(mCornerMarkInfoList.get(0).id.trim()) );
        }else{
            mCornerMarkIndex = sp.getInt("mark_index",0 );
        }


         for(int i=0;i<mMiniMasterInfoList.size();i++){
            if(mMiniMasterInfoList.get(i).id.trim().equals(""+mMainLogoIndex)){
                mMainLogoMediaURL = mMiniMasterInfoList.get(i).mediaURL.trim();
                mMainLogoVideoURL = mMiniMasterInfoList.get(i).videoURL.trim();
                Glide.with(mContext).load(mMainLogoMediaURL).into(mPreview_mainlogo);
            }


             if(mMiniMasterInfoList.get(i).id.trim().equals(""+mBackupPicIndex)){
                 mBackupPicMediaURL = mMiniMasterInfoList.get(i).mediaURL.trim();
                 mBackupPicVideoURL= mMiniMasterInfoList.get(i).videoURL.trim();
                 Glide.with(mContext).load(mBackupPicMediaURL).into(mPreview_backuppic);
             }


             if(mMiniMasterInfoList.get(i).id.trim().equals(""+mCornerMarkIndex)){
                 mCornerMarkMediaURL = mMiniMasterInfoList.get(i).mediaURL.trim();
                 mCornerMarkVideoURL = mMiniMasterInfoList.get(i).videoURL.trim();
                 Glide.with(mContext).load(mCornerMarkMediaURL).into(mPreview_cornermark);
             }
        }
            updateSp();
    }

    private View addTitleView(String type) {
        mPreview_title.removeAllViews();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,  LinearLayout.LayoutParams.WRAP_CONTENT);
        LayoutInflater inflater3 = LayoutInflater.from(mContext);
        View view;
        view = inflater3.inflate(R.layout.item_title_honghei, null);

        if(type.equals(Utils.HONG_HEI)){
            view = inflater3.inflate(R.layout.item_title_honghei, null);
        }else if(type.equals(Utils.FEN_HEI)){
            view = inflater3.inflate(R.layout.item_title_fenhei, null);
        }else if(type.equals(Utils.HUANG)){
            view = inflater3.inflate(R.layout.item_title_huang, null);
        }else if(type.equals(Utils.LAN_BAI)){
            view = inflater3.inflate(R.layout.item_title_lanbai, null);
        }else if(type.equals(Utils.LV_BAI)){
            view = inflater3.inflate(R.layout.item_title_lvbai, null);
        }else if(type.equals(Utils.LV_QIN)){
            view = inflater3.inflate(R.layout.item_title_lvqin, null);
        }else{
            view = inflater3.inflate(R.layout.item_title_other, null);
        }

        view.setLayoutParams(lp);
        if(type.equals(Utils.OTHER)){
            ImageView img = view.findViewById(R.id.img);
            img.setMaxHeight(92*mScreenWidth/750);
            img.setMinimumHeight(92*mScreenWidth/750);
            img.setAdjustViewBounds(true);
            Utils.setSize(img,1,mScreenWidth,-1,92);
            for(int i=0;i<mTitleInfoList.size();i++){
                if(mTitleInfoList.get(i).id.trim().equals(""+mTitleIndex)){
                    Glide.with(mContext).load(mTitleInfoList.get(i).videoURL.trim()).into(img);
                }
            }
        }else{
            mTitleTxt = view.findViewById(R.id.txt_title);
            mReporterTxt = view.findViewById(R.id.txt_reporter);
            sp = mContext.getSharedPreferences("push_state", 0);
            mTitleTxt.setText(sp.getString("title","标题内容"));
            mReporterTxt.setText(sp.getString("reporter","记者"));
        }


        return view;
    }

    private void initMainLogo() {

        LinearLayout mMainLogoLay = mView.findViewById(R.id.lay_main_logo);
        Utils.setMargins(mMainLogoLay,1,mScreenWidth,20,0,20,20);
//        mMainLogoBtn = mView.findViewById(R.id.btn_main_logo);
//        Utils.setSize(mMainLogoBtn,1,mScreenWidth,100,60);
//        if(mPreview_mainlogo.getVisibility() == View.VISIBLE){
//            mMainLogoBtn.setBackgroundResource(R.drawable.greenswitch_btn_on);
//        }
//        mMainLogoBtn.setOnClickListener(this);
        mMainLogoLayout = mView.findViewById(R.id.lay_main_logo_select);
        Utils.setSize(mMainLogoLayout,1,mScreenWidth,-1,148);
        Utils.setMargins(mMainLogoLayout,1,mScreenWidth,20,0,20,20);
        mMainLogoSelect = LayoutInflater.from(getActivity()).inflate(
                R.layout.view_horizontalscroll_, null);
        mMainLogoScrollView = mMainLogoSelect.findViewById(R.id.mColumnHorizontalScrollView);

        mMainLogoGroup = mMainLogoSelect.findViewById(R.id.mRadioGroup_content);
        Utils.setMargins(mMainLogoGroup,3,mScreenWidth,10,0,10,0);

        mMainLogoGroup.removeAllViews();

        int count = mMainLogoInfoList.size();

        mMainLogoScrollView.setParam(this.getActivity(), mScreenWidth,
                mMainLogoGroup);
        for (int i = 0; i < count; i++) {
            View mView = View.inflate(mContext.getApplicationContext(),
                    R.layout.item_seat, null);
            mView.setId(i);
            RecyclerImageView mImg = mView.findViewById(R.id.img);
            mImg.setMaxHeight(92*mScreenWidth/750);
            mImg.setMinimumHeight(92*mScreenWidth/750);
            mImg.setAdjustViewBounds(true);
            Utils.setSize(mImg,2,mScreenWidth,-1,92);
            Utils.setMargins(mImg,2,mScreenWidth,14,14,14,14);
            Glide.with(this).load(mMainLogoInfoList.get(i).mediaURL.trim()).into(mImg);
            //mImg.setImageResource(R.drawable.logo_inlz);

            RelativeLayout rl_img = mView.findViewById(R.id.rl_img);
            if (mMainLogoIndex == Integer.parseInt(mMainLogoInfoList.get(i).id.trim())) {
                rl_img.setBackgroundResource(R.drawable.layout_select_pic);
            }
            mView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    for (int i = 0; i < mMainLogoGroup.getChildCount(); i++) {
                        View mView = mMainLogoGroup.getChildAt(i);
                        RelativeLayout rl_img = mView.findViewById(R.id.rl_img);
                        if (mView != v) {
                            rl_img.setBackground(null);
                        } else {
                            rl_img.setBackgroundResource(R.drawable.layout_select_pic);
                            mMainLogoIndex = Integer.parseInt(mMainLogoInfoList.get(i).id.trim());
                            mMainLogoMediaURL = mMainLogoInfoList.get(i).mediaURL.trim();
                            mMainLogoVideoURL = mMainLogoInfoList.get(i).videoURL.trim();
                            Glide.with(mContext).load(mMainLogoInfoList.get(i).videoURL.trim()).into(mPreview_mainlogo);
                        }
                    }
                }
            });
            mMainLogoGroup.addView(mView, i);
        }
        mMainLogoLayout.addView(mMainLogoSelect);
    }

    private void initTitle() {

        LinearLayout mTitleLay = mView.findViewById(R.id.lay_add_title);
        Utils.setMargins(mTitleLay,1,mScreenWidth,20,0,20,20);
//        mTitleBtn = mView.findViewById(R.id.btn_title);
//        Utils.setSize(mTitleBtn,1,mScreenWidth,100,60);
//        if(mPreview_title.getVisibility() == View.VISIBLE){
//            mTitleBtn.setBackgroundResource(R.drawable.greenswitch_btn_on);
//        }
//        mTitleBtn.setOnClickListener(this);
        mTitleLayout = mView.findViewById(R.id.lay_title_select);
        Utils.setSize(mTitleLayout,1,mScreenWidth,-1,148);
        Utils.setMargins(mTitleLayout,1,mScreenWidth,20,0,20,20);
        mTitleSelect = LayoutInflater.from(getActivity()).inflate(
                R.layout.view_horizontalscroll_, null);
        mTitleScrollView = mTitleSelect.findViewById(R.id.mColumnHorizontalScrollView);

        mTitleGroup = mTitleSelect.findViewById(R.id.mRadioGroup_content);
        Utils.setMargins(mTitleGroup,3,mScreenWidth,10,0,10,0);

        mTitleGroup.removeAllViews();

        int count = mTitleInfoList.size();

        mTitleScrollView.setParam(this.getActivity(), mScreenWidth,
                mTitleGroup);
        for (int i = 0; i < count; i++) {
            View mView = View.inflate(mContext.getApplicationContext(),
                    R.layout.item_seat, null);
            mView.setId(i);
            RecyclerImageView mImg = mView.findViewById(R.id.img);
            mImg.setMaxHeight(92*mScreenWidth/750);
            mImg.setMinimumHeight(92*mScreenWidth/750);
            mImg.setAdjustViewBounds(true);
            Utils.setSize(mImg,2,mScreenWidth,-1,92);
            Utils.setMargins(mImg,2,mScreenWidth,14,14,14,14);
            Glide.with(this).load(mTitleInfoList.get(i).mediaURL.trim()).into(mImg);

            RelativeLayout rl_img = mView.findViewById(R.id.rl_img);
            if (mTitleIndex == Integer.parseInt(mTitleInfoList.get(i).id.trim())) {
                rl_img.setBackgroundResource(R.drawable.layout_select_pic);
            }
            mView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    for (int i = 0; i < mTitleGroup.getChildCount(); i++) {
                        View mView = mTitleGroup.getChildAt(i);
                        RelativeLayout rl_img = mView.findViewById(R.id.rl_img);
                        if (mView != v) {
                            rl_img.setBackground(null);
                        } else {
                            rl_img.setBackgroundResource(R.drawable.layout_select_pic);
                            mTitleIndex = Integer.parseInt(mTitleInfoList.get(i).id.trim());
                            if(mTitleIndex == 1){
                                mPreview_title.addView(addTitleView(Utils.HONG_HEI));
                            }else if(mTitleIndex == 2){
                                mPreview_title.addView(addTitleView(Utils.FEN_HEI));
                            }else if(mTitleIndex == 3){
                                mPreview_title.addView(addTitleView(Utils.HUANG));
                            }else if(mTitleIndex == 4){
                                mPreview_title.addView(addTitleView(Utils.LAN_BAI));
                            }else if(mTitleIndex == 5){
                                mPreview_title.addView(addTitleView(Utils.LV_BAI));
                            }else if(mTitleIndex == 6){
                                mPreview_title.addView(addTitleView(Utils.LV_QIN));
                            }else{
                                mPreview_title.addView(addTitleView(Utils.OTHER));
                            }

                        }
                    }
                }
            });
            mTitleGroup.addView(mView, i);
        }

        mTitleLayout.addView(mTitleSelect);


        mAddTitleBtn = mView.findViewById(R.id.btn_add_title);
        Utils.setSize(mAddTitleBtn,1,mScreenWidth,150,60);
        mAddTitleBtn.setOnClickListener(this);
    }



    private void initBackupPic() {

        LinearLayout mPicLay = mView.findViewById(R.id.lay_backup_pic);
        Utils.setMargins(mPicLay,1,mScreenWidth,20,0,20,20);
//        mBackupPicBtn = mView.findViewById(R.id.btn_backup_pic);
//        Utils.setSize(mBackupPicBtn,1,mScreenWidth,100,60);
//        if(mPreview_backuppic.getVisibility() == View.VISIBLE){
//            mBackupPicBtn.setBackgroundResource(R.drawable.greenswitch_btn_on);
//        }
//        mBackupPicBtn.setOnClickListener(this);
        mBackupPicLayout = mView.findViewById(R.id.lay_backup_pic_select);
        Utils.setSize(mBackupPicLayout,1,mScreenWidth,-1,148);
        Utils.setMargins(mBackupPicLayout,1,mScreenWidth,20,0,20,20);
        mBackupPicSelect = LayoutInflater.from(getActivity()).inflate(
                R.layout.view_horizontalscroll_, null);
        mBackupPicScrollView = mBackupPicSelect.findViewById(R.id.mColumnHorizontalScrollView);

        mBackupPicGroup = mBackupPicSelect.findViewById(R.id.mRadioGroup_content);
        Utils.setMargins(mBackupPicGroup,3,mScreenWidth,10,0,10,0);

        mBackupPicGroup.removeAllViews();

        int count = mBackupPicInfoList.size();


        mBackupPicScrollView.setParam(this.getActivity(), mScreenWidth,
                mBackupPicGroup);
        for (int i = 0; i < count; i++) {
            View mView = View.inflate(mContext.getApplicationContext(),
                    R.layout.item_seat, null);
            mView.setId(i);
            RecyclerImageView mImg = mView.findViewById(R.id.img);
            mImg.setMaxHeight(92*mScreenWidth/750);
            mImg.setMinimumHeight(92*mScreenWidth/750);
            mImg.setAdjustViewBounds(true);
            Utils.setSize(mImg,2,mScreenWidth,-1,92);
            Utils.setMargins(mImg,2,mScreenWidth,14,14,14,14);
            Glide.with(this).load(mBackupPicInfoList.get(i).mediaURL.trim()).into(mImg);
           // mImg.setImageResource(R.drawable.default_bg);
            RelativeLayout rl_img = mView.findViewById(R.id.rl_img);
            if (mBackupPicIndex == Integer.parseInt(mBackupPicInfoList.get(i).id.trim())) {
                rl_img.setBackgroundResource(R.drawable.layout_select_pic);
            }
            mView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    for (int i = 0; i < mBackupPicGroup.getChildCount(); i++) {
                        View mView = mBackupPicGroup.getChildAt(i);
                        RelativeLayout rl_img = mView.findViewById(R.id.rl_img);
                        if (mView != v) {
                            rl_img.setBackground(null);
                        } else {
                            rl_img.setBackgroundResource(R.drawable.layout_select_pic);
                            mBackupPicIndex = Integer.parseInt(mBackupPicInfoList.get(i).id.trim());
                            mBackupPicMediaURL = mBackupPicInfoList.get(i).mediaURL.trim();
                            mBackupPicVideoURL = mBackupPicInfoList.get(i).videoURL.trim();
                            Glide.with(mContext).load(mBackupPicInfoList.get(i).videoURL.trim()).into(mPreview_backuppic);
                        }
                    }
                }
            });
            mBackupPicGroup.addView(mView, i);
        }


        mBackupPicLayout.addView(mBackupPicSelect);
    }

    private void initCornerMark() {

        LinearLayout mCorLay = mView.findViewById(R.id.lay_corner_mark);
        Utils.setMargins(mCorLay,1,mScreenWidth,20,0,20,20);
//        mCornerMarkBtn = mView.findViewById(R.id.btn_corner_mark);
//        Utils.setSize(mCornerMarkBtn,1,mScreenWidth,100,60);
//        if(mPreview_cornermark.getVisibility() == View.VISIBLE){
//            mCornerMarkBtn.setBackgroundResource(R.drawable.greenswitch_btn_on);
//        }
//        mCornerMarkBtn.setOnClickListener(this);
        mCornerMarkLayout = mView.findViewById(R.id.lay_corner_mark_select);
        Utils.setSize(mCornerMarkLayout,1,mScreenWidth,-1,148);
        Utils.setMargins(mCornerMarkLayout,1,mScreenWidth,20,0,20,20);
        mCornerMarkSelect = LayoutInflater.from(getActivity()).inflate(
                R.layout.view_horizontalscroll_, null);
        mCornerMarkScrollView = mCornerMarkSelect.findViewById(R.id.mColumnHorizontalScrollView);

        mCornerMarkGroup = mCornerMarkSelect.findViewById(R.id.mRadioGroup_content);
        Utils.setMargins(mCornerMarkGroup,3,mScreenWidth,10,0,10,0);

        mCornerMarkGroup.removeAllViews();

        int count = mCornerMarkInfoList.size();


        mCornerMarkScrollView.setParam(this.getActivity(), mScreenWidth,
                mCornerMarkGroup);
        for (int i = 0; i < count; i++) {
            View mView = View.inflate(mContext.getApplicationContext(),
                    R.layout.item_seat, null);
            mView.setId(i);
            RecyclerImageView mImg = mView.findViewById(R.id.img);
            mImg.setMaxHeight(92*mScreenWidth/750);
            mImg.setMinimumHeight(92*mScreenWidth/750);
            mImg.setAdjustViewBounds(true);
            Utils.setSize(mImg,2,mScreenWidth,-1,92);
            Utils.setMargins(mImg,2,mScreenWidth,14,14,14,14);
            Glide.with(this).load(mCornerMarkInfoList.get(i).mediaURL.trim()).into(mImg);
            RelativeLayout rl_img = mView.findViewById(R.id.rl_img);
            if (mCornerMarkIndex == Integer.parseInt(mCornerMarkInfoList.get(i).id.trim())) {
                rl_img.setBackgroundResource(R.drawable.layout_select_pic);
            }
            mView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    for (int i = 0; i < mCornerMarkGroup.getChildCount(); i++) {
                        View mView = mCornerMarkGroup.getChildAt(i);
                        RelativeLayout rl_img = mView.findViewById(R.id.rl_img);
                        if (mView != v) {
                            rl_img.setBackground(null);
                        } else {
                            rl_img.setBackgroundResource(R.drawable.layout_select_pic);
                            mCornerMarkIndex = Integer.parseInt(mCornerMarkInfoList.get(i).id.trim());
                            mCornerMarkMediaURL = mCornerMarkInfoList.get(i).mediaURL.trim();
                            mCornerMarkVideoURL = mCornerMarkInfoList.get(i).videoURL.trim();
                            Glide.with(mContext).load(mCornerMarkInfoList.get(i).mediaURL.trim()).into(mPreview_cornermark);
                        }
                    }
                }
            });
            mCornerMarkGroup.addView(mView, i);
        }


        mCornerMarkLayout.addView(mCornerMarkSelect);
    }

    private void updateSp(){
        sp = mContext.getSharedPreferences("push_state", 0);
        editor = sp.edit();

        editor.putInt("logo_state",  mPreview_mainlogo.getVisibility());
        editor.putInt("logo_index",  mMainLogoIndex);
        editor.putString("logo_media",mMainLogoMediaURL);
        editor.putString("logo_video",mMainLogoVideoURL);

        editor.putInt("title_state",  mPreview_title.getVisibility());
        editor.putInt("title_index",  mTitleIndex);

        editor.putInt("backup_state",  mPreview_backuppic.getVisibility());
        editor.putInt("backup_index",  mBackupPicIndex);
        editor.putString("backup_media",mBackupPicMediaURL);
        editor.putString("backup_video",mBackupPicVideoURL);

        editor.putInt("mark_state", mPreview_cornermark.getVisibility());
        editor.putInt("mark_index",  mCornerMarkIndex);
        editor.putString("mark_media",mCornerMarkMediaURL);
        editor.putString("mark_video",mCornerMarkVideoURL);

        editor.putBoolean("need_update",true);
        editor.commit();
        Toast.makeText(getActivity().getApplicationContext()
                , "更新完毕！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id == R.id.btn_update){
            updateSp();
        }else if(id == R.id.btn_main_logo){
//                if( mPreview_mainlogo.getVisibility()== View.VISIBLE){
//                    mPreview_mainlogo.setVisibility(View.INVISIBLE);
//                    mMainLogoBtn.setBackgroundResource(R.drawable.greenswitch_btn_off);
//                }else{
//                    mPreview_mainlogo.setVisibility(View.VISIBLE);
//                    mMainLogoBtn.setBackgroundResource(R.drawable.greenswitch_btn_on);
//                }


        }else if (id == R.id.btn_add_title){
                showAddTitleDialog();
        }else if(id == R.id.btn_title){
      //      initData();
//            sp = mContext.getSharedPreferences("push_state", 0);
//            if(!sp.getString("title","").isEmpty()){
//            if( mPreview_title.getVisibility()== View.VISIBLE){
//                mPreview_title.setVisibility(View.INVISIBLE);
//                mTitleBtn.setBackgroundResource(R.drawable.greenswitch_btn_off);
//            }else{
//                mPreview_title.setVisibility(View.VISIBLE);
//                mTitleBtn.setBackgroundResource(R.drawable.greenswitch_btn_on);
//            }
//        }else {
//            Toast.makeText(getActivity().getApplicationContext()
//                    , "请先选择一个字幕！", Toast.LENGTH_SHORT).show();
//        }

        }else if(id == R.id.btn_backup_pic){
//            if( mPreview_backuppic.getVisibility()== View.VISIBLE){
//                mPreview_backuppic.setVisibility(View.INVISIBLE);
//                mBackupPicBtn.setBackgroundResource(R.drawable.greenswitch_btn_off);
//            }else{
//                mPreview_backuppic.setVisibility(View.VISIBLE);
//                mBackupPicBtn.setBackgroundResource(R.drawable.greenswitch_btn_on);
//            }
        }else if(id == R.id.btn_corner_mark){
//            if( mPreview_cornermark.getVisibility()== View.VISIBLE){
//                mPreview_cornermark.setVisibility(View.INVISIBLE);
//                mCornerMarkBtn.setBackgroundResource(R.drawable.greenswitch_btn_off);
//            }else{
//                mPreview_cornermark.setVisibility(View.VISIBLE);
//                mCornerMarkBtn.setBackgroundResource(R.drawable.greenswitch_btn_on);
//            }
        }

    }

    private void showAddTitleDialog() {

        AddTitleDialog editDialog = new AddTitleDialog(mContext,mScreenWidth);
        editDialog.show();
        editDialog.setOnPosNegClickListener(new AddTitleDialog.OnPosNegClickListener() {
            @Override
            public void posClickListener(String value) {
                Log.e("WLH"," value = "+value );
                if(value!=null&&value.equals("update")){
                    String type = Utils.HONG_HEI;
                    if(mTitleIndex == 1){
                        type= Utils.HONG_HEI;
                    }else if(mTitleIndex == 2){
                        type= Utils.FEN_HEI;
                    }else if(mTitleIndex == 3){
                        type= Utils.HUANG;
                    }else if(mTitleIndex == 4){
                        type= Utils.LAN_BAI;
                    }else if(mTitleIndex == 5){
                        type= Utils.LV_BAI;
                    }else if(mTitleIndex == 6){
                        type= Utils.LV_QIN;
                    }else {
                        type= Utils.OTHER;
                    }
                    mPreview_title.addView(addTitleView(type));
                    mPreview_title.setVisibility(View.VISIBLE);
                  //  mTitleBtn.setBackgroundResource(R.drawable.greenswitch_btn_on);
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
            switch (msg.arg1) {
                case 1:
                    InputStream inStream = new ByteArrayInputStream(
                            result.getBytes());
                    ConfigureHandle xmlHandle = new ConfigureHandle(mContext);
                    mMiniMasterInfoList = xmlHandle.readXML(inStream);

                    mMainLogoInfoList = new ArrayList<>();
                    for(int i=0;i<mMiniMasterInfoList.size();i++){
                        if(mMiniMasterInfoList.get(i).itype.trim().equals("1")){
                            mMainLogoInfoList.add(mMiniMasterInfoList.get(i));
                        }
                    }
                 //   Log.e("WLH","mMainLogoInfoList.size() = "+mMainLogoInfoList.size());


                    mTitleInfoList = new ArrayList<>();
                    for(int i=0;i<mMiniMasterInfoList.size();i++){
                        if(mMiniMasterInfoList.get(i).itype.trim().equals("0")){
                            mTitleInfoList.add(mMiniMasterInfoList.get(i));
                        }
                    }
                 //   Log.e("WLH","mTitleInfoList.size() = "+mTitleInfoList.size());

                    mBackupPicInfoList= new ArrayList<>();
                    for(int i=0;i<mMiniMasterInfoList.size();i++){
                        if(mMiniMasterInfoList.get(i).itype.trim().equals("2")){
                            mBackupPicInfoList.add(mMiniMasterInfoList.get(i));
                        }
                    }
                 //   Log.e("WLH","mBackupPicInfoList.size() = "+mBackupPicInfoList.size());

                    mCornerMarkInfoList= new ArrayList<>();
                    for(int i=0;i<mMiniMasterInfoList.size();i++){
                        if(mMiniMasterInfoList.get(i).itype.trim().equals("3")){
                            mCornerMarkInfoList.add(mMiniMasterInfoList.get(i));
                        }
                    }

               //     mCornerMarkInfoList = mBackupPicInfoList;
               //     Log.e("WLH","mCornerMarkInfoList.size() = "+mCornerMarkInfoList.size());


                    initPreview();
                    updatePreview();
                    initMainLogo();
                    initTitle();
                    initBackupPic();
                    initCornerMark();
                    Handler handler = new Handler();
                    handler.postDelayed(runnable, 500);

                    break;
            }
        }
    };
    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            int[] location = new int[2];
            int offset = 0 ;
            int i =0;
            for(i =0;i<mMainLogoInfoList.size();i++){
                if (mMainLogoIndex == Integer.parseInt(mMainLogoInfoList.get(i).id.trim())){
                    mMainLogoGroup.getChildAt(i).getLocationOnScreen(location);
                    offset = location[0]+ mMainLogoGroup.getChildAt(i).getWidth()- mMainLogoScrollView.getMeasuredWidth();
                    if (offset < 0) {
                        offset = 0;
                    }
                    mMainLogoScrollView.scrollTo(offset, 0);// 改变滚动条的位置
                }
            }


            for(i =0;i<mTitleInfoList.size();i++){
                if (mTitleIndex == Integer.parseInt(mTitleInfoList.get(i).id.trim())){
                    mTitleGroup.getChildAt(i).getLocationOnScreen(location);
                    offset = location[0]+ mTitleGroup.getChildAt(i).getWidth()- mTitleScrollView.getMeasuredWidth();
                    if (offset < 0) {
                        offset = 0;
                    }
                    mTitleScrollView.scrollTo(offset, 0);// 改变滚动条的位置
                }
            }

            for(i =0;i<mBackupPicInfoList.size();i++){
                if (mBackupPicIndex == Integer.parseInt(mBackupPicInfoList.get(i).id.trim())){
                    mBackupPicGroup.getChildAt(i).getLocationOnScreen(location);
                    offset = location[0]+ mBackupPicGroup.getChildAt(i).getWidth()- mBackupPicScrollView.getMeasuredWidth();
                    if (offset < 0) {
                        offset = 0;
                    }
                    mBackupPicScrollView.scrollTo(offset, 0);// 改变滚动条的位置
                }
            }


            for(i =0;i<mCornerMarkInfoList.size();i++){
                if (mCornerMarkIndex == Integer.parseInt(mCornerMarkInfoList.get(i).id.trim())){
                    mCornerMarkGroup.getChildAt(i).getLocationOnScreen(location);
                    offset = location[0]+ mCornerMarkGroup.getChildAt(i).getWidth()- mCornerMarkScrollView.getMeasuredWidth();
                    if (offset < 0) {
                        offset = 0;
                    }
                    mCornerMarkScrollView.scrollTo(offset, 0);// 改变滚动条的位置
                }
            }

        }
    };



    private void getMiniMasterInfo() {
        mMiniMasterInfoList = new ArrayList<MiniMasterInfo>();
        sp = mContext.getSharedPreferences("logininfo", 0);
        mUserID = sp.getString("userID", "");
        mPassword = sp.getString("password", "");

            if (Utils.isConnect(mContext)) {

                if (executorService == null) {
                    executorService = Executors.newCachedThreadPool();

                }

                executorService.submit(new Runnable() {
                    public void run() {
                        SoapObject so = new SoapObject("http://tempuri.org/",
                                "MiniMaster_Info");
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
                            result = envelope.getResponse().toString();
                          //  Log.e("WLH", "result = " + result);
                            android.os.Message msg = mHandler.obtainMessage();
                            msg.arg1 = 1;
                            msg.sendToTarget();

                        } catch (Exception ex) {
                            Log.e("WLH", ex.toString());
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
