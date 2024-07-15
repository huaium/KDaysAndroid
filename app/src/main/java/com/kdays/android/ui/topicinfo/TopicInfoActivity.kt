package com.kdays.android.ui.topicinfo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kdays.android.BaseActivity
import com.kdays.android.databinding.ActivityTopicinfoBinding
import com.kdays.android.ext.IntExts.divideRoundUp
import com.kdays.android.ext.StringExts.showToast
import com.kdays.android.logic.exception.UserCausedException
import com.kdays.android.ui.topicinfo.KDaysTagHandler.BuyCallback
import com.kdays.android.ui.topicinfo.TopicInfoAdapter.ScrollOrStartCallback
import com.kdays.android.utils.NetworkUtils.handleNetwork
import com.kdays.android.utils.UiUtils.editableFactory

class TopicInfoActivity : BaseActivity(), ScrollOrStartCallback,
    BuyCallback {

    companion object {
        private const val TID = "tid"
        private const val PID = "pid"
        private const val SUBJECT = "subject"

        fun actionStart(
            context: Context,
            tid: String,
            subject: String = ""
        ) {
            val intent = Intent(context, TopicInfoActivity::class.java).apply {
                putExtra(TID, tid)
                putExtra(SUBJECT, subject)
            }
            context.startActivity(intent)
        }
    }

    val requestPidLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val pid = result.data?.getStringExtra(PID)
                if (pid != null) {
                    ++viewModel.itemTotal
                    refreshPageTotal()
                    val newPageStr = viewModel.pageTotal.toString()
                    viewModel.getTopicInfo(newPageStr)

                    val editable = editableFactory.newEditable(newPageStr)
                    binding.topicInfoPage.pageInput.text = editable
                    binding.topicInfoPage.pageTotal.text = newPageStr

                    scrollToPid(pid)
                }
            }
        }

    private val viewModel: TopicInfoViewModel by viewModels<TopicInfoViewModel> {
        TopicInfoViewModel.Factory(intent.getStringExtra(TID)!!)
    }
    private lateinit var binding: ActivityTopicinfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopicinfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.topicInfoToolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // set title
        val subject = intent.getStringExtra(SUBJECT)
        if (!subject.isNullOrEmpty()) {
            title = Html.fromHtml(subject, Html.FROM_HTML_MODE_COMPACT)
        }

        val layoutManager = LinearLayoutManager(this)
        binding.topicInfoRecyclerView.layoutManager = layoutManager
        val adapter = TopicInfoAdapter(this, viewModel.postList)
        binding.topicInfoRecyclerView.adapter = adapter

        if (viewModel.noCache()) {
            viewModel.getTopicInfo("1")
        }

        viewModel.topicInfoLiveData.observe(this) { result ->
            binding.topicInfoSwipeRefreshLayout.isRefreshing = true
            val topicInfoData = result.getOrNull()
            if (topicInfoData != null) {
                val oldItemCount = viewModel.postList.size
                viewModel.postList.clear()
                adapter.notifyItemRangeRemoved(0, oldItemCount)
                viewModel.postList.addAll(topicInfoData.posts)
                adapter.notifyItemRangeInserted(0, viewModel.postList.size)

                viewModel.itemTotal = topicInfoData.pager.count
                viewModel.pageSize = topicInfoData.pager.size

                refreshPageTotal()
                binding.topicInfoPage.pageTotal.text = viewModel.pageTotal.toString()
            } else {
                val exception = result.exceptionOrNull()
                if (exception is UserCausedException) {
                    exception.message?.showToast()
                    exception.printStackTrace()
                } else {
                    handleNetwork {
                        "未能获取帖子列表，请尝试刷新".showToast()
                        exception?.printStackTrace()
                    }
                }
            }
            binding.topicInfoSwipeRefreshLayout.isRefreshing = false
            binding.topicInfoPage.pageLayout.visibility = View.VISIBLE
        }

        viewModel.buyLiveData.observe(this) { result ->
            val buyMsg = result.getOrNull()
            if (buyMsg != null) {
                buyMsg.showToast()
                viewModel.getTopicInfo(viewModel.currentPage.toString())
            } else {
                val exception = result.exceptionOrNull()
                if (exception is UserCausedException) {
                    exception.message?.showToast()
                    exception.printStackTrace()
                } else {
                    handleNetwork {
                        "未知底层原因，购买失败".showToast()
                        exception?.printStackTrace()
                    }
                }
            }
        }

        binding.topicInfoSwipeRefreshLayout.setOnRefreshListener {
            viewModel.getTopicInfo(viewModel.currentPage.toString())
        }

        binding.topicInfoPage.pageInput.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                val inputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(
                    binding.topicInfoPage.pageInput.windowToken,
                    0
                )
                view.clearFocus()

                viewModel.currentPage = binding.topicInfoPage.pageInput.text.toString().toInt()
                if (viewModel.currentPage in 1..viewModel.pageTotal) {
                    viewModel.getTopicInfo(viewModel.currentPage.toString())
                    scrollToTop()
                } else
                    "页数超出范围".showToast()
                return@setOnEditorActionListener true
            }
            false
        }

        binding.topicInfoPage.pagePrevious.setOnClickListener {
            onClickPageButton(false)
        }

        binding.topicInfoPage.pageNext.setOnClickListener {
            onClickPageButton(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun scrollOrStart(destTid: String, destPid: String) {
        if (viewModel.tid == destTid) {
            scrollToPid(destPid)
        } else {
            actionStart(this, destTid)
        }
    }

    fun tid(): String {
        return viewModel.tid
    }

    private fun scrollToPid(destPid: String) {
        for ((aPosition, aPost) in viewModel.postList.withIndex()) {
            if (aPost.pid == destPid) {
                val layoutManager = binding.topicInfoRecyclerView.layoutManager

                // View of target item
                val targetView = layoutManager?.findViewByPosition(aPosition)

                // calculate the offset of target item
                val offset = layoutManager?.getDecoratedTop(targetView!!)

                if (offset != null) {
                    binding.topicInfoNestedScrollView.smoothScrollTo(0, offset, 1000)
                }

                break
            }
        }
    }

    private fun scrollToTop() {
        binding.topicInfoNestedScrollView.smoothScrollTo(0, 0, 1000)
    }

    private fun onClickPageButton(next: Boolean) {
        if (viewModel.currentPage > viewModel.pageTotal) {
            viewModel.getTopicInfo(viewModel.pageTotal.toString())
            val editable = editableFactory.newEditable(viewModel.pageTotal.toString())
            binding.topicInfoPage.pageInput.text = editable
            return
        } else if (viewModel.currentPage < 1) {
            viewModel.getTopicInfo("1")
            val editable = editableFactory.newEditable("1")
            binding.topicInfoPage.pageInput.text = editable
            return
        }

        if (next) {
            if (viewModel.currentPage < viewModel.pageTotal) {
                viewModel.currentPage++
                viewModel.getTopicInfo(viewModel.currentPage.toString())
                val editable = editableFactory.newEditable(viewModel.currentPage.toString())
                binding.topicInfoPage.pageInput.text = editable
                scrollToTop()
            }
            // will not scroll to top if viewModel.currentPage ==viewModel.pageTotal
        } else {
            if (viewModel.currentPage > 1) {
                viewModel.currentPage--
                viewModel.getTopicInfo(viewModel.currentPage.toString())
                val editable = editableFactory.newEditable(viewModel.currentPage.toString())
                binding.topicInfoPage.pageInput.text = editable
            }
            scrollToTop()
        }
    }

    private fun refreshPageTotal() {
        viewModel.pageTotal =
            if (viewModel.itemTotal - 1 <= viewModel.pageSize) 1 else (viewModel.itemTotal - 1).divideRoundUp(
                viewModel.pageSize
            )
    }

    override fun buy(pid: String) {
        viewModel.buy(tid(), pid)
    }

    override fun onLoginSuccess() {
        super.onLoginSuccess()
        viewModel.getTopicInfo(viewModel.currentPage.toString())
    }
}