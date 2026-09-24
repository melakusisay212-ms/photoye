package com.habesha.photos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habesha.photos.ui.theme.Accent
import com.habesha.photos.ui.theme.Bg
import com.habesha.photos.ui.theme.TextPrimary
import com.habesha.photos.ui.theme.TextSecondary

@Composable
fun PermissionScreen(onRequest: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Bg)
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Photos Habesha",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Your memories, on the Ethiopian calendar.",
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        Text(
            "This app needs access to the photos and videos stored on your device so it can organize them by Ethiopian date.\n\nNothing is uploaded. Everything stays on your phone.",
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onRequest,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Accent)
        ) {
            Text("Grant access", fontWeight = FontWeight.Medium)
        }
    }
}
