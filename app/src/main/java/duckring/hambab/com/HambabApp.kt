package duckring.hambab.com

import android.app.Application
import duckring.hambab.com.data.store.FeedStore
import duckring.hambab.com.data.store.MockStore

class HambabApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // mock store 시드 보장 (StateFlow 초기화 트리거)
        MockStore.ensureSeeded()
        FeedStore.ensureSeeded()
    }
}
