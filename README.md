# 함밥 (Hambab) — Android

> 혼자라서 포기했던 메뉴를, 실제로 먹게 해주는 외식 실행 플랫폼.
> Web/Hambab 와 동일한 데이터 모델 / 톤앤매너 / 화면 흐름.

## 현재 상태 (2026-05-30)

- **빌드**: Compose Material 3, Kotlin, AGP 8.x, minSdk 26 / targetSdk 35.
- **데이터**: in-memory mock (`MockStore` + `Seed`, `FeedStore` + `FeedSeed`) — Supabase 연동 시 swap.
- **인증**: 닉네임 mock / 시드 유저 체험 로그인 (`AuthRepository`).
- **피드**: 인스타 레이아웃 참고 피드 화면 추가 — `FeedScreen` / `FeedDetailScreen` / `FeedNewScreen`.
- **네비**: 5탭 (홈 / 피드 / + 발행 중앙 amber / 지금 / 내 함밥).

## 빠른 시작

```bash
cd Android/hambab_android
cp local.properties.example local.properties   # sdk.dir 경로 확인
./gradlew assembleDebug                          # APK → app/build/outputs/apk/debug/
./gradlew bundleRelease                          # AAB → app/build/outputs/bundle/release/
```

---

## Play Console 첫 심사 제출 체크리스트

### CEO 가 직접 할 일 (코드로 자동화 불가)

| 항목 | 상태 | 비고 |
|---|---|---|
| Keystore 발급 | 미완 | `keytool -genkey -v -keystore hambab.keystore -alias hambab -keyalg RSA -keysize 2048 -validity 10000` |
| `app/build.gradle.kts` signingConfig 등록 | 미완 | CEO 가 keystore 경로/비번 입력 |
| Privacy Policy URL | 미완 | `https://hambab.com/privacy` stub — 실 페이지 CEO 작성 |
| App Icon — Play Console 업로드용 512px PNG | 미완 | `mipmap-*` 의 현재 기본 아이콘 교체 권장 |
| 스크린샷 5장 (폰 + 10인치 태블릿 각 1장 이상) | 미완 | 에뮬레이터 또는 실기기 캡처 |
| Data Safety 폼 답변 (Play Console UI 클릭) | 미완 | 아래 "Data Safety 답변지" 참고 |
| 앱 설명 (Play Console 스토어 등록 정보) | 미완 | 아래 "스토어 설명 초안" 참고 |
| Play Console 첫 내부 테스트 트랙 등록 | 미완 | AAB 업로드 → 내부 테스터 추가 → 검토 후 프로덕션 |

### 자동 완료 (코드에서 처리됨)

| 항목 | 상태 |
|---|---|
| `targetSdk = 35` (2025+ 강제) | ✅ |
| `minSdk = 26` | ✅ |
| INTERNET 권한 only | ✅ READ_CONTACTS / AD_ID / 위치 없음 |
| Privacy Policy URL stub `https://hambab.com/privacy` | ✅ (manifest 추가 아래 참고) |
| `proguard-rules.pro` 기본 룰 | ✅ Kakao SDK 미사용 → v1 OK |
| `versionCode = 1`, `versionName = "0.1.0"` | ✅ |

### Privacy Policy URL — Manifest 추가 방법
`AndroidManifest.xml` 의 `<application>` 태그에 아래를 추가하세요 (CEO 액션):
```xml
<meta-data
    android:name="com.google.android.gms.ads.APPLICATION_ID"
    android:value="ca-app-pub-XXXXXXXXXXXXXXXX~XXXXXXXXXX"/>
```
또는 Play Console 스토어 등록 정보 → 개인정보처리방침 URL 란에 직접 입력.

### Data Safety 답변지

함밥 v1 은 **모든 데이터가 기기 내 in-memory mock** 입니다. 서버 전송 없음.

| 질문 | 답변 |
|---|---|
| 앱이 데이터를 수집하나요? | **아니요** (v1 mock) |
| 데이터를 제3자와 공유하나요? | **아니요** |
| 사용자가 데이터 삭제를 요청할 수 있나요? | **예** (앱 삭제 시 전체 삭제) |
| 앱에서 데이터를 암호화하나요? | **예** (기기 저장소 암호화) |
| 아동용 앱인가요? | **아니요** (만 18세 이상 권장) |

_Supabase 연동 후: 이메일/닉네임 수집 → Data Safety 재작성 필요._

### 스토어 설명 초안 (한국어)

**짧은 설명 (80자):**
혼자라서 포기했던 고기·회·훠궈·곱창, 같은 메뉴 원하는 사람과 바로 함밥해요.

