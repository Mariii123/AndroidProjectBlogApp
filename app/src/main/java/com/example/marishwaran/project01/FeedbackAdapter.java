package com.example.marishwaran.project01;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedViewHolder>  {
    public List<FeedbackList> feedbackLists;
    Context context;

    public FeedbackAdapter(List<FeedbackList> feedbackLists) {
        this.feedbackLists = feedbackLists;
    }

    @NonNull

    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedback_layout, parent, false);
        context = parent.getContext();
        return new FeedViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {
        String name = feedbackLists.get(position).getFname();
        String img = feedbackLists.get(position).getFimg();
        String desc = feedbackLists.get(position).getFdesc();
        holder.setFname(name);
        holder.setFimg(img);
        holder.setFDesc(desc);
        long millisec = feedbackLists.get(position).getFtimestamp().getTime();
        String dateString = new SimpleDateFormat().format(new Date(millisec));
        holder.setFdate(dateString);
    }

    @Override
    public int getItemCount() {
        return feedbackLists.size();
    }

    public class FeedViewHolder extends RecyclerView.ViewHolder{
        TextView fname;
        CircleImageView fimg;
        TextView fdesc;
        TextView fts;
        View mView;
        public FeedViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setFname(String name){
            fname = mView.findViewById(R.id.feedback_name);
            fname.setText(name);
        }
        public void setFimg(String img){
            fimg = mView.findViewById(R.id.feedback_img);
            Glide.with(context).setDefaultRequestOptions(
                    new RequestOptions().placeholder(R.drawable.userphoto)
            ).load(img).into(fimg);
        }
        public void setFDesc(String desc){
            fdesc = mView.findViewById(R.id.feedback_comment);
            fdesc.setText(desc);
        }
        public void setFdate(String date){
            fts = mView.findViewById(R.id.feedback_time);
            fts.setText(date);
        }
    }
}
