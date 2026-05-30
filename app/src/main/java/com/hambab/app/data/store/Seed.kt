package com.hambab.app.data.store

import com.hambab.app.data.model.*

// Web lib/seed.ts 의 6 유저 / 8 함밥 / 6 식당 을 동일하게 미러링.
// "앱 열었는데 아무도 없음" 회피 (기획서 7).

private const val MIN = 60_000L
private const val HOUR = 60 * MIN
private const val DAY = 24 * HOUR

private fun nowPlus(deltaMs: Long): Long = System.currentTimeMillis() + deltaMs

object Seed {
    val users: List<User> = listOf(
        User(
            id = "u_seed_1", nickname = "강남러",
            mannerScore = 92, trustGrade = TrustGrade.verified,
            favTags = listOf(VibeTag.spicy_ok, VibeTag.no_drink),
            favMenus = listOf(MenuCategory.gogi, MenuCategory.mala),
            createdAt = nowPlus(-30 * DAY),
        ),
        User(
            id = "u_seed_2", nickname = "회한입",
            mannerScore = 88, trustGrade = TrustGrade.verified,
            favTags = listOf(VibeTag.quiet, VibeTag.no_drink),
            favMenus = listOf(MenuCategory.hwe, MenuCategory.omakase),
            createdAt = nowPlus(-60 * DAY),
        ),
        User(
            id = "u_seed_3", nickname = "곱창마니아",
            mannerScore = 95, trustGrade = TrustGrade.regular,
            favTags = listOf(VibeTag.talk_moderate, VibeTag.late_night),
            favMenus = listOf(MenuCategory.gopchang, MenuCategory.gogi),
            createdAt = nowPlus(-120 * DAY),
        ),
        User(
            id = "u_seed_4", nickname = "성수직장러",
            mannerScore = 76, trustGrade = TrustGrade.newbie,
            favTags = listOf(VibeTag.quick),
            favMenus = listOf(MenuCategory.jjimdak),
            createdAt = nowPlus(-10 * DAY),
        ),
        User(
            id = "u_seed_5", nickname = "홍대마라",
            mannerScore = 84, trustGrade = TrustGrade.verified,
            favTags = listOf(VibeTag.spicy_ok, VibeTag.talk_moderate),
            favMenus = listOf(MenuCategory.mala, MenuCategory.hwe),
            createdAt = nowPlus(-45 * DAY),
        ),
        User(
            id = "u_seed_6", nickname = "야식좋아",
            mannerScore = 70, trustGrade = TrustGrade.newbie,
            favTags = listOf(VibeTag.late_night, VibeTag.honbab_ok),
            favMenus = listOf(MenuCategory.gopchang, MenuCategory.yang),
            noShowCount = 1,
            createdAt = nowPlus(-5 * DAY),
        ),
    )

