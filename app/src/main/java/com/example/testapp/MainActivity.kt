package com.example.testapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.testapp.ui.EnterDataFragment
import com.example.testapp.ui.MainFragment
import com.example.testapp.ui.MainFragment.Companion.ACTION_DATA_FRAGMENT

class MainActivity : AppCompatActivity() {
    companion object {
        fun intentOf(cont: Context, act: String ="", bundle: Bundle? = null): Intent {
            val intent = Intent(cont, MainActivity::class.java).setAction(act)
            if (bundle != null) intent.putExtras(bundle)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        onNewIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.action == ACTION_DATA_FRAGMENT) enterData()
        else showMain()
    }

    private fun enterData() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, EnterDataFragment())
            .commitNow()
    }

    private fun showMain() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, MainFragment.newInstance())
            .commitNow()
    }

}