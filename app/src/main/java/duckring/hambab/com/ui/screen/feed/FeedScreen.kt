package duckring.hambab.com.ui.screen.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import duckring.hambab.com.data.model.*
import duckring.hambab.com.data.store.FeedRepository
import duckring.hambab.com.data.store.FeedStore
import duckring.hambab.com.ui.component.EmptyState
import duckring.hambab.com.ui.component.PostCard
import duckring.hambab.com.ui.theme.*
import duckring.hambab.com.util.label
import duckring.hambab.com.util.formatFeedTime
import kotlinx.coroutines.launch

// FeedScreen — 인스타 홈 피드 구조 차용.
// v0.2.0: FeedRepository 경유 — Supabase 설정 시 실 DB, 미설정 시 mock fall-through.
// LaunchedEffect(filter 변경) 로 pull/refresh 패턴.

@Composable
fun FeedScreen(
    onPost: (String) -> Unit,
    onCreate: () -> Unit,
    onMeal: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()

    var filterKind by remember { mutableStateOf<PostKind?>(null) }
    var filterDistrict by remember { mutableStateOf<District?>(null) }
    var filterMenu by remember { mutableStateOf<MenuCategory?>(null) }

    var feed by remember { mutableStateOf<List<PostWithMeta>>(emptyList()) }
    var liveStrip by remember { mutableStateOf<List<PostWithMeta>>(emptyList()) }
    var loadKey by remember { mutableStateOf(0) }  // pull-to-refresh 트리거

    // 필터 변경 or 강제 재로드 시 Supabase(또는 mock) 에서 재조회
    LaunchedEffect(filterKind, filterDistrict, filterMenu, loadKey) {
        feed = FeedRepository.listFeed(
            kind = filterKind,
            district = filterDistrict,
            menu = filterMenu,
        )
    }
    LaunchedEffect(loadKey) {
        liveStrip = FeedRepository.listLiveStrip()
    }

    // mock 변경 감지 — FeedStore.posts 변경 시 로컬 목록 자동 갱신
    val postsSnapshot by FeedStore.posts.collectAsState()
    LaunchedEffect(postsSnapshot.size) {
        // mock 모드에서 FeedStore 가 변경되면 피드 목록 재조회
        feed = FeedRepository.listFeed(
            kind = filterKind,
            district = filterDistrict,
            menu = filterMenu,
        )
    }

    Scaffold(
        containerColor = HbCream,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreate,
                containerColor = HbAmber,
                contentColor = HbCreamCard,
                modifier = Modifier
                    .size(56.dp)
                    .semantics { contentDescription = "피드 올리기" },
            ) {
                Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(24.dp))
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
            contentPadding = PaddingValues(bottom = innerPadding.calculateBottomPadding() + 80.dp),
        ) {
            // ── LiveStrip (인스타 Story strip 차용) ─────────────────────
            if (liveStrip.isNotEmpty()) {
                item(key = "live_strip") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(HbCreamCard)
                            .padding(vertical = 12.dp),
                    ) {
                        Text(
                            text = "지금 먹는 중",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = HbAmberDeep, fontWeight = FontWeight.SemiBold,
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                        Spacer(Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                        ) {
                            items(liveStrip, key = { it.post.id }) { post ->
                                LiveStripItem(
                                    post = post,
                                    onClick = { onPost(post.post.id) },
                                )
                            }
                        }
                    }
                }
                item(key = "strip_divider") {
                    Box(Modifier.fillMaxWidth().height(6.dp).background(HbBorder))
                }
            }

            // ── 필터 칩 row 1: kind ──────────────────────────────────────
            item(key = "filter_kind") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(HbCreamCard)
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilterChip(
                        label = "전체",
                        active = filterKind == null,
                        onClick = { filterKind = null },
                    )
                    PostKind.values().forEach { k ->
                        FilterChip(
                            label = k.feedLabel(),
                            active = filterKind == k,
                            onClick = { filterKind = if (filterKind == k) null else k },
                        )
                    }
                }
            }

            // ── 필터 칩 row 2: district + menu ──────────────────────────
            item(key = "filter_district") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(HbCream)
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilterChip("전 지역", filterDistrict == null) { filterDistrict = null }
                    District.values().forEach { d ->
                        FilterChip(
                            label = d.label(),
                            active = filterDistrict == d,
                            onClick = { filterDistrict = if (filterDistrict == d) null else d },
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    FilterChip("전 메뉴", filterMenu == null) { filterMenu = null }
                    MenuCategory.values().forEach { m ->
                        FilterChip(
                            label = "${m.emoji()} ${m.label()}",
                            active = filterMenu == m,
                            onClick = { filterMenu = if (filterMenu == m) null else m },
                        )
                    }
                }
            }

            // ── 본문 피드 카드 ────────────────────────────────────────────
            if (feed.isEmpty()) {
                item(key = "empty") {
                    Box(Modifier.fillMaxWidth().padding(top = 48.dp)) {
                        EmptyState(
                            title = "아직 피드가 없어요",
                            subtitle = "내가 먼저 올리면 다른 함밥러가 합류해요.",
                            ctaLabel = "피드 올리기",
                            onCta = onCreate,
                        )
                    }
                }
            } else {
                items(feed, key = { it.post.id }) { postWithMeta ->
                    // 카드 노출 시 appetite view 자동 트리거 (Supabase 또는 mock)
                    LaunchedEffect(postWithMeta.post.id) {
                        FeedRepository.recordAppetite(postWithMeta.post.id, PostAppetiteAction.view)
                    }
                    PostCard(
                        post = postWithMeta,
                        onTap = { id ->
                            scope.launch {
                                FeedRepository.recordAppetite(id, PostAppetiteAction.tap)
                            }
                            onPost(id)
                        },
                        onCta = { id ->
                            scope.launch {
                                FeedRepository.recordAppetite(id, PostAppetiteAction.cta_click)
                            }
                            val mealId = postWithMeta.post.mealId
                            if (mealId != null) onMeal(mealId)
                            else onPost(id)  // 상세에서 CTA 처리
                        },
                    )
                }
            }
        }
    }
}

