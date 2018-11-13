package wanglihua.liveconsole.Adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import wanglihua.liveconsole.Model.TitleInfo;
import wanglihua.liveconsole.R;
import wanglihua.liveconsole.Utils.Utils;


public class TitleListAdapter extends BaseAdapter {

	private final static int TYPE_COUNT = 2;

	private LayoutInflater inflater;
	private Context mContext = null;
	private ArrayList<TitleInfo> mTitleInfos = null;
	private Object tag = new Object();
	private int mScreenWidth = 0;
	
	public TitleListAdapter(Context _context,int width,ArrayList<TitleInfo> mTitleInfos) {

		this.mContext = _context;
		this.mTitleInfos = mTitleInfos;
		this.mScreenWidth = width;
		inflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return TYPE_COUNT;
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	public int getCount() {
		return mTitleInfos.size();
	}

	public TitleInfo getItem(int arg0) {
		return mTitleInfos.get(arg0);
	}

	public long getItemId(int arg0) {
		return 0;
	}

	@SuppressLint("NewApi")
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_title, null);

			holder = new ViewHolder();
			holder.mIdTxt = convertView.findViewById(R.id.txt_id);
			Utils.setSize(holder.mIdTxt,1,mScreenWidth,40,40);
			holder.mTitleTxt =  convertView.findViewById(R.id.txt_title);
			holder.mReporterTxt = convertView.findViewById(R.id.txt_reporter);
			holder.mLine = convertView.findViewById(R.id.line);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();

		}

		holder.mIdTxt.setText((mTitleInfos.size()-position)+"");
		holder.mTitleTxt.setText(mTitleInfos.get(position).mTitle.trim());
		holder.mReporterTxt.setText(mTitleInfos.get(position).mReporter.trim());
		if(position == mTitleInfos.size()-1){
			holder.mLine.setVisibility(View.GONE);
		}else{
			holder.mLine.setVisibility(View.VISIBLE);
		}
		convertView.setId(position);
	

		return convertView;
	}

	public class ViewHolder {

		TextView mIdTxt;
		TextView mTitleTxt;
		TextView mReporterTxt;
		View mLine;
	}

	
	


}
