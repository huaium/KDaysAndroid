package com.kdays.android.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ClickableSpan
import android.view.View
import androidx.activity.viewModels
import androidx.core.text.method.LinkMovementMethodCompat
import com.kdays.android.BaseActivity
import com.kdays.android.BuildConfig.UC_ADDRESS
import com.kdays.android.MainActivity
import com.kdays.android.R
import com.kdays.android.databinding.ActivityLoginBinding
import com.kdays.android.ext.StringExts.openUrl
import com.kdays.android.ext.StringExts.showToast
import com.kdays.android.ui.about.AboutFragment.Companion.AGREEMENT_URL


class LoginActivity : BaseActivity() {

    companion object {
        fun actionStart(
            context: Context,
        ) {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.loginToolbar.toolbar)

        viewModel.removeAllDao()

        // start setting spannableString
        val spannableString = SpannableString("我已阅读并同意使用协议")

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                AGREEMENT_URL.openUrl(this@LoginActivity)
            }
        }

        // set spannable range
        spannableString.setSpan(
            clickableSpan,
            spannableString.length - 4,
            spannableString.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // set spannableString to TextView
        binding.loginAgreement.text = spannableString

        // set LinkMovementMethod to make link clickable
        binding.loginAgreement.movementMethod = LinkMovementMethodCompat.getInstance()
        // end setting spannableString

        binding.loginButton.setOnClickListener {
            val isAgreed = binding.loginAgreement.isChecked

            if (isAgreed) {
                val username = binding.loginUsername.text.toString()
                val password = binding.loginPassword.text.toString()

                login(username, password)
            } else {
                binding.loginAgreement.startAnimation(
                    android.view.animation.AnimationUtils.loadAnimation(
                        this,
                        R.anim.translate_check_box_shake
                    )
                )
                "使用协议未同意".showToast()
            }
        }

        binding.loginRegisterButton.setOnClickListener {
            val url = "$UC_ADDRESS/sso/account/create"
            url.openUrl(this)
        }

        binding.loginForget.setOnClickListener {
            val url = "$UC_ADDRESS/sso/forget/password"
            url.openUrl(this)
        }
    }

    override fun onLoginSuccess() {
        MainActivity.actionStart(this)
        finish()
    }
}
