# 함밥 (Hambab) — Android

> 혼자라서 포기했던 메뉴를, 실제로 먹게 해주는 외식 실행 플랫폼.
> Web/Hambab 와 동일한 데이터 모델 / 톤앤매너 / 화면 흐름.

## 현재 베타 (2026-05)

- **빌드**: Compose Material 3, Kotlin 2.1.21, AGP 8.9.1, Gradle 8.11.1.
- **데이터**: in-memory mock (`MockStore` + `Seed`) — Web `lib/store.ts` 와 동일한 6 유저 / 8 함밥 / 6 식당 시드.
- **인증**: 닉네임 mock 또는 시드 유저 체험 로그인 (`AuthRepository`).
- **다음 단계**: Supabase 연동 (다음 세션 — Web `lib/store-supabase.ts` 와 동일 시그니처).

## 빠른 시작

```bash
cd Android/hambab_android
cp local.properties.example local.properties   # sdk.dir 경로 확인
./gradlew assembleDebug                          # ./app/build/outputs/apk/debug/app-debug.apk
```

또는 Android Studio 에서 폴더 열고 Sync → Run.

## 디렉토리

```
Android/hambab_android/
├── build.gradle.kts              루트 (plugins alias 만)
├── settings.gradle.kts           include(":app")
├── gradle/libs.versions.toml     의존성 카탈로그 (mock-only — Hilt/Firebase/Supabase 없음)
├── gradle.properties             JVM 4G heap
└── app/
    ├── build.gradle.kts          AGP 설정 (minSdk 26 / target 35)
    └── src/main/
        ├── AndroidManifest.xml
        ├── res/                  values (cream/amber/brown) + themes (light only)
        └── java/com/hambab/app/
            ├── HambabApp.kt              Application — MockStore.ensureSeeded()
            ├── MainActivity.kt           Compose entry
            ├── data/
            │   ├── model/Models.kt       User / Meal / Participant / Restaurant
            │   ├── store/Seed.kt         6 유저 / 8 함밥 / 6 식당 (Web 와 동기화)
            │   ├── store/MockStore.kt    StateFlow 기반 in-memory + density 계산
            │   └── auth/AuthRepository.kt 닉네임 mock 로그인
            ├── util/
            │   ├── Catalog.kt            DISTRICTS / MENUS / VIBE_TAGS / TRUST_GRADES
            │   └── Format.kt             "오늘 오후 7시" / "2시간 후" / "1/4명"
            └── ui/
                ├── theme/
                │   ├── Color.kt          HbCream/Amber/Brown/Fg 토큰 (hambab-design 스킬 1:1)
                │   ├── Type.kt           Pretendard 위계 (11~32sp)
                │   ├── Shape.kt          16dp card / 9999 chip / 12dp button
                │   └── Theme.kt          Material 3 ColorScheme override (light only)
                ├── component/
                │   ├── Surfaces.kt           HbCard (brown 알파 8% 그림자)
                │   ├── TagChip.kt
                │   ├── MannerBadge.kt
                │   ├── EmptyState.kt
                │   ├── DensityStat.kt        가로 스크롤 밀도 strip
                │   ├── FilterBar.kt          지역 · 메뉴 chip 군
                │   ├── MealCard.kt           메뉴 emoji + 인원 fraction + 태그 + 호스트
                │   └── HambabBars.kt         상단 TopBar + 하단 BottomBar 4탭
                ├── nav/HambabNav.kt          Scaffold + NavHost
                └── screen/
                    ├── home/HomeScreen.kt    밀도 strip + 지금/예약 진입 + 지금 함밥 3건
                    ├── now/NowScreen.kt      필터 + 3시간 이내 리스트
                    ├── scheduled/ScheduledScreen.kt  필터 + 예약 리스트
                    ├── newmeal/NewMealScreen.kt 함밥 만들기 (mode/메뉴/지역/시간/태그)
                    ├── detail/MealDetailScreen.kt  상세 + 참여/취소/호스트 액션
                    ├── profile/ProfileScreen.kt    내 함밥 + 데모 리셋
                    └── login/LoginScreen.kt        닉네임 + 시드 유저 체험
```

## 디자인 / 톤앤매너

- 정본 문서: `../../Web/Hambab/docs/tone-and-manner.md`
- 스킬: `/hambab-design` — 코드 작성 / 화면 추가 / 마켓 자료 작성 시 무조건 호출
- 색 토큰은 `ui/theme/Color.kt` 에 노출 (HbCream / HbAmber / HbBrown / HbFg / HbOk·Warn·Danger)
- 4축 (타이포 · 색 · 간격 · 인터랙션) 체크리스트 + 안티패턴 갤러리는 스킬 본문 참고
- 다크 모드는 v1 미지원 — 시스템 다크여도 cream 톤 강제

## Web/Android 동기화 매트릭스

| 영역 | Web (Next.js) | Android (Compose) | 일치도 |
|---|---|---|---|
| 데이터 모델 | `lib/types.ts` | `data/model/Models.kt` | ✅ 100% (snake_case ↔ camelCase) |
| 시드 (6 유저 / 8 함밥 / 6 식당) | `lib/seed.ts` | `data/store/Seed.kt` | ✅ 동일 |
| 카탈로그 | `lib/constants.ts` | `util/Catalog.kt` | ✅ 동일 |
| 톤 | `tailwind.config.ts hb.*` | `ui/theme/Color.kt Hb*` | ✅ 동일 |
| 메뉴 emoji | 🥩🐟🍲🍢🍖🍗🍣 | 동일 | ✅ |
| 라우트 | 7 사용자 + 6 어드민 | 7 사용자 (어드민 X) | 어드민은 Web 전용 |
| 인증 | 닉네임 mock | 닉네임 mock | ✅ |
| 백엔드 | mock localStorage / Supabase ready | mock in-memory | Supabase 다음 단계 |

## 다음 단계

1. **Supabase 연동 (다음 세션)** — `data/store/MockStore.kt` 와 동일 시그니처의 `SupabaseStore.kt` 추가 + DI 또는 단순 swap. Web `lib/store-supabase.ts` 패턴 차용.
2. **Pretendard 폰트** — `res/font/pretendard.xml` (Google Fonts API or 로컬 ttf 4종) → `Type.kt` 의 `HbFontFamily` 교체
3. **카카오 / 애플 / 구글 로그인** — Supabase Auth provider 활성화 + Credential Manager + Kakao SDK (참고: 메모리 `kakao-android-integration-playbook`)
4. **카드 호버 / pressed 상태** — `combinedClickable` + `InteractionSource` 로 200ms ease-out transition
5. **TalkBack 라벨** — `MealCard` 에 `Modifier.semantics { contentDescription = "..." }` 추가
6. **첫 Play Store 배포** — `/play-store-first-deploy` 스킬 함정 9개 체크
