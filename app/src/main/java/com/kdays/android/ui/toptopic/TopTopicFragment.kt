package com.kdays.android.ui.toptopic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kdays.android.MainViewModel
import com.kdays.android.MainViewModel.CurrentFragment.TopTopic
import com.kdays.android.databinding.FragmentToptopicBinding
import com.kdays.android.ext.StringExts.showToast
import com.kdays.android.utils.NetworkUtils.handleNetwork

class TopTopicFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()
    private var _binding: FragmentToptopicBinding? = null

    // ViewBinding is only valid between onCreateView and onDestroyView
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentToptopicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set current fragment
        viewModel.setCurrentFragment(TopTopic)

        val topicsLayoutManager = LinearLayoutManager(requireContext())
        binding.topTopicRecyclerViewTopics.layoutManager = topicsLayoutManager
        val topicsAdapter = TopTopicAdapter(viewModel.topTopicsList)
        binding.topTopicRecyclerViewTopics.adapter = topicsAdapter

        val postsLayoutManager = LinearLayoutManager(requireContext())
        binding.topTopicRecyclerViewPosts.layoutManager = postsLayoutManager
        val postsAdapter = TopTopicAdapter(viewModel.topPostsList)
        binding.topTopicRecyclerViewPosts.adapter = postsAdapter

        if (viewModel.noTopTopicCache()) {
            viewModel.refresh()
        }

        viewModel.topTopicLiveData.observe(viewLifecycleOwner) { result ->
            binding.topTopicSwipeRefreshLayout.isRefreshing = true
            val topTopicData = result.getOrNull()
            if (topTopicData != null) {
                // Load RecyclerView for both lists
                val oldTopicsItemCount = viewModel.topTopicsList.size
                val oldPostsItemCount = viewModel.topPostsList.size
                viewModel.topTopicsList.clear()
                viewModel.topPostsList.clear()
                topicsAdapter.notifyItemRangeRemoved(0, oldTopicsItemCount)
                postsAdapter.notifyItemRangeRemoved(0, oldPostsItemCount)
                viewModel.topTopicsList.addAll(topTopicData.topics)
                viewModel.topPostsList.addAll(topTopicData.posts)
                topicsAdapter.notifyItemRangeInserted(0, viewModel.topTopicsList.size)
                postsAdapter.notifyItemRangeInserted(0, viewModel.topPostsList.size)

                binding.topTopicDividerTopics.visibility = View.VISIBLE
                binding.topTopicDividerPosts.visibility = View.VISIBLE
            } else {
                handleNetwork {
                    "未能获取最新列表，请尝试刷新".showToast()
                    result.exceptionOrNull()?.printStackTrace()
                }
            }
            binding.topTopicSwipeRefreshLayout.isRefreshing = false
        }

        binding.topTopicSwipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}