package com.hambab.app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hambab.app.data.model.*
import com.hambab.app.data.store.FeedSeed
import com.hambab.app.ui.theme.*
import com.hambab.app.util.emoji
import com.hambab.app.util.label
import com.hambab.app.util.formatFeedTime

// PostCard — 함밥 피드 카드.
// 인스타와의 5축 차이:
//   끝점 = "1탭 같이 먹기" (자랑/박제 X)
//   평가축 = 식욕 점수 (좋아요/leaderboard X)
//   하트/댓글/공유 아이콘 row 없음 — amber CTA 1개만.

// ─── 기본 PostCard (피드 리스트) ──────────────────────────────────────────────
@Composable
fun PostCard(
    post: PostWithMeta,
    onTap: (String) -> Unit,
    onCta: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val p = post.post
    val emoji = p.photoUrls.firstOrNull() ?: p.menu.emoji()
    val extraPhotos = (p.photoUrls.size - 1).coerceAtLeast(0)

    val a11yDesc = buildString {
        append("${p.menu.label()} 함밥, ${p.district.label()}")
        p.spotLabel?.let { append(", $it") }
        append(", 식욕 ${p.appetiteScore}점")
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = a11yDesc }
            .clickable { onTap(p.id) },
    ) {
        // ── 사진 영역 (메뉴 emoji 거대 배경) ─────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
                .background(HbCreamSoft),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = emoji, fontSize = 80.sp)

            // kind 배지 — 좌상단
            KindBadge(
                kind = p.kind,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp),
            )

            // boost 배지 — 우상단 (있을 때만)
            if (p.boostUntil != null && p.boostUntil > System.currentTimeMillis()) {
                BoostBadge(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp),
                )
            }

            // 사진 +N — 우하단
            if (extraPhotos > 0) {
                Text(
                    text = "+$extraPhotos",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = HbCreamCard, fontWeight = FontWeight.Bold,
                    ),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                        .clip(HbChipShape)
                        .background(HbFg.copy(alpha = 0.5f))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                )
            }
        }

        // ── 본문 ────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(HbCreamCard)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Spacer(Modifier.height(10.dp))

            // 메타 행: 메뉴 chip + 지역 chip + spot_label
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TagChip(label = p.menu.label())
                TagChip(label = p.district.label())
                p.spotLabel?.let { spot ->
                    TagChip(
                        label = spot,
                        background = HbCream,
                        foreground = HbFgSoft,
                    )
                }
            }

            // caption
            if (!p.caption.isNullOrBlank()) {
                Text(
                    text = p.caption,
                    style = MaterialTheme.typography.bodyMedium.copy(color = HbFg),
                    maxLines = 2,
                )
            }

            // 시간 + 호스트 + 식욕 점수 행
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = formatFeedTime(p.timeContext, p.kind),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = HbAmberDeep, fontWeight = FontWeight.SemiBold,
                    ),
                )
                Text("·", style = MaterialTheme.typography.labelSmall.copy(color = HbFgMuted))
                Text(
                    text = post.author.nickname,
                    style = MaterialTheme.typography.labelMedium.copy(color = HbFgSoft),
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "🔥 ${p.appetiteScore}",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = HbBrownSoft, fontWeight = FontWeight.SemiBold,
                    ),
                )
            }

            Spacer(Modifier.height(4.dp))

            // 풀폭 amber CTA — 1탭 같이 먹기 (함밥 본질)
            CtaButton(
                label = if (p.mealId != null) "함밥 자세히 보기" else "같이 먹기",
                onClick = { onCta(p.id) },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(12.dp))
        }

        // 구분선
        Box(
            Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(HbBorder),
        )
    }
}