    val meals: List<Meal> = listOf(
        Meal(
            id = "m_seed_1", hostId = "u_seed_1", mode = MealMode.instant,
            menu = MenuCategory.gogi, title = "강남 삼겹살 같이 먹어요",
            district = District.gangnam, spotLabel = "강남역 11번 출구 인근",
            meetAt = nowPlus(45 * MIN), maxPeople = 4,
            tags = listOf(VibeTag.quick, VibeTag.no_drink),
            notes = "1시간 안에 끝낼 분 환영",
            createdAt = nowPlus(-15 * MIN),
        ),
        Meal(
            id = "m_seed_2", hostId = "u_seed_2", mode = MealMode.instant,
            menu = MenuCategory.hwe, title = "오늘 대방어 한 점 어때요",
            district = District.gangnam, spotLabel = "신논현 골목",
            meetAt = nowPlus(2 * HOUR), maxPeople = 3,
            tags = listOf(VibeTag.quiet, VibeTag.no_drink),
            createdAt = nowPlus(-25 * MIN),
        ),
        Meal(
            id = "m_seed_3", hostId = "u_seed_3", mode = MealMode.scheduled,
            menu = MenuCategory.gopchang, title = "내일 저녁 성수 곱창 4인",
            district = District.seongsu, spotLabel = "성수역 2번 출구",
            meetAt = nowPlus(DAY + 4 * HOUR), maxPeople = 4,
            tags = listOf(VibeTag.late_night, VibeTag.talk_moderate),
            createdAt = nowPlus(-3 * HOUR),
        ),
        Meal(
            id = "m_seed_4", hostId = "u_seed_4", mode = MealMode.instant,
            menu = MenuCategory.jjimdak, title = "성수 찜닭 점심 빠르게",
            district = District.seongsu, meetAt = nowPlus(40 * MIN), maxPeople = 2,
            tags = listOf(VibeTag.quick),
            createdAt = nowPlus(-10 * MIN),
        ),
        Meal(
            id = "m_seed_5", hostId = "u_seed_5", mode = MealMode.scheduled,
            menu = MenuCategory.mala, title = "주말 홍대 훠궈 같이 먹을 사람",
            district = District.hongdae, spotLabel = "홍대입구 9번 출구",
            meetAt = nowPlus(2 * DAY + 5 * HOUR), maxPeople = 4,
            tags = listOf(VibeTag.spicy_ok, VibeTag.talk_moderate),
            createdAt = nowPlus(-8 * HOUR),
        ),
        Meal(
            id = "m_seed_6", hostId = "u_seed_6", mode = MealMode.instant,
            menu = MenuCategory.yang, title = "야식 양꼬치 누구",
            district = District.hongdae, meetAt = nowPlus(3 * HOUR), maxPeople = 4,
            tags = listOf(VibeTag.late_night, VibeTag.spicy_ok),
            createdAt = nowPlus(-30 * MIN),
        ),
        Meal(
            id = "m_seed_7", hostId = "u_seed_1", mode = MealMode.scheduled,
            menu = MenuCategory.omakase, title = "이번 주 금요일 오마카세 디너",
            district = District.gangnam, spotLabel = "압구정",
            meetAt = nowPlus(2 * DAY + 12 * HOUR), maxPeople = 2,
            tags = listOf(VibeTag.quiet),
            notes = "분위기 조용한 곳 선호",
            createdAt = nowPlus(-DAY),
        ),
        Meal(
            id = "m_seed_8", hostId = "u_seed_2", mode = MealMode.instant,
            menu = MenuCategory.gogi, title = "오늘 회식 후 2차 삼겹",
            district = District.gangnam, meetAt = nowPlus(90 * MIN), maxPeople = 4,
            tags = listOf(VibeTag.late_night),
            createdAt = nowPlus(-20 * MIN),
        ),
    )

    val participants: List<Participant> = buildList {
        // 호스트 자동 참가
        meals.forEach { m ->
            add(
                Participant(
                    id = "p_seed_${m.id}_host",
                    mealId = m.id, userId = m.hostId,
                    status = ParticipantStatus.joined,
                    joinedAt = m.createdAt,
                ),
            )
        }
        // 게스트 일부
        add(Participant("p_seed_g1", "m_seed_2", "u_seed_6", ParticipantStatus.joined, nowPlus(-20 * MIN)))
        add(Participant("p_seed_g2", "m_seed_4", "u_seed_3", ParticipantStatus.joined, nowPlus(-5 * MIN)))
        add(Participant("p_seed_g3", "m_seed_7", "u_seed_5", ParticipantStatus.joined, nowPlus(-HOUR)))
        add(Participant("p_seed_g4", "m_seed_8", "u_seed_3", ParticipantStatus.joined, nowPlus(-15 * MIN)))
        add(Participant("p_seed_g5", "m_seed_8", "u_seed_5", ParticipantStatus.joined, nowPlus(-12 * MIN)))
    }

    val restaurants: List<Restaurant> = listOf(
        Restaurant("r_seed_1", "강남 곱창전", District.gangnam,
            listOf(MenuCategory.gopchang, MenuCategory.gogi),
            partner = true, hambabSet = "함밥 2인 곱창세트", discountLabel = "함밥러 10% 할인"),
        Restaurant("r_seed_2", "성수 마라마을", District.seongsu,
            listOf(MenuCategory.mala),
            partner = true, hambabSet = "훠궈 2~4인 세트", discountLabel = "평일 점심 8% 할인"),
        Restaurant("r_seed_3", "홍대 양갈비집", District.hongdae,
            listOf(MenuCategory.yang, MenuCategory.gogi),
            partner = true, discountLabel = "함밥 인원 1명당 5% 추가 할인"),
        Restaurant("r_seed_4", "강남 일식바", District.gangnam,
            listOf(MenuCategory.hwe, MenuCategory.omakase),
            partner = true, hambabSet = "디너 오마카세 2인", discountLabel = "5% 함밥러 할인"),
        Restaurant("r_seed_5", "성수 찜닭공방", District.seongsu,
            listOf(MenuCategory.jjimdak), partner = false),
        Restaurant("r_seed_6", "홍대 마라샹궈", District.hongdae,
            listOf(MenuCategory.mala), partner = true, hambabSet = "마라 2인 세트"),
    )
}
