package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.example.data.AppDatabase
import com.example.data.AppRepository
import com.example.ui.AddContentScreen
import com.example.ui.AppViewModel
import com.example.ui.AppViewModelFactory
import com.example.ui.ContentDetailScreen
import com.example.ui.MainDashboardScreen
import com.example.ui.SplashScreen
import com.example.ui.theme.MyApplicationTheme

import android.content.Intent

class MainActivity : ComponentActivity() {
    private var sharedUrlState = mutableStateOf<String?>(null)

    private fun extractUrl(text: String?): String? {
        if (text == null) return null
        val urlRegex = "(https?://[\\w-]+(\\.[\\w-]+)+(/\\S*)?)".toRegex()
        val matchResult = urlRegex.find(text)
        return matchResult?.value ?: text.trim()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.action == Intent.ACTION_SEND && intent.type?.startsWith("text/") == true) {
            sharedUrlState.value = extractUrl(intent.getStringExtra(Intent.EXTRA_TEXT))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        if (intent?.action == Intent.ACTION_SEND && intent.type?.startsWith("text/") == true) {
            sharedUrlState.value = extractUrl(intent.getStringExtra(Intent.EXTRA_TEXT))
        }
        
        if (FirebaseApp.getApps(this).isEmpty()) {
            val options = FirebaseOptions.Builder()
                .setApiKey("AIzaSyDSXlxBtbeW0XSBMrgOOSlIRsYNaFBZZxI")
                .setApplicationId("1:296531380597:android:a5983ac77c8d215616eb3c")
                .setProjectId("auth-ba934")
                .setStorageBucket("auth-ba934.firebasestorage.app")
                .setGcmSenderId("296531380597")
                .build()
            FirebaseApp.initializeApp(this, options)
        }
        
        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "two-tap-db"
        ).fallbackToDestructiveMigration(dropAllTables = false).build()
        val repository = AppRepository(
            database.savedContentDao(),
            database.aiRecommendationDao()
        )

        setContent {
            MyApplicationTheme {
                val viewModel: AppViewModel = viewModel(factory = AppViewModelFactory(repository))
                TwoTapApp(
                    viewModel = viewModel, 
                    sharedUrl = sharedUrlState.value,
                    onSharedUrlHandled = { sharedUrlState.value = null }
                )
            }
        }
    }
}

