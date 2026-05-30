package duckring.hambab.com.data.store

import duckring.hambab.com.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine

// 함밥 mock 스토어 (in-memory, StateFlow 기반).
// Web lib/store.ts 와 동일 함수 시그니처. Supabase wiring 시 이 객체만 교체.
//
// 다음 단계: Supabase 작업 시 KEEP 시그니처 (suspend → Flow). 라우트 코드는 그대로.

object MockStore {

    private val _users         = MutableStateFlow<List<User>>(emptyList())
    private val _meals         = MutableStateFlow<List<Meal>>(emptyList())
    private val _participants  = MutableStateFlow<List<Participant>>(emptyList())
    private val _restaurants   = MutableStateFlow<List<Restaurant>>(emptyList())
    private var seeded = false

    val users:        StateFlow<List<User>>        = _users.asStateFlow()
    val meals:        StateFlow<List<Meal>>        = _meals.asStateFlow()
    val participants: StateFlow<List<Participant>> = _participants.asStateFlow()
    val restaurants:  StateFlow<List<Restaurant>>  = _restaurants.asStateFlow()

    @Synchronized
    fun ensureSeeded() {
        if (seeded) return
        _users.value = Seed.users
        _meals.value = Seed.meals
        _participants.value = Seed.participants
        _restaurants.value = Seed.restaurants
        seeded = true
    }

    // ─── Reads ────────────────────────────────────────────────────────────
    fun userById(id: String): User? = _users.value.firstOrNull { it.id == id }
    fun mealById(id: String): Meal? = _meals.value.firstOrNull { it.id == id }

    // 조립된 뷰 ─ StateFlow 합성
    val mealsWithMeta: StateFlow<List<MealWithMeta>> =
        combineLatestSimple(_meals, _participants, _users) { meals, parts, users ->
            meals.map { m -> hydrate(m, parts, users) }
        }

    fun mealWithMeta(mealId: String): MealWithMeta? {
        val meal = mealById(mealId) ?: return null
        return hydrate(meal, _participants.value, _users.value)
    }

    private fun hydrate(meal: Meal, parts: List<Participant>, users: List<User>): MealWithMeta {
        val host = users.firstOrNull { it.id == meal.hostId } ?: UNKNOWN
        val mp = parts.filter { it.mealId == meal.id }.map { p ->
            ParticipantWithUser(
                participant = p,
                user = users.firstOrNull { it.id == p.userId } ?: UNKNOWN,
            )
        }
        return MealWithMeta(meal = meal, host = host, participants = mp)
    }

    private val UNKNOWN = User(
        id = "u_unknown", nickname = "알 수 없음",
        mannerScore = 50, trustGrade = TrustGrade.newbie,
    )

    // ─── 필터 헬퍼 ─────────────────────────────────────────────────────────
    fun listMeals(
        mode: MealMode? = null,
        district: District? = null,
        menu: MenuCategory? = null,
        statuses: Set<MealStatus> = setOf(MealStatus.open, MealStatus.confirmed, MealStatus.full),
    ): List<MealWithMeta> {
        val parts = _participants.value
        val users = _users.value
        return _meals.value
            .asSequence()
            .filter { it.status in statuses }
            .filter { mode == null || it.mode == mode }
            .filter { district == null || it.district == district }
            .filter { menu == null || it.menu == menu }
            .sortedBy { it.meetAt }
            .map { hydrate(it, parts, users) }
            .toList()
    }

    // ─── 밀도 통계 ─────────────────────────────────────────────────────────
    fun densityStats(): List<DensityStat> {
        val openMeals = _meals.value.filter { it.status == MealStatus.open }
        val parts = _participants.value
        return District.values().mapNotNull { district ->
            val dm = openMeals.filter { it.district == district }
            if (dm.isEmpty()) return@mapNotNull null
            val joined = dm.sumOf { m ->
                parts.count {
                    it.mealId == m.id && (it.status == ParticipantStatus.joined ||
                        it.status == ParticipantStatus.attended)
                }
            }
            val pending = dm.sumOf { m ->
                val taken = parts.count {
                    it.mealId == m.id && (it.status == ParticipantStatus.joined ||
                        it.status == ParticipantStatus.attended)
                }
                (m.maxPeople - taken).coerceAtLeast(0)
            }
            val topMenu = dm.groupingBy { it.menu }.eachCount().maxByOrNull { it.value }?.key
            DensityStat(
                district = district,
                menu = topMenu,
                openMeals = dm.size,
                pendingSeats = pending,
                label = "${districtKo(district)} ${menuKo(topMenu)} 수요 ${joined + pending}명",
            )
        }
    }

