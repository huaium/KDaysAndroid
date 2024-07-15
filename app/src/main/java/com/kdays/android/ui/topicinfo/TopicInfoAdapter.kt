package com.kdays.android.ui.topicinfo

import android.annotation.SuppressLint
import android.text.SpannableString
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.core.text.method.LinkMovementMethodCompat
import androidx.recyclerview.widget.RecyclerView
import com.kdays.android.R
import com.kdays.android.ext.ImageViewExts.loadRounded
import com.kdays.android.ext.StringExts.openUrl
import com.kdays.android.ext.StringExts.parse
import com.kdays.android.ext.StringExts.parseDate
import com.kdays.android.logic.model.topicinfo.Post
import com.kdays.android.logic.model.urlparsed.OutLinkResult
import com.kdays.android.logic.model.urlparsed.TopicInfoResult
import com.kdays.android.logic.model.urlparsed.TopicListResult
import com.kdays.android.ui.editor.EditorActivity
import com.kdays.android.ui.text.TextActivity
import com.kdays.android.ui.topiclist.TopicListActivity
import com.kdays.android.ui.usertopics.UserTopicsActivity

class TopicInfoAdapter(private val activity: TopicInfoActivity, private val postList: List<Post>) :
    RecyclerView.Adapter<TopicInfoAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userAvatar: ImageView = view.findViewById(R.id.post_user_avatar)
        val userName: TextView = view.findViewById(R.id.post_user_name)
        val userLv: TextView = view.findViewById(R.id.post_user_lv)
        val floor: TextView = view.findViewById(R.id.post_floor)
        val date: TextView = view.findViewById(R.id.post_date)
        val content: TextView = view.findViewById(R.id.post_content)
        val reply: ImageView = view.findViewById(R.id.post_reply)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = postList[position]
        val readableDate = post.date.parseDate()
        post.author.let { author ->
            holder.userAvatar.apply {
                loadRounded(
                    imageUrl = author.avatar,
                    placeholder = R.drawable.ic_user
                )
                setOnClickListener {
                    UserTopicsActivity.actionStart(
                        activity,
                        author.uid,
                        author.avatar,
                        author.nick,
                        author.groupName
                    )
                    /*"${BuildConfig.BBS_ADDRESS}/user/profile?uid=${post.author.uid}".openUrl(activity)*/
                }
            }
            holder.userName.text = author.nick
            holder.userLv.text = author.groupName
        }
        holder.floor.apply {
            val floor = text.toString().format(post.floor)
            text = floor
        }
        holder.date.text = readableDate

        // start handling content
        val spannableString = SpannableString(
            HtmlCompat.fromHtml(
                post.content,
                HtmlCompat.FROM_HTML_MODE_COMPACT,
                GlideImageGetter(holder.content),
                KDaysTagHandler(activity, post.pid)
            )
        )

        val urlSpans = spannableString.getSpans(0, spannableString.length, URLSpan::class.java)

        for (urlSpan in urlSpans) {
            val start = spannableString.getSpanStart(urlSpan)
            val end = spannableString.getSpanEnd(urlSpan)
            val flags = spannableString.getSpanFlags(urlSpan)

            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val url = urlSpan.url

                    when (val result = url.parse()) {

                        is TopicInfoResult -> {
                            val callback = activity as ScrollOrStartCallback
                            callback.scrollOrStart(result.tid, result.pid)
                        }

                        is TopicListResult -> TopicListActivity.actionStart(
                            activity,
                            result.fid,
                            result.page
                        )

                        is OutLinkResult -> result.url.openUrl(activity)
                    }
                }
            }

            spannableString.removeSpan(urlSpan)
            spannableString.setSpan(clickableSpan, start, end, flags)
        }

        holder.content.apply {
            text = spannableString
            movementMethod = LinkMovementMethodCompat.getInstance()
            setOnLongClickListener {
                TextActivity.actionStart(activity, text)
                true
            }
        }
        // end handling content

        holder.reply.setOnClickListener {
            EditorActivity.replyActionStart(
                activity,
                activity.tid(),
                post.content,
                post.author.nick,
                post.pid
            )
        }
    }

    override fun getItemCount() = postList.size

    interface ScrollOrStartCallback {
        fun scrollOrStart(destTid: String, destPid: String)
    }
}