@Composable
fun TwoTapApp(viewModel: AppViewModel, sharedUrl: String? = null, onSharedUrlHandled: () -> Unit = {}) {
    val navController = rememberNavController()
    
    // Quick hack to pass content to detail screen without serialization in Nav
    var selectedContent by remember { mutableStateOf<com.example.data.SavedContent?>(null) }
    
    androidx.compose.runtime.LaunchedEffect(sharedUrl) {
        if (!sharedUrl.isNullOrBlank()) {
            navController.navigate("add?url=${android.net.Uri.encode(sharedUrl)}") {
                popUpTo("main")
            }
            onSharedUrlHandled()
        }
    }

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(
                onNavigateToWelcome = {
                    navController.navigate("welcome") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateToMain = {
                    navController.navigate("main") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }
        composable("welcome") {
            com.example.ui.WelcomeScreen(
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToSignUp = { navController.navigate("signup") }
            )
        }
        composable("login") {
            com.example.ui.LoginScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSignUp = { 
                    navController.navigate("signup") {
                        popUpTo("welcome")
                    } 
                },
                onNavigateToForgotPassword = { navController.navigate("forgot_password") },
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }
        composable("signup") {
            com.example.ui.SignUpScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLogin = { 
                    navController.navigate("login") {
                        popUpTo("welcome")
                    } 
                },
                onSignUpSuccess = {
                    navController.navigate("email_verification")
                }
            )
        }
        composable("forgot_password") {
            com.example.ui.ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("email_verification") {
            com.example.ui.EmailVerificationScreen(
                onVerified = {
                    navController.navigate("onboarding") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }
        composable("onboarding") {
            com.example.ui.OnboardingScreen(onFinish = {
                navController.navigate("main") {
                    popUpTo("onboarding") { inclusive = true }
                }
            })
        }
        composable("main") {
            MainDashboardScreen(
                viewModel = viewModel,
                onNavigateToAdd = { url ->
                    if (url != null) {
                        navController.navigate("add?url=${android.net.Uri.encode(url)}")
                    } else {
                        navController.navigate("add")
                    }
                },
                onNavigateToDetail = { content ->
                    selectedContent = content
                    navController.navigate("detail")
                },
                onNavigateToLibrary = { navController.navigate("library") },
                onNavigateToAiAssistant = { navController.navigate("ai_assistant") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToSmartLearning = { mode -> navController.navigate("smart_learning_time/$mode") }
            )
        }
        
        composable("smart_learning_time/{mode}") { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode") ?: "Read"
            com.example.ui.SmartLearningTimeScreen(
                mode = mode,
                onNavigateBack = { navController.popBackStack() },
                onTimeSelected = { time -> navController.navigate("smart_learning_thinking/$mode/$time") }
            )
        }
        
        composable("smart_learning_thinking/{mode}/{time}") { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode") ?: "Read"
            val time = backStackEntry.arguments?.getString("time") ?: "10 Minutes"
            com.example.ui.SmartLearningThinkingScreen(
                viewModel = viewModel,
                mode = mode,
                time = time,
                onNavigateToRecommendation = { navController.navigate("smart_learning_recommendation/$mode/$time") {
                    popUpTo("smart_learning_time/$mode") { inclusive = true }
                } }
            )
        }
        
        composable("smart_learning_recommendation/{mode}/{time}") { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode") ?: "Read"
            val time = backStackEntry.arguments?.getString("time") ?: "10 Minutes"
            com.example.ui.SmartLearningRecommendationScreen(
                viewModel = viewModel,
                mode = mode,
                time = time,
                onNavigateBack = { navController.popBackStack("main", false) },
                onStartLearning = { navController.navigate("smart_learning_session") }
            )
        }
        
        composable("smart_learning_session") {
            com.example.ui.SmartLearningSessionScreen(
                onNavigateBack = { navController.popBackStack() },
                onComplete = { navController.navigate("smart_learning_complete") {
                    popUpTo("main") { inclusive = false }
                } }
            )
        }
        
        composable("smart_learning_complete") {
            com.example.ui.SmartLearningCompleteScreen(
                onContinueLearning = { navController.navigate("main") { popUpTo(0) } },
                onReturnHome = { navController.navigate("main") { popUpTo(0) } }
            )
        }
        composable("library") {
            com.example.ui.LibraryScreen(
                viewModel = viewModel,
                onNavigateHome = { navController.navigate("main") { popUpTo("main") { inclusive = true } } },
                onNavigateToAiAssistant = { navController.navigate("ai_assistant") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToDetail = { content ->
                    selectedContent = content
                    navController.navigate("detail")
                }
            )
        }
        composable("ai_assistant") {
            com.example.ui.AiAssistantScreen(
                viewModel = viewModel,
                onNavigateHome = { navController.navigate("main") { popUpTo("main") { inclusive = true } } },
                onNavigateToLibrary = { navController.navigate("library") },
                onNavigateToProfile = { navController.navigate("profile") }
            )
        }
        composable("profile") {
            val context = androidx.compose.ui.platform.LocalContext.current
            com.example.ui.ProfileScreen(
                viewModel = viewModel,
                onNavigateHome = { navController.navigate("main") { popUpTo("main") { inclusive = true } } },
                onNavigateToLibrary = { navController.navigate("library") },
                onNavigateToAiAssistant = { navController.navigate("ai_assistant") },
                onLogout = {
                    val auth = com.example.network.AuthHelper.getAuth(context)
                    auth?.signOut()
                    navController.navigate("welcome") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = "add?url={url}",
            arguments = listOf(androidx.navigation.navArgument("url") {
                type = androidx.navigation.NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val url = backStackEntry.arguments?.getString("url")
            AddContentScreen(
                viewModel = viewModel,
                initialUrl = url,
                onBack = { navController.popBackStack() },
                onNavigateHome = { navController.navigate("main") { popUpTo("main") { inclusive = true } } }
            )
        }
        composable("detail") {
            selectedContent?.let { content ->
                ContentDetailScreen(
                    content = content,
                    onBack = { navController.popBackStack() },
                    onDelete = { id -> viewModel.deleteContent(id) }
                )
            } ?: run {
                navController.popBackStack()
            }
        }
    }
}
