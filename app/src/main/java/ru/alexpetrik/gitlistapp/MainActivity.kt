package ru.alexpetrik.gitlistapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    private lateinit var fragmentInstance: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(savedInstanceState == null) {

            val sp = getSharedPreferences(Utils.accessTokenField, Context.MODE_PRIVATE)
            val accessToken = sp.getString(Utils.accessTokenField, "")

            fragmentInstance = if (accessToken.isNullOrEmpty()) {
                LoginFragment.newInstance()
            } else {
                // TODO новый фрагмент
                UserCardFragment.newInstance()
            }

            supportFragmentManager.beginTransaction()
                .apply {
                    add(R.id.fragmentContainer, fragmentInstance)
                    commit()
                }
        }
    }

}