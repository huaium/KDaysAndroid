package com.kdays.android.ui.settings

import android.os.Bundle
import android.os.Debug
import android.view.View
import android.widget.Toast
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.kdays.android.R
import com.kdays.android.ext.StringExts.openUrl
import com.kdays.android.ext.StringExts.showToast
import com.kdays.android.logic.dao.DeveloperDao.ENABLE_DEVELOPER
import com.kdays.android.logic.dao.DeveloperDao.isDeveloperEnabled
import com.kdays.android.utils.UiUtils
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingsFragment : PreferenceFragmentCompat() {

    companion object {
        // uc center
        private const val CHANGE_PASSWORD_URL = "https://uc.kdays.net/security/password"
        private const val CHANGE_PASSWORD = "change_password"
        private const val CHANGE_AVATAR_URL = "https://uc.kdays.net/user/avatar"
        private const val CHANGE_AVATAR = "change_avatar"

        // general
        const val USE_BROWSER = "use_browser"

        // theme
        const val THEME_SELECTOR = "theme_selector"
        const val THEME_AUTO = "auto"
        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"

        // developer
        private const val DEVELOPER = "developer"
        const val METHOD_TRACING = "method_tracing"
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // uc center
        findPreference<Preference>(CHANGE_PASSWORD)?.setOnPreferenceClickListener {
            CHANGE_PASSWORD_URL.openUrl(requireContext())
            true
        }

        findPreference<Preference>(CHANGE_AVATAR)?.setOnPreferenceClickListener {
            CHANGE_AVATAR_URL.openUrl(requireContext())
            true
        }

        // theme
        findPreference<ListPreference>(THEME_SELECTOR)?.setOnPreferenceChangeListener { _, newValue ->
            when (newValue as String) {
                THEME_AUTO -> UiUtils.setAutoTheme()
                THEME_LIGHT -> UiUtils.setLightTheme()
                THEME_DARK -> UiUtils.setDarkTheme()
            }

            true
        }

        // developer
        val developerCategory = findPreference<PreferenceCategory>(DEVELOPER)
        developerCategory?.isVisible = isDeveloperEnabled()

        findPreference<SwitchPreferenceCompat>(ENABLE_DEVELOPER)?.setOnPreferenceChangeListener { _, newValue ->
            developerCategory?.isVisible = newValue as Boolean

            true
        }

        findPreference<SwitchPreferenceCompat>(METHOD_TRACING)?.setOnPreferenceChangeListener { _, newValue ->
            val start = newValue as Boolean
            if (start) {
                // Current date and time
                val dateFormat: DateFormat =
                    SimpleDateFormat("dd_MM_yyyy_hh_mm_ss", Locale.getDefault())
                val logDate: String = dateFormat.format(Date())
                // Apply the date and time to the name of the trace log
                Debug.startMethodTracing("kdays-$logDate")
            } else {
                Debug.stopMethodTracing()
                "调用日志已保存至 Android/data/com.kdays.android/files".showToast(Toast.LENGTH_LONG)
            }

            true
        }
    }
}