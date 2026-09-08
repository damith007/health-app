package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ChronoTab
import com.example.ui.theme.OnPrimary
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.Primary
import com.example.ui.theme.SecondaryGreen
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceDark

@Composable
fun ChronoTopHeader(
    currentTab: ChronoTab,
    sprintDayTag: String = "D-14",
    currentTimeLive: String? = null,
    onNotificationsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    unreadNotificationsCount: Int = 0,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceDark.copy(alpha = 0.95f))
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Chrono90 Timeline",
                    tint = Primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "DTrack",
                        color = OnSurface,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.2).sp
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(9999.dp))
                            .background(SecondaryGreen.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = sprintDayTag,
                            color = SecondaryGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                    if (currentTimeLive != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(9999.dp))
                                .background(Primary.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                PulsingDot(color = Primary, sizeDp = 5)
                                Text(
                                    text = currentTimeLive,
                                    color = Primary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                Text(
                    text = currentTab.title,
                    color = OnSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(contentAlignment = Alignment.TopEnd) {
                IconButton(
                    onClick = onNotificationsClick,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("notifications_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = if (unreadNotificationsCount > 0) Primary else OnSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }

                if (unreadNotificationsCount > 0) {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp, end = 4.dp)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(SecondaryGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$unreadNotificationsCount",
                            color = Color.Black,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Primary)
                    .clickable { onProfileClick() }
                    .testTag("profile_avatar_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User Profile",
                    tint = OnPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun PulsingDot(
    color: Color = SecondaryGreen,
    sizeDp: Int = 8
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = Modifier
            .size(sizeDp.dp)
            .alpha(alpha)
            .clip(CircleShape)
            .background(color)
    )
}
