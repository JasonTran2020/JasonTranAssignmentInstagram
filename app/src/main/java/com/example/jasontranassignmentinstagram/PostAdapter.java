package com.example.jasontranassignmentinstagram;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.List;

public class PostAdapter extends  RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;

    public PostAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.item_post,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  ViewHolder holder, int position) {
        Post post=posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPostUser;
        private ImageView ivImage;
        private TextView tvPostDesc;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPostUser= itemView.findViewById(R.id.tvPostUser);
            ivImage=itemView.findViewById(R.id.ivImage);
            tvPostDesc=itemView.findViewById(R.id.tvPostDesc);

        }

        public void bind(Post post) {
            tvPostDesc.setText(post.getDescription());
            tvPostUser.setText(post.getUser().getUsername());

            ParseFile image=post.getImage();
            if(image!=null)
            {
                Glide.with(context).load(post.getImage().getUrl()).into(ivImage);
            }
        }
    }
}
