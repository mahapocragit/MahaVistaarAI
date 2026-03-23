package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.pestIdentification.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import `in`.co.appinventor.services_api.settings.AppSettings
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityCameraBinding
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.configureLocale
import `in`.gov.mahapocra.mahavistaarai.util.LocalCustom.switchLanguage
import java.io.File

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private lateinit var imageCapture: ImageCapture
    private var tempFile: File? = null

    var languageToLoad = "mr"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (AppSettings.getLanguage(this@CameraActivity)
                .equals("1", ignoreCase = true)
        ) {
            languageToLoad = "en"
        }
        switchLanguage(this, languageToLoad)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (languageToLoad == "en") {
            AlertDialog.Builder(this)
                .setTitle("Image Capture Tips")
                .setMessage(
                    "• Use bright daylight or good lighting\n" +
                            "• Image should be clear and not blurry\n" +
                            "• Keep the object fully visible and centered\n" +
                            "• Avoid shadows and cluttered backgrounds"
                )
                .setPositiveButton("Continue") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        } else {
            AlertDialog.Builder(this)
                .setTitle("प्रतिमा कॅप्चर करण्याच्या सूचना")
                .setMessage(
                    "• तेजस्वी दिवसाच्या प्रकाशात किंवा चांगल्या प्रकाशात फोटो घ्या\n" +
                            "• प्रतिमा स्पष्ट असावी आणि धूसर नसावी\n" +
                            "• वस्तू पूर्णपणे दिसेल अशी आणि मध्यभागी ठेवा\n" +
                            "• सावल्या आणि गोंधळलेली पार्श्वभूमी टाळा"
                )
                .setPositiveButton("पुढे चला") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
        startCamera()
        setupClicks()
    }

    private fun setupClicks() = with(binding) {
        btnCapture.setOnClickListener { captureImage() }
        btnCancel.setOnClickListener { discardImage() }
        btnConfirm.setOnClickListener { confirmImage() }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageCapture
            )

        }, ContextCompat.getMainExecutor(this))
    }

    private fun captureImage() {
        tempFile = File(cacheDir, "temp_${System.currentTimeMillis()}.jpg")

        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(tempFile!!)
            .build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    showPreview()
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        this@CameraActivity,
                        exception.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    private fun showPreview() = with(binding) {
        ivCaptured.setImageURI(Uri.fromFile(tempFile))
        ivCaptured.visibility = View.VISIBLE

        previewView.visibility = View.GONE
        btnCapture.visibility = View.GONE
        overlayView.visibility = View.GONE

        btnCancel.visibility = View.VISIBLE
        btnConfirm.visibility = View.VISIBLE
    }

    private fun discardImage() {
        tempFile?.let {
            Toast.makeText(
                this,
                "Discarded: ${it.toUri()}",
                Toast.LENGTH_SHORT
            ).show()
            it.delete()
        }
        return resetUI()
    }

    private fun confirmImage() {
        val finalFile = File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "IMG_${System.currentTimeMillis()}.jpg"
        )

        tempFile?.copyTo(finalFile, overwrite = true)
        tempFile?.delete()

        val imageUri = finalFile.toUri()
        setResult(
            RESULT_OK,
            Intent().apply {
                putExtra("image_uri", imageUri.toString())
            }
        )
        finish()
    }

    private fun resetUI() = with(binding) {
        ivCaptured.visibility = View.GONE
        btnCancel.visibility = View.GONE
        btnConfirm.visibility = View.GONE

        overlayView.visibility = View.VISIBLE
        previewView.visibility = View.VISIBLE
        btnCapture.visibility = View.VISIBLE
    }

    override fun attachBaseContext(newBase: Context) {
        languageToLoad = if (AppSettings.getLanguage(newBase).equals("1", ignoreCase = true)) {
            "en"
        } else {
            "mr"
        }
        val updatedContext = configureLocale(newBase, languageToLoad) // Example: set to French
        super.attachBaseContext(updatedContext)
    }
}