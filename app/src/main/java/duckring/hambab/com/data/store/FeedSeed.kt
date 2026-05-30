package duckring.hambab.com.data.store

import duckring.hambab.com.data.model.*

// 피드 시드 데이터 — Web lib/feed-store.ts SEED_POSTS 6개 한국어 카피 그대로 미러.
// photo_urls 는 메뉴 emoji 단일 문자열로 대체 (mock).
// 사람 셀카 emoji 절대 X. 음식/식재료 emoji 만.

private const val MIN = 60_000L
private const val HOUR = 60 * MIN
private const val DAY = 24 * HOUR

private fun nowPlus(deltaMs: Long): Long = System.currentTimeMillis() + deltaMs

// 식욕 점수: cta*5 + tap*2 + view*0.1
private fun calc(view: Int, tap: Int, cta: Int): Int =
    Math.round(cta * 5f + tap * 2f + view * 0.1f)

object FeedSeed {
    val posts: List<Post> = listOf(
        // ① 라이브 — 강남 곱창 (가장 강한 식욕 자극)
        Post(
            id = "po_seed_1",
            kind = PostKind.live,
            authorId = "u_seed_1",
            photoUrls = listOf("🍢"),
            caption = "강남 곱창 1자리 남았어요. 30분 안에 합류 가능하면 같이 먹어요.",
            menu = MenuCategory.gopchang,
            district = District.gangnam,
            spotLabel = "신논현 곱창마실",
            timeContext = nowPlus(-15 * MIN),
            tags = listOf(VibeTag.talk_moderate),
            viewCount = 142,
            tapCount = 28,
            ctaClickCount = 6,
            appetiteScore = calc(142, 28, 6),
            moderation = PostModeration.approved,
            reportCount = 0,
            createdAt = nowPlus(-15 * MIN),
        ),
        // ② 라이브 — 성수 오마카세
        Post(
            id = "po_seed_2",
            kind = PostKind.live,
            authorId = "u_seed_2",
            photoUrls = listOf("🍣"),
            caption = "오마카세 자리 1개 남음. 조용히 드실 분 환영이에요.",
            menu = MenuCategory.omakase,
            district = District.seongsu,
            spotLabel = "성수 스시 오마카세",
            timeContext = nowPlus(-30 * MIN),
            tags = listOf(VibeTag.quiet, VibeTag.no_drink),
            viewCount = 89,
            tapCount = 17,
            ctaClickCount = 3,
            appetiteScore = calc(89, 17, 3),
            moderation = PostModeration.approved,
            reportCount = 0,
            createdAt = nowPlus(-30 * MIN),
        ),
        // ③ 예약 함밥 모집 — 홍대 마라탕 7시
        Post(
            id = "po_seed_3",
            kind = PostKind.scheduled,
            authorId = "u_seed_3",
            photoUrls = listOf("🍲"),
            caption = "오늘 저녁 7시 홍대 마라탕 2명 모집. 매운 거 환영.",
            menu = MenuCategory.mala,
            district = District.hongdae,
            spotLabel = "홍대 라화쿵푸",
            timeContext = nowPlus(2 * HOUR),
            tags = listOf(VibeTag.spicy_ok, VibeTag.talk_moderate),
            viewCount = 211,
            tapCount = 45,
            ctaClickCount = 9,
            appetiteScore = calc(211, 45, 9),
            moderation = PostModeration.approved,
            reportCount = 0,
            createdAt = nowPlus(-3 * HOUR),
        ),
        // ④ 예약 함밥 — 강남 회 내일 점심 (moderation=pending → 피드 비노출, 어드민 큐)
        Post(
            id = "po_seed_4",
            kind = PostKind.scheduled,
            authorId = "u_seed_1",
            photoUrls = listOf("🐟"),
            caption = "내일 점심 강남 회 1팀 모집. 빠른 식사 환영.",
            menu = MenuCategory.hwe,
            district = District.gangnam,
            spotLabel = "강남 모듬회 1번지",
            timeContext = nowPlus(20 * HOUR),
            tags = listOf(VibeTag.quick, VibeTag.no_drink),
            viewCount = 156,
            tapCount = 22,
            ctaClickCount = 4,
            appetiteScore = calc(156, 22, 4),
            moderation = PostModeration.pending,          // 검수 큐 시연용
            reportCount = 0,
            createdAt = nowPlus(-1 * HOUR),
        ),
        // ⑤ 식당 공식 콘텐츠 — 성수 양갈비 함밥 세트 (boost 7일, B2B)
        Post(
            id = "po_seed_5",
            kind = PostKind.restaurant,
            authorId = "u_seed_2",
            photoUrls = listOf("🍖"),
            caption = "\"함밥 2인 세트\" 양갈비 + 비빔국수 33,000원. 6시~8시 단독 운영.",
            menu = MenuCategory.yang,
            district = District.seongsu,
            spotLabel = "성수 양갈비집",
            timeContext = nowPlus(4 * HOUR),
            tags = listOf(VibeTag.talk_moderate),
            viewCount = 320,
            tapCount = 61,
            ctaClickCount = 14,
            appetiteScore = calc(320, 61, 14),
            moderation = PostModeration.approved,
            reportCount = 0,
            boostUntil = nowPlus(7 * DAY),
            createdAt = nowPlus(-6 * HOUR),
        ),
        // ⑥ 후기 — 강남 한우 (재유입 loop 트리거)
        Post(
            id = "po_seed_6",
            kind = PostKind.review,
            authorId = "u_seed_3",
            photoUrls = listOf("🥩"),
            caption = "어제 강남 고기 함밥 성사. 매너 좋은 분들이라 또 가고 싶어요.",
            menu = MenuCategory.gogi,
            district = District.gangnam,
            spotLabel = "강남 한우 정육식당",
            timeContext = nowPlus(-22 * HOUR),
            tags = listOf(VibeTag.talk_moderate),
            viewCount = 198,
            tapCount = 33,
            ctaClickCount = 8,
            appetiteScore = calc(198, 33, 8),
            moderation = PostModeration.approved,
            reportCount = 0,
            createdAt = nowPlus(-20 * HOUR),
        ),
    )
}
