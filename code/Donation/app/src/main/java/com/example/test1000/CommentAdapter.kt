package com.example.test1000

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.test1000.model.Comment

class CommentAdapter(private val commentList: List<Comment>) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val commentContent: TextView = itemView.findViewById(R.id.comment_content)
        val userName: TextView = itemView.findViewById(R.id.comment_user_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = commentList[position]
        holder.commentContent.text = comment.text
        holder.userName.text = comment.userName
    }

    override fun getItemCount() = commentList.size
}
