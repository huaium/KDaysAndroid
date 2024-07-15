package com.kdays.android.ui.text

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.kdays.android.BaseActivity
import com.kdays.android.databinding.ActivityTextBinding

class TextActivity : BaseActivity() {

    companion object {
        private const val TEXT = "text"

        fun actionStart(
            context: Context,
            text: CharSequence
        ) {
            val intent = Intent(context, TextActivity::class.java).apply {
                putExtra(TEXT, text)
            }
            context.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityTextBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTextBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textSelector.text = intent.getCharSequenceExtra(TEXT)
    }
}