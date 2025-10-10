package pe.edu.upc.tripmatch.presentation.view

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import pe.edu.upc.tripmatch.R
import pe.edu.upc.tripmatch.data.model.AgencyProfileDto
import pe.edu.upc.tripmatch.presentation.di.PresentationModule
import pe.edu.upc.tripmatch.presentation.viewmodel.AgencyProfileViewModel
import pe.edu.upc.tripmatch.presentation.viewmodel.ReviewUi
import pe.edu.upc.tripmatch.ui.theme.*
import kotlin.math.ceil
import kotlin.math.floor

@Composable
fun AgencyProfileScreen(
    viewModel: AgencyProfileViewModel = PresentationModule.getAgencyProfileViewModel(),
    onNavigateBack: () -> Unit,
    onEditProfile: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = AppBackground
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = TurquoiseDark)
                    }
                }
                uiState.errorMessage != null -> {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text(text = "Error: ${uiState.errorMessage}", color = Color.Red, textAlign = TextAlign.Center)
                    }
                }
                else -> {
                    val profile = uiState.agencyProfile
                    val reviews = uiState.reviews

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        item {
                            Spacer(Modifier.height(56.dp))
                            HeaderSection(
                                avatarUrl = profile?.avatarUrl ?: "",
                                agencyName = profile?.agencyName ?: "",
                                ruc = profile?.ruc,
                                rating = uiState.rating,
                                reviewCount = uiState.reviewCount
                            )
                            Spacer(Modifier.height(20.dp))
                        }

                        item {
                            CategoryChips()
                            Spacer(Modifier.height(24.dp))
                        }

                        item {
                            Column(Modifier.padding(horizontal = 20.dp)) {
                                AboutSection(description = profile?.description ?: "")
                                Spacer(Modifier.height(24.dp))

                                ContactSection(
                                    phone = profile?.contactPhone,
                                    email = profile?.contactEmail
                                )
                                Spacer(Modifier.height(24.dp))

                                ActionsSection(
                                    profile = profile,
                                    onEditClick = onEditProfile
                                )
                                Spacer(Modifier.height(24.dp))

                                HorizontalDivider(color = DividerColor.copy(alpha = 0.6f), thickness = 1.dp)
                                Spacer(Modifier.height(24.dp))
                                Text(
                                    text = "Reseñas",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(Modifier.height(16.dp))
                            }
                        }

                        if (reviews.isEmpty()) {
                            item {
                                Text(
                                    "Aún no hay reseñas.",
                                    color = TextSecondary,
                                    modifier = Modifier.padding(horizontal = 20.dp)
                                )
                            }
                        } else {
                            items(reviews) { review ->
                                Box(Modifier.padding(horizontal = 20.dp)) {
                                    ReviewItem(review = review)
                                }
                                Spacer(Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }
            BackButton(onClick = onNavigateBack)
        }
    }
}

@Composable
private fun BackButton(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .padding(start = 16.dp, top = 16.dp)
            .size(40.dp),
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.8f),
        tonalElevation = 4.dp,
        shadowElevation = 2.dp
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = TextPrimary
            )
        }
    }
}

