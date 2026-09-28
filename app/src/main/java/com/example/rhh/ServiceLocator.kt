package com.example.rhh

import android.content.Context
import com.example.rhh.data.api.RetrofitClient
import com.example.rhh.data.auth.TokenStore
import com.example.rhh.data.repo.DriveLedgerRepo

object ServiceLocator {
    lateinit var tokenStore: TokenStore
        private set
    lateinit var repo: DriveLedgerRepo
        private set

    fun init(context: Context) {
        if (::repo.isInitialized) return
        tokenStore = TokenStore(context.applicationContext)
        val api = RetrofitClient.create(tokenStore)
        repo = DriveLedgerRepo(api, tokenStore)
    }
}
