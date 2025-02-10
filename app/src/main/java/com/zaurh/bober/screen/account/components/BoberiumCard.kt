package com.zaurh.bober.screen.account.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaurh.bober.R
import com.zaurh.bober.ui.theme.outfitFamily

@Composable
fun BoberiumCard(
    frontSide: Boolean,
    expiresDate: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(1.dp, color = colorResource(R.color.darkBlue).copy(alpha = 0.2f), shape = RoundedCornerShape(7))
            .clickable {
                onClick()
            }
           ,
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Box(
            modifier = Modifier
                .height(200.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                colorResource(R.color.backgroundBottom),
                                colorResource(R.color.pink2),

                                ),
                            startY = 300f
                        )
                    )
            )
            if (frontSide){
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

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
                    Spacer(Modifier.size(16.dp))
                    Text(
                        "- Unlimited likes\n- See who liked you\n- Read receipt \n- See online/last seen",
                        color = Color.White,
                        fontFamily = outfitFamily
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    val daysText = if(expiresDate > 1) "days" else "day"
                    Text(
                        text = "Expires in $expiresDate $daysText",
                        style = TextStyle(color = Color.White, fontSize = 16.sp),
                        fontFamily = outfitFamily
                    )
                }
            }else{
                Column(
                    modifier = Modifier
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(40.dp).background(colorResource(R.color.darkBlue)),
                    )
                    Spacer(Modifier.size(8.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth(fraction = 0.9f).height(30.dp).background(colorResource(R.color.white)),
                    )
                    Spacer(Modifier.size(8.dp))
                    Text(
                        modifier = Modifier.graphicsLayer(scaleX = -1f),text=
                        "This card is your exclusive key to enhanced features and privileges within our app." +
                                " Thank you for being a valued member of our community."
                                ,color = Color.White, fontSize = 12.sp,
                        fontFamily = outfitFamily
                    )
                }

            }

        }
    }
}