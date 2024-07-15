package com.kdays.android.ui.editor

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kdays.android.BaseActivity
import com.kdays.android.R
import com.kdays.android.databinding.ActivityEditorBinding
import com.kdays.android.ext.EditorActivityExts.selectPicture
import com.kdays.android.ext.RichTextStateExts.addImage
import com.kdays.android.ext.StringExts.openUrl
import com.kdays.android.ext.StringExts.showToast
import com.kdays.android.logic.exception.UserCausedException
import com.kdays.android.ui.editor.DraftsAdapter.InsertCallback
import com.kdays.android.ui.editor.DraftsAdapter.RemoveCallback
import com.kdays.android.ui.editor.components.editor.RichTextEditorColumn
import com.kdays.android.ui.theme.KDaysTheme
import com.kdays.android.ui.topicinfo.TopicInfoActivity
import com.kdays.android.utils.NetworkUtils.handleNetwork
import com.kdays.android.utils.UiUtils
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import java.io.File
import kotlin.properties.Delegates


class EditorActivity : BaseActivity(), InsertCallback, RemoveCallback {

    companion object {
        // Pub
        private const val FID = "fid"
        private const val FORUM_NAME = "forum_name"
        private const val CAT_LIST = "cat_list"

        // Reply
        private const val TID = "tid"
        private const val PID = "pid"
        private const val QUOTE = "quote"
        private const val QUOTED_NICK = "quoted_nick"
        private const val QUOTED_PID = "quoted_pid"
        private var isPub by Delegates.notNull<Boolean>()

        fun pubActionStart(
            context: Context,
            fid: String,
            forumName: String,
            catList: String,
        ) {
            isPub = true
            val intent = Intent(context, EditorActivity::class.java).apply {
                putExtra(FID, fid)
                putExtra(FORUM_NAME, forumName)
                putExtra(CAT_LIST, catList)
            }
            context.startActivity(intent)
        }

        fun replyActionStart(
            activity: TopicInfoActivity,
            tid: String,
            quote: String,
            quotedNick: String,
            quotedPid: String,
        ) {
            isPub = false
            val intent = Intent(activity, EditorActivity::class.java).apply {
                putExtra(TID, tid)
                putExtra(QUOTE, quote)
                putExtra(QUOTED_NICK, quotedNick)
                putExtra(QUOTED_PID, quotedPid)
            }
            activity.requestPidLauncher.launch(intent)
        }
    }

    private lateinit var binding: ActivityEditorBinding
    private val viewModel: EditorViewModel by viewModels()
    private lateinit var richTextState: RichTextState
    private val draftsAdapter by lazy { DraftsAdapter(this, viewModel.drafts) }
    private lateinit var draftsDialog: AlertDialog

    private val send = {
        if (isPub) {
            viewModel.pub(
                viewModel.fid,
                viewModel.selectedCat,
                binding.editorSubject.text.toString(),
                richTextState.toBBCode()
            )
        } else {
            val quote = intent.getStringExtra(QUOTE)
            val quotedNick = intent.getStringExtra(QUOTED_NICK)
            val quotedPid = intent.getStringExtra(QUOTED_PID)
            val tid = intent.getStringExtra(TID) ?: ""
            viewModel.reply(
                tid,
                "[quote=${quotedNick},${quotedPid}]${quote}[/quote]" + richTextState.toBBCode()
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.editorToolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.editor.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                KDaysTheme {
                    richTextState = rememberRichTextState()

                    RichTextEditorColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        state = richTextState,
                        onClickSend = send,
                        onImageClick = {
                            selectPicture { list ->
                                viewModel.upload(list, viewModel.fid)
                            }
                        }
                    )
                }
            }
        }

        if (isPub) {
            viewModel.fid = intent.getStringExtra(FID)!!
            // Change title according to forum name
            title = intent.getStringExtra(FORUM_NAME)

            // Cast catList to Map<String,String>
            val catListType = object : TypeToken<Map<String, String>>() {}.type
            val catList =
                Gson().fromJson<Map<String, String>>(intent.getStringExtra(CAT_LIST), catListType)

            // Initialize default viewModel.selectedCat
            if (catList.isNotEmpty()) {
                val firstItem = catList.entries.first()
                viewModel.selectedCat = firstItem.key
                binding.editorCategories.text = firstItem.value
                binding.editorCategories.visibility = View.VISIBLE
            } else {
                viewModel.selectedCat = "0"
            }

            binding.editorCategories.setOnClickListener {
                showPopupMenu(it, catList)
            }

            viewModel.pubLiveData.observe(this) { result ->
                val pubData = result.getOrNull()
                if (pubData != null) {
                    val tid = pubData.tid
                    TopicInfoActivity.actionStart(this, tid)
                    finish()
                } else {
                    val exception = result.exceptionOrNull()
                    if (exception is UserCausedException) {
                        exception.message?.showToast()
                        exception.printStackTrace()
                    } else {
                        handleNetwork {
                            "未知底层原因，未能发布成功".showToast()
                            exception?.printStackTrace()
                        }
                    }
                }
            }
        } else {
            binding.editorHeader.visibility = View.GONE

            viewModel.replyLiveData.observe(this) { result ->
                val replyData = result.getOrNull()
                if (replyData != null) {
                    val pid = replyData.pid
                    intent.putExtra(PID, pid)
                    setResult(RESULT_OK, intent)
                    finish()
                } else {
                    val exception = result.exceptionOrNull()
                    if (exception is UserCausedException) {
                        exception.message?.showToast()
                        exception.printStackTrace()
                    } else {
                        handleNetwork {
                            "未知底层原因，未能回复成功".showToast()
                            exception?.printStackTrace()
                        }
                    }
                }
            }
        }

