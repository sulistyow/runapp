package com.liztstudio.runtime.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.liztstudio.runtime.R
import com.liztstudio.runtime.databinding.FragmentSettingBinding
import com.liztstudio.runtime.utils.Constant
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : Fragment() {

    private lateinit var bind: FragmentSettingBinding

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentSettingBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadFieldsFromSharedPref()

        bind.btnApplyChanges.setOnClickListener {
            val success = applyChangeSharedPref()
            if (success) {
                Snackbar.make(view, "Saved changes", Snackbar.LENGTH_LONG).show()
            } else {
                Snackbar.make(view, "Please fill all fields", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun applyChangeSharedPref(): Boolean {
        val nameText = bind.etName.text.toString()
        val weightText = bind.etWeight.text.toString()
        if (nameText.isEmpty() || weightText.isEmpty()) {
            return false
        }
        sharedPreferences.edit()
            .putString(Constant.KEY_NAME, nameText)
            .putFloat(Constant.KEY_WEIGHT, weightText.toFloat())
            .apply()
        val toolbarText = "Let's go, $nameText"
        val tvToolbar = requireActivity().findViewById<TextView>(R.id.tvToolbarTitle)
        tvToolbar.text = toolbarText
        return true
    }

    private fun loadFieldsFromSharedPref() {
        val name = sharedPreferences.getString(Constant.KEY_NAME, "")
        val weight = sharedPreferences.getFloat(Constant.KEY_WEIGHT, 80f)
        bind.etName.setText(name)
        bind.etWeight.setText(weight.toString())
    }
}