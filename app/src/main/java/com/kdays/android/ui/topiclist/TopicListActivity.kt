package com.kdays.android.ui.topiclist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kdays.android.BaseActivity
import com.kdays.android.R
import com.kdays.android.databinding.ActivityTopiclistBinding
import com.kdays.android.ext.ImageViewExts.load
import com.kdays.android.ext.IntExts.divideRoundUp
import com.kdays.android.ext.StringExts.showToast
import com.kdays.android.ui.editor.EditorActivity
import com.kdays.android.utils.NetworkUtils.handleNetwork
import com.kdays.android.utils.UiUtils.editableFactory

class TopicListActivity : BaseActivity() {

    companion object {
        private const val FORUM_ICON = "forum_icon"
        private const val FORUM_NAME = "forum_name"
        private const val FID = "fid"
        private const val PAGE = "page"

        fun actionStart(
            context: Context,
            fid: String,
            page: String = "1",
            forumIcon: String = "",
            forumName: String = "",
        ) {
            val intent = Intent(context, TopicListActivity::class.java).apply {
                putExtra(FORUM_ICON, forumIcon)
                putExtra(FORUM_NAME, forumName)
                putExtra(FID, fid)
                putExtra(PAGE, page)
            }
            context.startActivity(intent)
        }
    }

    private val viewModel: TopicListViewModel by viewModels<TopicListViewModel> {
        TopicListViewModel.Factory(intent.getStringExtra(FID)!!)
    }
    private lateinit var binding: ActivityTopiclistBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopiclistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.topicListToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // setup recyclerview for both topped and normal topics
        val toppedLayoutManager = LinearLayoutManager(this)
        binding.topicListRecyclerViewTopped.layoutManager = toppedLayoutManager
        val toppedAdapter = TopicListAdapter(viewModel.toppedTopicList)
        binding.topicListRecyclerViewTopped.adapter = toppedAdapter

        val normalLayoutManager = LinearLayoutManager(this)
        binding.topicListRecyclerViewNormal.layoutManager = normalLayoutManager
        val normalAdapter = TopicListAdapter(viewModel.normalTopicList)
        binding.topicListRecyclerViewNormal.adapter = normalAdapter

        // Get forum data
        // arguments have default values, thus not null
        val forumIcon = intent.getStringExtra(FORUM_ICON)!!
        val forumName = intent.getStringExtra(FORUM_NAME)!!
        val page = intent.getStringExtra(PAGE)!!

        // Load toolbar with caches
        if (forumIcon.isNotEmpty() && forumName.isNotEmpty()) {
            binding.topicListCollapsingToolbar.title = forumName
            binding.topicListImageView.load(forumIcon, R.drawable.ic_forum)
        }

        if (viewModel.noCache()) {
            viewModel.getTopicList(page)
        }

        viewModel.topicListLiveData.observe(this) { result ->
            binding.topicListSwipeRefreshLayout.isRefreshing = true
            val topicListData = result.getOrNull()
            if (topicListData != null) {
                // Load toolbar with fetching resources
                if (forumIcon.isEmpty() || forumName.isEmpty()) {
                    binding.topicListCollapsingToolbar.title = topicListData.forum.name
                    binding.topicListImageView.load(topicListData.forum.icon, R.drawable.ic_forum)
                }

                // Update viewModel.admins
                viewModel.admins.clear()
                viewModel.admins.addAll(topicListData.forum.manager)

                // Load RecyclerView for both topped and normal topics
                val oldToppedItemCount = viewModel.toppedTopicList.size
                val oldNormalItemCount = viewModel.normalTopicList.size
                viewModel.toppedTopicList.clear()
                viewModel.normalTopicList.clear()
                toppedAdapter.notifyItemRangeRemoved(0, oldToppedItemCount)
                normalAdapter.notifyItemRangeRemoved(0, oldNormalItemCount)

                viewModel.toppedTopicList.addAll(topicListData.topics.topped)
                viewModel.normalTopicList.addAll(topicListData.topics.normal)
                toppedAdapter.notifyItemRangeInserted(0, viewModel.toppedTopicList.size)
                normalAdapter.notifyItemRangeInserted(0, viewModel.normalTopicList.size)

                binding.topicListDivider.visibility = View.VISIBLE

                // Load page switch layout
                refreshPageTotal(topicListData.pages.count, topicListData.pages.size)
                binding.topicListPage.pageTotal.text = viewModel.pageTotal.toString()

                // Initialize viewModel.catList
                viewModel.catList = topicListData.catList.toString()
            } else {
                handleNetwork {
                    "未能获取主题列表，请尝试刷新".showToast()
                    result.exceptionOrNull()?.printStackTrace()
                }
            }
            binding.topicListSwipeRefreshLayout.isRefreshing = false
            binding.topicListPage.pageLayout.visibility = View.VISIBLE
        }

