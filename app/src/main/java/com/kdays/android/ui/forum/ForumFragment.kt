package com.kdays.android.ui.forum

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kdays.android.MainViewModel
import com.kdays.android.MainViewModel.CurrentFragment.Forum
import com.kdays.android.databinding.FragmentForumBinding
import com.kdays.android.ext.StringExts.showToast
import com.kdays.android.utils.NetworkUtils.handleNetwork

class ForumFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()
    private var _binding: FragmentForumBinding? = null

    // ViewBinding is only valid between onCreateView and onDestroyView
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set current fragment
        viewModel.setCurrentFragment(Forum)

        val layoutManager = LinearLayoutManager(requireContext())
        binding.forumOuterRecyclerView.layoutManager = layoutManager
        val outerAdapter = OuterForumAdapter(viewModel.outerForumList)
        binding.forumOuterRecyclerView.adapter = outerAdapter

        if (viewModel.noForumCache()) {
            viewModel.refresh()
        }

        viewModel.outerForumListLiveData.observe(viewLifecycleOwner) { result ->
            binding.forumSwipeRefreshLayout.isRefreshing = true
            val outerForumList = result.getOrNull()
            if (outerForumList != null) {
                val oldItemCount = viewModel.outerForumList.size
                viewModel.outerForumList.clear()
                outerAdapter.notifyItemRangeRemoved(0, oldItemCount)
                viewModel.outerForumList.addAll(outerForumList)
                outerAdapter.notifyItemRangeInserted(0, viewModel.outerForumList.size)
            } else {
                handleNetwork {
                    "未能获取板块列表，请尝试刷新".showToast()
                    result.exceptionOrNull()?.printStackTrace()
                }
            }
            binding.forumSwipeRefreshLayout.isRefreshing = false
        }

        binding.forumSwipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}