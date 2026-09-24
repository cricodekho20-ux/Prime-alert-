package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NewsArticle
import com.example.ui.theme.BreakingPulseRed
import com.example.ui.theme.PrimeRedDark
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BreakingNewsTicker(
    breakingNewsList: List<NewsArticle>,
    onNewsClick: (String) -> Unit
) {
    if (breakingNewsList.isEmpty()) return

    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(breakingNewsList.size) {
        if (breakingNewsList.size > 1) {
            while (true) {
                delay(4500)
                currentIndex = (currentIndex + 1) % breakingNewsList.size
            }
        }
    }

    val currentArticle = breakingNewsList.getOrNull(currentIndex % breakingNewsList.size) ?: return

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    listOf(PrimeRedDark, BreakingPulseRed, PrimeRedDark)
                )
            )
            .clickable { onNewsClick(currentArticle.id) }
            .padding(horizontal = 12.dp, vertical = 7.dp)
            .testTag("breaking_news_ticker")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // "BREAKING" Badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(BreakingPulseRed)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "BREAKING",
                    color = BreakingPulseRed,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Animated headline text
            Box(modifier = Modifier.weight(1f)) {
                AnimatedContent(
                    targetState = currentArticle,
                    transitionSpec = {
                        (slideInVertically { height -> height } + fadeIn()) with
                                (slideOutVertically { height -> -height } + fadeOut())
                    },
                    label = "tickerAnimation"
                ) { target ->
                    Text(
                        text = target.headline.replace("🔴 ", "").replace("BREAKING: ", ""),
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Open Breaking News",
                tint = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.size(12.dp)
            )
        }
    }
}
