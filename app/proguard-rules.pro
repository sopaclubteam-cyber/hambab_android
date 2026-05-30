# 함밥 ProGuard / R8 룰
# v1: Kakao SDK 미사용 — 기본 룰셋으로 충분.
# v2 (Kakao SDK 추가 시): android-build-playbook 함정 #18 참고.
#   -keepattributes Signature
#   -keep class com.kakao.** { *; }
#   -keep interface com.kakao.** { *; }
#   -keep class retrofit2.** { *; }
#   -keep interface retrofit2.** { *; }
#   -keep class okhttp3.** { *; }

# Compose Kotlin serialization (표준)
-keepattributes Signature
-keepattributes *Annotation*

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# Navigation
-keep class androidx.navigation.** { *; }

# StateFlow / Flow
-keep class kotlin.coroutines.** { *; }

# Data classes — JSON 직렬화 시 필요 (Supabase 연동 후)
# -keep class com.hambab.app.data.model.** { *; }