// ─── compact variant (관련 콘텐츠 슬롯용) ────────────────────────────────────
@Composable
fun PostCardCompact(
    post: PostWithMeta,
    onTap: (String) -> Unit,
    onCta: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val p = post.post
    val emoji = p.photoUrls.firstOrNull() ?: p.menu.emoji()

    val a11yDesc = "${p.menu.label()} 함밥, ${p.district.label()}, 같이 먹기"

    Column(
        modifier = modifier
            .width(160.dp)
            .clip(HbCardShape)
            .background(HbCreamCard)
            .border(1.dp, HbBorder, HbCardShape)
            .clickable { onTap(p.id) }
            .semantics { contentDescription = a11yDesc },
    ) {
        // 사진 영역 (정사각)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(HbCreamSoft),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = emoji, fontSize = 48.sp)
            KindBadge(
                kind = p.kind,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp),
            )
        }

        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "${p.menu.label()} · ${p.district.label()}",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = HbFg, fontWeight = FontWeight.SemiBold,
                ),
                maxLines = 1,
            )
            p.spotLabel?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall.copy(color = HbFgSoft),
                    maxLines = 1,
                )
            }
            Spacer(Modifier.height(2.dp))
            CtaButton(
                label = "같이 먹기",
                onClick = { onCta(p.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 36.dp),
                textSize = 11.sp,
                paddingV = 6.dp,
            )
        }
    }
}

// ─── 내부 공용 컴포넌트 ───────────────────────────────────────────────────────

@Composable
private fun KindBadge(kind: PostKind, modifier: Modifier = Modifier) {
    val (bg, fg, label) = when (kind) {
        PostKind.live       -> Triple(HbDanger, HbCreamCard, "🔴 지금")
        PostKind.scheduled  -> Triple(HbAmber, HbCreamCard, "예약")
        PostKind.restaurant -> Triple(HbBrown, HbCreamCard, "식당")
        PostKind.review     -> Triple(HbCreamSoft, HbBrown, "후기")
    }
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(
            color = fg, fontWeight = FontWeight.Bold,
        ),
        modifier = modifier
            .clip(HbChipShape)
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    )
}

@Composable
private fun BoostBadge(modifier: Modifier = Modifier) {
    Text(
        text = "⬆ 함밥 세트",
        style = MaterialTheme.typography.labelSmall.copy(
            color = HbBrown, fontWeight = FontWeight.Bold,
        ),
        modifier = modifier
            .clip(HbChipShape)
            .background(HbAmberSoft.copy(alpha = 0.9f))
            .padding(horizontal = 8.dp, vertical = 3.dp),
    )
}

@Composable
internal fun CtaButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textSize: androidx.compose.ui.unit.TextUnit = 14.sp,
    paddingV: androidx.compose.ui.unit.Dp = 14.dp,
) {
    val a11yDesc = label
    Box(
        modifier = modifier
            .heightIn(min = 48.dp)
            .clip(HbButtonShape)
            .background(HbAmber)
            .clickable { onClick() }
            .semantics { contentDescription = a11yDesc },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                color = HbCreamCard,
                fontWeight = FontWeight.Bold,
                fontSize = textSize,
            ),
            modifier = Modifier.padding(vertical = paddingV),
        )
    }
}

// ─── Previews ────────────────────────────────────────────────────────────────

@Preview(name = "PostCard Light", showBackground = true, backgroundColor = 0xFFFEF7E5)
@Composable
private fun PostCardPreviewLight() {
    com.hambab.app.ui.theme.HambabTheme {
        val post = PostWithMeta(
            post = FeedSeed.posts[0],
            author = com.hambab.app.data.store.Seed.users[0],
        )
        PostCard(post = post, onTap = {}, onCta = {})
    }
}

@Preview(name = "PostCard Night", showBackground = true, backgroundColor = 0xFF1C1C1E,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PostCardPreviewNight() {
    com.hambab.app.ui.theme.HambabTheme {
        val post = PostWithMeta(
            post = FeedSeed.posts[4],  // 부스트 포스트
            author = com.hambab.app.data.store.Seed.users[1],
        )
        PostCard(post = post, onTap = {}, onCta = {})
    }
}

@Preview(name = "PostCardCompact Light", showBackground = true, backgroundColor = 0xFFFEF7E5)
@Composable
private fun PostCardCompactPreview() {
    com.hambab.app.ui.theme.HambabTheme {
        val post = PostWithMeta(
            post = FeedSeed.posts[2],
            author = com.hambab.app.data.store.Seed.users[2],
        )
        PostCardCompact(post = post, onTap = {}, onCta = {})
    }
}
