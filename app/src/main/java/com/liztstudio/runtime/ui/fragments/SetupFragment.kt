package com.liztstudio.runtime.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.liztstudio.runtime.R
import com.liztstudio.runtime.databinding.FragmentSetupBinding
import com.liztstudio.runtime.utils.Constant
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : Fragment() {
    private lateinit var bind: FragmentSetupBinding

    @Inject
    lateinit var sharedPref: SharedPreferences

    @set:Inject
    var isFirstOpen = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentSetupBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isFirstOpen) {
            val navOptions = NavOptions.Builder().setPopUpTo(R.id.setupFragment, true)
                .build()
            findNavController().navigate(
                R.id.action_setupFragment_to_runFragment,
                savedInstanceState,
                navOptions
            )
        }

        bind.tvContinue.setOnClickListener {
            val success = writePersonalData()
            if (success) {
                findNavController().navigate(R.id.action_setupFragment_to_runFragment)
            } else {
                Snackbar.make(requireView(), "Please fill all field", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun writePersonalData(): Boolean {
        val name = bind.etName.text.toString()
        val weight = bind.etWeight.text.toString()
        if (name.isEmpty() || weight.isEmpty()) {
            return false
        }
        sharedPref.edit().putString(Constant.KEY_NAME, name)
            .putFloat(Constant.KEY_WEIGHT, weight.toFloat())
            .putBoolean(Constant.KEY_FIRST_TIME_TOOGLE, false).apply()

        val toolbarText = "Let's go, $name"
        val tvToolbar = requireActivity().findViewById<TextView>(R.id.tvToolbarTitle)
        tvToolbar.text = toolbarText
        return true
    }
}