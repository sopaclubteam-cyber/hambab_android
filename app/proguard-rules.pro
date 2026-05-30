# 함밥 ProGuard / R8 룰
# v2.0 (Supabase Kotlin SDK + Ktor OkHttp 추가)

# ── 공통 ────────────────────────────────────────────────────────────────────
# Kotlin reflection / Serialization 에 필수 (android-build-playbook #18)
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# ── Kotlin Coroutines ────────────────────────────────────────────────────────
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# ── Kotlin Serialization (Supabase JSON 파싱) ────────────────────────────────
-keepclassmembers class kotlinx.serialization.json.** { *; }
-keep class kotlinx.serialization.** { *; }
-dontwarn kotlinx.serialization.**
# Data 클래스 시리얼라이저 런타임 리플렉션 보호
-keep class duckring.hambab.com.data.remote.** { *; }

# ── Ktor (Supabase HTTP transport) ───────────────────────────────────────────
# android-build-playbook #4: Ktor missing class
-keep class io.ktor.** { *; }
-keep interface io.ktor.** { *; }
-dontwarn io.ktor.**

# ── Supabase Kotlin SDK ──────────────────────────────────────────────────────
-keep class io.github.jan.supabase.** { *; }
-keep interface io.github.jan.supabase.** { *; }
-dontwarn io.github.jan.supabase.**

# ── OkHttp (Ktor engine) ─────────────────────────────────────────────────────
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# ── Navigation ──────────────────────────────────────────────────────────────
-keep class androidx.navigation.** { *; }

# ── StateFlow / Flow / Coroutines ───────────────────────────────────────────
-keep class kotlin.coroutines.** { *; }
