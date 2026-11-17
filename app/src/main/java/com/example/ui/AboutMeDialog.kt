package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.BuildConfig
import com.example.R

private val CardBorderCyan = Color(0xFF00E5FF)
private val CardBorderPurple = Color(0xFFD500F9)
private val CardBorderViolet = Color(0xFF7B2CBF)
private val CardSurfaceLight = Color(0xFFF3F6FB)
private val BioCardSurface = Color(0xFFE9EFFA)
private val BannerDarkBg = Color(0xFF060A18)
private val HeadingNavy = Color(0xFF0A1445)
private val BodyDarkText = Color(0xFF141B2D)

private data class AboutBulletItem(
    val title: String,
    val body: String
)

private val ABOUT_BULLETS = listOf(
    AboutBulletItem(
        title = "Not Your Average Dev:",
        body = "Powered by caffeine, dad jokes, & a healthy dose of Deadpool-style sarcasm."
    ),
    AboutBulletItem(
        title = "Indie App Builder:",
        body = "Crafting lightweight, bloat-free Android, PC, & web based applications."
    ),
    AboutBulletItem(
        title = "For The People:",
        body = "Dedicated to open-source, free, & user-controlled software."
    ),
    AboutBulletItem(
        title = "Gaming Tinkerer:",
        body = "Passionate about both modern titles & retro emulation, bridging the gap across PC, handhelds, & mobile."
    ),
    AboutBulletItem(
        title = "Off the Clock:",
        body = "Dad, Husband, Eagle Scout, Gamer, & Hooper."
    )
)

