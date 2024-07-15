package com.kdays.android.ui.forum

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kdays.android.R
import com.kdays.android.logic.model.forum.OuterForum

class OuterForumAdapter(private val outerForumList: List<OuterForum>) :
    RecyclerView.Adapter<OuterForumAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        val outerForumName: TextView = view.findViewById(R.id.outer_forum_name)

        private val innerRecyclerView: RecyclerView =
            itemView.findViewById(R.id.outer_forum_recycler_view)

        fun bindInnerForum(outerForum: OuterForum) {
            innerRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            innerRecyclerView.adapter = InnerForumAdapter(outerForum.forums)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_outer_forum, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val outerForum = outerForumList[position]
        holder.outerForumName.text = outerForum.name
        holder.bindInnerForum(outerForum)
    }

    override fun getItemCount() = outerForumList.size
}