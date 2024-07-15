package com.kdays.android.ui.login

import androidx.lifecycle.ViewModel
import com.kdays.android.logic.Repository

class LoginViewModel : ViewModel() {
    // Storage
    fun removeAllDao() = Repository.removeAllDao()
}