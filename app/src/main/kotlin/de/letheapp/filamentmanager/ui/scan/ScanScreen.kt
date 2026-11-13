package de.letheapp.filamentmanager.ui.scan

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import de.letheapp.filamentmanager.R
import de.letheapp.filamentmanager.ui.editor.FilamentFormState
import java.io.File

/**
 * Kamera-Scan des Spulen-Etiketts (PLAN.md, Schritt 5): CameraX-Vorschau +
 * Auslöser, Foto wird per ML-Kit-On-Device-Texterkennung ausgewertet
 * ([parseLabelText]), das Ergebnis landet als Entwurf in [ScanDraftHolder]
 * und [onScanned] navigiert in den Editor, wo nicht erkannte Pflichtfelder
 * rot umrandet erscheinen, bis sie angegeben werden.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(onScanned: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED,
        )
    }
    val requestPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted -> hasCameraPermission = granted }

    var isAnalyzing by remember { mutableStateOf(false) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    val previewView = remember { PreviewView(context) }

    if (hasCameraPermission) {
        DisposableEffect(lifecycleOwner) {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener(
                {
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also { it.surfaceProvider = previewView.surfaceProvider }
                    val capture = ImageCapture.Builder().build()
                    imageCapture = capture
                    runCatching {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            capture,
                        )
                    }
                },
                ContextCompat.getMainExecutor(context),
            )
            onDispose {
                runCatching { cameraProviderFuture.get().unbindAll() }
            }
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.scan_title)) }) }) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (!hasCameraPermission) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(stringResource(R.string.scan_permission_missing))
                    Button(
                        onClick = { requestPermission.launch(Manifest.permission.CAMERA) },
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    ) {
                        Text(stringResource(R.string.scan_permission_request))
                    }
                }
            } else {
                AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    Text(
                        stringResource(R.string.scan_hint),
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(8.dp),
                    )
                    Button(
                        enabled = !isAnalyzing,
                        onClick = {
                            val capture = imageCapture ?: return@Button
                            val photoFile = File.createTempFile("label_scan_", ".jpg", context.cacheDir)
                            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                            isAnalyzing = true
                            capture.takePicture(
                                outputOptions,
                                ContextCompat.getMainExecutor(context),
                                object : ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                        analyzeLabel(context, photoFile) { form ->
                                            isAnalyzing = false
                                            photoFile.delete()
                                            if (form != null) {
                                                ScanDraftHolder.set(form)
                                                onScanned()
                                            } else {
                                                Toast.makeText(context, context.getString(R.string.scan_error), Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }

                                    override fun onError(exception: ImageCaptureException) {
                                        isAnalyzing = false
                                        photoFile.delete()
                                        Toast.makeText(context, context.getString(R.string.scan_error), Toast.LENGTH_SHORT).show()
                                    }
                                },
                            )
                        },
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    ) {
                        if (isAnalyzing) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        } else {
                            Text(stringResource(R.string.scan_capture))
                        }
                    }
                }
            }
        }
    }
}

private fun analyzeLabel(
    context: android.content.Context,
    file: File,
    onResult: (FilamentFormState?) -> Unit,
) {
    runCatching {
        val image = InputImage.fromFilePath(context, Uri.fromFile(file))
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            .process(image)
            .addOnSuccessListener { visionText -> onResult(parseLabelText(visionText.text)) }
            .addOnFailureListener { onResult(null) }
    }.onFailure { onResult(null) }
}
