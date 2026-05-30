package com.hambab.app

import android.app.Application
import com.hambab.app.data.store.FeedStore
import com.hambab.app.data.store.MockStore

class HambabApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // mock store 시드 보장 (StateFlow 초기화 트리거)
        MockStore.ensureSeeded()
        FeedStore.ensureSeeded()
    }
}
