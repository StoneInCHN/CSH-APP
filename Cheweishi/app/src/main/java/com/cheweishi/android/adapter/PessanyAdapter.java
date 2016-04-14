package com.cheweishi.android.adapter;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cheweishi.android.cheweishi.R;
import com.cheweishi.android.activity.BaseActivity;
import com.cheweishi.android.entity.PessanyResponse;
import com.cheweishi.android.entity.PessanySearch;
import com.cheweishi.android.utils.StringUtil;

public class PessanyAdapter extends BaseAdapter {
    private BaseActivity context;
    private ViewHolder viewHolder;
    private LayoutInflater mInflater;
    private List<PessanyResponse.MsgBean> listPessanySearch;

    public PessanyAdapter(BaseActivity context,
                          List<PessanyResponse.MsgBean> listPessanySearch) {
        this.context = context;
        this.listPessanySearch = listPessanySearch;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listPessanySearch == null ? 1 : listPessanySearch.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    public void setData(List<PessanyResponse.MsgBean> listPessanySearch) {
        if (listPessanySearch.size() > 0) {
            this.listPessanySearch = listPessanySearch;
            this.notifyDataSetChanged();
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_peccancy, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_peccany_plateCode = (TextView) convertView
                    .findViewById(R.id.tv_peccany_plateCode);
            viewHolder.tv_notDeal = (TextView) convertView
                    .findViewById(R.id.tv_notDeal);
            viewHolder.tv_cut = (TextView) convertView
                    .findViewById(R.id.tv_cut);
            viewHolder.tv_punish = (TextView) convertView
                    .findViewById(R.id.tv_punish);
            viewHolder.img_peccany_item = (ImageView) convertView
                    .findViewById(R.id.img_peccany_item);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tv_peccany_plateCode.setText(listPessanySearch.get(position).getPlate());
        viewHolder.tv_cut.setText("" + listPessanySearch.get(position).getScore());
        viewHolder.tv_punish.setText("" + listPessanySearch.get(position).getFinesAmount());


        return convertView;
    }

    class ViewHolder {
        ImageView img_peccany_item;

        TextView tv_peccany_plateCode;
        TextView tv_notDeal;
        TextView tv_cut;
        TextView tv_punish;

    }

}
