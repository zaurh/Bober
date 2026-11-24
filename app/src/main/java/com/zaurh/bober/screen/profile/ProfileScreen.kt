package com.zaurh.bober.screen.profile

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.zaurh.bober.R
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.profile.components.ProfileDialogs
import com.zaurh.bober.ui.components.BoberButton
import com.zaurh.bober.ui.components.CircleActionButton
import com.zaurh.bober.ui.components.EmptyState
import com.zaurh.bober.ui.theme.BoberPalette
import com.zaurh.bober.ui.theme.PhotoScrim
import com.zaurh.bober.util.calculateAge
import kotlinx.coroutines.launch

/** How the current user relates to the profile shown; decides which actions appear. */
private enum class Relation { Self, Blocked, Matched, Liked, Passed, None }

private fun relationTo(me: UserData?, other: UserData): Relation {
    val id = other.id
    return when {
        me == null || id.isNullOrEmpty() -> Relation.None
        me.id == id -> Relation.Self
        me.blockList.orEmpty().contains(id) -> Relation.Blocked
        me.matchList.orEmpty().any { it.id == id } -> Relation.Matched
        me.likedUsers.orEmpty().contains(id) -> Relation.Liked
        me.passedUsers.orEmpty().any { it.userId == id } -> Relation.Passed
        else -> Relation.None
    }
}

@Composable
fun ProfileScreen(
    username: String,
    navController: NavController,
    profileViewModel: ProfileViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val state = profileViewModel.state.value
    val me = profileViewModel.currentUserDataState.collectAsState().value

    LaunchedEffect(username) { profileViewModel.load(username) }
    LaunchedEffect(Unit) {
        profileViewModel.messages.collect { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
    }

    // Unmatched or blocked: this person is gone from chats and lists, so leave to the main screen.
    fun leave() {
        if (!navController.popBackStack(Screen.PagerScreen.route, inclusive = false)) navController.popBackStack()
    }

    val user = state.user
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        when {
            user != null && user.username == username -> {
                ProfileDialogs(profileViewModel, user, onRelationshipEnded = ::leave)
                ProfileBody(
                    user = user,
                    relation = relationTo(me, user),
                    busy = state.busy,
                    onBack = { navController.popBackStack() },
                    onMessage = { navController.navigate(Screen.ChatScreen.createRoute(user.username)) },
                    onEditProfile = { navController.navigate(Screen.EditProfileScreen.route) },
                    onLike = { profileViewModel.like(user.id.orEmpty()) { navController.popBackStack() } },
                    onPass = { profileViewModel.pass(user.id.orEmpty()) { navController.popBackStack() } },
                    onUnlike = { profileViewModel.unLike(user.id.orEmpty()) { navController.popBackStack() } },
                    onUnblock = { profileViewModel.unblock(user.id.orEmpty()) },
                    onDialog = profileViewModel::openDialog,
                )
            }

            state.error != null -> Column(Modifier.statusBarsPadding()) {
                BackButton(onClick = { navController.popBackStack() }, onPhoto = false)
                EmptyState(title = "Couldn't load this profile", message = state.error)
            }

            else -> CircularProgressIndicator(Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.primary)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProfileBody(
    user: UserData,
    relation: Relation,
    busy: Boolean,
    onBack: () -> Unit,
    onMessage: () -> Unit,
    onEditProfile: () -> Unit,
    onLike: () -> Unit,
    onPass: () -> Unit,
    onUnlike: () -> Unit,
    onUnblock: () -> Unit,
    onDialog: (ProfileDialog) -> Unit,
) {
    val context = LocalContext.current
    val name = user.name?.ifEmpty { null } ?: user.username.orEmpty()
    val age = runCatching { calculateAge(user.birthDate.orEmpty()) }.getOrDefault(0)
    val canSwipe = relation == Relation.None

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = if (canSwipe) 120.dp else 32.dp)
                .navigationBarsPadding()
        ) {
            PhotoPager(user.imageUrl.orEmpty(), name, age, user.jobTitle.orEmpty())

            Column(Modifier.padding(horizontal = 20.dp)) {
                Spacer(Modifier.height(20.dp))
                when (relation) {
                    Relation.Self -> BoberButton(text = "Edit profile", onClick = onEditProfile)
                    Relation.Matched -> BoberButton(text = "Send a message", onClick = onMessage)
                    Relation.Liked -> OutlinedButton(onClick = onUnlike, enabled = !busy, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                        Text("Unlike $name")
                    }
                    Relation.Blocked -> OutlinedButton(onClick = onUnblock, enabled = !busy, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                        Text("Unblock $name")
                    }
                    Relation.Passed, Relation.None -> Unit
                }
                if (relation != Relation.Passed && relation != Relation.None) Spacer(Modifier.height(8.dp))

                val about = user.aboutMe.orEmpty()
                if (about.isNotBlank()) {
                    Section("About me") {
                        Text(about, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                val basics = buildList {
                    user.gender?.displayName?.takeIf { it.isNotEmpty() }?.let { add(it) }
                    user.height?.takeIf { it > 0 }?.let { add("$it cm") }
                    if (age > 0) add("$age years old")
                }
                if (basics.isNotEmpty()) {
                    Section("Basics") {
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            basics.forEach { Tag(it) }
                        }
                    }
                }

                val interests = user.interests.orEmpty()
                if (interests.isNotEmpty()) {
                    Section("Interests") {
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            interests.forEach { Tag(it.displayName, highlighted = true) }
                        }
                    }
                }

                val languages = user.languages.orEmpty()
                if (languages.isNotEmpty()) {
                    Section("Languages") {
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            languages.forEach { Tag(it.displayName) }
                        }
                    }
                }
            }
        }

        // Floating controls over the photo.
        Row(
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton(onClick = onBack, onPhoto = true)
            Spacer(Modifier.weight(1f))
            if (relation != Relation.Self) {
                var menu by remember { mutableStateOf(false) }
                Box {
                    IconButton(
                        onClick = { menu = true },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Black.copy(alpha = 0.35f), contentColor = Color.White)
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                    DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                        DropdownMenuItem(text = { Text("Share profile") }, onClick = {
                            menu = false
                            val send = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "https://zaurh.github.io/bobersite/?username=${user.username}")
                            }
                            context.startActivity(Intent.createChooser(send, null))
                        })
                        if (relation == Relation.Matched) {
                            DropdownMenuItem(text = { Text("Unmatch") }, onClick = { menu = false; onDialog(ProfileDialog.Unmatch) })
                        }
                        DropdownMenuItem(text = { Text("Report") }, onClick = { menu = false; onDialog(ProfileDialog.Report) })
                        if (relation != Relation.Blocked) {
                            DropdownMenuItem(
                                text = { Text("Block", color = MaterialTheme.colorScheme.error) },
                                onClick = { menu = false; onDialog(ProfileDialog.Block) }
                            )
                        }
                    }
                }
            }
        }

        if (canSwipe) {
            Row(
                Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                CircleActionButton(Icons.Default.Close, "Pass", BoberPalette.Pass, onClick = onPass, enabled = !busy)
                CircleActionButton(Icons.Default.Favorite, "Like", BoberPalette.Like, onClick = onLike, enabled = !busy)
            }
        }
    }
}