// ─── LiveStrip 아이템 (인스타 Story circle 차용) ──────────────────────────────
@Composable
private fun LiveStripItem(
    post: PostWithMeta,
    onClick: () -> Unit,
) {
    val p = post.post
    val emoji = p.photoUrls.firstOrNull() ?: p.menu.emoji()
    val a11yDesc = "라이브 함밥: ${p.menu.label()}, ${p.district.label()}"

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .width(72.dp)
            .clickable(onClick = onClick)
            .semantics { contentDescription = a11yDesc },
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            // 원형 배경
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(HbCreamSoft),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = emoji, fontSize = 26.sp)
            }
            // 빨간 펄스 점 — "지금 먹는 중" 시그널
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(HbCreamCard)
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(HbDanger),
            )
        }
        Text(
            text = p.district.label(),
            style = MaterialTheme.typography.labelSmall.copy(
                color = HbFgSoft, fontWeight = FontWeight.Medium,
            ),
            maxLines = 1,
        )
    }
}

// ─── 필터 칩 ─────────────────────────────────────────────────────────────────
@Composable
private fun FilterChip(
    label: String,
    active: Boolean,
    onClick: () -> Unit,
) {
    val bg = if (active) HbAmber else HbCreamSoft
    val fg = if (active) HbCreamCard else HbFgSoft
    Text(
        text = label,
        style = MaterialTheme.typography.labelMedium.copy(
            color = fg, fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
        ),
        modifier = Modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
            .semantics { contentDescription = "$label 필터" },
    )
}

// PostKind 피드 표시 라벨
private fun PostKind.feedLabel() = when (this) {
    PostKind.live       -> "🔴 지금"
    PostKind.scheduled  -> "예약"
    PostKind.restaurant -> "식당"
    PostKind.review     -> "후기"
}

// MenuCategory emoji 확장 (PostCard 와 동일 source)
private fun MenuCategory.emoji() = duckring.hambab.com.util.MENUS.firstOrNull { it.key == this }?.emoji ?: "🍽️"
