package com.kdays.android.ui.usertopics

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kdays.android.BaseActivity
import com.kdays.android.R
import com.kdays.android.databinding.ActivityUsertopicsBinding
import com.kdays.android.ext.ImageViewExts.loadRounded
import com.kdays.android.ext.IntExts.divideRoundUp
import com.kdays.android.ext.StringExts.showToast
import com.kdays.android.utils.NetworkUtils.handleNetwork
import com.kdays.android.utils.UiUtils.editableFactory

class UserTopicsActivity : BaseActivity() {
    companion object {
        private const val AVATAR = "avatar"
        private const val NAME = "name"
        private const val UID = "uid"
        private const val LV = "lv"

        fun actionStart(
            context: Context,
            uid: String,
            avatar: String,
            name: String,
            lv: String = ""
        ) {
            val intent = Intent(context, UserTopicsActivity::class.java).apply {
                putExtra(AVATAR, avatar)
                putExtra(NAME, name)
                putExtra(UID, uid)
                putExtra(LV, lv)
            }
            context.startActivity(intent)
        }
    }

    private val viewModel: UserTopicsViewModel by viewModels<UserTopicsViewModel> {
        UserTopicsViewModel.Factory(intent.getStringExtra(UID)!!)
    }
    private lateinit var binding: ActivityUsertopicsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsertopicsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.userTopicsToolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Get user data
        // arguments have default values, thus not null
        val avatar = intent.getStringExtra(AVATAR)!!
        val name = intent.getStringExtra(NAME)!!
        val lv = intent.getStringExtra(LV)!!

        // setup recyclerview for topics
        val layoutManager = LinearLayoutManager(this)
        binding.userTopicsRecyclerView.layoutManager = layoutManager
        val adapter = UserTopicsAdapter(viewModel.topicList, name)
        binding.userTopicsRecyclerView.adapter = adapter

        // Load bar with caches
        if (lv.isNotEmpty()) {
            // Open from TopicInfoActivity
            binding.userTopicsHeader.userHeaderAvatar.loadRounded(
                imageUrl = avatar,
                placeholder = R.drawable.ic_user
            )
            binding.userTopicsHeader.userHeaderName.text = name
            binding.userTopicsHeader.userHeaderLv.text = lv
        } else {
            // Open from UserFragment
            title = getString(R.string.topics)
            binding.userTopicsHeader.userHeaderLayout.visibility = View.GONE
        }

        if (viewModel.noCache()) {
            viewModel.getUserTopics("1")
        }

        viewModel.topicListLiveData.observe(this) { result ->
            binding.userTopicsSwipeRefreshLayout.isRefreshing = true
            val topicListData = result.getOrNull()
            if (topicListData != null) {
                // Load RecyclerView for topics
                val oldItemCount = viewModel.topicList.size
                viewModel.topicList.clear()
                adapter.notifyItemRangeRemoved(0, oldItemCount)

                viewModel.topicList.addAll(topicListData.topics.normal)
                adapter.notifyItemRangeInserted(0, viewModel.topicList.size)

                // Load page switch layout
                refreshPageTotal(topicListData.pages.count, topicListData.pages.size)
                binding.userTopicsPage.pageTotal.text = viewModel.pageTotal.toString()
            } else {
                handleNetwork {
                    "未能获取主题列表，请尝试刷新".showToast()
                    result.exceptionOrNull()?.printStackTrace()
                }
            }
            binding.userTopicsSwipeRefreshLayout.isRefreshing = false
            binding.userTopicsPage.pageLayout.visibility = View.VISIBLE
        }

        binding.userTopicsSwipeRefreshLayout.setOnRefreshListener {
            viewModel.getUserTopics(binding.userTopicsPage.pageInput.text.toString())
        }

        binding.userTopicsPage.pageInput.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                val inputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(
                    binding.userTopicsPage.pageInput.windowToken,
                    0
                )
                view.clearFocus()

                val currentPage = binding.userTopicsPage.pageInput.text.toString().toInt()

                if (currentPage in 1..viewModel.pageTotal)
                    viewModel.getUserTopics(currentPage.toString())
                else
                    "页数超出范围".showToast()
                return@setOnEditorActionListener true
            }
            false
        }

        binding.userTopicsPage.pagePrevious.setOnClickListener {
            onClickPageButton(false)
        }

        binding.userTopicsPage.pageNext.setOnClickListener {
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

    private fun scrollToTop() {
        binding.userTopicsNestedScrollView.smoothScrollTo(0, 0, 1000)
    }

    private fun onClickPageButton(next: Boolean) {
        var currentPage = binding.userTopicsPage.pageInput.text.toString().toInt()
        if (currentPage > viewModel.pageTotal) {
            viewModel.getUserTopics(viewModel.pageTotal.toString())
            val editable = editableFactory.newEditable(viewModel.pageTotal.toString())
            binding.userTopicsPage.pageInput.text = editable
            return
        } else if (currentPage < 1) {
            viewModel.getUserTopics("1")
            val editable = editableFactory.newEditable("1")
            binding.userTopicsPage.pageInput.text = editable
            return
        }

        if (next) {
            if (currentPage < viewModel.pageTotal) {
                currentPage++
                viewModel.getUserTopics(currentPage.toString())
                val editable = editableFactory.newEditable(currentPage.toString())
                binding.userTopicsPage.pageInput.text = editable
                scrollToTop()
            }
            // will not scroll to top if currentPage == viewModel.pageTotal
        } else {
            if (currentPage > 1) {
                currentPage--
                viewModel.getUserTopics(currentPage.toString())
                val editable = editableFactory.newEditable(currentPage.toString())
                binding.userTopicsPage.pageInput.text = editable
            }
            scrollToTop()
        }
    }

    private fun refreshPageTotal(itemTotal: Int, pageSize: Int) {
        viewModel.pageTotal =
            if (itemTotal - 1 <= pageSize) 1 else (itemTotal - 1).divideRoundUp(pageSize)
    }

    override fun onLoginSuccess() {
        super.onLoginSuccess()
        viewModel.getUserTopics(binding.userTopicsPage.pageInput.text.toString())
    }
}