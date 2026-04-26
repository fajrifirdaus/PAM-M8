package org.example.project.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import org.example.project.screens.*
import org.example.project.viewmodel.NewsViewModel
import org.example.project.viewmodel.NotesViewModel
import org.example.project.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    notesViewModel:   NotesViewModel   = koinViewModel(),
    profileViewModel: ProfileViewModel = koinViewModel(),
    newsViewModel:    NewsViewModel    = koinViewModel()
) {
    val navController   = rememberNavController()
    val notesUiState    by notesViewModel.uiState.collectAsStateWithLifecycle()
    val isDarkMode      = notesUiState.isDarkMode

    val navBackStack    by navController.currentBackStackEntryAsState()
    val currentRoute    = navBackStack?.destination?.route

    val bottomNavItems  = listOf(
        BottomNavItem.News,
        BottomNavItem.Notes,
        BottomNavItem.Favorites,
        BottomNavItem.Profile
    )
    val bottomNavRoutes = bottomNavItems.map { it.screen.route }
    val showBottomBar   = currentRoute in bottomNavRoutes

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope       = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(notesUiState.snackbarMessage) {
        notesUiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            notesViewModel.clearSnackbar()
        }
    }

    ModalNavigationDrawer(
        drawerState     = drawerState,
        gesturesEnabled = showBottomBar,
        drawerContent   = {
            ModalDrawerSheet(modifier = Modifier.width(280.dp)) {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                    Column {
                        Icon(Icons.Rounded.NoteAlt, null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("PAM News App", fontSize = 22.sp, fontWeight = FontWeight.Black)
                        Text("Pertemuan 8 — IF25-22017",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(Modifier.height(8.dp))

                Text("MENU UTAMA", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))

                bottomNavItems.forEach { item ->
                    NavigationDrawerItem(
                        icon     = { Icon(item.icon, item.label) },
                        label    = { Text(item.label, fontWeight = FontWeight.SemiBold) },
                        selected = currentRoute == item.screen.route,
                        onClick  = {
                            scope.launch { drawerState.close() }
                            navController.navigate(item.screen.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                    )
                }

                Spacer(Modifier.height(8.dp))
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(Modifier.height(8.dp))

                Text("LAINNYA", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))

                NavigationDrawerItem(
                    icon     = { Icon(Icons.Rounded.Settings, "Settings") },
                    label    = { Text("Pengaturan", fontWeight = FontWeight.SemiBold) },
                    selected = currentRoute == Screen.Settings.route,
                    onClick  = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Settings.route)
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                )
                NavigationDrawerItem(
                    icon     = { Icon(Icons.Rounded.Info, "About") },
                    label    = { Text("Tentang Aplikasi", fontWeight = FontWeight.SemiBold) },
                    selected = currentRoute == Screen.About.route,
                    onClick  = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.About.route)
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                )

                Spacer(Modifier.weight(1f))
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (isDarkMode) Icons.Rounded.DarkMode else Icons.Rounded.LightMode,
                            null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(if (isDarkMode) "Dark Mode" else "Light Mode",
                            fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                    Switch(checked = isDarkMode,
                        onCheckedChange = { notesViewModel.toggleDarkMode() })
                }
            }
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                // ✅ FIX: Pakai AnimatedVisibility seperti bottomBar
                // agar tidak ada layout jump — tinggi TopAppBar tetap "reserved"
                // selama animasi, tidak langsung hilang/muncul secara instan
                AnimatedVisibility(
                    visible = showBottomBar,
                    enter   = fadeIn(animationSpec = tween(200)),
                    exit    = fadeOut(animationSpec = tween(200))
                ) {
                    TopAppBar(
                        title = {
                            Text(
                                text = when (currentRoute) {
                                    Screen.NewsList.route  -> "📰 Berita"
                                    Screen.Notes.route     -> "📝 Catatan"
                                    Screen.Favorites.route -> "🔖 Favorit"
                                    Screen.Profile.route   -> "👤 Profil"
                                    else                   -> "PAM App"
                                },
                                fontWeight = FontWeight.Black, fontSize = 20.sp
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Rounded.Menu, "Menu",
                                    tint = MaterialTheme.colorScheme.primary)
                            }
                        },
                        actions = {
                            IconButton(onClick = { notesViewModel.toggleDarkMode() }) {
                                Icon(
                                    if (isDarkMode) Icons.Rounded.WbSunny else Icons.Rounded.NightsStay,
                                    "Dark Mode",
                                    tint = if (isDarkMode) Color(0xFFFFD600)
                                    else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface)
                    )
                }
            },
            bottomBar = {
                AnimatedVisibility(
                    visible = showBottomBar,
                    enter   = fadeIn(animationSpec = tween(200)),
                    exit    = fadeOut(animationSpec = tween(200))
                ) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp
                    ) {
                        bottomNavItems.forEach { item ->
                            NavigationBarItem(
                                selected = currentRoute == item.screen.route,
                                onClick  = {
                                    navController.navigate(item.screen.route) {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState    = true
                                    }
                                },
                                icon  = { Icon(item.icon, item.label) },
                                label = {
                                    Text(item.label,
                                        fontWeight = if (currentRoute == item.screen.route)
                                            FontWeight.ExtraBold else FontWeight.Normal)
                                }
                            )
                        }
                    }
                }
            }
        ) { padding ->

            NavHost(
                navController       = navController,
                startDestination    = Screen.NewsList.route,
                modifier            = Modifier.padding(padding),
                enterTransition     = { fadeIn(animationSpec = tween(durationMillis = 200)) },
                exitTransition      = { fadeOut(animationSpec = tween(durationMillis = 200)) },
                popEnterTransition  = { fadeIn(animationSpec = tween(durationMillis = 200)) },
                popExitTransition   = { fadeOut(animationSpec = tween(durationMillis = 200)) }
            ) {

                composable(Screen.NewsList.route) {
                    NewsListScreen(
                        viewModel      = newsViewModel,
                        isDarkMode     = isDarkMode,
                        onArticleClick = { id ->
                            navController.navigate(Screen.NewsDetail.createRoute(id))
                        }
                    )
                }

                composable(
                    route     = Screen.NewsDetail.route,
                    arguments = listOf(navArgument("articleId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val articleId = backStackEntry.arguments?.getInt("articleId") ?: return@composable
                    NewsDetailScreen(
                        articleId  = articleId,
                        viewModel  = newsViewModel,
                        isDarkMode = isDarkMode,
                        onBack     = { navController.popBackStack() }
                    )
                }

                composable(Screen.Notes.route) {
                    NotesScreen(
                        viewModel   = notesViewModel,
                        isDarkMode  = isDarkMode,
                        onNoteClick = { id -> navController.navigate(Screen.NoteDetail.createRoute(id)) },
                        onAddNote   = { navController.navigate(Screen.AddNote.route) }
                    )
                }
                composable(Screen.Favorites.route) {
                    FavoritesScreen(
                        viewModel   = notesViewModel,
                        isDarkMode  = isDarkMode,
                        onNoteClick = { id -> navController.navigate(Screen.NoteDetail.createRoute(id)) }
                    )
                }
                composable(Screen.Profile.route) {
                    ProfileScreen(
                        profileViewModel = profileViewModel,
                        isDarkMode       = isDarkMode
                    )
                }
                composable(
                    route     = Screen.NoteDetail.route,
                    arguments = listOf(navArgument("noteId") { type = NavType.IntType })
                ) { back ->
                    val noteId = back.arguments?.getInt("noteId") ?: return@composable
                    NoteDetailScreen(
                        noteId     = noteId,
                        viewModel  = notesViewModel,
                        isDarkMode = isDarkMode,
                        onBack     = { navController.popBackStack() },
                        onEdit     = { id -> navController.navigate(Screen.EditNote.createRoute(id)) }
                    )
                }
                composable(Screen.AddNote.route) {
                    AddNoteScreen(
                        viewModel = notesViewModel,
                        onBack    = { navController.popBackStack() }
                    )
                }
                composable(
                    route     = Screen.EditNote.route,
                    arguments = listOf(navArgument("noteId") { type = NavType.IntType })
                ) { back ->
                    val noteId = back.arguments?.getInt("noteId") ?: return@composable
                    EditNoteScreen(
                        noteId    = noteId,
                        viewModel = notesViewModel,
                        onBack    = { navController.popBackStack() }
                    )
                }
                composable(Screen.Settings.route) {
                    SettingsScreen(
                        viewModel        = notesViewModel,
                        isDarkMode       = isDarkMode,
                        onToggleDarkMode = { notesViewModel.toggleDarkMode() },
                        onBack           = { navController.popBackStack() }
                    )
                }
                composable(Screen.About.route) {
                    AboutScreen(onBack = { navController.popBackStack() })
                }
            }
        }
    }
}
