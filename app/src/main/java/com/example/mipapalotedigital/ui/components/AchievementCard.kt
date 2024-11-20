package com.example.mipapalotedigital.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AchievementCard(
    title: String,
    description: String,
    zoneColor: Color,
    isUnlocked: Boolean,
    achievementId: String,
    modifier: Modifier = Modifier
) {
    val animatedZoneColor by animateColorAsState(
        targetValue = zoneColor,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "color"
    )

    val containerColor = if (isUnlocked) {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
    } else {
        Color(0xFF1A1A1A)
    }

    Card(
        modifier = modifier
            .padding(8.dp)
            .aspectRatio(0.75f)
            .shadow(
                elevation = if (isUnlocked) 8.dp else 2.dp,
                spotColor = if (isUnlocked) animatedZoneColor else Color.DarkGray,
                ambientColor = if (isUnlocked) animatedZoneColor else Color.DarkGray,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(
            width = 1.dp,
            color = if (isUnlocked) animatedZoneColor.copy(alpha = 0.3f) else Color(0xFF2A2A2A)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    if (isUnlocked) {
                        drawGlow(animatedZoneColor)
                    }
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Trophy Icon
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    if (isUnlocked) animatedZoneColor else Color(0xFF3A3A3A),
                                    if (isUnlocked) animatedZoneColor.copy(alpha = 0.7f) else Color(0xFF2A2A2A)
                                )
                            )
                        )
                        .border(
                            width = 2.dp,
                            color = if (isUnlocked) animatedZoneColor.copy(alpha = 0.5f) else Color(0xFF3A3A3A),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isUnlocked) Icons.Rounded.EmojiEvents else Icons.Rounded.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(36.dp),
                        tint = if (isUnlocked) Color.White else Color.Gray.copy(alpha = 0.7f)
                    )
                }

                // Title and Description
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUnlocked) animatedZoneColor else Color.Gray,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            shadow = if (isUnlocked) {
                                Shadow(
                                    color = animatedZoneColor.copy(alpha = 0.3f),
                                    offset = Offset(0f, 2f),
                                    blurRadius = 3f
                                )
                            } else null
                        )
                    )

                    Text(
                        text = description,
                        fontSize = 12.sp,
                        color = if (isUnlocked)
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        else
                            Color.Gray.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 16.sp
                    )
                }

                // Status Indicator
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(
                            if (isUnlocked)
                                animatedZoneColor.copy(alpha = 0.15f)
                            else
                                Color(0xFF2A2A2A)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isUnlocked) Icons.Rounded.EmojiEvents else Icons.Rounded.Lock,
                        contentDescription = if (isUnlocked) "Unlocked" else "Locked",
                        modifier = Modifier.size(14.dp),
                        tint = if (isUnlocked) animatedZoneColor else Color.Gray.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawGlow(color: Color) {
    drawCircle(
        color = color.copy(alpha = 0.1f),
        radius = size.width * 0.8f,
        center = Offset(size.width * 0.5f, size.height * 0.2f),
        alpha = 0.4f
    )
}