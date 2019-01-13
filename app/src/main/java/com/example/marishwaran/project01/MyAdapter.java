package com.example.marishwaran.project01;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    public List<BlogPost> blog_list;
    FirebaseFirestore firestore;
    public Context context;
    public MyAdapter(List<BlogPost> blog_list){
        this.blog_list = blog_list;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_layout, parent, false);
        context = parent.getContext();
        firestore = FirebaseFirestore.getInstance();
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        String desc = blog_list.get(position).getDesc();
        holder.setDesc(desc);
        String img_post = blog_list.get(position).getImage_url();
        holder.setPostImg(img_post);
        String uid = blog_list.get(position).getUser_id();
        firestore.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        String n = task.getResult().getString("Username");
                        String i = task.getResult().getString("Userimg");
                        holder.setUserData(n, i);

                    }
                }
            }
        });
        long millisec = blog_list.get(position).getTimestamp().getTime();
        String dateString = new SimpleDateFormat().format(new Date(millisec));
        holder.setPostTime(dateString);
    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView post_desc;
        ImageView post_img;
        TextView post_user;
        CircleImageView post_user_img;
        TextView post_date;
        public MyViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setDesc(String desc){
            post_desc = mView.findViewById(R.id.home_post_desc);
            post_desc.setText(desc);
        }
        public void setPostImg(String imgUrl){
            post_img = mView.findViewById(R.id.home_post_pics);
            Glide.with(context).load(imgUrl).into(post_img);
        }
        public void setUserData(String name, String usrImg){
            post_user = mView.findViewById(R.id.home_post_usr_name);
            post_user.setText(name);
            post_user_img = mView.findViewById(R.id.home_post_pic);
            Glide.with(context).load(usrImg).into(post_user_img);
        }
        public void setPostTime(String dateString) {
            post_date = mView.findViewById(R.id.home_post_time);
            post_date.setText(dateString);
        }
    }
}
