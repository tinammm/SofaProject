package com.sofascoremini.ui.settings

import android.annotation.SuppressLint
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.sofascoremini.databinding.FragmentSettingsBinding
import com.sofascoremini.util.setUpAppTheme

const val THEME = "PREF_THEME"
const val DATE = "PREF_DATE"

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val preferences by lazy { PreferenceManager.getDefaultSharedPreferences(requireContext()) }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // theme setting
        binding.themeGroup.children.forEach { button ->
            (button as RadioButton).also {
                if (it.text.toString().lowercase() == getSavedTheme()) {
                    it.isChecked = true
                }

                it.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        val theme = buttonView.text.toString().lowercase()
                        saveSelectedTheme(theme)
                        setUpAppTheme(theme)
                    }
                }
            }
        }

        // date format setting
        binding.dateGroup.children.forEach { button ->
            (button as RadioButton).also {
                if (it.text.toString() == getSavedDateFormat()) {
                    it.isChecked = true
                }

                it.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        saveSelectedDateFormat(buttonView.text.toString())
                    }
                }
            }
        }

    }

    private fun getSavedTheme(): String? {
        return preferences.getString(THEME, "light")
    }

    private fun getSavedDateFormat(): String? {
        return preferences.getString(DATE, "DD / MM / YYYY")
    }

    @SuppressLint("ApplySharedPref")
    private fun saveSelectedTheme(theme: String) {
        preferences.edit().putString(THEME, theme).commit()
    }

    @SuppressLint("ApplySharedPref")
    private fun saveSelectedDateFormat(format: String) {
        preferences.edit().putString(DATE, format).commit()
    }
}