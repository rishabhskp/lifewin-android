package app.lifewin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import app.lifewin.R;
import app.lifewin.ui.activity.MainMenu;


public class LeftMenuAdapter extends BaseAdapter {

    private Context context;
    private MainMenu.LeftMenuItem[] items;

    public LeftMenuAdapter(Context context, MainMenu.LeftMenuItem[] items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.include_left_menu_list_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvTile.setText(items[position].getTitle());
        viewHolder.imageView.setImageResource(items[position].getResId());
        return convertView;
    }

    static class ViewHolder {
        ImageView imageView;
         TextView tvTile;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.iv_img);
            tvTile = (TextView) view.findViewById(R.id.tv_title);
        }
    }
}
