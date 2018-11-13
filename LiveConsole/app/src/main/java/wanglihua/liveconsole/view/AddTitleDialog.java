package wanglihua.liveconsole.view;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import wanglihua.liveconsole.Adapter.TitleListAdapter;
import wanglihua.liveconsole.Model.TitleInfo;
import wanglihua.liveconsole.Model.TitleRepo;
import wanglihua.liveconsole.R;
import wanglihua.liveconsole.Utils.Utils;


/**
 * @author gyw
 * @version 1.0
 * @time: 2015-10-12 下午3:06:52
 * @fun:
 */
public class AddTitleDialog extends Dialog implements View.OnClickListener {

    private View mView;
    private Context mContext;
    private int mScreenWidth = 0;
    private TitleRepo repo;
    private SharedPreferences sp = null;
    private SharedPreferences.Editor editor;

    private LinearLayout mBgLay;
    private RelativeLayout mTitleLay;
    private ImageView mCloseBtn;
    private EditText mTitleEdt;
    private EditText mReporterEdt;
    private ImageView mUseBtn, mAddBtn, mClearBtn;
    private MaxListView mListView;
    private TitleListAdapter mListAdapter;
    private ArrayList<TitleInfo> mData;
    private String mTitle, mReporter;


    public AddTitleDialog(Context context, int ScreenWidth) {

        this(context, 0, null, ScreenWidth);
    }

    public AddTitleDialog(Context context, int theme, View contentView, int ScreenWidth) {
        super(context, theme == 0 ? R.style.MyDialogStyle : theme);

        this.mView = contentView;
        this.mContext = context;
        this.mScreenWidth = ScreenWidth;
        repo = new TitleRepo(mContext);
        if (mView == null) {
            mView = View.inflate(mContext, R.layout.dialog_add_title, null);
        }

        init();
        initView();
        initData();
    }

    private void init() {
        this.setContentView(mView);
    }

    private void initView() {
        mBgLay = mView.findViewById(R.id.lay_bg);
        Utils.setSize(mBgLay, 3, mScreenWidth, 580, -1);

        mTitleLay = mView.findViewById(R.id.lay_title);
        Utils.setSize(mTitleLay, 1, mScreenWidth, -1, 66);

        mCloseBtn = mView.findViewById(R.id.btn_close);
        Utils.setSize(mCloseBtn, 2, mScreenWidth, 66, 66);
        mCloseBtn.setOnClickListener(this);

        mTitleEdt = mView.findViewById(R.id.edt_title);
        Utils.setSize(mTitleEdt, 1, mScreenWidth, 458, 66);

        mReporterEdt = mView.findViewById(R.id.edt_reporter);
        Utils.setSize(mReporterEdt, 1, mScreenWidth, 458, 66);

        mUseBtn = mView.findViewById(R.id.btn_use_title);
        Utils.setSize(mUseBtn, 2, mScreenWidth, 180, 60);
        mUseBtn.setOnClickListener(this);

        mAddBtn = mView.findViewById(R.id.btn_add_title);
        Utils.setSize(mAddBtn, 2, mScreenWidth, 180, 60);
        mAddBtn.setOnClickListener(this);

        mClearBtn = mView.findViewById(R.id.btn_clear);
        Utils.setSize(mClearBtn, 1, mScreenWidth, 180, 60);
        mClearBtn.setVisibility(View.GONE);
        mClearBtn.setOnClickListener(this);

        mListView = mView.findViewById(R.id.list_title);
        mListView.setListViewHeight(440);
        mData = new ArrayList<TitleInfo>();
        mListAdapter = new TitleListAdapter(mContext, mScreenWidth, mData);
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String  title = mData.get(i).mTitle;
                String  reporter = mData.get(i).mReporter;
                sp = mContext.getSharedPreferences("push_state", 0);
                editor = sp.edit();
                editor.putString("title",  title);
                editor.putString("reporter", reporter);
                editor.commit();
                onPosNegClickListener.posClickListener("update");
                Toast.makeText(mContext.getApplicationContext()
                        , "字幕已更换！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initData() {
        mTitleEdt.setText("");
        mTitle = "";
        mReporterEdt.setText("");
        mReporter = "";
        mData.clear();

        mData.addAll(repo.getList());
        Collections.reverse(mData);
        if(mData.size()>0){
            mClearBtn.setVisibility(View.VISIBLE);
        }else {
            mClearBtn.setVisibility(View.GONE);
        }
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                this.dismiss();
                break;
            case R.id.btn_use_title:
                if (onPosNegClickListener != null) {
                    mTitle = mTitleEdt.getText().toString().trim();
                    mReporter = mReporterEdt.getText().toString().trim();
                    if(mReporter.isEmpty()){
                        mReporter = " ";
                    }
                    if (mTitle.isEmpty()) {
                        Toast.makeText(mContext, "标题不能为空", Toast.LENGTH_SHORT).show();
                    }else {
                     //   Utils.saveUseingTitle(mContext, mTitle, mReporter);
                        sp = mContext.getSharedPreferences("push_state", 0);
                        editor = sp.edit();
                        editor.putString("title",  mTitle);
                        editor.putString("reporter", mReporter);
                        editor.commit();
                        onPosNegClickListener.posClickListener("update");
                        this.dismiss();
                    }
                }
                break;

            case R.id.btn_add_title:    //确认
                if (onPosNegClickListener != null) {
                    mTitle = mTitleEdt.getText().toString().trim();
                    mReporter = mReporterEdt.getText().toString().trim();
                    if(mReporter.isEmpty()){
                        mReporter = " ";
                    }
                    if (!mTitle.isEmpty()) {
                        TitleInfo mInfo = new TitleInfo();
                        mInfo.mTitle = mTitle;
                        mInfo.mReporter = mReporter;
                        repo.insert(mInfo);
                   //     onPosNegClickListener.negCliclListener(null);
                        initData();
                        //    this.dismiss();
                    } else {
                        Toast.makeText(mContext, "标题不能为空", Toast.LENGTH_SHORT).show();
                    }
                }

                break;
            case R.id.btn_clear:
                repo.delList();
                initData();
                break;
        }
    }


    private OnPosNegClickListener onPosNegClickListener;

    public void setOnPosNegClickListener(OnPosNegClickListener onPosNegClickListener) {
        this.onPosNegClickListener = onPosNegClickListener;
    }

    public interface OnPosNegClickListener {
        void posClickListener(String value);

        void negCliclListener(String value);
    }

}
