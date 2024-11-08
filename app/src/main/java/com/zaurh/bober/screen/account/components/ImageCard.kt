package com.zaurh.bober.screen.account.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaurh.bober.R

@Composable
fun ImageCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {  },
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Box(modifier = modifier.height(200.dp)) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                colorResource(R.color.backgroundBottom),
                                Color.Transparent,

                                ),
                            startY = 300f
                        )
                    )
            )
            Column(modifier = Modifier
                .padding(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painterResource(R.drawable.bober_ic),
                            modifier = Modifier.size(26.dp),
                            contentDescription = "bober",
                            tint = Color.White
                        )
                        Spacer(Modifier.size(4.dp))
                        Text(
                            text = "Boberium",
                            fontSize = 20.sp,
                            fontFamily = FontFamily(Font(R.font.bober_font)),
                            color = Color.White
                        )
                    }
                    Text(
                        text = "FREE",
                        fontSize = 22.sp,
                        fontFamily = FontFamily(Font(R.font.bober_font)),
                        color = Color.White
                    )

                }
                Spacer(Modifier.size(16.dp))
                Text(
                    "- Unlimited likes\n- See who liked you\n- Read receipt \n- See online/last seen",
                    color = Color.White
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Button(enabled = false,colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.darkBlue)
                ), onClick = {
                    onClick()
                }) {
                    Text(
                        text = "Activated",
                        style = TextStyle(color = Color.White, fontSize = 16.sp)
                    )
                }
            }
        }
    }
}