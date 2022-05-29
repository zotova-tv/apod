package zotova_tv.apod.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import zotova_tv.apod.databinding.SettingsFragmentBinding
import zotova_tv.apod.ui.MainActivity.Companion.NIGHT_MODE_TAG
import zotova_tv.apod.ui.MainActivity.Companion.SETTINGS_PREFS

class SettingsFragment : Fragment() {

    private var _binding: SettingsFragmentBinding? = null
    private val binding get() = _binding!!
    private var switchNightModeListener: SwitchNightModeListener? = null
    private var prefs: SharedPreferences? = null
    private var nightModeIsChecked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SettingsFragmentBinding.inflate(inflater, container, false)
        activity?.let {
            switchNightModeListener = it as SwitchNightModeListener
            prefs = it.getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE).also {prefs ->
                nightModeIsChecked = prefs.getBoolean(NIGHT_MODE_TAG, false)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding.switchNightMode){
            isChecked = nightModeIsChecked
            setOnCheckedChangeListener{ _, isChecked ->
                switchNightModeListener?.onSwitchNightMode(isChecked)
            }
        }
    }

    interface SwitchNightModeListener {
        fun onSwitchNightMode(isChecked: Boolean)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
