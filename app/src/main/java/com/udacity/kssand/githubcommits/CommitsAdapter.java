package com.udacity.kssand.githubcommits;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by kssand on 08-Apr-16.
 */
public class CommitsAdapter extends BaseAdapter {
    List<Committer> commits;
    Context context;

    public CommitsAdapter(Context context, List<Committer> objects) {
        this.context=context;
        this.commits=objects;
    }

    @Override
    public int getCount() {
        return commits.size();
    }

    @Override
    public Object getItem(int position) {
        return commits.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.listview_git, parent, false);
            holder = new ViewHolder();
            holder.messageTV = (TextView) convertView.findViewById(R.id.list_item_message_textview);
            holder.authorTV = (TextView) convertView.findViewById(R.id.list_item_name_textview);
            holder.dateTV = (TextView) convertView.findViewById(R.id.list_item_date_textview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.messageTV.setText("Message: " + commits.get(position).getMesssage());
        holder.authorTV.setText("Name: "+commits.get(position).getName());
        holder.dateTV.setText("Date: "+commits.get(position).getDate());

        return convertView;
    }

    static class ViewHolder{
        TextView authorTV,messageTV,dateTV;

    }
}