/** Full-width photo carousel: tap the left / right half to browse, with position bars on top. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PhotoPager(photos: List<String>, name: String, age: Int, jobTitle: String) {
    val pages = photos.ifEmpty { listOf("") }
    val pager = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()
    val height = LocalConfiguration.current.screenHeightDp.dp * 0.62f

    Box(
        Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        HorizontalPager(state = pager, modifier = Modifier.fillMaxSize()) { index ->
            AsyncImage(
                model = pages[index],
                contentDescription = name,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.placeholder_image),
                error = painterResource(R.drawable.placeholder_image),
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(pages.size) {
                        detectTapGestures { offset ->
                            val target = if (offset.x < size.width / 2) pager.currentPage - 1 else pager.currentPage + 1
                            scope.launch { pager.animateScrollToPage(target.coerceIn(0, pages.lastIndex)) }
                        }
                    }
            )
        }
        if (pages.size > 1) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                pages.indices.forEach { index ->
                    Box(
                        Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(if (index == pager.currentPage) Color.White else Color.White.copy(alpha = 0.35f))
                    )
                }
            }
        }
        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(PhotoScrim)
                .padding(start = 20.dp, end = 20.dp, top = 72.dp, bottom = 20.dp)
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (age > 0) {
                    Spacer(Modifier.size(8.dp))
                    Text("$age", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Normal, color = Color.White)
                }
            }
            if (jobTitle.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Build, null, tint = Color.White.copy(alpha = 0.9f), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.size(6.dp))
                    Text(jobTitle, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.9f))
                }
            }
        }
    }
}

@Composable
private fun BackButton(onClick: () -> Unit, onPhoto: Boolean) {
    IconButton(
        onClick = onClick,
        colors = if (onPhoto) IconButtonDefaults.iconButtonColors(containerColor = Color.Black.copy(alpha = 0.35f), contentColor = Color.White)
        else IconButtonDefaults.iconButtonColors()
    ) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Spacer(Modifier.height(20.dp))
    Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
    Spacer(Modifier.height(10.dp))
    content()
}

@Composable
private fun Tag(text: String, highlighted: Boolean = false) {
    Surface(
        shape = CircleShape,
        color = if (highlighted) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = if (highlighted) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
    }
}
