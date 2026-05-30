package com.hambab.app.data.auth

import com.hambab.app.data.model.TrustGrade
import com.hambab.app.data.model.User
import com.hambab.app.data.store.MockStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// 닉네임 mock 인증. Supabase Auth (카카오 1순위) 로 swap 예정.

object AuthRepository {

    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    fun currentUser(): User? = _currentUserId.value?.let { MockStore.userById(it) }

    fun signIn(nickname: String): User {
        val cleaned = nickname.trim().ifBlank { "이름 없는 함밥러" }
        val id = "u_${System.currentTimeMillis().toString(36)}"
        val u = User(
            id = id, nickname = cleaned,
            mannerScore = 80, trustGrade = TrustGrade.newbie,
        )
        MockStore.upsertUser(u)
        _currentUserId.value = id
        return u
    }

    fun signInAsSeed(seedUserId: String): User? {
        val u = MockStore.userById(seedUserId) ?: return null
        _currentUserId.value = seedUserId
        return u
    }

    fun signOut() { _currentUserId.value = null }
}
