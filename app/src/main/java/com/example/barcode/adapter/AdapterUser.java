package com.example.barcode.adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barcode.R;
import com.example.barcode.object.User;
import com.example.barcode.util.Util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AdapterUser extends BaseAdapter {
    private List<User> listUser;
    private Context context;
    private int layout;

    public AdapterUser(Context context, int layout, List<User> listUser) {
        this.listUser = listUser;
        this.context = context;
        this.layout = layout;
    }

    class ViewHolder{
        private TextView tvName, tvDateOfBirth, tvAdress, tvCMND, tvPhoneNumber;
        public ViewHolder(View view){
            tvName = (TextView) view.findViewById(R.id.tvItemName);
            tvDateOfBirth = (TextView) view.findViewById(R.id.tvItemDateOfBirth);
            tvAdress = (TextView) view.findViewById(R.id.tvItemAdress);
            tvCMND = (TextView) view.findViewById(R.id.tvItemCMND);
            tvPhoneNumber = (TextView) view.findViewById(R.id.tvItemPhoneNumber);
        }
    }


    @Override
    public int getCount() {
        return listUser.size();
    }

    @Override
    public Object getItem(int i) {
        return listUser.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        User u = listUser.get(position);
        if(u != null){
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            viewHolder.tvName.setText(u.getName());
            viewHolder.tvDateOfBirth.setText(dateFormat.format(u.getDateOfBirth()));
            viewHolder.tvAdress.setText(u.getAdress());
            viewHolder.tvCMND.setText(u.getCMND());
            viewHolder.tvPhoneNumber.setText(u.getPhoneNumber());
        }

        return convertView;
    }

}
