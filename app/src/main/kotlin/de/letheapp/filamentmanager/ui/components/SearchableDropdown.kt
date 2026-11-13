package de.letheapp.filamentmanager.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Wiederverwendbares Dropdown mit Live-Suche für Stammdaten mit vielen möglichen
 * Werten (Hersteller/Typ/Farbe) – siehe PLAN.md, Abschnitt
 * "Hersteller- und Stammdaten-Auswahl (Dropdown mit Suche)".
 *
 * Ohne Sucheingabe stehen die drei meistgenutzten Einträge ([topEntries]) oben,
 * abgetrennt von der restlichen alphabetischen Liste ([allEntriesAlpha]). Mit
 * Sucheingabe wird [allEntriesAlpha] nach Teilstring gefiltert. Am Ende der
 * gefilterten Liste steht immer ein "Neu anlegen"-Eintrag.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchableDropdown(
    label: String,
    value: String,
    topEntries: List<String>,
    allEntriesAlpha: List<String>,
    onValueSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    var query by remember(value) { mutableStateOf(value) }

    val filtered = remember(query, allEntriesAlpha) {
        if (query.isBlank()) {
            allEntriesAlpha
        } else {
            allEntriesAlpha.filter { it.contains(query, ignoreCase = true) }
        }
    }
    val showTop3 = query.isBlank() && topEntries.isNotEmpty()
    val restAlpha = if (showTop3) filtered.filterNot { it in topEntries } else filtered
    val exactMatchExists = filtered.any { it.equals(query, ignoreCase = true) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                expanded = true
            },
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(),
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            Box(modifier = Modifier.padding(horizontal = 4.dp)) {
                LazyColumn {
                    if (showTop3) {
                        items(topEntries) { entry ->
                            DropdownRow(entry) {
                                query = entry
                                expanded = false
                                onValueSelected(entry)
                            }
                        }
                        if (restAlpha.isNotEmpty()) {
                            item { HorizontalDivider() }
                        }
                    }
                    items(restAlpha) { entry ->
                        DropdownRow(entry) {
                            query = entry
                            expanded = false
                            onValueSelected(entry)
                        }
                    }
                    if (query.isNotBlank() && !exactMatchExists) {
                        item {
                            DropdownRow("Neu anlegen: „$query“") {
                                expanded = false
                                onValueSelected(query)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DropdownRow(text: String, onClick: () -> Unit) {
    androidx.compose.material3.DropdownMenuItem(
        text = { Text(text) },
        onClick = onClick,
    )
}
