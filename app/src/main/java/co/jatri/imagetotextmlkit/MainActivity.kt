package co.jatri.imagetotextmlkit

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.pm.PackageManager
import android.media.Image
import android.os.Bundle
import android.provider.MediaStore
import android.util.Size
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import co.jatri.imagetotextmlkit.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var imageCapture: ImageCapture ? = null
    private val cameraProviderFuture by lazy { ProcessCameraProvider.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkPermissionAndOpenCamera()
        setUpCamera()
        binding.fab.setOnClickListener { takePicture() }
    }

    private fun checkPermissionAndOpenCamera(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
        }
    }

    @SuppressLint("RestrictedApi")
    private fun setUpCamera(){
        // Initialize the ImageCapture use case.
        imageCapture = ImageCapture.Builder().build()

        // Set up the camera preview.
        val preview = Preview.Builder().build()
        preview.setSurfaceProvider(binding.previewView.surfaceProvider)


        // Create a CameraSelector object.
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        val imageAnalysis = ImageAnalysis.Builder()
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
            .setMaxResolution(Size(640, 480))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this)) {

        }

        // Create a UseCaseGroup object.
        val useCaseGroup = UseCaseGroup.Builder()
            .addUseCase(preview)
            .addUseCase(imageAnalysis)
            .addUseCase(imageCapture!!)
            .build()


        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Bind the use cases to the camera.
            cameraProvider.bindToLifecycle(this, cameraSelector, useCaseGroup)
        }, ContextCompat.getMainExecutor(this))

    }

    @SuppressLint("RestrictedApi")
    private fun takePicture(){
        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            ContentValues()
        ).build()

        imageCapture!!.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {

                }

                override fun onError(exception: ImageCaptureException) {

                }

            }
        )
    }
}