**전체 설명:**
함밥은 "혼자 먹기 싫지만 배달은 싫은 날" 을 위한 외식 실행 앱입니다.

원하는 메뉴·지역·시간을 올리면 같은 걸 원하는 사람과 연결돼요.
소개팅 앱이 아니에요. 한 끼를 같이 먹는 앱입니다.

- 고기 / 회 / 훠궈·마라 / 곱창 / 양갈비 / 찜닭 / 오마카세
- 강남 · 성수 · 홍대 (초기 밀도 집중)
- 지금 함밥 (1~3시간 이내) + 예약 함밥 (미리 날짜 잡기)
- 매너 점수 시스템 — 노쇼 즉시 차감
- 콘텐츠 피드 — 지금 먹는 중 / 모집 중 / 식당 세트 / 후기

---

## 디렉토리 (v1 피드 추가 후)

```
app/src/main/java/com/hambab/app/
├── HambabApp.kt              Application — MockStore + FeedStore 시드
├── MainActivity.kt           Compose entry
├── data/
│   ├── model/Models.kt       User / Meal / Post / PostKind / PostModeration 등
│   ├── store/
│   │   ├── Seed.kt           6 유저 / 8 함밥 / 6 식당
│   │   ├── MockStore.kt      StateFlow in-memory 스토어
│   │   ├── FeedSeed.kt       피드 시드 6개 (Web feed-store.ts 1:1)
│   │   └── FeedStore.kt      피드 StateFlow 스토어
│   └── auth/AuthRepository.kt
├── util/
│   ├── Catalog.kt            DISTRICTS / MENUS / VIBE_TAGS + districtLabel
│   └── Format.kt             formatMeetTime / formatFeedTime / PostKind.label
└── ui/
    ├── theme/Color,Type,Shape,Theme.kt
    ├── component/
    │   ├── Surfaces.kt (HbCard)
    │   ├── MealCard.kt
    │   ├── PostCard.kt       PostCard (기본) + PostCardCompact + CtaButton
    │   ├── TagChip.kt
    │   ├── MannerBadge.kt
    │   ├── EmptyState.kt
    │   ├── DensityStat.kt
    │   ├── FilterBar.kt
    │   └── HambabBars.kt     TopBar + 5탭 BottomBar (+ 중앙 amber 발행 버튼)
    ├── nav/HambabNav.kt      feed / feed/{postId} / feed/new 라우트 추가
    └── screen/
        ├── home/HomeScreen.kt
        ├── feed/
        │   ├── FeedScreen.kt       LiveStrip + 필터 2줄 + 피드 카드 무한 스크롤
        │   ├── FeedDetailScreen.kt 상세 + 관련 포스트 2개 재유입 loop
        │   └── FeedNewScreen.kt    발행 폼 (kind/메뉴/지역/시간/caption/tags)
        ├── now/NowScreen.kt
        ├── scheduled/ScheduledScreen.kt
        ├── newmeal/NewMealScreen.kt
        ├── detail/MealDetailScreen.kt
        ├── profile/ProfileScreen.kt
        └── login/LoginScreen.kt
```

## 디자인 / 톤앤매너

- 정본 문서: `../../Web/Hambab/docs/tone-and-manner.md`
- 스킬: `/hambab-design`
- 4축 체크리스트: 타이포(sp 단위) / 색(cream 배경) / 간격(16dp 카드) / 인터랙션(CTA 48dp min)
- 다크 모드: v1 미지원 — cream 톤 강제 (v2 계획)

## 알려진 제한사항

- 모든 데이터 in-memory mock — 앱 재시작 시 시드 리셋
- Supabase 미연동 — 사진 업로드 mock (메뉴 emoji 대체)
- 카카오/구글/애플 로그인 미연동 — 닉네임 mock 만
- 피드 발행 시 검수 mock — 실제 어드민 검수 로직 없음 (Web 어드민 페이지 별도)
- Pretendard 폰트 미적용 — system SansSerif 사용 중

## 다음 단계

1. Play Console 첫 내부 테스트 트랙 등록 (AAB 업로드)
2. CEO Keystore 발급 + `signingConfig` 등록
3. Privacy Policy 실 페이지 작성 (`https://hambab.com/privacy`)
4. Supabase 연동 — `FeedStore` / `MockStore` → `SupabaseStore` swap
5. Pretendard 폰트 추가 (`res/font/pretendard.xml`)
6. 카카오 로그인 (android-build-playbook 함정 #18 참고)
