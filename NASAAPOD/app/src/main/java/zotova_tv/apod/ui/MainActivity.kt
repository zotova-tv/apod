package zotova_tv.apod.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomappbar.BottomAppBar
import zotova_tv.apod.R
import zotova_tv.apod.databinding.MainActivityBinding
import zotova_tv.apod.ui.notes.NotesFragment
import zotova_tv.apod.ui.picture.PictureOfTheDayFragment
import zotova_tv.apod.ui.settings.SettingsFragment
import zotova_tv.apod.ui.wiki.WikiSearchFragment


class MainActivity :
    AppCompatActivity(),
    SettingsFragment.SwitchNightModeListener {

    private lateinit var binding: MainActivityBinding
    private var prefs: SharedPreferences? = null
    private var isMain = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setBottomAppBar()
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(
                    R.id.container,
                    PictureOfTheDayFragment.newInstance(),
                    MAIN_PICTURE_OF_THE_DAY_FRAGMENT_TAG
                )
                .commit()
        }

        prefs = getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE)
        val nightModeIsChecked = prefs?.getBoolean(NIGHT_MODE_TAG, false) ?: false
        switchNightMode(nightModeIsChecked)
    }

    override fun onSwitchNightMode(isChecked: Boolean) {
        prefs?.let {
            it.edit().also { editor ->
                editor.putBoolean(NIGHT_MODE_TAG, isChecked)
                editor.apply()
            }
        }
        switchNightMode(isChecked)
    }

    private fun switchNightMode(isChecked: Boolean) {
        when (isChecked) {
            true -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            false -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_bottom_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.app_bar_settings -> supportFragmentManager.beginTransaction()
                .add(R.id.container, SettingsFragment()).addToBackStack(null).commit()
            R.id.app_bar_notes -> {
                supportFragmentManager.beginTransaction()
                    .add(R.id.container, NotesFragment()).addToBackStack(null).commit()
//                binding.fab.hide()
            }
            R.id.app_bar_search -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, WikiSearchFragment(), WIKI_SEARCH_FRAGMENT_TAG)
                    .addToBackStack(null)
                    .commit()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setBottomAppBar() {
        setSupportActionBar(binding.bottomAppBar)
        binding.fab.setOnClickListener {
            if (isMain) {
                isMain = false
                binding.bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
                binding.fab.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_back_fab
                    )
                )
                binding.bottomAppBar.replaceMenu(R.menu.menu_bottom_bar_other_screen)
            } else {
                isMain = true
                binding.bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
                binding.bottomAppBar.replaceMenu(R.menu.menu_bottom_bar)
                binding.fab.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_plus_fab
                    )
                )
                supportFragmentManager.popBackStack()
            }
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStackImmediate(
                null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        } else {
            super.onBackPressed()
            binding.fab.performClick()
        }
    }

    companion object {
        const val WIKI_SEARCH_FRAGMENT_TAG = "WIKI_SEARCH_FRAGMENT_TAG"
        const val SETTINGS_PREFS = "SETTINGS_PREFS"
        const val NIGHT_MODE_TAG = "NIGHT_MODE_TAG"
        const val MAIN_PICTURE_OF_THE_DAY_FRAGMENT_TAG = "MAIN_PICTURE_OF_THE_DAY_FRAGMENT"
    }
}