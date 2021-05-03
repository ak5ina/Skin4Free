package com.scrippy3.skin4free;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class customDecisionAdapterGA extends BaseAdapter {
    Context context;
    private ArrayList<String> list1;
    private ArrayList<String> list2;
    private ArrayList<String> list3;
    private ArrayList<String> list4;

    public customDecisionAdapterGA(Context context, ArrayList<String>list1, ArrayList<String>list2, ArrayList<String>list3, ArrayList<String>list4) {
        this.context= context;
        this.list1= list1;
        this.list2= list2;
        this.list3= list3;
        this.list4= list4;

    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View convertView =  view;
        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item_ref,viewGroup,false);

        }
        TextView t1 = (TextView) convertView.findViewById(R.id.listview_gift);
        TextView t2 = (TextView) convertView.findViewById(R.id.listview_value);
        Button b1 = (Button) convertView.findViewById(R.id.button_watch_ga);
        ImageView i1 = (ImageView) convertView.findViewById(R.id.image_giveaway);

        // Verify value of position not greater than size of ArrayList.
        if(position < list1.size())
            t1.setText(list1.get(position));

        if(position< list2.size()) {
            t2.setText(list2.get(position));
            if (list2.get(position).equals("false")){
                t2.setTextColor(Color.RED);
            } else {
                t2.setTextColor(Color.GREEN);
            }
        }
        if(position< list3.size()) {
            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    
                }
            });

        }

        if(position< list4.size()) {

        }



        return convertView;
    }

    @Override
    public int getCount()
    {
        if(list1.size() < list2.size())
            return list2.size();
        else
            return list1.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}