@Composable
fun AboutMeDialog(
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val kofiUrl = stringResource(R.string.about_me_url_kofi)
    val githubUrl = stringResource(R.string.about_me_url_github)
    val projectsUrl = stringResource(R.string.about_me_url_projects)

    val cardShape = RoundedCornerShape(22.dp)
    val outerGradient = remember {
        Brush.linearGradient(
            colors = listOf(CardBorderCyan, CardBorderViolet, CardBorderPurple)
        )
    }
    val bioBoxBorderGradient = remember {
        Brush.linearGradient(
            colors = listOf(CardBorderCyan.copy(alpha = 0.45f), CardBorderPurple.copy(alpha = 0.45f))
        )
    }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 410.dp)
                    .fillMaxWidth()
                    .shadow(20.dp, cardShape, ambientColor = CardBorderCyan, spotColor = CardBorderPurple)
                    .clip(cardShape)
                    .background(CardSurfaceLight)
                    .border(2.5.dp, outerGradient, cardShape)
                    .testTag("about_me_dialog")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // --- TOP CYBER HEADER + OVERLAPPING TRON/DAFT PUNK AVATAR ---
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(142.dp)
                    ) {
                        // Geometric Tron Neon Header Canvas
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(112.dp)
                        ) {
                            val w = size.width
                            val h = size.height

                            val bannerPath = Path().apply {
                                moveTo(0f, 0f)
                                lineTo(w, 0f)
                                lineTo(w, h * 0.76f)
                                lineTo(w * 0.80f, h)
                                lineTo(w * 0.20f, h)
                                lineTo(0f, h * 0.76f)
                                close()
                            }
                            drawPath(path = bannerPath, color = BannerDarkBg)

                            // Left glowing cyan geometric streaks
                            drawLine(
                                color = CardBorderCyan.copy(alpha = 0.65f),
                                start = Offset(0f, h * 0.38f),
                                end = Offset(w * 0.25f, h * 0.96f),
                                strokeWidth = 3.5.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                            drawLine(
                                color = Color(0xFF0077FF).copy(alpha = 0.50f),
                                start = Offset(w * 0.04f, 0f),
                                end = Offset(w * 0.30f, h * 0.65f),
                                strokeWidth = 5.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                            drawLine(
                                color = CardBorderCyan.copy(alpha = 0.30f),
                                start = Offset(0f, h * 0.65f),
                                end = Offset(w * 0.16f, h * 0.94f),
                                strokeWidth = 2.dp.toPx()
                            )

                            // Right glowing magenta/purple geometric streaks
                            drawLine(
                                color = CardBorderPurple.copy(alpha = 0.65f),
                                start = Offset(w, h * 0.38f),
                                end = Offset(w * 0.75f, h * 0.96f),
                                strokeWidth = 3.5.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                            drawLine(
                                color = CardBorderViolet.copy(alpha = 0.55f),
                                start = Offset(w * 0.96f, 0f),
                                end = Offset(w * 0.70f, h * 0.65f),
                                strokeWidth = 5.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                            drawLine(
                                color = CardBorderPurple.copy(alpha = 0.30f),
                                start = Offset(w, h * 0.65f),
                                end = Offset(w * 0.84f, h * 0.94f),
                                strokeWidth = 2.dp.toPx()
                            )

                            // Rim highlight along bottom chamfered edge
                            drawLine(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        CardBorderCyan.copy(alpha = 0.75f),
                                        Color.Transparent,
                                        CardBorderPurple.copy(alpha = 0.75f)
                                    )
                                ),
                                start = Offset(0f, h * 0.76f),
                                end = Offset(w, h * 0.76f),
                                strokeWidth = 1.5.dp.toPx()
                            )
                        }

                        // Dismiss Button in top-right corner of dark banner
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(48.dp)
                                .testTag("btn_close_about_me")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close About Me",
                                tint = Color.White.copy(alpha = 0.85f),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Circular Glowing Tron / Daft Punk Avatar overlapping seam
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .size(104.dp)
                                .shadow(12.dp, CircleShape, ambientColor = CardBorderCyan, spotColor = CardBorderPurple)
                                .clip(CircleShape)
                                .background(BannerDarkBg)
                                .border(
                                    width = 3.dp,
                                    brush = Brush.linearGradient(
                                        colors = listOf(CardBorderCyan, Color.White, CardBorderPurple)
                                    ),
                                    shape = CircleShape
                                )
                                .padding(2.5.dp)
                                .clip(CircleShape)
                                .testTag("about_me_avatar")
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_jhor_avatar),
                                contentDescription = "JHor Tron Daft Punk Mask Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // --- FUTURISTIC NAME & HEADER ---
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(3.5.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color.Transparent, Color(0xFF0077FF), CardBorderCyan)
                                    )
                                )
                        )
                        Text(
                            text = stringResource(R.string.about_me_developer_name),
                            color = HeadingNavy,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.SansSerif,
                            letterSpacing = 2.sp,
                            modifier = Modifier.padding(horizontal = 14.dp)
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(3.5.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(CardBorderPurple, CardBorderViolet, Color.Transparent)
                                    )
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Tron Disc Divider Ornament
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.5.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color.Transparent, CardBorderCyan)
                                    )
                                )
                        )
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .size(11.dp)
                                .border(2.dp, HeadingNavy, CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.5.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(CardBorderPurple, Color.Transparent)
                                    )
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "ABOUT ME",
                        color = HeadingNavy,
                        fontSize = 16.5.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        letterSpacing = 1.4.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // --- STRUCTURED CYBER BIO CARD (Fits cleanly on single screen) ---
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(BioCardSurface)
                            .border(1.dp, bioBoxBorderGradient, RoundedCornerShape(14.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        ABOUT_BULLETS.forEach { item ->
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(
                                        SpanStyle(
                                            color = CardBorderViolet,
                                            fontWeight = FontWeight.Black
                                        )
                                    ) {
                                        append("• ")
                                    }
                                    withStyle(
                                        SpanStyle(
                                            color = HeadingNavy,
                                            fontWeight = FontWeight.Bold
                                        )
                                    ) {
                                        append(item.title)
                                        append(" ")
                                    }
                                    withStyle(
                                        SpanStyle(
                                            color = BodyDarkText,
                                            fontWeight = FontWeight.Medium
                                        )
                                    ) {
                                        append(item.body)
                                    }
                                },
                                fontSize = 11.sp,
                                lineHeight = 14.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // --- DIRECT URL LINK PILL BUTTONS ---
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 22.dp),
                        verticalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        // 1. Support on Ko-fi -> https://ko-fi.com/jhor23
                        CyberPillButton(
                            label = "SUPPORT ON KO-FI",
                            gradient = Brush.horizontalGradient(
                                listOf(Color(0xFF00B49D), Color(0xFF008F88))
                            ),
                            borderColor = Color(0xFF33F3FF),
                            onClick = { openExternalUrl(context, kofiUrl) },
                            trailingIcon = {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.LocalCafe,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = null,
                                        tint = Color(0xFFFF3B5C),
                                        modifier = Modifier
                                            .size(8.dp)
                                            .padding(bottom = 1.dp)
                                    )
                                }
                            },
                            testTag = "btn_about_kofi"
                        )

                        // 2. View Source (GitHub) -> https://github.com/JHor23/Weigh-High
                        CyberPillButton(
                            label = "VIEW SOURCE (GITHUB)",
                            gradient = Brush.horizontalGradient(
                                listOf(Color(0xFF071A36), Color(0xFF0D2D59))
                            ),
                            borderColor = Color(0xFF00A8FF),
                            onClick = { openExternalUrl(context, githubUrl) },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Code,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            testTag = "btn_about_github"
                        )

                        // 3. Check Out My Projects -> https://github.com/JHor23
                        CyberPillButton(
                            label = "CHECK OUT MY PROJECTS",
                            gradient = Brush.horizontalGradient(
                                listOf(Color(0xFF560094), Color(0xFF8300DC))
                            ),
                            borderColor = Color(0xFFE066FF),
                            onClick = { openExternalUrl(context, projectsUrl) },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Folder,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            testTag = "btn_about_projects"
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // --- VERSION & FOOTER ---
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 22.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.5.dp)
                                .background(CardBorderCyan.copy(alpha = 0.65f))
                        )
                        Text(
                            text = "v${BuildConfig.VERSION_NAME} | Thanks for the support!",
                            color = HeadingNavy.copy(alpha = 0.85f),
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.5.dp)
                                .background(CardBorderPurple.copy(alpha = 0.65f))
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun CyberPillButton(
    label: String,
    gradient: Brush,
    borderColor: Color,
    onClick: () -> Unit,
    trailingIcon: @Composable () -> Unit,
    testTag: String
) {
    val pillShape = RoundedCornerShape(21.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .shadow(4.dp, pillShape, ambientColor = borderColor, spotColor = borderColor)
            .clip(pillShape)
            .background(gradient)
            .border(1.5.dp, borderColor, pillShape)
            .clickable { onClick() }
            .padding(horizontal = 16.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.6.sp,
            maxLines = 1
        )
        Spacer(modifier = Modifier.width(8.dp))
        trailingIcon()
    }
}

private fun openExternalUrl(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(context, "Unable to open link", Toast.LENGTH_SHORT).show()
    }
}