@Composable
private fun HeaderSection(
    avatarUrl: String,
    agencyName: String,
    ruc: String?,
    rating: Float,
    reviewCount: Int
) {
    Box(
        modifier = Modifier.size(110.dp)
    ) {
        AsyncImage(
            model = avatarUrl.ifEmpty { R.drawable.ic_tripmatch_logo },
            contentDescription = "Agency Avatar",
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(24.dp)
                .clip(CircleShape)
                .background(TurquoiseDark)
                .border(2.dp, AppBackground, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Verified",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
    Spacer(Modifier.height(16.dp))

    Text(
        text = agencyName,
        fontSize = 26.sp,
        fontWeight = FontWeight.ExtraBold,
        color = TextPrimary,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 20.dp)
    )

    if (!ruc.isNullOrBlank()) {
        Spacer(Modifier.height(4.dp))
        Text(
            text = "RUC: $ruc",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary
        )
    }

    Spacer(Modifier.height(8.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        RatingBar(rating = rating, starSize = 18.dp)
        Spacer(Modifier.width(4.dp))
        Text(
            text = "%.1f".format(rating),
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            fontSize = 16.sp
        )
        Text(
            text = "($reviewCount reseñas)",
            fontWeight = FontWeight.Normal,
            color = TextSecondary,
            fontSize = 15.sp
        )
    }
}

@Composable
fun RatingBar(rating: Float, starSize: Dp) {
    Row {
        val fullStars = floor(rating).toInt()
        val halfStar = ceil(rating) > rating && rating > fullStars
        val emptyStars = 5 - fullStars - if (halfStar) 1 else 0

        repeat(fullStars) {
            Icon(Icons.Filled.Star, contentDescription = null, tint = StarGold, modifier = Modifier.size(starSize))
        }
        if (halfStar) {
            Icon(Icons.Filled.StarHalf, contentDescription = null, tint = StarGold, modifier = Modifier.size(starSize))
        }
        repeat(emptyStars) {
            Icon(Icons.Outlined.StarBorder, contentDescription = null, tint = StarGold, modifier = Modifier.size(starSize))
        }
    }
}

@Composable
private fun CategoryChips() {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        val chips = listOf("Familiar", "Aventura", "Full day")
        chips.forEach { Chip(text = it) }
    }
}

@Composable
private fun Chip(text: String) {
    Surface(
        color = TurquoiseLight,
        shape = RoundedCornerShape(999.dp)
    ) {
        Text(
            text = text,
            color = TurquoiseDark,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun AboutSection(description: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Acerca de",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(Modifier.height(8.dp))
        if (description.isNotEmpty()) {
            Text(
                text = description,
                fontSize = 15.sp,
                color = TextSecondary,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun ContactSection(phone: String?, email: String?) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (!phone.isNullOrBlank()) {
            InfoRow(
                icon = Icons.Default.Call,
                text = phone,
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                    context.startActivity(intent)
                }
            )
        }
        if (!email.isNullOrBlank()) {
            InfoRow(
                icon = Icons.Default.Email,
                text = email,
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:$email")
                    }
                    context.startActivity(intent)
                }
            )
        }
    }
}

@Composable
private fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TurquoiseDark,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = text,
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ActionsSection(profile: AgencyProfileDto?, onEditClick: () -> Unit) {
    val context = LocalContext.current
    val socialLinks = mutableListOf<Pair<Painter, () -> Unit>>()

    profile?.socialLinkFacebook?.takeIf { it.isNotBlank() }?.let { url ->
        socialLinks.add(painterResource(id = R.drawable.ic_facebook) to { openUrlInBrowser(context, url) })
    }
    profile?.socialLinkInstagram?.takeIf { it.isNotBlank() }?.let { url ->
        socialLinks.add(painterResource(id = R.drawable.ic_instagram) to { openUrlInBrowser(context, url) })
    }
    profile?.socialLinkWhatsapp?.takeIf { it.isNotBlank() }?.let { number ->
        val whatsappUrl = "https://wa.me/$number"
        socialLinks.add(painterResource(id = R.drawable.ic_whatsapp) to { openUrlInBrowser(context, whatsappUrl) })
    }
    profile?.contactEmail?.takeIf { it.isNotBlank() }?.let { email ->
        socialLinks.add(painterResource(id = R.drawable.ic_gmail) to {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
            context.startActivity(intent)
        })
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            socialLinks.forEach { (painter, action) ->
                SocialIconButton(painter = painter, description = "Social Link", onClick = action)
            }
        }
        Spacer(Modifier.weight(1f))
        Button(
            onClick = onEditClick,
            shape = RoundedCornerShape(999.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TurquoiseButton),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(text = "Editar", color = TurquoiseDark, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun SocialIconButton(painter: Painter, description: String, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(TurquoiseLight)
    ) {
        Icon(painter = painter, contentDescription = description, tint = Color.Unspecified)
    }
}

private fun openUrlInBrowser(context: android.content.Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (e: Exception) {
    }
}

@Composable
private fun ReviewItem(review: ReviewUi) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AsyncImage(
            model = R.drawable.ic_tripmatch_logo,
            contentDescription = "Reviewer Avatar",
            modifier = Modifier.size(44.dp).clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Column {
            Text(
                text = review.author,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                RatingBar(rating = review.rating.toFloat(), starSize = 16.dp)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = review.comment,
                fontSize = 15.sp,
                color = TextSecondary,
                lineHeight = 21.sp
            )
        }
    }
}