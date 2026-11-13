package de.letheapp.filamentmanager.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.letheapp.filamentmanager.R
import de.letheapp.filamentmanager.data.catalog.parseCatalogHex
import de.letheapp.filamentmanager.ui.components.SearchableDropdown

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilamentEditorScreen(
    viewModel: FilamentEditorViewModel,
    isEditing: Boolean,
    onSaved: () -> Unit,
    onDeleted: () -> Unit,
) {
    val state by viewModel.form.collectAsState()
    val manufacturerTop3 by viewModel.manufacturerTop3.collectAsState()
    val manufacturerAll by viewModel.manufacturerAllAlpha.collectAsState()
    val typeTop3 by viewModel.typeTop3.collectAsState()
    val typeAll by viewModel.typeAllAlpha.collectAsState()
    val colorTop3 by viewModel.colorTopEntries.collectAsState()
    val colorAll by viewModel.colorAllEntries.collectAsState()

    val isValid = state.brand.isNotBlank() && state.type.isNotBlank() && state.colorName.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            if (isEditing) R.string.editor_title_edit else R.string.editor_title_new,
                        ),
                    )
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(stringResource(R.string.editor_section_basic), style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            Text(
                stringResource(R.string.editor_required_hint),
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                color = androidx.compose.material3.MaterialTheme.colorScheme.error,
            )

            OutlinedTextField(
                value = state.sku,
                onValueChange = { v -> viewModel.update { it.copy(sku = v) } },
                label = { Text(stringResource(R.string.editor_sku)) },
                modifier = Modifier.fillMaxWidth(),
            )
            SearchableDropdown(
                label = stringResource(R.string.editor_brand) + " *",
                value = state.brand,
                topEntries = manufacturerTop3,
                allEntriesAlpha = manufacturerAll,
                onValueSelected = viewModel::selectBrand,
                modifier = Modifier.fillMaxWidth(),
                isError = state.brand.isBlank(),
            )
            SearchableDropdown(
                label = stringResource(R.string.editor_type) + " *",
                value = state.type,
                topEntries = typeTop3,
                allEntriesAlpha = typeAll,
                onValueSelected = viewModel::selectType,
                modifier = Modifier.fillMaxWidth(),
                isError = state.type.isBlank(),
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                SearchableDropdown(
                    label = stringResource(R.string.editor_color_name) + " *",
                    value = state.colorName,
                    topEntries = colorTop3,
                    allEntriesAlpha = colorAll,
                    onValueSelected = viewModel::selectColor,
                    modifier = Modifier.weight(1f),
                    isError = state.colorName.isBlank(),
                )
                val swatchColor = state.colorHex?.let { hex ->
                    runCatching { Color(parseCatalogHex(hex)) }.getOrNull()
                }
                if (swatchColor != null) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(swatchColor)
                            .border(1.dp, androidx.compose.material3.MaterialTheme.colorScheme.outline, CircleShape),
                    )
                }
            }
            OutlinedTextField(
                value = state.diameterMm,
                onValueChange = { v -> viewModel.update { it.copy(diameterMm = v) } },
                label = { Text(stringResource(R.string.editor_diameter)) },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.spoolWeightG,
                onValueChange = { v -> viewModel.update { it.copy(spoolWeightG = v) } },
                label = { Text(stringResource(R.string.editor_spool_weight)) },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.lengthTotalM,
                onValueChange = { v -> viewModel.update { it.copy(lengthTotalM = v) } },
                label = { Text(stringResource(R.string.editor_length_total)) },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.lengthRemainingM,
                onValueChange = { v -> viewModel.update { it.copy(lengthRemainingM = v) } },
                label = { Text(stringResource(R.string.editor_length_remaining)) },
                modifier = Modifier.fillMaxWidth(),
            )

            HorizontalDivider()
            Text(stringResource(R.string.editor_section_temps), style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = state.extruderTempMin,
                onValueChange = { v -> viewModel.update { it.copy(extruderTempMin = v) } },
                label = { Text(stringResource(R.string.editor_extruder_min)) },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.extruderTempMax,
                onValueChange = { v -> viewModel.update { it.copy(extruderTempMax = v) } },
                label = { Text(stringResource(R.string.editor_extruder_max)) },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.hotbedTempMin,
                onValueChange = { v -> viewModel.update { it.copy(hotbedTempMin = v) } },
                label = { Text(stringResource(R.string.editor_hotbed_min)) },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.hotbedTempMax,
                onValueChange = { v -> viewModel.update { it.copy(hotbedTempMax = v) } },
                label = { Text(stringResource(R.string.editor_hotbed_max)) },
                modifier = Modifier.fillMaxWidth(),
            )

            HorizontalDivider()
            Text(stringResource(R.string.editor_section_advanced), style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = state.density,
                onValueChange = { v -> viewModel.update { it.copy(density = v) } },
                label = { Text(stringResource(R.string.editor_density)) },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.flowRatePercent,
                onValueChange = { v -> viewModel.update { it.copy(flowRatePercent = v) } },
                label = { Text(stringResource(R.string.editor_flow_rate)) },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.retractionDistanceMm,
                onValueChange = { v -> viewModel.update { it.copy(retractionDistanceMm = v) } },
                label = { Text(stringResource(R.string.editor_retraction_distance)) },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.retractionSpeedMmS,
                onValueChange = { v -> viewModel.update { it.copy(retractionSpeedMmS = v) } },
                label = { Text(stringResource(R.string.editor_retraction_speed)) },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.fanSpeedPercent,
                onValueChange = { v -> viewModel.update { it.copy(fanSpeedPercent = v) } },
                label = { Text(stringResource(R.string.editor_fan_speed)) },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.printSpeedMmS,
                onValueChange = { v -> viewModel.update { it.copy(printSpeedMmS = v) } },
                label = { Text(stringResource(R.string.editor_print_speed)) },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.purchaseUrl,
                onValueChange = { v -> viewModel.update { it.copy(purchaseUrl = v) } },
                label = { Text(stringResource(R.string.editor_purchase_url)) },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.pricePerKg,
                onValueChange = { v -> viewModel.update { it.copy(pricePerKg = v) } },
                label = { Text(stringResource(R.string.editor_price_per_kg)) },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.notes,
                onValueChange = { v -> viewModel.update { it.copy(notes = v) } },
                label = { Text(stringResource(R.string.editor_notes)) },
                modifier = Modifier.fillMaxWidth(),
            )

            Button(
                onClick = { viewModel.save(onSaved) },
                enabled = isValid,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.editor_save))
            }
            if (isEditing) {
                OutlinedButton(onClick = { viewModel.delete(onDeleted) }, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.editor_delete))
                }
            }
        }
    }
}
