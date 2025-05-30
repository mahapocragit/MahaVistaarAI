package `in`.gov.mahapocra.mahavistaarai.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import `in`.gov.mahapocra.mahavistaarai.data.model.CaptchaResult
import java.util.Random

object CaptchaGenerator {

    fun generateCaptchaBitmap(width: Int, height: Int, codeLength: Int = 6): CaptchaResult {
        val captchaText = generateRandomText(codeLength)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        canvas.drawColor(Color.LTGRAY)

        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 40f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }

        val fontMetrics = paint.fontMetrics
        val x = (width - paint.measureText(captchaText)) / 2
        val y = (height - fontMetrics.ascent - fontMetrics.descent) / 2

        canvas.drawText(captchaText, x, y, paint)
        drawNoise(canvas, width, height)

        return CaptchaResult(bitmap, captchaText)
    }

    private fun generateRandomText(length: Int): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..length).map { chars.random() }.joinToString("")
    }

    private fun drawNoise(canvas: Canvas, width: Int, height: Int) {
        val paint = Paint().apply {
            color = Color.DKGRAY
            strokeWidth = 2f
        }
        val random = Random()
        repeat(8) {
            canvas.drawLine(
                random.nextInt(width).toFloat(), random.nextInt(height).toFloat(),
                random.nextInt(width).toFloat(), random.nextInt(height).toFloat(),
                paint
            )
        }
    }
}

