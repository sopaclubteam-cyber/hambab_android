package duckring.hambab.com.data.auth

import android.content.Context
import duckring.hambab.com.data.model.TrustGrade
import duckring.hambab.com.data.model.User
import duckring.hambab.com.data.store.MockStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

// 닉네임 mock 인증. Supabase Auth (카카오 1순위) 로 swap 예정.
// v0.2.0: getDeviceId() 추가 — Supabase appetite 로그의 user_id 에 사용.
// 디바이스 UUID 는 SharedPreferences 에 영구 저장 (앱 재설치 시 재생성).

object AuthRepository {

    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    private var _deviceId: String? = null

    fun currentUser(): User? = _currentUserId.value?.let { MockStore.userById(it) }

    /**
     * 디바이스 레벨 익명 UUID.
     * SharedPreferences key = "hambab_device_id".
     * 어드민 KPI 에서 unique user 카운트 의미 있게 집계됨.
     * context 없이 호출 시 — null 반환 (Supabase insert 는 user_id=null 허용).
     */
    fun getDeviceId(context: Context): String {
        _deviceId?.let { return it }
        val prefs = context.getSharedPreferences("hambab_prefs", Context.MODE_PRIVATE)
        val existing = prefs.getString("hambab_device_id", null)
        if (existing != null) {
            _deviceId = existing
            return existing
        }
        val newId = UUID.randomUUID().toString()
        prefs.edit().putString("hambab_device_id", newId).apply()
        _deviceId = newId
        return newId
    }

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
