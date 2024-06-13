package com.cse535.news_app

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.launch

class Settings : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            SettingsScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = Modifier.fillMaxSize().background(colorScheme.background)
    ) {
        TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary
        )

    )
        Card(
            modifier = Modifier.padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            LanguageDropdown(selectedLanguage = "English") { language ->
                // Handle language selection
            }
        }
//        LanguageDropdown(selectedLanguage = "English") { language ->
//            // Handle language selection
//        }
    }
}

@Composable
fun LanguageDropdown(selectedLanguage: String, onLanguageSelected: (String) -> Unit) {
    // Define the list of languages
    val languages = listOf("English:en", "French:fr", "German:de")
    // Get the SharedPreferences instance
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LocalContext.current)

    // Get the previously selected language from SharedPreferences, defaulting to English
    var currentLanguage by remember {
        mutableStateOf(sharedPreferences.getString("selected_language", "English")!!)
    }

    // State to track whether the dropdown is expanded
    var expanded by remember { mutableStateOf(false) }

    // Handle language selection change
    Column {
        // Display the selected language and toggle the dropdown when clicked
        Box(
            modifier = Modifier
                .padding(16.dp)
                .background(color = colorScheme.surface, shape = RoundedCornerShape(4.dp))
                .clickable(onClick = { expanded = true })
                .width(200.dp)
        ) {
            Text(
                text = currentLanguage,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterStart)
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(8.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
        ) {
            languages.forEach { language ->
                DropdownMenuItem(
                    text = {Text(
                        text = language,
                        modifier = Modifier.padding(8.dp)
                    )},
                    onClick = {
                        currentLanguage = language
                        // Save the selected language to SharedPreferences
                        sharedPreferences.edit().putString("selected_language", language.takeLast(2)).apply()
                        // Notify the caller that the language has been selected
                        onLanguageSelected(language)
                        // Close the dropdown
                        expanded = false

                    }
                )
            }
        }
    }
}


@Preview
@Composable
fun SettingsScreenPreview() {
    LanguageDropdown(selectedLanguage = "English"){
        language -> println("Selected language: $language")
    }
}
