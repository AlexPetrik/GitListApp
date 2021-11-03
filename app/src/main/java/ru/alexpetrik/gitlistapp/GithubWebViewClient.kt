package ru.alexpetrik.gitlistapp

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
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

            if (request?.url.toString().contains("code"))
                githubDialog.dismiss()
            return true
        }
        return false
    }

    @DelicateCoroutinesApi
    private fun handleUrl(url: String) {
        val uri = Uri.parse(url)
        if (url.contains("code")) {
            val githubCode = uri.getQueryParameter("code") ?: ""
            requestForAccessToken(githubCode)
        }
    }

    @DelicateCoroutinesApi
    private fun requestForAccessToken(githubCode: String) {
        val grantType = "authorization_code"
        val postParams = "grant_type=" + grantType +
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
            withContext(Dispatchers.Main) {
                val jsonObject = JSONTokener(response).nextValue() as JSONObject

                val accessToken = jsonObject.getString("access_token")

                fetchGithubUserProfile(accessToken)
            }
        }

    }

    @DelicateCoroutinesApi
    fun fetchGithubUserProfile(token: String) {
        GlobalScope.launch(Dispatchers.Default) {
            val tokenURLFull =
                "https://api.github.com/user"

            val url = URL(tokenURLFull)
            val httpsURLConnection =
                withContext(Dispatchers.IO) { url.openConnection() as HttpsURLConnection }
            httpsURLConnection.requestMethod = "GET"
            httpsURLConnection.setRequestProperty("Authorization", "Bearer $token")
            httpsURLConnection.doInput = true
            httpsURLConnection.doOutput = false
            val response = httpsURLConnection.inputStream.bufferedReader()
                .use { it.readText() }
            val jsonObject = JSONTokener(response).nextValue() as JSONObject
            Log.i("GitHub Access Token: ", token)

            // GitHub Id
            val githubId = jsonObject.getInt("id")
            Log.i("GitHub Id: ", githubId.toString())

            // GitHub Display Name
            val githubDisplayName = jsonObject.getString("login")
            Log.i("GitHub Display Name: ", githubDisplayName)

            // GitHub Email
            val githubEmail = jsonObject.getString("email")
            Log.i("GitHub Email: ", githubEmail)

            // GitHub Profile Avatar URL
            val githubAvatarURL = jsonObject.getString("avatar_url")
            Log.i("Github Profile Avatar URL: ", githubAvatarURL)

        }
    }
}
