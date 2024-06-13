package com.cse535.news_app

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationProvider
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.cse535.news_app.ui.theme.News_appTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Locale
import android.content.res.Configuration
import android.preference.PreferenceManager
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val isDarkMode = remember { mutableStateOf(isSystemInDarkTheme(context)) }

            News_appTheme(isDarkMode.value) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(this, isDarkMode.value) {
                        isDarkMode.value = !isDarkMode.value
                    }
//                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//                    val city = getCurrentCity(fusedLocationClient, this)
                }
            }
        }
    }
}

fun isSystemInDarkTheme(context: Context): Boolean {
    return context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(context: Context, isDarkMode: Boolean, toggleDarkMode: () -> Unit) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val newsResponse = remember { mutableStateOf<NewsResponse?>(null) }
    val newsList = remember {
        mutableStateOf<List<Article>>(emptyList())
    }
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    val prefLang = sharedPreferences.getString("selected_language", "en") ?: "en"

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            newsList.value = getCategoryNewsList("general", prefLang)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { drawerContent(drawerState, coroutineScope, context) },
        content = {
            Scaffold(
                topBar = { MainTopBar(drawerState, coroutineScope, toggleDarkMode, context) },
                content = { paddingValues ->
                    Column() {
                        Spacer(modifier = Modifier.padding(40.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 5.dp)
                                .horizontalScroll(rememberScrollState())
                                .background(Color.LightGray)
                        ) {
                            Button(
                                onClick = {
                                    coroutineScope.launch() {
                                        newsList.value = getCategoryNewsList("general", prefLang)
                                    }
                                          },
                                modifier = Modifier
                                    .padding(16.dp)
                                    .background(MaterialTheme.colorScheme.primary,shape = RoundedCornerShape(32.dp))
                                    .height(48.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            )
                            {
                                Text(text = "General")
                            }
                            Button(
                                onClick = {
                                    coroutineScope.launch() {
                                        newsList.value = getCategoryNewsList("technology", prefLang)
                                        Log.d("News_2", newsList.value.toString())
                                    }
                                          },
                                modifier = Modifier
                                    .padding(16.dp)
                                    .background(MaterialTheme.colorScheme.primary,shape = RoundedCornerShape(32.dp))
                                    .height(48.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            )
                            {
                                Text(text = "Technology")
                            }
                            Button(
                                onClick = {
                                    coroutineScope.launch() {
                                        newsList.value = getCategoryNewsList("sports", prefLang)
                                    }
                                },
                                modifier = Modifier
                                    .padding(16.dp)
                                    .background(MaterialTheme.colorScheme.primary,shape = RoundedCornerShape(32.dp))
                                    .height(48.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            )
                            {
                                Text(text = "Sports")
                            }
                            Button(
                                onClick = {
                                    coroutineScope.launch() {
                                        val locationProviderClient = LocationServices.getFusedLocationProviderClient(context)
                                        var city = getCurrentCity(locationProviderClient, context)
                                        delay(1500)
                                        city = "delhi"
                                        Log.d("News", city)
                                        newsList.value = getKeywordNewsList(city.lowercase())
                                    }
                                          },
                                modifier = Modifier
                                    .padding(16.dp)
                                    .background(MaterialTheme.colorScheme.primary,shape = RoundedCornerShape(32.dp))
                                    .height(48.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            )
                            {
                                Text(text = "Near Me")
                            }
                        }

                        Log.d("News", newsList.value.toString())
                        NewsList(articles = newsList.value, context = context)
                    }
                }
            )
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(drawerState: DrawerState, coroutineScope: CoroutineScope, toggleDarkMode: () -> Unit, context: Context) {
    Column (modifier =  Modifier.padding(vertical = 2.dp)){
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {IconButton(onClick = {
                coroutineScope.launch {
                    drawerState.open()
            }}){
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null,
                    tint = Color.Black
                )}
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "News App",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
            }
        },

        actions = {
            IconButton(
                onClick = toggleDarkMode
            ) {
                val icon = if (isSystemInDarkTheme()) Icons.Default.LightMode else Icons.Default.DarkMode
                Icon(
                    imageVector = icon,
                    contentDescription = "Toggle Dark Mode",
                    tint = Color.Black
                )
            }

        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary
        )

    )
        val newsList = remember {
            mutableStateOf<List<Article>>(emptyList())
        }
    }
}

@Composable
fun NewsList(articles: List<Article>, context:Context) {
    // Add LazyColumn here
    LazyColumn (modifier = Modifier.fillMaxSize()){
        items(articles.size) { index ->
            SingleNews(articles[index], context)
        }
    }
}

/* gonna send a list of strings
list -> headline, content, URL, imageURL, source, time
*/

@Composable
fun SingleNews(article: Article, context: Context) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                val intent = Intent(context, NewsDetail::class.java).apply {
                    putExtra(NewsDetail.NEWS_DETAIL_TITLE, article.title)
                    putExtra(NewsDetail.NEWS_DETAIL_TEXT, article.content)
                    putExtra(NewsDetail.NEWS_DETAIL_URL, article.url.toString())
                    putExtra(NewsDetail.NEWS_DETAIL_IMAGE_URL, article.urlToImage)
                    putExtra(NewsDetail.NEWS_DETAIL_SOURCE, article.source.name)
                    putExtra(NewsDetail.NEWS_DETAIL_TIME, article.publishedAt)
                }
                context.startActivity(intent)
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (article.urlToImage != null) {
                Image(
                    painter = rememberAsyncImagePainter(article.urlToImage),
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = article.source?.name ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = article.title ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = article.publishedAt?.let { formatDate(it) } ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

private fun formatDate(dateString: String): String {
    // Convert the date string to a Date object
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    var date = Date()
    try{
        date = dateFormat.parse(dateString)
    }
    catch(e: Exception){
        return dateString
    }

    // Format the Date object to a desired pattern
    val outputFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    return outputFormat.format(date)
}

fun getDummyHeadlines(num: Int): List<String> {
    val headlines = mutableListOf<String>()
    for (i in 1..num) {
        headlines.add("Headline $i")
    }
    return headlines
}

suspend fun getCurrentCity(locationClient: FusedLocationProviderClient, context: Context): String {
    // Initialize lat and long as nullable Double variables
    var lat: Double? = null
    var long: Double? = null

    // Use suspendCoroutine to create a suspending function
    return suspendCoroutine { continuation ->
        // Call getLatLong to fetch lat and long
        getLatLong(locationClient, context) { pair ->
            lat = pair.first
            long = pair.second

            // Check if lat and long are not null
            if (lat != null && long != null) {
                // Use Geocoder to get the city name based on lat and long
                val geocoder = Geocoder(context, Locale.getDefault())
                val addr = geocoder.getFromLocation(lat!!, long!!, 1)
                val cityName = addr?.get(0)?.locality ?: "Delhi"

                // Resume the coroutine with the city name
                continuation.resume(cityName)
            } else {
                // Resume the coroutine with a default city name if lat or long is null
                continuation.resume("Delhi")
            }
        }
    }
}



fun getLatLong(locationClient: FusedLocationProviderClient, context: Context, callback: (Pair<Double, Double>) -> Unit) {
    try {
        locationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val lat = location.latitude
                val long = location.longitude
                callback(Pair(lat, long))
            } else {
                // Handle case where location is null
                callback(Pair(0.0, 0.0))
            }
        }
    } catch (e: SecurityException) {
        // Handle security exception
        callback(Pair(0.0, 0.0))
    }
}

@Composable
fun DrawerHeader() {
    Box (modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp).background(Color.LightGray)) {
        Text(text = "News App", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun DrawerBody(drawerState: DrawerState, coroutineScope: CoroutineScope, context: Context){
    Column(modifier = Modifier.padding(vertical = 8.dp).background(Color.LightGray).fillMaxWidth()) {
        IconButton(onClick = {
            coroutineScope.launch {
                drawerState.close()
            }
        }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black
            )
        }
        IconButton(onClick = {
            val intent = Intent(context, Settings::class.java)
            context.startActivity(intent)
        }) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Back",
                tint = Color.Black
            )
        }
    }
}
@Composable
fun drawerContent (drawerState: DrawerState, coroutineScope: CoroutineScope, context: Context){
    Column(modifier = Modifier.fillMaxSize(0.4f).background(Color.LightGray)) {
        DrawerHeader()
        DrawerBody(drawerState, coroutineScope, context)
    }
}

@Composable
fun NewsTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String,
    placeholder: String = "",
    onImeAction: () -> Unit = {}
) {
    val context = LocalContext.current
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .semantics { contentDescription = label }, // Set content description for accessibility
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            onImeAction()
        })
    )
}
@Composable
fun NewsText(
    text: String,
    modifier: Modifier = Modifier,
    contentDescription: String? =  "Breaking News Text"
) {
    BasicTextField(
        value = text,
        onValueChange = { /* Handle value change if needed */ },
        modifier = modifier.semantics {
            this.contentDescription = contentDescription ?: text
        }
    )
}

suspend fun getCategoryNewsList(category: String, lang: String): List<Article> {
    return suspendCoroutine { continuation ->
        getNewsByCategory(category, lang, object : NewsCallback {
            override fun onSuccess(newsResponse: NewsResponse) {
                val newsList = newsResponse.articles
                Log.d("News_1", newsList.toString())
                continuation.resume(newsList)
            }

            override fun onFailure(message: String) {
                Log.d("News", message)
                continuation.resume(emptyList()) // Return empty list on failure
            }
        })
    }
}

suspend fun getKeywordNewsList(keyword: String): List<Article> {
    return suspendCoroutine { continuation ->
        getNewsByKeyword(keyword, object : NewsCallback {
            override fun onSuccess(newsResponse: NewsResponse) {
                val newsList = newsResponse.articles
                Log.d("News_1", newsList.toString())
                continuation.resume(newsList)
            }

            override fun onFailure(message: String) {
                Log.d("News", message)
                continuation.resume(emptyList()) // Return empty list on failure
            }
        })
    }
}