package com.hambab.app.ui.screen.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hambab.app.data.model.*
import com.hambab.app.data.store.FeedStore
import com.hambab.app.ui.component.MannerBadge
import com.hambab.app.ui.component.PostCardCompact
import com.hambab.app.ui.component.TagChip
import com.hambab.app.ui.component.CtaButton
import com.hambab.app.ui.theme.*
import com.hambab.app.util.formatFeedTime
import com.hambab.app.util.label

// FeedDetailScreen — 인스타 단일 포스트 상세 차용.
// 차이: 하트/댓글/공유 없음. CTA = "같이 먹기" 1탭. 관련 콘텐츠 재유입 loop.

@Composable
fun FeedDetailScreen(
    postId: String,
    onBack: () -> Unit,
    onMeal: (String) -> Unit,
    onPost: (String) -> Unit,
    onCta: (String) -> Unit,  // CTA 탭 — meal_id 있으면 meals/{id}, 없으면 newmeal with post
) {
    val postsSnapshot by FeedStore.posts.collectAsState()

    val postWithMeta = remember(postsSnapshot, postId) {
        FeedStore.getPostById(postId)
    }

    // 노출 시 tap 신호 기록 (상세 진입 = tap)
    LaunchedEffect(postId) {
        FeedStore.recordAppetite(postId, PostAppetiteAction.tap)
    }

    if (postWithMeta == null) {
        Box(Modifier.fillMaxSize().background(HbCream), contentAlignment = Alignment.Center) {
            Text("포스트를 찾을 수 없어요.", style = MaterialTheme.typography.bodyMedium.copy(color = HbFgSoft))
        }
        return
    }

    val p = postWithMeta.post
    val emoji = p.photoUrls.firstOrNull() ?: p.menu.emoji()

    // 같은 메뉴+지역 관련 포스트 2개 (재유입 loop)
    val related = remember(postsSnapshot, postId) {
        FeedStore.listFeed(menu = p.menu, district = p.district)
            .filter { it.post.id != postId }
            .take(2)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(HbCream),
    ) {
        // ── 상단 back + 호스트 row ───────────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HbCreamCard)
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.semantics { contentDescription = "뒤로 가기" },
                ) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = HbFg)
                }
                MannerBadge(grade = postWithMeta.author.trustGrade)
                Text(
                    text = postWithMeta.author.nickname,
                    style = MaterialTheme.typography.titleSmall.copy(color = HbFg),
                )
                Text(
                    text = "매너 ${postWithMeta.author.mannerScore}",
                    style = MaterialTheme.typography.labelSmall.copy(color = HbFgSoft),
                )
            }
        }

        // ── 큰 사진 영역 (메뉴 emoji 140sp) ─────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .background(HbCreamSoft),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = emoji, fontSize = 140.sp)
                // boost 배지
                if (p.boostUntil != null && p.boostUntil > System.currentTimeMillis()) {
                    Text(
                        text = "⬆ 함밥 세트",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = HbBrown, fontWeight = FontWeight.Bold,
                        ),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(percent = 50))
                            .background(HbAmberSoft.copy(alpha = 0.9f))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                    )
                }
            }
        }

        // ── 메타 행 ──────────────────────────────────────────────────────
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HbCreamCard)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // 메뉴 chip + 지역 chip + spot_label chip
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TagChip(label = p.menu.label())
                    TagChip(label = p.district.label())
                    p.spotLabel?.let { spot ->
                        TagChip(label = spot, background = HbCream, foreground = HbFgSoft)
                    }
                }

                // caption
                if (!p.caption.isNullOrBlank()) {
                    Text(
                        text = p.caption,
                        style = MaterialTheme.typography.bodyLarge.copy(color = HbFg),
                    )
                }

                // 시간 + 호스트 2x1 grid
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(HbCreamSoft)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "모임 시간",
                            style = MaterialTheme.typography.labelSmall.copy(color = HbFgMuted),
                        )
                        Text(
                            text = formatFeedTime(p.timeContext, p.kind),
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = HbAmberDeep, fontWeight = FontWeight.Bold,
                            ),
                        )
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        horizontalAlignment = Alignment.End,
                    ) {
                        Text(
                            text = "올린 사람",
                            style = MaterialTheme.typography.labelSmall.copy(color = HbFgMuted),
                        )
                        Text(
                            text = postWithMeta.author.nickname,
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = HbFg, fontWeight = FontWeight.SemiBold,
                            ),
                        )
                    }
                }

                // 태그 chip wrap
                if (p.tags.isNotEmpty()) {
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        p.tags.forEach { tag -> TagChip(label = tag.label()) }
                    }
                }

                // 풀폭 amber CTA
                val ctaLabel = if (p.mealId != null) "함밥 자세히 보기" else "같이 먹기"
                CtaButton(
                    label = ctaLabel,
                    onClick = {
                        FeedStore.recordAppetite(p.id, PostAppetiteAction.cta_click)
                        if (p.mealId != null) onMeal(p.mealId) else onCta(p.id)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp),
                )

                // 카운터 (디버그/투명성 톤)
                Text(
                    text = "노출 ${p.viewCount}  ·  탭 ${p.tapCount}  ·  같이먹기 ${p.ctaClickCount}  ·  식욕 ${p.appetiteScore}점",
                    style = MaterialTheme.typography.labelSmall.copy(color = HbFgMuted),
                )
            }
        }

        // ── 구분 ─────────────────────────────────────────────────────────
        item {
            Box(Modifier.fillMaxWidth().height(6.dp).background(HbBorder))
        }

        // ── 같은 메뉴+지역 관련 포스트 (재유입 loop) ─────────────────────
        if (related.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(HbCream)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = "${p.district.label()} ${p.menu.label()} 다른 함밥",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = HbFg, fontWeight = FontWeight.Bold,
                        ),
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(related, key = { it.post.id }) { rel ->
                            PostCardCompact(
                                post = rel,
                                onTap = { id ->
                                    FeedStore.recordAppetite(id, PostAppetiteAction.tap)
                                    onPost(id)
                                },
                                onCta = { id ->
                                    FeedStore.recordAppetite(id, PostAppetiteAction.cta_click)
                                    val mid = rel.post.mealId
                                    if (mid != null) onMeal(mid) else onPost(id)
                                },
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(32.dp)) }
    }
}

// MenuCategory emoji 확장 (로컬 사용)
private fun MenuCategory.emoji() = com.hambab.app.util.MENUS.firstOrNull { it.key == this }?.emoji ?: "🍽️"
