package com.kdays.android.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kdays.android.R
import com.kdays.android.databinding.FragmentAboutBinding
import com.kdays.android.ext.StringExts.openUrl
import com.kdays.android.ext.StringExts.showToast
import com.kdays.android.logic.dao.DeveloperDao.enableDeveloper
import com.kdays.android.logic.dao.DeveloperDao.isDeveloperEnabled
import com.kdays.android.utils.UiUtils.getVersion

class AboutFragment : Fragment() {

    companion object {
        const val ABOUT_US_URL = "https://kdays.net/about"
        const val AGREEMENT_URL = "https://uc.kdays.net/page/agreement"
        const val STATUS_URL = "https://status.kdays.net"
    }

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.aboutLogo.setOnLongClickListener {
            if (!isDeveloperEnabled()) {
                enableDeveloper()
                "开发者选项已启用".showToast()
            }
            true
        }

        // get and set version
        val version: String = getVersion()
        binding.aboutVersion.text = getString(R.string.about_version, version)

        binding.aboutUs.setOnClickListener {
            ABOUT_US_URL.openUrl(requireContext())
        }

        binding.aboutAgreement.setOnClickListener {
            AGREEMENT_URL.openUrl(requireContext())
        }

        binding.aboutStatus.setOnClickListener {
            STATUS_URL.openUrl(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}