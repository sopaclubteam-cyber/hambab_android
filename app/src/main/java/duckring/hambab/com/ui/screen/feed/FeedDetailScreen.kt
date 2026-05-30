package duckring.hambab.com.ui.screen.feed

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
import duckring.hambab.com.ui.component.MannerBadge
import duckring.hambab.com.ui.component.PostCardCompact
import duckring.hambab.com.ui.component.TagChip
import duckring.hambab.com.ui.component.CtaButton
import duckring.hambab.com.ui.theme.*
import duckring.hambab.com.util.formatFeedTime
import duckring.hambab.com.util.label
import kotlinx.coroutines.launch

// FeedDetailScreen — 인스타 단일 포스트 상세 차용.
// v0.2.0: FeedRepository 경유 — tap 신호 Supabase 로 전송.

@Composable
fun FeedDetailScreen(
    postId: String,
    onBack: () -> Unit,
    onMeal: (String) -> Unit,
    onPost: (String) -> Unit,
    onCta: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    var postWithMeta by remember { mutableStateOf<PostWithMeta?>(null) }
    var related by remember { mutableStateOf<List<PostWithMeta>>(emptyList()) }

    // 포스트 로드 + tap 신호 (상세 진입 = tap)
    LaunchedEffect(postId) {
        postWithMeta = FeedRepository.getPostById(postId)
        FeedRepository.recordAppetite(postId, PostAppetiteAction.tap)
        // 같은 메뉴+지역 관련 포스트 (재유입 loop)
        val p = postWithMeta?.post
        if (p != null) {
            related = FeedRepository.listFeed(menu = p.menu, district = p.district)
                .filter { it.post.id != postId }
                .take(2)
        }
    }

    if (postWithMeta == null) {
        Box(Modifier.fillMaxSize().background(HbCream), contentAlignment = Alignment.Center) {
            Text(
                "포스트를 찾을 수 없어요.",
                style = MaterialTheme.typography.bodyMedium.copy(color = HbFgSoft),
            )
        }
        return
    }

    val p = postWithMeta!!.post
    val emoji = p.photoUrls.firstOrNull() ?: p.menu.emoji()

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
                MannerBadge(grade = postWithMeta!!.author.trustGrade)
                Text(
                    text = postWithMeta!!.author.nickname,
                    style = MaterialTheme.typography.titleSmall.copy(color = HbFg),
                )
                Text(
                    text = "매너 ${postWithMeta!!.author.mannerScore}",
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

                if (!p.caption.isNullOrBlank()) {
                    Text(
                        text = p.caption,
                        style = MaterialTheme.typography.bodyLarge.copy(color = HbFg),
                    )
                }

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
                            text = postWithMeta!!.author.nickname,
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = HbFg, fontWeight = FontWeight.SemiBold,
                            ),
                        )
                    }
                }

                if (p.tags.isNotEmpty()) {
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        p.tags.forEach { tag -> TagChip(label = tag.label()) }
                    }
                }

                val ctaLabel = if (p.mealId != null) "함밥 자세히 보기" else "같이 먹기"
                CtaButton(
                    label = ctaLabel,
                    onClick = {
                        scope.launch {
                            FeedRepository.recordAppetite(p.id, PostAppetiteAction.cta_click)
                        }
                        if (p.mealId != null) onMeal(p.mealId) else onCta(p.id)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp),
                )

                Text(
                    text = "노출 ${p.viewCount}  ·  탭 ${p.tapCount}  ·  같이먹기 ${p.ctaClickCount}  ·  식욕 ${p.appetiteScore}점",
                    style = MaterialTheme.typography.labelSmall.copy(color = HbFgMuted),
                )
            }
        }

        item {
            Box(Modifier.fillMaxWidth().height(6.dp).background(HbBorder))
        }

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
                                    scope.launch {
                                        FeedRepository.recordAppetite(id, PostAppetiteAction.tap)
                                    }
                                    onPost(id)
                                },
                                onCta = { id ->
                                    scope.launch {
                                        FeedRepository.recordAppetite(id, PostAppetiteAction.cta_click)
                                    }
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

private fun MenuCategory.emoji() = duckring.hambab.com.util.MENUS.firstOrNull { it.key == this }?.emoji ?: "🍽️"
