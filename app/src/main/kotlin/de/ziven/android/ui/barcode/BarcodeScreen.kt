package de.ziven.android.ui.barcode

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarcodeScreen(
    onDone: () -> Unit,
    viewModel: BarcodeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Barcode scannen") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            when (val s = state) {
                is BarcodeUiState.Scanning -> CameraPermissionGate(onBarcode = viewModel::onBarcode)
                is BarcodeUiState.Loading -> {
                    CircularProgressIndicator()
                    Text("Suche Produkt für ${s.code}...")
                }
                is BarcodeUiState.Found -> ProductFoundCard(
                    code = s.code,
                    name = s.product.name,
                    brand = s.product.brand,
                    onAdd = { qty, unit -> viewModel.addProduct(qty, unit) },
                )
                is BarcodeUiState.Manual -> ManualProductCard(
                    barcode = s.code,
                    onAdd = { barcode, name, brand, qty, unit ->
                        viewModel.addManual(barcode, name, brand, qty, unit)
                    },
                )
                is BarcodeUiState.Added -> {
                    Text("Produkt hinzugefügt.", style = MaterialTheme.typography.headlineSmall)
                    Button(onClick = onDone) { Text("Zurück") }
                }
                is BarcodeUiState.Error -> {
                    Text(s.message, color = MaterialTheme.colorScheme.error)
                    TextButton(onClick = viewModel::reset) { Text("Nochmal scannen") }
                }
            }
        }
    }
}

@Composable
fun CameraPermissionGate(onBarcode: (String) -> Unit) {
    val context = LocalContext.current
    val permission = Manifest.permission.CAMERA
    var hasPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasPermission = granted },
    )

    if (hasPermission) {
        BarcodeCameraPreview(onBarcode = onBarcode)
    } else {
        Column {
            Text("Kamera-Berechtigung wird benötigt, um Barcodes zu scannen.")
            Button(onClick = { launcher.launch(permission) }) { Text("Berechtigung erteilen") }
        }
    }
}

@Composable
fun BarcodeCameraPreview(onBarcode: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val executor = remember { Executors.newSingleThreadExecutor() }
    val scanner = remember { BarcodeScanning.getClient() }
    var scanned by remember { mutableStateOf(false) }

    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val analysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                analysis.setAnalyzer(executor) { imageProxy ->
                    if (scanned) {
                        imageProxy.close()
                        return@setAnalyzer
                    }
                    processImage(imageProxy, scanner) { code ->
                        if (!scanned && code != null) {
                            scanned = true
                            onBarcode(code)
                        }
                    }
                }
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        analysis,
                    )
                } catch (e: Exception) {
                    Log.e("Barcode", "Camera binding failed", e)
                }
            }, ContextCompat.getMainExecutor(ctx))
            previewView
        },
    )
}

private fun processImage(imageProxy: ImageProxy, scanner: com.google.mlkit.vision.barcode.BarcodeScanner, onResult: (String?) -> Unit) {
    val mediaImage = imageProxy.image ?: run {
        imageProxy.close()
        return
    }
    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
    scanner.process(image)
        .addOnSuccessListener { barcodes ->
            val code = barcodes.firstOrNull()?.rawValue
            onResult(code)
        }
        .addOnCompleteListener { imageProxy.close() }
}

@Composable
fun ProductFoundCard(code: String, name: String, brand: String?, onAdd: (Double, String) -> Unit) {
    var quantity by remember { mutableStateOf("1") }
    var unit by remember { mutableStateOf("g") }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(name, style = MaterialTheme.typography.titleLarge)
        brand?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
        Text("Barcode: $code", style = MaterialTheme.typography.bodySmall)
        OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Menge") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
        OutlinedTextField(value = unit, onValueChange = { unit = it }, label = { Text("Einheit") })
        Button(onClick = { onAdd(quantity.toDoubleOrNull() ?: 1.0, unit) }) { Text("Zum Vorrat hinzufügen") }
    }
}

@Composable
fun ManualProductCard(
    barcode: String,
    onAdd: (String, String, String?, Double, String) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var unit by remember { mutableStateOf("g") }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Produkt nicht gefunden. Bitte Daten eingeben.", color = MaterialTheme.colorScheme.error)
        Text("Barcode: $barcode", style = MaterialTheme.typography.bodySmall)
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        OutlinedTextField(value = brand, onValueChange = { brand = it }, label = { Text("Marke") })
        OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Menge") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
        OutlinedTextField(value = unit, onValueChange = { unit = it }, label = { Text("Einheit") })
        Button(
            onClick = { onAdd(barcode, name, brand.takeIf { it.isNotBlank() }, quantity.toDoubleOrNull() ?: 1.0, unit) },
            enabled = name.isNotBlank() && quantity.isNotBlank() && unit.isNotBlank(),
        ) { Text("Hinzufügen") }
    }
}
