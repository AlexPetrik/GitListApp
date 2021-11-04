package ru.alexpetrik.gitlistapp

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.edit
import kotlinx.coroutines.*
import org.json.JSONObject
import org.json.JSONTokener
import java.io.OutputStreamWriter
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class GithubWebViewClient(private val githubDialog: Dialog) : WebViewClient() {

    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        if (request?.url.toString().startsWith(BuildConfig.REDIRECT_URL)) {
            handleUrl(request?.url.toString())

            if (request?.url.toString().contains(Utils.codeField))
                githubDialog.dismiss()
            return true
        }
        return false
    }

    @DelicateCoroutinesApi
    private fun handleUrl(url: String) {
        val uri = Uri.parse(url)
        if (url.contains(Utils.codeField)) {
            val githubCode = uri.getQueryParameter(Utils.codeField) ?: ""
            requestForAccessToken(githubCode)
        }
    }

    @DelicateCoroutinesApi
    private fun requestForAccessToken(githubCode: String) {
        val postParams = "grant_type=authorization_code" +
                "&code=" + githubCode +
                "&redirect_uri=" + BuildConfig.REDIRECT_URL +
                "&client_id=" + BuildConfig.CLIENT_ID +
                "&client_secret=" + BuildConfig.CLIENT_SECRET
        GlobalScope.launch(Dispatchers.Default) {
            val url = URL(BuildConfig.ACCESS_TOKEN_URL)
            val httpsURLConnection =
                withContext(Dispatchers.IO) { url.openConnection() as HttpsURLConnection }
            httpsURLConnection.requestMethod = "POST"
            httpsURLConnection.setRequestProperty(
                "Accept",
                "application/json"
            );
            httpsURLConnection.doInput = true
            httpsURLConnection.doOutput = true
            val outputStreamWriter = OutputStreamWriter(httpsURLConnection.outputStream)
            withContext(Dispatchers.IO) {
                outputStreamWriter.write(postParams)
                outputStreamWriter.flush()
            }
            val response = httpsURLConnection.inputStream.bufferedReader()
                .use { it.readText() }

            val jsonObject = JSONTokener(response).nextValue() as JSONObject

            val accessToken = jsonObject.getString(Utils.accessTokenField)
            githubDialog.context.getSharedPreferences(Utils.accessTokenField, Context.MODE_PRIVATE).edit {
                putString(Utils.accessTokenField, accessToken)
            }
        }
    }
}
