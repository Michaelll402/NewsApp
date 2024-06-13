package com.cse535.news_app

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import android.util.Log
import androidx.compose.ui.text.font.FontWeight
import android.net.Uri
import androidx.compose.material3.Card
import android.speech.tts.TextToSpeech
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Locale

class NewsDetail : ComponentActivity(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    companion object {
        const val NEWS_DETAIL_TEXT = "news_detail_text"
        const val NEWS_DETAIL_TITLE = "news_detail_title"
        const val NEWS_DETAIL_URL = "news_detail_url"
        const val NEWS_DETAIL_IMAGE_URL = "news_detail_image"
        const val NEWS_DETAIL_SOURCE = "news_detail_source"
        const val NEWS_DETAIL_TIME = "news_detail_time"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val txt = intent.getStringExtra(NEWS_DETAIL_TEXT) ?: ""
        val title = intent.getStringExtra(NEWS_DETAIL_TITLE) ?: ""
        val imageUrl = intent.getStringExtra(NEWS_DETAIL_IMAGE_URL) ?: ""
        val url = intent.getStringExtra(NEWS_DETAIL_URL) ?: ""
        tts = TextToSpeech(this, this)
        setContent() {
            NewsDetailScreen(title, txt, imageUrl, url, this, tts)
        }
    }

    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.ENGLISH)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "TTS Not Supported for this news", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
}

fun playNews(text: String, tts: TextToSpeech) {
    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
}

fun stopNews(tts: TextToSpeech) {
    tts.stop()
}

fun playAtSpeedNews(text: String, tts: TextToSpeech, speed: Float) {
    tts.stop()
    tts.setSpeechRate(speed)
    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NewsDetailScreen(title: String, content: String,imageURL: String, url:String, context: Context, tts: TextToSpeech) {
    val shareText = "Check out this news article: $title\n$url"
    val showWebView = remember { mutableStateOf(false) }
    Scaffold(
        topBar = { NewsDetailTopBar(shareText, url, context, showWebView, tts, content) },
        content = {
                paddingValues ->
            //Spacer(modifier = Modifier.padding(30.dp))
            Column(modifier = Modifier.padding(paddingValues)){

                if (showWebView.value) {
                    WebViewComposable(
                        url = url,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
                else{
                    NewsContentView(title, content, imageURL)
                }
            }
        }
    )

}
@Composable
fun NewsContentView(title: String, content: String, imageURL: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ImageFromUrl(imageUrl = imageURL)
        }
        Text(
            text = content,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
@Composable
fun ImageFromUrl(imageUrl: String) {
    Image(
        painter = rememberAsyncImagePainter(imageUrl),
        contentDescription = null,
        modifier = Modifier
            .heightIn(200.dp)
            .fillMaxWidth()
            .padding(16.dp)
    )
}

class Item(
    val title: String,
    val function: () -> Unit
)


@Composable
fun MenuButton(
    text: String,
    menuItems: List<Item>,
    onItemClick: (String) -> Unit
) {
    val isMenuExpanded =  remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .wrapContentSize(Alignment.TopStart)
            .padding(16.dp)
    ) {
        Button(
            onClick = { isMenuExpanded.value = true }
        ) {
            Text(text)
        }

        DropdownMenu(
            expanded = isMenuExpanded.value,
            onDismissRequest = { isMenuExpanded.value = false },
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            menuItems.forEach { item ->
                DropdownMenuItem(text = { Text(item.title) }, onClick = {
                    item.function()
                    onItemClick(item.title)
                    isMenuExpanded.value = false
                })
                Spacer(modifier = Modifier.heightIn(16.dp))
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailTopBar(shareText:String, url: String, context: Context,showWebView: MutableState<Boolean>, tts: TextToSpeech, content: String) {
    Log.d("NewsDetailTopBar", "Open in Browser URL: $url")
    TopAppBar(title = { /*TODO*/
        Row(){
            Text("News Detail", style = MaterialTheme.typography.bodyLarge)
        }},
        actions = {
            IconButton(onClick = {shareNews(shareText, context)}) {
                Icon(Icons.Default.Share, contentDescription = "Share")
            }
            IconButton(onClick = { showWebView.value = !showWebView.value }) {
                Icon(Icons.Default.OpenInBrowser, contentDescription = "Open in Browser")
            }
            IconButton(onClick = { playNews(content, tts)}){
                Icon(Icons.Default.PlayCircle, contentDescription = "Play News")
            }
            IconButton(onClick = { stopNews(tts) }) {
                Icon(Icons.Default.Stop, contentDescription = "Stop Play News")
            }
            IconButton(onClick = { playAtSpeedNews(content, tts, 1.5f) }) {
                Icon(Icons.Default.FastForward, contentDescription = "Fast Forward")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary
        )
    )
}

//@Composable
//fun WebViewComposable(url: String, modifier: Modifier = Modifier) {
//    val webViewState = rememberWebViewState(url)
//    AndroidView(
//        factory = { context ->
//            WebView(context).apply {
//                webViewClient = WebViewClient()
//                settings.javaScriptEnabled = true
//            }
//        },
//        update = { webView ->
//            webView.loadUrl(webViewState.current)
//        },
//        modifier = modifier
//    )
//}

@Composable
fun WebViewComposable(url: String, modifier: Modifier = Modifier) {
    val webViewState = remember { mutableStateOf(url) }
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                webViewState.value.let { url ->
                    loadUrl(url)
                }
            }
        },
        update = { webView ->
            webViewState.value.let { url ->
                if (url != webView.url) {
                    webView.loadUrl(url)
                }
            }
        },
        modifier = modifier
    )
}

fun shareNews(news: String, context: Context) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, news) // Share the news headline
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}