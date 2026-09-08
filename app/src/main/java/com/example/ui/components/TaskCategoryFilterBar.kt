package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.OnPrimary
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.Primary
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh

@Composable
fun TaskCategoryFilterBar(
    selectedCategory: String?,
    onSelectCategory: (String?) -> Unit,
    categories: List<String> = listOf("#DeepWork", "#Code", "#Fitness", "#Routine", "#Growth", "#LifeOps"),
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(if (selectedCategory != null) Primary else SurfaceContainerHigh)
                .clickable { onSelectCategory(null) }
                .testTag("clear_category_filter"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (selectedCategory != null) Icons.Default.Clear else Icons.Default.FilterAlt,
                contentDescription = "Category Filter",
                tint = if (selectedCategory != null) OnPrimary else OnSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f)
        ) {
            item {
                val isAllSelected = selectedCategory == null
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(if (isAllSelected) Primary.copy(alpha = 0.25f) else SurfaceContainer)
                        .clickable { onSelectCategory(null) }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .testTag("filter_category_all"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "All Slots",
                        color = if (isAllSelected) Primary else OnSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            items(categories) { cat ->
                val isSelected = selectedCategory.equals(cat, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(if (isSelected) Primary.copy(alpha = 0.25f) else SurfaceContainer)
                        .clickable {
                            onSelectCategory(if (isSelected) null else cat)
                        }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .testTag("filter_category_${cat.replace("#", "")}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cat,
                        color = if (isSelected) Primary else OnSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
