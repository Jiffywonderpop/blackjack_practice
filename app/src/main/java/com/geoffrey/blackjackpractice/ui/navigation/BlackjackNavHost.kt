package com.geoffrey.blackjackpractice.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.geoffrey.blackjackpractice.BlackjackApplication
import com.geoffrey.blackjackpractice.di.ViewModelFactory
import com.geoffrey.blackjackpractice.ui.history.SessionDetailScreen
import com.geoffrey.blackjackpractice.ui.history.SessionHistoryScreen
import com.geoffrey.blackjackpractice.ui.history.SessionHistoryViewModel
import com.geoffrey.blackjackpractice.ui.home.HomeScreen
import com.geoffrey.blackjackpractice.ui.play.PlayScreen
import com.geoffrey.blackjackpractice.ui.play.PlayViewModel
import com.geoffrey.blackjackpractice.ui.quiz.StrategyQuizScreen
import com.geoffrey.blackjackpractice.ui.quiz.StrategyQuizViewModel
import com.geoffrey.blackjackpractice.ui.settings.SettingsScreen
import com.geoffrey.blackjackpractice.ui.settings.SettingsViewModel
import com.geoffrey.blackjackpractice.ui.speeddrill.SpeedDrillScreen
import com.geoffrey.blackjackpractice.ui.speeddrill.SpeedDrillViewModel
import com.geoffrey.blackjackpractice.ui.stats.StatsDashboardScreen
import com.geoffrey.blackjackpractice.ui.stats.StatsViewModel

@Composable
fun BlackjackNavHost(navController: NavHostController = rememberNavController()) {
    val application = LocalContext.current.applicationContext as BlackjackApplication
    val factory = ViewModelFactory(application.container)

    NavHost(navController = navController, startDestination = Destinations.HOME) {
        composable(Destinations.HOME) {
            HomeScreen(
                onPlay = { navController.navigate(Destinations.PLAY) },
                onSpeedDrill = { navController.navigate(Destinations.SPEED_DRILL) },
                onQuiz = { navController.navigate(Destinations.STRATEGY_QUIZ) },
                onStats = { navController.navigate(Destinations.STATS_DASHBOARD) },
                onHistory = { navController.navigate(Destinations.SESSION_HISTORY) },
                onSettings = { navController.navigate(Destinations.SETTINGS) }
            )
        }
        composable(Destinations.PLAY) {
            val viewModel: PlayViewModel = viewModel(factory = factory)
            PlayScreen(viewModel, onBack = { navController.popBackStack() })
        }
        composable(Destinations.SPEED_DRILL) {
            val viewModel: SpeedDrillViewModel = viewModel(factory = factory)
            SpeedDrillScreen(viewModel, onBack = { navController.popBackStack() })
        }
        composable(Destinations.STRATEGY_QUIZ) {
            val viewModel: StrategyQuizViewModel = viewModel(factory = factory)
            StrategyQuizScreen(viewModel, onBack = { navController.popBackStack() })
        }
        composable(Destinations.STATS_DASHBOARD) {
            val viewModel: StatsViewModel = viewModel(factory = factory)
            StatsDashboardScreen(viewModel, onBack = { navController.popBackStack() })
        }
        composable(Destinations.SESSION_HISTORY) {
            val viewModel: SessionHistoryViewModel = viewModel(factory = factory)
            SessionHistoryScreen(
                viewModel,
                onBack = { navController.popBackStack() },
                onOpenSession = { sessionId -> navController.navigate(Destinations.sessionDetail(sessionId)) }
            )
        }
        composable(
            Destinations.SESSION_DETAIL,
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val viewModel: SessionHistoryViewModel = viewModel(factory = factory)
            val sessionId = backStackEntry.arguments?.getLong("sessionId") ?: return@composable
            SessionDetailScreen(viewModel, sessionId, onBack = { navController.popBackStack() })
        }
        composable(Destinations.SETTINGS) {
            val viewModel: SettingsViewModel = viewModel(factory = factory)
            SettingsScreen(viewModel, onBack = { navController.popBackStack() })
        }
    }
}
