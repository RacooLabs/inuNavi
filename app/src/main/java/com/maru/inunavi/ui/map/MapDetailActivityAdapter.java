package com.maru.inunavi.ui.map;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.maru.inunavi.R;
import com.maru.inunavi.ui.timetable.search.Lecture;

import java.util.List;

public class MapDetailActivityAdapter extends RecyclerView.Adapter<com.maru.inunavi.ui.map.MapDetailActivityAdapter.MyViewHolder> {

    private List<Place> mData;

    private OnItemClickListener mListener = null ;

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {

        this.mListener = listener ;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        public ConstraintLayout card_constraint;
        public TextView map_frag_detail_cardView_title;
        public ImageView map_frag_detail_cardView_image;


        public MyViewHolder(View v) {

            super(v);


            card_constraint = v.findViewById(R.id.card_constraint);
            map_frag_detail_cardView_image =  v.findViewById(R.id.map_frag_detail_cardView_image);
            map_frag_detail_cardView_title =  v.findViewById(R.id.map_frag_detail_cardView_title);

            card_constraint.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    int pos = getAdapterPosition() ;

                    if (pos != RecyclerView.NO_POSITION) {
                        if(mListener != null){
                            mListener.onItemClick(v,pos);
                        }
                    }

                }
            });

        }

    }

    public MapDetailActivityAdapter(List<Place> mData) {

        this.mData = mData;

    }


    @Override
    public MapDetailActivityAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.map_fragment_detail_cardview, parent, false);

        MapDetailActivityAdapter.MyViewHolder vh = new MapDetailActivityAdapter.MyViewHolder(view);
        return vh;

    }


    @Override
    public void onBindViewHolder(com.maru.inunavi.ui.map.MapDetailActivityAdapter.MyViewHolder holder, int position) {

        holder.map_frag_detail_cardView_title.setText(mData.get(position).getTitle());

        FindImage findImage = new FindImage();
        holder.map_frag_detail_cardView_image.setImageResource(findImage.getPlaceImageId(mData.get(position).getPlaceCode()));


    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }


}

