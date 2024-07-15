package com.kdays.android.ui.editor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kdays.android.R
import com.kdays.android.logic.model.draft.drafts.Draft

class DraftsAdapter(private val activity: EditorActivity, private val drafts: List<Draft>) :
    RecyclerView.Adapter<DraftsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val date: TextView = view.findViewById(R.id.drafts_date)
        val title: TextView = view.findViewById(R.id.drafts_title)
        val insertButton: FloatingActionButton = view.findViewById(R.id.drafts_insert)
        val deleteButton: FloatingActionButton = view.findViewById(R.id.drafts_remove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_drafts, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val draft = drafts[position]

        holder.date.text = draft.date
        holder.title.text = draft.title.takeIf { it.isNotEmpty() } ?: "未命名"
        holder.insertButton.setOnClickListener {
            val callback = activity as InsertCallback
            callback.onInsertClick(draft.title, draft.content)
        }
        if (draft.id == "auto") {
            holder.deleteButton.visibility = View.INVISIBLE
        } else {
            holder.deleteButton.setOnClickListener {
                val callback = activity as RemoveCallback
                callback.onRemoveClick(draft.id)
            }
        }
    }

    override fun getItemCount() = drafts.size

    interface InsertCallback {
        fun onInsertClick(title: String, content: String)
    }

    interface RemoveCallback {
        fun onRemoveClick(id: String)
    }
}