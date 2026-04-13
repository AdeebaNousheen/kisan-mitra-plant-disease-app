package com.adeeba.plantdiseaseapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.*
import com.adeeba.plantdiseaseapp.entity.DetectionEntity

@Composable
fun CameraScreen(
    language: String,
    selectedCrop: String,
    onResult: (String, Double, String, String, String) -> Unit,
    onBack: () -> Unit
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isLoading by remember { mutableStateOf(false) }
    var camera by remember { mutableStateOf<Camera?>(null) }
    var isTorchOn by remember { mutableStateOf(false) }

    val previewView = remember { PreviewView(context) }

    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    // 🔐 PERMISSION
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { hasPermission = it }

    LaunchedEffect(Unit) {
        if (!hasPermission) launcher.launch(Manifest.permission.CAMERA)
    }

    if (!hasPermission) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Camera permission required")
        }
        return
    }

    // 📷 CAMERA SETUP
    LaunchedEffect(Unit) {
        val provider = ProcessCameraProvider.getInstance(context)

        provider.addListener({
            val cameraProvider = provider.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            cameraProvider.unbindAll()

            camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageCapture
            )

        }, ContextCompat.getMainExecutor(context))
    }

    // 🌐 INTERNET CHECK
    fun isInternetAvailable(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    // 🚀 MAIN FUNCTION
    fun uploadImage(file: File) {

        if (!file.exists() || file.length() == 0L) {
            Toast.makeText(context, "Image error", Toast.LENGTH_SHORT).show()
            return
        }

        isLoading = true

        // 📴 OFFLINE MODE
        if (!isInternetAvailable()) {

            try {
                val model = TFLiteModel(context)
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)

                if (bitmap == null) {
                    isLoading = false
                    Toast.makeText(context, "Image error", Toast.LENGTH_SHORT).show()
                    return
                }

                val result = model.predict(bitmap)

                val name = result.name
                val confidence = (result.confidence * 100).toDouble()
                val description = result.description
                val treatment = result.treatment
                val prevention = result.prevention

                (context as Activity).runOnUiThread {

                    isLoading = false

                    val dao = DatabaseProvider.getDatabase(context).detectionDao()

                    val entity = DetectionEntity(
                        name = name,
                        confidence = confidence,
                        description = description,
                        treatment = treatment,
                        prevention = prevention,
                        imagePath = file.absolutePath
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        dao.insertDetection(entity)
                    }

                    onResult(name, confidence, description, treatment, prevention)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                isLoading = false
                Toast.makeText(context, "Offline model error", Toast.LENGTH_SHORT).show()
            }

            return
        }

        // 🌐 ONLINE API
        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "device_id",
                Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ANDROID_ID
                )
            )
            .addFormDataPart("language", language)
            .addFormDataPart(
                "image",
                file.name,
                file.asRequestBody("image/*".toMediaType())
            )
            .build()

        val request = Request.Builder()
            .url("https://kisan-mitra-production.up.railway.app/api/v1/detect/upload")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                (context as Activity).runOnUiThread {
                    isLoading = false
                    Toast.makeText(context, "Network error", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {

                val json = response.body?.string()

                try {
                    val obj = JSONObject(json ?: "{}")
                    val data = obj.getJSONObject("data")
                    val pesticideData = data.getJSONObject("pesticide_recommendation")

                    val name = pesticideData.optString("disease_name", "Unknown")
                    val rawConfidence = data.optDouble("confidence", 0.0)
                    val confidence = if (rawConfidence <= 1) rawConfidence * 100 else rawConfidence

                    val description = pesticideData.optString("application_method", "")
                    val treatment = pesticideData.optString("primary_pesticide", "")
                    val prevention = pesticideData.optString("safety", "")

                    (context as Activity).runOnUiThread {

                        isLoading = false

                        val dao = DatabaseProvider.getDatabase(context).detectionDao()

                        val entity = DetectionEntity(
                            name = name,
                            confidence = confidence,
                            description = description,
                            treatment = treatment,
                            prevention = prevention,
                            imagePath = file.absolutePath
                        )

                        CoroutineScope(Dispatchers.IO).launch {
                            dao.insertDetection(entity)
                        }

                        onResult(name, confidence, description, treatment, prevention)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()

                    (context as Activity).runOnUiThread {
                        isLoading = false
                        Toast.makeText(context, "Parsing error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    // 📸 GALLERY
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val file = File(context.cacheDir, "gallery.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            uploadImage(file)
        }
    }

    // 🎨 UI
    Box(Modifier.fillMaxSize()) {

        AndroidView({ previewView }, modifier = Modifier.fillMaxSize())

        IconButton(onClick = onBack, modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Default.ArrowBack, null, tint = Color.White)
        }

        IconButton(
            onClick = {
                isTorchOn = !isTorchOn
                camera?.cameraControl?.enableTorch(isTorchOn)
            },
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
        ) {
            Icon(
                if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                null,
                tint = Color.White
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 30.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            IconButton(onClick = { galleryLauncher.launch("image/*") }) {
                Icon(Icons.Default.Photo, null, tint = Color.White)
            }

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .border(4.dp, Color.White, CircleShape)
                    .clickable {

                        val file = File(context.cacheDir, "capture.jpg")

                        val outputOptions =
                            ImageCapture.OutputFileOptions.Builder(file).build()

                        imageCapture.takePicture(
                            outputOptions,
                            ContextCompat.getMainExecutor(context),
                            object : ImageCapture.OnImageSavedCallback {

                                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                    uploadImage(file)
                                }

                                override fun onError(exception: ImageCaptureException) {
                                    Toast.makeText(context, "Capture Failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
            )

            IconButton(onClick = {}) {
                Icon(Icons.Default.Help, null, tint = Color.White)
            }
        }

        if (isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = Color(0xFF4CAF50))
                Spacer(modifier = Modifier.height(12.dp))
                Text("🤖 AI is analyzing leaf...", color = Color.White)
            }
        }
    }
}