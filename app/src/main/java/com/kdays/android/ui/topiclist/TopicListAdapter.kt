package com.kdays.android.ui.topiclist

import android.graphics.Typeface
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kdays.android.R
import com.kdays.android.ext.StringExts.parseColor
import com.kdays.android.ext.StringExts.parseDate
import com.kdays.android.logic.model.topiclist.Topic
import com.kdays.android.ui.topicinfo.TopicInfoActivity

class TopicListAdapter(private val topicList: List<Topic>) :
    RecyclerView.Adapter<TopicListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val topicSubject: TextView = view.findViewById(R.id.topic_subject)
        val topicAuthor: TextView = view.findViewById(R.id.topic_source)
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

        if (topic.style != null) {
            if (topic.style.bold)
                holder.topicSubject.setTypeface(null, Typeface.BOLD)

            val color = topic.style.color.parseColor()
            if (color != -1) {
                holder.topicSubject.setTextColor(color)
            }
        }

        holder.topicSubject.text = Html.fromHtml(topic.subject, Html.FROM_HTML_MODE_COMPACT)
        holder.topicAuthor.text = topic.author.nick
        holder.topicReplies.apply {
            val replies = text.toString().format(topic.replies)
            text = replies
        }

        val readableCreateAt = topic.createAt.parseDate()
        holder.topicCreateAt.text = readableCreateAt
    }

    override fun getItemCount() = topicList.size
}