        binding.topicListSwipeRefreshLayout.setOnRefreshListener {
            viewModel.getTopicList(binding.topicListPage.pageInput.text.toString())
        }

        binding.topicListPage.pageInput.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                val inputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(
                    binding.topicListPage.pageInput.windowToken,
                    0
                )
                view.clearFocus()

                val currentPage = binding.topicListPage.pageInput.text.toString().toInt()

                if (currentPage in 1..viewModel.pageTotal)
                    viewModel.getTopicList(currentPage.toString())
                else
                    "页数超出范围".showToast()
                return@setOnEditorActionListener true
            }
            false
        }

        binding.topicListPage.pagePrevious.setOnClickListener {
            onClickPageButton(false)
        }

        binding.topicListPage.pageNext.setOnClickListener {
            onClickPageButton(true)
        }

        binding.pubTopic.setOnClickListener {
            viewModel.catList?.let {
                EditorActivity.pubActionStart(this, viewModel.fid, forumName, it)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

            R.id.menu_admins -> {
                if (viewModel.admins.isNotEmpty()) {
                    val sb = StringBuilder()
                    viewModel.admins.forEachIndexed { index, admin ->
                        sb.append(admin)
                        if (index != viewModel.admins.lastIndex) {
                            sb.append("\n")
                        }
                    }
                    val alertDialog = MaterialAlertDialogBuilder(this)
                        .setTitle("版主")
                        .setMessage(sb.toString())
                        .setCancelable(true)
                        .setPositiveButton("返回") { dialog, _ -> dialog.dismiss() }
                        .create()
                    alertDialog.show()
                } else {
                    "该板块无版主".showToast()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.topiclist_menu, menu)
        return true
    }

    private fun scrollToTop() {
        binding.topicListNestedScrollView.smoothScrollTo(0, 0, 1000)
    }

    private fun onClickPageButton(next: Boolean) {
        var currentPage = binding.topicListPage.pageInput.text.toString().toInt()
        if (currentPage > viewModel.pageTotal) {
            viewModel.getTopicList(viewModel.pageTotal.toString())
            val editable = editableFactory.newEditable(viewModel.pageTotal.toString())
            binding.topicListPage.pageInput.text = editable
            return
        } else if (currentPage < 1) {
            viewModel.getTopicList("1")
            val editable = editableFactory.newEditable("1")
            binding.topicListPage.pageInput.text = editable
            return
        }

        if (next) {
            if (currentPage < viewModel.pageTotal) {
                currentPage++
                viewModel.getTopicList(currentPage.toString())
                val editable = editableFactory.newEditable(currentPage.toString())
                binding.topicListPage.pageInput.text = editable
                scrollToTop()
            }
            // will not scroll to top if currentPage == viewModel.pageTotal
        } else {
            if (currentPage > 1) {
                currentPage--
                viewModel.getTopicList(currentPage.toString())
                val editable = editableFactory.newEditable(currentPage.toString())
                binding.topicListPage.pageInput.text = editable
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
        viewModel.getTopicList(binding.topicListPage.pageInput.text.toString())
    }
}