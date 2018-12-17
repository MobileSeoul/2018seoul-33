package com.example.user.mjw0617;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class Adapter extends BaseAdapter {

    private ArrayList<DTO> listCustom = new ArrayList<>();

    // ListView에 보여질 Item 수
    @Override
    public int getCount() {
        return listCustom.size();
    }

    // 하나의 Item(ImageView 1, TextView 2)
    @Override
    public Object getItem(int position) {
        return listCustom.get(position);
    }

    // Item의 id : Item을 구별하기 위한 것으로 position 사용
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 실제로 Item이 보여지는 부분
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CustomViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_custom, null, false);

            holder = new CustomViewHolder();

            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.dspsn = (TextView) convertView.findViewById(R.id.dspsn);
//            holder.homepage = (TextView) convertView.findViewById(R.id.homepage);
            holder.addrss = (TextView) convertView.findViewById(R.id.address);
//            holder.tel = (TextView) convertView.findViewById(R.id.tel);

            convertView.setTag(holder);
        } else {
            holder = (CustomViewHolder) convertView.getTag();
        }

        DTO dto = listCustom.get(position);

        holder.name.setText(dto.getName());
        holder.dspsn.setText(dto.getDspsn());
//        holder.homepage.setText(dto.getHomepage());
//        holder.tel.setText(dto.getTel());
        holder.addrss.setText(dto.getAddress());

        return convertView;
    }

    class CustomViewHolder {
        TextView name;
        TextView dspsn;
//        TextView tel;
        TextView addrss;
//        TextView homepage;
    }

    // MainActivity에서 Adapter에있는 ArrayList에 data를 추가시켜주는 함수
    public void addItem(DTO dto) {
        listCustom.add(dto);
    }
}