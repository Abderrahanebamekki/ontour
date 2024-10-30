package com.example.ontour.screen



import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun NumberedCircle(
    number: Int ,
    modifier: Modifier
) {

    Canvas(
        modifier = modifier
            .size(150.dp)
            .border(
                width = 5.dp ,
                brush = Brush.horizontalGradient(
                            colors = listOf(
                               Color(0xFF00637C),
                               Color(0xFF00B5E2)
                            )),
                shape = CircleShape
            )
    ) {
        val radius = size.minDimension / 2

        // Draw the circle
        drawCircle(
            color = Color.White,
            radius = radius,
            center = center
        )

        // Draw the number inside the circle
        drawContext.canvas.nativeCanvas.apply {
            val textPaint = Paint().apply {
                color = Color.Magenta.toArgb()
                textSize = radius * 1.8f // Adjust text size relative to circle size
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }

            // Draw the number
            drawText(
                number.toString(),
                center.x,
                center.y + (textPaint.descent() - textPaint.ascent()) /3, // Adjust for vertical centering
                textPaint
            )
        }
    }
}


