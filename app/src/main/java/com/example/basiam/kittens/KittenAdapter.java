package com.example.basiam.kittens;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;



public class KittenAdapter extends ArrayAdapter<Kitten> {



    public KittenAdapter(Activity context, ArrayList<Kitten> Kittens) {
        super(context, 0, Kittens);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        Kitten currentKitten = getItem(position);
        

        TextView cutenessTextView = (TextView) listItemView.findViewById(R.id.cuteness);
        int cuteness = currentKitten.getCuteness();
        cutenessTextView.setText(String.valueOf(cuteness));
        GradientDrawable cutenessCircle = (GradientDrawable) cutenessTextView.getBackground();
        int cutenessColor = getCutenessColor(currentKitten.getCuteness());
        cutenessCircle.setColor(cutenessColor);


        ImageView urlView = (ImageView) listItemView.findViewById(R.id.image);
        Picasso.with(getContext()).load(currentKitten.getUrl()).into(urlView);
        

        return listItemView;
    }

    

    private int getCutenessColor(int cuteness) {
        int cutenessColorResourceId;
        switch (cuteness) {
            case 0:
            case 1:
                cutenessColorResourceId = R.color.cuteness1;
                break;
            case 2:
                cutenessColorResourceId = R.color.cuteness2;
                break;
            case 3:
                cutenessColorResourceId = R.color.cuteness3;
                break;
            case 4:
                cutenessColorResourceId = R.color.cuteness4;
                break;
            case 5:
                cutenessColorResourceId = R.color.cuteness5;
                break;
            case 6:
                cutenessColorResourceId = R.color.cuteness6;
                break;
            case 7:
                cutenessColorResourceId = R.color.cuteness7;
                break;
            case 8:
                cutenessColorResourceId = R.color.cuteness8;
                break;
            case 9:
                cutenessColorResourceId = R.color.cuteness9;
                break;
            default:
                cutenessColorResourceId = R.color.cuteness10plus;
                break;
        }
        return ContextCompat.getColor(getContext(), cutenessColorResourceId);
    }

}