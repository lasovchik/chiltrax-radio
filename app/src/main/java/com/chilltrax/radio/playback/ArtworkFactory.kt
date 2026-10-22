package com.chilltrax.radio.playback

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Typeface
import androidx.core.graphics.createBitmap
import java.io.ByteArrayOutputStream

internal object ArtworkFactory {

    private const val SIZE = 512

    fun create(): ByteArray {
        val bitmap = createBitmap(SIZE, SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        paint.shader = LinearGradient(
            0f,
            0f,
            SIZE.toFloat(),
            SIZE.toFloat(),
            intArrayOf(0xFF7C3AED.toInt(), 0xFF2563EB.toInt()),
            null,
            Shader.TileMode.CLAMP,
        )
        canvas.drawRect(0f, 0f, SIZE.toFloat(), SIZE.toFloat(), paint)

        paint.shader = null
        paint.color = 0xFF0B0B18.toInt()
        val center = SIZE / 2f
        val vinylRadius = SIZE * 0.35f
        canvas.drawCircle(center, center, vinylRadius, paint)

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1.5f
        paint.color = 0x33FFFFFF
        for (groove in 1..7) {
            canvas.drawCircle(center, center, vinylRadius - groove * 14f, paint)
        }

        paint.style = Paint.Style.FILL
        paint.shader = LinearGradient(
            center - vinylRadius * 0.3f,
            center - vinylRadius * 0.3f,
            center + vinylRadius * 0.3f,
            center + vinylRadius * 0.3f,
            intArrayOf(0xFFA78BFA.toInt(), 0xFF2563EB.toInt()),
            null,
            Shader.TileMode.CLAMP,
        )
        canvas.drawCircle(center, center, vinylRadius * 0.34f, paint)
        paint.shader = null

        paint.color = 0xFF050510.toInt()
        canvas.drawCircle(center, center, vinylRadius * 0.055f, paint)

        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 34f
        canvas.drawText("CHILLTRAX", center, SIZE - 54f, paint)

        paint.textSize = 18f
        paint.color = 0xCCFFFFFF.toInt()
        canvas.drawText("ONLINE RADIO", center, SIZE - 28f, paint)

        val output = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
        bitmap.recycle()
        return output.toByteArray()
    }
}
