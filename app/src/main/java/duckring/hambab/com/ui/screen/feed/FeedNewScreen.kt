package duckring.hambab.com.ui.screen.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import duckring.hambab.com.data.auth.AuthRepository
import duckring.hambab.com.data.model.*
import duckring.hambab.com.data.store.FeedRepository
import duckring.hambab.com.ui.component.CtaButton
import duckring.hambab.com.ui.component.TagChip
import duckring.hambab.com.ui.theme.*
import duckring.hambab.com.util.DISTRICTS
import duckring.hambab.com.util.MENUS
import duckring.hambab.com.util.VIBE_TAGS
import duckring.hambab.com.util.label
import kotlinx.coroutines.launch

// FeedNewScreen — 피드 발행 폼.
// 핵심: 자랑/박제 차단 안내 카피 포함. 발행 시 moderation=pending. 어드민 검수 안내 토스트.

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FeedNewScreen(
    onBack: () -> Unit,
    onPublished: () -> Unit,
) {
    var selectedKind by remember { mutableStateOf(PostKind.scheduled) }
    var selectedMenu by remember { mutableStateOf<MenuCategory?>(null) }
    var selectedDistrict by remember { mutableStateOf<District?>(null) }
    var spotLabel by remember { mutableStateOf("") }
    var caption by remember { mutableStateOf("") }
    val selectedTags = remember { mutableStateListOf<VibeTag>() }

    // emoji picker — mock: 메뉴 선택 시 자동 세팅
    var selectedEmoji by remember { mutableStateOf<String?>(null) }

    var validationError by remember { mutableStateOf<String?>(null) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(Modifier.fillMaxSize().background(HbCream)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp),
        ) {
            // ── 헤더 ────────────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(HbCreamCard)
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.semantics { contentDescription = "뒤로 가기" },
                    ) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = HbFg)
                    }
                    Text(
                        text = "피드 올리기",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = HbFg, fontWeight = FontWeight.Bold,
                        ),
                    )
                }
            }

            // ── 자랑/박제 차단 안내 카피 ──────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(HbAmber.copy(alpha = 0.12f))
                        .padding(16.dp),
                ) {
                    Text(
                        text = "함밥 피드는 자랑/박제가 아니에요.\n메뉴와 시간으로 같이 먹을 사람을 찾는 공간이에요.\n얼굴 셀카, 자랑 글은 모더레이션 됩니다.",
                        style = MaterialTheme.typography.bodySmall.copy(color = HbBrown),
                    )
                }
            }

            // ── 사진 (mock: emoji picker) ─────────────────────────────
            item {
                SectionTitle("사진 (필수)")
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(HbCreamSoft)
                        .border(1.dp, HbBorder, RoundedCornerShape(12.dp))
                        .clickable {
                            // 갤러리 stub — 메뉴 선택 시 자동 emoji 사용
                            selectedEmoji = selectedMenu?.let {
                                duckring.hambab.com.util.MENUS.firstOrNull { m -> m.key == it }?.emoji
                            } ?: "🍽️"
                        }
                        .semantics { contentDescription = "사진 선택, 현재 mock — 메뉴 선택 시 자동 emoji" },
                    contentAlignment = Alignment.Center,
                ) {
                    if (selectedEmoji != null) {
                        Text(text = selectedEmoji!!, style = MaterialTheme.typography.displayLarge.copy(fontSize = 64.sp))
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(Icons.Outlined.CameraAlt, null, tint = HbFgMuted, modifier = Modifier.size(32.dp))
                            Text(
                                "메뉴 선택 후 탭",
                                style = MaterialTheme.typography.bodySmall.copy(color = HbFgMuted),
                            )
                        }
                    }
                }
            }

            // ── kind 선택 ─────────────────────────────────────────────
            item {
                SectionTitle("종류")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    PostKind.values().forEach { k ->
                        SelectableChip(
                            label = k.displayName(),
                            active = selectedKind == k,
                            onClick = { selectedKind = k },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            // ── 메뉴 선택 ─────────────────────────────────────────────
            item {
                SectionTitle("메뉴 (필수)")
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    MENUS.forEach { m ->
                        SelectableChip(
                            label = "${m.emoji} ${m.label}",
                            active = selectedMenu == m.key,
                            onClick = {
                                selectedMenu = m.key
                                selectedEmoji = m.emoji
                            },
                        )
                    }
                }
            }

            // ── 지역 선택 ─────────────────────────────────────────────
            item {
                SectionTitle("지역 (필수)")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    DISTRICTS.forEach { d ->
                        SelectableChip(
                            label = d.label,
                            active = selectedDistrict == d.key,
                            onClick = { selectedDistrict = d.key },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            // ── 식당명 (선택) ─────────────────────────────────────────
            item {
                SectionTitle("식당명 (선택)")
                OutlinedTextField(
                    value = spotLabel,
                    onValueChange = { spotLabel = it },
                    placeholder = { Text("예: 신논현 곱창마실", style = MaterialTheme.typography.bodyMedium.copy(color = HbFgMuted)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HbAmber,
                        unfocusedBorderColor = HbBorder,
                        focusedTextColor = HbFg,
                        unfocusedTextColor = HbFg,
                    ),
                )
            }

            // ── caption ──────────────────────────────────────────────
            item {
                SectionTitle("한 줄 소개 (선택)")
                OutlinedTextField(
                    value = caption,
                    onValueChange = { caption = it.take(80) },
                    placeholder = { Text("메뉴 협상/분위기 한 줄. 자랑 글 차단.", style = MaterialTheme.typography.bodyMedium.copy(color = HbFgMuted)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HbAmber,
                        unfocusedBorderColor = HbBorder,
                        focusedTextColor = HbFg,
                        unfocusedTextColor = HbFg,
                    ),
                    supportingText = { Text("${caption.length}/80", style = MaterialTheme.typography.labelSmall.copy(color = HbFgMuted)) },
                )
            }

            // ── 태그 ─────────────────────────────────────────────────
            item {
                SectionTitle("분위기 태그 (선택)")
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    VIBE_TAGS.forEach { t ->
                        val active = selectedTags.contains(t.key)
                        SelectableChip(
                            label = t.label,
                            active = active,
                            onClick = {
                                if (active) selectedTags.remove(t.key) else selectedTags.add(t.key)
                            },
                        )
                    }
                }
            }

            // ── 유효성 오류 ──────────────────────────────────────────
            if (validationError != null) {
                item {
                    Text(
                        text = validationError!!,
                        style = MaterialTheme.typography.bodySmall.copy(color = HbDanger),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    )
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }

        // ── 발행 버튼 (하단 고정) ────────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(HbCreamCard)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            CtaButton(
                label = "발행하기 (검수 후 노출)",
                onClick = {
                    validationError = null
                    when {
                        selectedEmoji == null -> validationError = "사진(emoji)을 선택해 주세요."
                        selectedMenu == null -> validationError = "메뉴를 선택해 주세요."
                        selectedDistrict == null -> validationError = "지역을 선택해 주세요."
                        else -> {
                            val userId = AuthRepository.currentUserId.value ?: "u_guest"
                            scope.launch {
                                FeedRepository.createPost(
                                    kind = selectedKind,
                                    authorId = userId,
                                    photoUrls = listOf(selectedEmoji!!),
                                    caption = caption.trim().ifBlank { null },
                                    menu = selectedMenu!!,
                                    district = selectedDistrict!!,
                                    spotLabel = spotLabel.trim().ifBlank { null },
                                    timeContextMs = System.currentTimeMillis(),
                                    tags = selectedTags.toList(),
                                )
                                snackbar.showSnackbar("검수 후 피드에 노출돼요. 보통 24시간 내 처리됩니다.")
                            }
                            onPublished()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
            )
        }

        SnackbarHost(
            hostState = snackbar,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp),
        )
    }
}

// ─── 내부 공용 ────────────────────────────────────────────────────────────────

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium.copy(
            color = HbAmberDeep, fontWeight = FontWeight.SemiBold,
        ),
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp),
    )
}

@Composable
private fun SelectableChip(
    label: String,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bg = if (active) HbAmber else HbCreamSoft
    val fg = if (active) HbCreamCard else HbFgSoft
    val border = if (active) HbAmber else HbBorder
    Text(
        text = label,
        style = MaterialTheme.typography.labelMedium.copy(
            color = fg, fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
        ),
        modifier = modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(percent = 50))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .semantics { contentDescription = "$label 선택" },
    )
}

private fun PostKind.displayName() = when (this) {
    PostKind.live       -> "지금"
    PostKind.scheduled  -> "예약"
    PostKind.restaurant -> "식당"
    PostKind.review     -> "후기"
}