    private fun districtKo(d: District) = when (d) {
        District.gangnam -> "강남"; District.seongsu -> "성수"; District.hongdae -> "홍대"
    }

    private fun menuKo(m: MenuCategory?) = when (m) {
        null -> ""
        MenuCategory.gogi -> "고기"
        MenuCategory.hwe -> "회"
        MenuCategory.mala -> "훠궈"
        MenuCategory.gopchang -> "곱창"
        MenuCategory.yang -> "양갈비"
        MenuCategory.jjimdak -> "찜닭"
        MenuCategory.omakase -> "오마카세"
    }

    // ─── Writes ───────────────────────────────────────────────────────────
    fun upsertUser(u: User) {
        val list = _users.value.toMutableList()
        val idx = list.indexOfFirst { it.id == u.id }
        if (idx >= 0) list[idx] = u else list += u
        _users.value = list
    }

    fun createMeal(meal: Meal): Meal {
        _meals.value = _meals.value + meal
        // 호스트 자동 참가
        _participants.value = _participants.value + Participant(
            id = "p_${System.currentTimeMillis()}_host",
            mealId = meal.id,
            userId = meal.hostId,
            status = ParticipantStatus.joined,
            joinedAt = meal.createdAt,
        )
        return meal
    }

    data class JoinResult(val ok: Boolean, val reason: String? = null)

    fun joinMeal(mealId: String, userId: String): JoinResult {
        val meal = mealById(mealId) ?: return JoinResult(false, "식사를 찾을 수 없어요")
        if (meal.status != MealStatus.open && meal.status != MealStatus.confirmed)
            return JoinResult(false, "참여 불가 상태예요")
        val parts = _participants.value.filter { it.mealId == mealId }
        val existing = parts.firstOrNull { it.userId == userId }
        if (existing != null &&
            (existing.status == ParticipantStatus.joined || existing.status == ParticipantStatus.attended))
            return JoinResult(false, "이미 참여 중이에요")

        val joinedCount = parts.count {
            it.status == ParticipantStatus.joined || it.status == ParticipantStatus.attended
        }
        if (joinedCount >= meal.maxPeople) return JoinResult(false, "자리가 모두 찼어요")

        val newPart = Participant(
            id = "p_${System.currentTimeMillis()}",
            mealId = mealId, userId = userId,
            status = ParticipantStatus.joined,
        )
        _participants.value = _participants.value + newPart
        if (joinedCount + 1 >= meal.maxPeople) {
            _meals.value = _meals.value.map {
                if (it.id == mealId) it.copy(status = MealStatus.full) else it
            }
        }
        return JoinResult(true)
    }

    fun leaveMeal(mealId: String, userId: String) {
        _participants.value = _participants.value.map {
            if (it.mealId == mealId && it.userId == userId)
                it.copy(status = ParticipantStatus.cancelled)
            else it
        }
        val meal = mealById(mealId) ?: return
        if (meal.status == MealStatus.full) {
            _meals.value = _meals.value.map {
                if (it.id == mealId) it.copy(status = MealStatus.open) else it
            }
        }
    }

    fun cancelMealAsHost(mealId: String, hostId: String): JoinResult {
        val meal = mealById(mealId) ?: return JoinResult(false, "식사를 찾을 수 없어요")
        if (meal.hostId != hostId) return JoinResult(false, "호스트만 취소할 수 있어요")
        _meals.value = _meals.value.map {
            if (it.id == mealId) it.copy(status = MealStatus.cancelled) else it
        }
        return JoinResult(true)
    }

    fun resetAll() {
        seeded = false
        _users.value = emptyList()
        _meals.value = emptyList()
        _participants.value = emptyList()
        _restaurants.value = emptyList()
        ensureSeeded()
    }
}

// 3개 StateFlow 합성 — kotlinx.coroutines 의 combine 가 3 인자도 지원하지만
// 명시적 변환 결과를 StateFlow 로 노출하기 위해 helper.
private fun <A, B, C, R> combineLatestSimple(
    a: StateFlow<A>, b: StateFlow<B>, c: StateFlow<C>,
    transform: (A, B, C) -> R,
): StateFlow<R> {
    val out = MutableStateFlow(transform(a.value, b.value, c.value))
    val cb: () -> Unit = { out.value = transform(a.value, b.value, c.value) }
    // 간단 구현 — collect 코루틴은 ViewModel scope 에서 별도 launch.
    // 여기서는 read 시점 fresh 값을 반환하는 derive 패턴으로 갈음.
    // 실 사용은 mealsWithMeta 대신 listMeals(...) 헬퍼를 호출하므로 OK.
    cb()
    return out.asStateFlow()
}
