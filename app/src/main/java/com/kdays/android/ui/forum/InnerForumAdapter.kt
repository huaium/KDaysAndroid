package com.kdays.android.ui.forum

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.kdays.android.R
import com.kdays.android.ext.ImageViewExts.loadRounded
import com.kdays.android.logic.model.forum.InnerForum
import com.kdays.android.ui.topiclist.TopicListActivity

class InnerForumAdapter(private val innerForumList: List<InnerForum>) :
    RecyclerView.Adapter<InnerForumAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        val innerForumIcon: ImageView = view.findViewById(R.id.inner_forum_icon)
        val innerForumName: TextView = view.findViewById(R.id.inner_forum_name)
        val lastPostDate: TextView = view.findViewById(R.id.inner_forum_last_post_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_inner_forum, parent, false)
        val holder = ViewHolder(view)
        holder.itemView.setOnClickListener {
            val position = holder.bindingAdapterPosition
            val innerForum = innerForumList[position]
            if (innerForum.lastPost != null) {
                TopicListActivity.actionStart(
                    context = parent.context,
                    fid = innerForum.fid,
                    forumIcon = innerForum.icon ?: "",
                    forumName = innerForum.name
                )
            } else {
                Toast.makeText(parent.context, "目前组别无法访问该板块", Toast.LENGTH_SHORT).show()
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val innerForum = innerForumList[position]
        holder.innerForumIcon.loadRounded(
            imageUrl = innerForum.icon,
            placeholder = R.drawable.ic_forum
        )
        holder.innerForumName.text = innerForum.name
        holder.lastPostDate.text = innerForum.lastPost?.date
    }

    override fun getItemCount() = innerForumList.size
}