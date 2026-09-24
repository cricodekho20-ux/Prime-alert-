package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NewsArticle
import com.example.ui.theme.PrimeRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareBottomSheet(
    article: NewsArticle,
    sheetState: SheetState,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val shareText = """
        🔴 Prime Alert — हर खबर पर आपकी नज़र
        
        "${article.headline}"
        
        पूरी खबर पढ़ें:
        https://primealert.in/news/${article.id}
    """.trimIndent()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .testTag("share_bottom_sheet")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    tint = PrimeRed,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "खबर शेयर करें (Share News)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = article.headline,
                fontSize = 13.sp,
                color = Color.Gray,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))

            // Social platforms grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                SharePlatformItem(
                    title = "WhatsApp",
                    color = Color(0xFF25D366),
                    icon = Icons.Default.Send,
                    onClick = {
                        shareToSpecificApp(context, shareText, "com.whatsapp")
                        onDismiss()
                    }
                )

                SharePlatformItem(
                    title = "Telegram",
                    color = Color(0xFF0088CC),
                    icon = Icons.Default.Send,
                    onClick = {
                        shareToSpecificApp(context, shareText, "org.telegram.messenger")
                        onDismiss()
                    }
                )

                SharePlatformItem(
                    title = "Facebook",
                    color = Color(0xFF1877F2),
                    icon = Icons.Default.Share,
                    onClick = {
                        shareToSpecificApp(context, shareText, "com.facebook.katana")
                        onDismiss()
                    }
                )

                SharePlatformItem(
                    title = "लिंक कॉपी",
                    color = Color(0xFF555555),
                    icon = Icons.Default.ContentCopy,
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Prime Alert News", shareText)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "लिंक कॉपी हो गया!", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                )

                SharePlatformItem(
                    title = "अन्य",
                    color = PrimeRed,
                    icon = Icons.Default.MoreHoriz,
                    onClick = {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Prime Alert खबर साझा करें")
                        context.startActivity(shareIntent)
                        onDismiss()
                    }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun SharePlatformItem(
    title: String,
    color: Color,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun shareToSpecificApp(context: Context, text: String, packageName: String) {
    try {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            setPackage(packageName)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback to generic chooser if app is not installed
        val genericIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(genericIntent, "Share"))
    }
}
