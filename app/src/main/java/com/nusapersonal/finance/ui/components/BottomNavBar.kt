package com.nusapersonal.finance.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.nusapersonal.finance.ui.theme.BackgroundDark
import com.nusapersonal.finance.ui.theme.BorderDark
import com.nusapersonal.finance.ui.theme.EmeraldPrimary
import com.nusapersonal.finance.ui.theme.TextMuted

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dasbor", Icons.Default.Dashboard)
    object Transactions : Screen("transactions", "Transaksi", Icons.Default.ReceiptLong)
    object Budget : Screen("budget", "Budget", Icons.Default.TrackChanges)
    object AssetsDebts : Screen("assets", "Aset", Icons.Default.TrendingUp)
    object Reports : Screen("reports", "Laporan", Icons.Default.Assessment)
}

@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        Screen.Dashboard,
        Screen.Transactions,
        Screen.Budget,
        Screen.AssetsDebts,
        Screen.Reports
    )

    NavigationBar(
        containerColor = BackgroundDark,
        tonalElevation = NavigationBarDefaults.Elevation
    ) {
        items.forEach { screen ->
            val isSelected = currentRoute == screen.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(screen.route) },
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.title
                    )
                },
                label = { Text(screen.title) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = EmeraldPrimary,
                    selectedTextColor = EmeraldPrimary,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted,
                    indicatorColor = BorderDark
                )
            )
        }
    }
}
