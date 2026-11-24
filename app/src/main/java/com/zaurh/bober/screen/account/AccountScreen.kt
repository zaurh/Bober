package com.zaurh.bober.screen.account

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.zaurh.bober.R
import com.zaurh.bober.data.user.PremiumCards
import com.zaurh.bober.data.user.UserData
import com.zaurh.bober.navigation.Screen
import com.zaurh.bober.screen.account.components.BoberiumCardDialog
import com.zaurh.bober.screen.account.components.BoberiumSection
import com.zaurh.bober.ui.components.CountBadge
import com.zaurh.bober.ui.theme.BrandGradient
import com.zaurh.bober.util.calculateAge

/** The things a complete profile has, in the order we suggest adding them. */
private fun missingProfileParts(user: UserData): List<String> = buildList {
    if (user.imageUrl.orEmpty().size < 3) add("${3 - user.imageUrl.orEmpty().size} more photo${if (user.imageUrl.orEmpty().size == 2) "" else "s"}")
    if (user.aboutMe.isNullOrBlank()) add("a bio")
    if (user.interests.isNullOrEmpty()) add("interests")
    if (user.name.isNullOrBlank()) add("your name")
    if (user.jobTitle.isNullOrBlank()) add("your job")
    if ((user.height ?: 0) <= 0) add("your height")
    if (user.languages.isNullOrEmpty()) add("languages")
}

private const val PROFILE_PARTS = 7

@Composable
fun AccountScreen(
    navController: NavController,
    accountViewModel: AccountViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val user = accountViewModel.userDataState.collectAsState().value
    val likesYou = accountViewModel.whoLikesState.collectAsState().value.size

    LaunchedEffect(Unit) { accountViewModel.getWhoLikes() }
    val error = accountViewModel.error.value
    LaunchedEffect(error) {
        if (error != null) {
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            accountViewModel.errorShown()
        }
    }

    if (user == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        // Photo with the brand ring
        Box(
            Modifier
                .size(148.dp)
                .border(4.dp, BrandGradient, CircleShape)
                .padding(8.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            AsyncImage(
                model = user.imageUrl?.firstOrNull(),
                contentDescription = "Your photo",
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.placeholder_image),
                error = painterResource(R.drawable.placeholder_image),
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(Modifier.height(12.dp))
        val age = runCatching { calculateAge(user.birthDate.orEmpty()) }.getOrDefault(0)
        Text(
            listOfNotNull(user.name?.ifBlank { null } ?: user.username, age.takeIf { it > 0 }?.toString()).joinToString(", "),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Text("@${user.username}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { navController.navigate(Screen.EditProfileScreen.route) }) { Text("Edit profile") }
            OutlinedButton(onClick = { navController.navigate(Screen.ProfileScreen.passUsername(user.username)) }) { Text("Preview") }
        }

        val missing = missingProfileParts(user)
        if (missing.isNotEmpty()) {
            Spacer(Modifier.height(20.dp))
            CompletionCard(
                done = PROFILE_PARTS - missing.size,
                total = PROFILE_PARTS,
                next = missing.take(2).joinToString(" and "),
                onClick = { navController.navigate(Screen.EditProfileScreen.route) }
            )
        }

        Spacer(Modifier.height(20.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PeopleTile(R.drawable.who_likes_me_ic, "Likes you", likesYou, Modifier.weight(1f)) {
                navController.navigate(Screen.WhoLikesScreen.route)
            }
            PeopleTile(R.drawable.liked_users_ic, "You liked", 0, Modifier.weight(1f)) {
                navController.navigate(Screen.LikedUsersScreen.route)
            }
            PeopleTile(R.drawable.blocked_users_ic, "Blocked", 0, Modifier.weight(1f)) {
                navController.navigate(Screen.BlockedUsersScreen.route)
            }
        }

        Spacer(Modifier.height(20.dp))
        val activeCard = user.premiumCards.orEmpty().firstOrNull { it.name == PremiumCards.BOBERIUM && it.isActive() }
        BoberiumSection(
            activeCard = activeCard,
            activating = accountViewModel.activating.value,
            onActivate = accountViewModel::boberium,
            onOpenCard = accountViewModel::openBoberiumCard,
        )
        val dialog = accountViewModel.boberiumDialog.value
        if (dialog.open && activeCard != null) {
            BoberiumCardDialog(card = activeCard, justActivated = dialog.justActivated, onDismiss = accountViewModel::closeBoberiumDialog)
        }
    }
}

@Composable
private fun CompletionCard(done: Int, total: Int, next: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Complete your profile", style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                Text("${done * 100 / total}%", style = MaterialTheme.typography.titleSmall)
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { done.toFloat() / total },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
            )
            Spacer(Modifier.height(8.dp))
            Text("Add $next to get more matches.", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun PeopleTile(icon: Int, title: String, count: Int, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = modifier
    ) {
        Box {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(painterResource(icon), contentDescription = null, modifier = Modifier.size(44.dp))
                Spacer(Modifier.height(8.dp))
                Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
            }
            if (count > 0) {
                CountBadge(count, Modifier.align(Alignment.TopEnd).padding(8.dp))
            }
        }
    }
}
