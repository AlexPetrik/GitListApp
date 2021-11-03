package ru.alexpetrik.gitlistapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import java.net.URL
import java.sql.Time
import java.util.concurrent.TimeUnit
import javax.net.ssl.HttpsURLConnection

class LoginFragment : Fragment() {

    private lateinit var githubDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginButton = view.findViewById<Button>(R.id.login_button)
        loginButton.setOnClickListener {
            val state = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())

            val githubAuthUrl = BuildConfig.AUTHORIZE_URL +
                    "?client_id=" + BuildConfig.CLIENT_ID +
                    "&scope=" + "read:user,user:email" +
                    "&redirect_uri=" + BuildConfig.REDIRECT_URL +
                    "&state=" + state

            setupGithubWebViewDialog(githubAuthUrl)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupGithubWebViewDialog(url: String) {
        githubDialog = Dialog(requireContext())
        val webView = WebView(requireContext())
        webView.isVerticalScrollBarEnabled = false
        webView.isHorizontalScrollBarEnabled = false
        webView.webViewClient = GithubWebViewClient(githubDialog)
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(url)
        githubDialog.setContentView(webView)
        githubDialog.show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = LoginFragment()
    }
}