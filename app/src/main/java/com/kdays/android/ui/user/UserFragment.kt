package com.kdays.android.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kdays.android.MainViewModel
import com.kdays.android.MainViewModel.CurrentFragment.User
import com.kdays.android.R
import com.kdays.android.databinding.FragmentUserBinding
import com.kdays.android.ext.ImageViewExts.loadRounded
import com.kdays.android.ext.StringExts.showToast
import com.kdays.android.logic.service.CountdownService
import com.kdays.android.ui.login.LoginActivity
import com.kdays.android.ui.usertopics.UserTopicsActivity
import com.kdays.android.utils.NetworkUtils.handleNetwork

class UserFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()
    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set current fragment
        viewModel.setCurrentFragment(User)

        val navController = findNavController()

        binding.userHeader.userHeaderLv.visibility = View.GONE

        // Fetching data from cache
        if (viewModel.isUserAvatarSaved() && viewModel.isUserNameSaved()) {
            val cachedAvatar = viewModel.getSavedUserAvatar()
            val cachedUserName = viewModel.getSavedUserName()
            binding.userHeader.userHeaderAvatar.loadRounded(
                imageUrl = cachedAvatar,
                placeholder = R.drawable.ic_user
            )
            binding.userHeader.userHeaderName.text = cachedUserName
        } else {
            viewModel.refresh()
        }

        viewModel.userInfoLiveData.observe(viewLifecycleOwner) { result ->
            binding.userSwipeRefreshLayout.isRefreshing = true
            val userInfoData = result.getOrNull()
            if (userInfoData != null) {
                // Save to cache
                viewModel.saveUserAvatar(userInfoData.fullAvatar)
                viewModel.saveUserName(userInfoData.nick)
                viewModel.saveUid(userInfoData.uid)

                // Use what we saved to load the view
                binding.userHeader.userHeaderAvatar.loadRounded(
                    imageUrl = userInfoData.fullAvatar,
                    placeholder = R.drawable.ic_user
                )
                binding.userHeader.userHeaderName.text = userInfoData.nick
            } else {
                handleNetwork {
                    "未能获取用户信息".showToast()
                    result.exceptionOrNull()?.printStackTrace()
                }
            }
            binding.userSwipeRefreshLayout.isRefreshing = false
        }

        binding.userSwipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
        }

        binding.userLogoutButton.setOnClickListener {
            val alertDialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle("退出登录")
                .setMessage("确定要退出登录吗？")
                .setCancelable(true)
                .setPositiveButton("确定") { dialog, _ ->
                    dialog.dismiss()

                    CountdownService.actionStop(requireContext())
                    LoginActivity.actionStart(requireActivity())
                    requireActivity().finish()
                }
                .setNegativeButton("取消") { dialog, _ -> dialog.dismiss() }
                .create()
            alertDialog.show()
        }

        binding.userSettings.setOnClickListener {
            navController.navigate(R.id.action_user_to_settings, null)
        }

        binding.userAbout.setOnClickListener {
            navController.navigate(R.id.action_user_to_about, null)
        }

        binding.userTopics.setOnClickListener {
            if (viewModel.isUidSaved()) {
                val uid = viewModel.getSavedUid()!!
                if (viewModel.isUserAvatarSaved() && viewModel.isUserNameSaved()) {
                    val avatar = viewModel.getSavedUserAvatar()!!
                    val name = viewModel.getSavedUserName()!!
                    UserTopicsActivity.actionStart(requireContext(), uid, avatar, name)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}