package duckring.hambab.com.data

import duckring.hambab.com.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.auth.Auth

// 앱 전역 Supabase 클라이언트 싱글톤.
// SUPABASE_URL / SUPABASE_ANON_KEY 가 비어 있으면 null 반환 → mock fall-through.
// 이중 초기화 방지: by lazy.

object SupabaseClient {

    /** URL/KEY 세팅 여부 — FeedRepository 가 분기 결정에 사용 */
    val isConfigured: Boolean
        get() = BuildConfig.SUPABASE_URL.isNotBlank() && BuildConfig.SUPABASE_ANON_KEY.isNotBlank()

    val client by lazy {
        require(isConfigured) { "Supabase URL/KEY 미설정. local.properties 확인." }
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY,
        ) {
            install(Postgrest)
            install(Auth)
        }
    }

    /** null-safe 접근 — isConfigured 가 false 면 null */
    fun clientOrNull() = if (isConfigured) client else null
}
