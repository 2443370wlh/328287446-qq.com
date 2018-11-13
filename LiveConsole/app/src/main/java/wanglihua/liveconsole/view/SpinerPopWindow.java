package wanglihua.liveconsole.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

import wanglihua.liveconsole.R;
import wanglihua.liveconsole.Utils.Utils;

/**
 * Created by Administrator on 2018/6/6.
 */
public class SpinerPopWindow<T> extends PopupWindow {
    private LayoutInflater inflater;
    private ListView mListView;
    private List<T> list;
    private MyAdapter  mAdapter;
    private int mScreenWidth = 0;

    public SpinerPopWindow(Context context, List<T> list, AdapterView.OnItemClickListener clickListener,int mScreenWidth) {
        super(context);
        inflater=LayoutInflater.from(context);
        this.list=list;
        this.mScreenWidth = mScreenWidth;
        init(clickListener);
    }

    private void init(AdapterView.OnItemClickListener clickListener){
        View view = inflater.inflate(R.layout.spiner_window_layout, null);
        setContentView(view);
        setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x00);
        setBackgroundDrawable(dw);
        mListView = view.findViewById(R.id.listview);
        mListView.setAdapter(mAdapter=new MyAdapter());
        mListView.setOnItemClickListener(clickListener);
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder=null;
            if(convertView==null){
                holder=new ViewHolder();
                convertView=inflater.inflate(R.layout.spiner_item_layout, null);
                holder.tvTime=(TextView) convertView.findViewById(R.id.txt_time);
                Utils.setSize(holder.tvTime,1,mScreenWidth,-1,60);
                convertView.setTag(holder);
            }else{
                holder=(ViewHolder) convertView.getTag();
            }
            holder.tvTime.setText(getItem(position).toString());
            return convertView;
        }
    }

    private class ViewHolder{
        private TextView tvTime;
    }
}