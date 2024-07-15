package com.kdays.android.ui.toptopic

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kdays.android.R
import com.kdays.android.logic.model.toptopic.Topic
import com.kdays.android.ui.topicinfo.TopicInfoActivity

class TopTopicAdapter(private val topicList: List<Topic>) :
    RecyclerView.Adapter<TopTopicAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val topicSubject: TextView = view.findViewById(R.id.topic_subject)
        val topicForum: TextView = view.findViewById(R.id.topic_source)
        val topicReplies: TextView = view.findViewById(R.id.topic_replies)
        val topicCreateAt: TextView = view.findViewById(R.id.topic_create_at)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_topic, parent, false)
        val holder = ViewHolder(view)
        holder.itemView.setOnClickListener {
            val position = holder.bindingAdapterPosition
            if (position != -1) {
                val topic = topicList[position]
                TopicInfoActivity.actionStart(
                    parent.context,
                    topic.tid,
                    topic.subject
                )
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val topic = topicList[position]

        holder.topicSubject.text = Html.fromHtml(topic.subject, Html.FROM_HTML_MODE_COMPACT)
        holder.topicForum.text = topic.forum
        holder.topicReplies.text = ""
        holder.topicCreateAt.text = ""
    }

    override fun getItemCount() = topicList.size
}