package com.hambab.app.ui.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hambab.app.data.auth.AuthRepository
import com.hambab.app.ui.component.HambabBottomBar
import com.hambab.app.ui.component.HambabTopBar
import com.hambab.app.ui.screen.detail.MealDetailScreen
import com.hambab.app.ui.screen.home.HomeScreen
import com.hambab.app.ui.screen.login.LoginScreen
import com.hambab.app.ui.screen.newmeal.NewMealScreen
import com.hambab.app.ui.screen.now.NowScreen
import com.hambab.app.ui.screen.profile.ProfileScreen
import com.hambab.app.ui.screen.scheduled.ScheduledScreen

object HambabRoute {
    const val HOME = "home"
    const val NOW = "now"
    const val SCHEDULED = "scheduled"
    const val NEW_MEAL = "meals/new"
    const val MEAL_DETAIL = "meals/{mealId}"
    const val PROFILE = "profile"
    const val LOGIN = "login"
    fun mealDetail(id: String) = "meals/$id"
}

@Composable
fun HambabNavHost() {
    val nav = rememberNavController()
    val currentRoute = nav.currentBackStackEntryAsState().value?.destination?.route
    val tabKey = when {
        currentRoute == HambabRoute.HOME -> "home"
        currentRoute == HambabRoute.NOW -> "now"
        currentRoute == HambabRoute.SCHEDULED -> "scheduled"
        currentRoute == HambabRoute.PROFILE -> "profile"
        else -> ""
    }
    val showChrome = currentRoute != HambabRoute.LOGIN

    val currentUserId by AuthRepository.currentUserId.collectAsState()
    val nicknameOrCta = AuthRepository.currentUser()?.nickname ?: "시작하기"

    Scaffold(
        topBar = {
            if (showChrome) {
                HambabTopBar(
                    onCreateMeal = {
                        if (currentUserId == null) nav.navigate(HambabRoute.LOGIN)
                        else nav.navigate(HambabRoute.NEW_MEAL)
                    },
                    onProfile = {
                        if (currentUserId == null) nav.navigate(HambabRoute.LOGIN)
                        else nav.navigate(HambabRoute.PROFILE)
                    },
                    nicknameOrCta = nicknameOrCta,
                )
            }
        },
        bottomBar = {
            if (showChrome) {
                HambabBottomBar(current = tabKey) { key ->
                    val target = when (key) {
                        "home" -> HambabRoute.HOME
                        "now" -> HambabRoute.NOW
                        "scheduled" -> HambabRoute.SCHEDULED
                        "profile" -> if (currentUserId == null) HambabRoute.LOGIN else HambabRoute.PROFILE
                        else -> HambabRoute.HOME
                    }
                    if (currentRoute != target) {
                        nav.navigate(target) {
                            popUpTo(HambabRoute.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            }
        },
    ) { inner ->
        Box(Modifier.fillMaxSize().padding(inner)) {
            NavHost(
                navController = nav,
                startDestination = HambabRoute.HOME,
            ) {
                composable(HambabRoute.HOME) {
                    HomeScreen(
                        onNow = { nav.navigate(HambabRoute.NOW) },
                        onScheduled = { nav.navigate(HambabRoute.SCHEDULED) },
                        onMeal = { id -> nav.navigate(HambabRoute.mealDetail(id)) },
                        onCreate = {
                            if (currentUserId == null) nav.navigate(HambabRoute.LOGIN)
                            else nav.navigate(HambabRoute.NEW_MEAL)
                        },
                    )
                }
                composable(HambabRoute.NOW) {
                    NowScreen(onMeal = { id -> nav.navigate(HambabRoute.mealDetail(id)) })
                }
                composable(HambabRoute.SCHEDULED) {
                    ScheduledScreen(onMeal = { id -> nav.navigate(HambabRoute.mealDetail(id)) })
                }
                composable(HambabRoute.NEW_MEAL) {
                    NewMealScreen(
                        onCreated = { id ->
                            nav.popBackStack()
                            nav.navigate(HambabRoute.mealDetail(id))
                        },
                        onCancel = { nav.popBackStack() },
                    )
                }
                composable(
                    route = HambabRoute.MEAL_DETAIL,
                    arguments = listOf(navArgument("mealId") { type = NavType.StringType }),
                ) { backStack ->
                    val id = backStack.arguments?.getString("mealId").orEmpty()
                    MealDetailScreen(
                        mealId = id,
                        onBack = { nav.popBackStack() },
                        onRequireLogin = { nav.navigate(HambabRoute.LOGIN) },
                    )
                }
                composable(HambabRoute.PROFILE) {
                    ProfileScreen(
                        onSignOut = {
                            AuthRepository.signOut()
                            nav.navigate(HambabRoute.HOME) {
                                popUpTo(HambabRoute.HOME) { inclusive = true }
                            }
                        },
                        onLogin = { nav.navigate(HambabRoute.LOGIN) },
                    )
                }
                composable(HambabRoute.LOGIN) {
                    LoginScreen(
                        onDone = { nav.popBackStack() },
                    )
                }
            }
        }
    }
}