        viewModel.draftsLiveData.observe(this) { result ->
            val draftsList = result.getOrNull()
            if (draftsList != null) {
                val oldItemCount = viewModel.drafts.size
                viewModel.drafts.clear()
                draftsAdapter.notifyItemRangeRemoved(0, oldItemCount)
                viewModel.drafts.addAll(draftsList)
                draftsAdapter.notifyItemRangeInserted(0, viewModel.drafts.size)
            } else {
                handleNetwork {
                    "未能获取草稿，请尝试刷新".showToast()
                    result.exceptionOrNull()?.printStackTrace()
                }
            }

        }

        viewModel.removeDraftLiveData.observe(this) { result ->
            val msg = result.getOrNull()
            if (msg != null) {
                msg.showToast()
                viewModel.refreshDrafts()
            } else {
                val exception = result.exceptionOrNull()
                if (exception is UserCausedException) {
                    exception.message?.showToast()
                    exception.printStackTrace()
                } else {
                    handleNetwork {
                        "未能删除草稿，请尝试刷新".showToast()
                        exception?.printStackTrace()
                    }
                }
            }
        }

        viewModel.saveDraftLiveData.observe(this) { result ->
            val msg = result.getOrNull()
            if (msg != null) {
                msg.showToast()
                viewModel.refreshDrafts()
            } else {
                val exception = result.exceptionOrNull()
                handleNetwork {
                    "未能保存草稿，请尝试刷新".showToast()
                    exception?.printStackTrace()
                }
            }
        }

        viewModel.uploadLiveData.observe(this) { results ->
            var anyFailed = false
            var msg: String? = null

            results.forEach { result ->
                val data = result.getOrNull()
                if (data != null) {
                    richTextState.addImage(data.url)
                } else {
                    anyFailed = true
                    val exception = result.exceptionOrNull()
                    if (exception is UserCausedException) {
                        msg = exception.message
                    }
                    exception?.printStackTrace()
                }
            }

            if (anyFailed) {
                handleNetwork {
                    if (msg != null) {
                        msg!!.showToast()
                    } else {
                        "未能上传成功，请尝试刷新".showToast()
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

            R.id.menu_bbcode_show -> {
                val bbCode = richTextState.toBBCode()

                val alertDialog = MaterialAlertDialogBuilder(this)
                    .setTitle("原始 BBCode")
                    .setMessage(bbCode)
                    .setCancelable(true)
                    .setNegativeButton("复制") { dialog, _ ->
                        // get clipboard manager
                        val clipboardManager =
                            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                        // create clip data
                        val clipData = ClipData.newPlainText("label", bbCode)

                        // put clip data into clipboard
                        clipboardManager.setPrimaryClip(clipData)

                        // reminder
                        "已复制到剪贴板".showToast()

                        dialog.dismiss()
                    }
                    .setPositiveButton("返回") { dialog, _ -> dialog.dismiss() }
                    .create()
                alertDialog.show()
            }

            R.id.menu_bbcode_help -> "https://bbs2.kdays.net/help/bbcode".openUrl(this)

            R.id.menu_drafts -> {
                draftsDialog = MaterialAlertDialogBuilder(this)
                    .setTitle("草稿箱")
                    .setView(R.layout.dialog_drafts)
                    .setNegativeButton("保存草稿") { dialog, _ ->
                        val content = richTextState.toBBCode()
                        if (content != "<p></p>") {
                            viewModel.saveDraft(
                                binding.editorSubject.text.toString(),
                                content
                            )
                        } else {
                            "草稿不能为空".showToast()
                        }
                        dialog.dismiss()
                    }
                    .setPositiveButton("返回") { dialog, _ -> dialog.dismiss() }
                    .setCancelable(true)
                    .create()
                draftsDialog.show()

                val layoutManager = LinearLayoutManager(this)
                val recyclerView: RecyclerView? =
                    draftsDialog.findViewById(R.id.drafts_recycler_view)
                recyclerView?.layoutManager = layoutManager
                recyclerView?.adapter = draftsAdapter

                viewModel.refreshDrafts()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.editor_menu, menu)
        return true
    }

    private fun showPopupMenu(view: View, map: Map<String, String>) {
        val popupMenu = PopupMenu(this, view)
        val menu = popupMenu.menu

        // dynamic add menu items for map
        map.entries.forEachIndexed { index, (key, value) ->
            menu.add(Menu.NONE, key.toInt(), index, value)
        }

        popupMenu.setOnMenuItemClickListener { menuItem ->
            viewModel.selectedCat = menuItem.itemId.toString()
            binding.editorCategories.text = menuItem.title
            true
        }

        popupMenu.show()
    }

    override fun onInsertClick(title: String, content: String) {
        binding.editorSubject.text = UiUtils.editableFactory.newEditable(title)
        richTextState.setHtml(content)
        draftsDialog.dismiss()
    }

    override fun onRemoveClick(id: String) {
        viewModel.removeDraft(id)
    }

    override fun onDestroy() {
        super.onDestroy()

        // remove cached images in the sandbox
        for (uri in viewModel.images) {
            uri?.let { media ->
                media.sandboxPath?.let { path ->
                    val file = File(path)
                    file.delete()
                }
            }
        }
    }
}