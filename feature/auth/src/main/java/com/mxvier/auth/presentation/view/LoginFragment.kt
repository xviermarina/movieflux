package com.mxvier.auth.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mxvier.auth.databinding.FragmentLoginBinding
import com.mxvier.auth.presentation.viewmodel.LoginUiState
import com.mxvier.auth.presentation.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import androidx.navigation.fragment.findNavController
import androidx.navigation.NavOptions
import androidx.core.net.toUri

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding acessado fora do ciclo de vida da View")

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        executor = ContextCompat.getMainExecutor(requireContext())
        setupBiometricPrompt()
        setupListeners()
        observeUiState()

        viewModel.loadSavedPreferences()
    }

    private fun setupListeners() {
        binding.authEtUser.doOnTextChanged { _, _, _, _ -> binding.authTilUser.error = null }
        binding.authEtPassword.doOnTextChanged { _, _, _, _ -> binding.authTilPassword.error = null }

        binding.authBtnLogin.setOnClickListener {
            val username = binding.authEtUser.text.toString().trim()
            val password = binding.authEtPassword.text.toString().trim()
            val isRememberMeChecked = binding.authSwitchRememberMe.isChecked

            binding.authTilUser.error = null
            binding.authTilPassword.error = null

            var hasError = false

            if (username.isEmpty()) {
                binding.authTilUser.error = getString(com.mxvier.auth.R.string.auth_error_empty_user)
                hasError = true
            }

            if (password.isEmpty()) {
                binding.authTilPassword.error = getString(com.mxvier.auth.R.string.auth_error_empty_password)
                hasError = true
            }

            if (hasError) return@setOnClickListener

            viewModel.login(username, password, isRememberMeChecked)
        }
    }

    private fun setupBiometricPrompt() {
        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                viewModel.loginWithBiometric()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    Toast.makeText(requireContext(), getString(com.mxvier.auth.R.string.auth_biometric_error_negative), Toast.LENGTH_SHORT).show()
                }
            }
        })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(com.mxvier.auth.R.string.auth_biometric_title))
            .setSubtitle(getString(com.mxvier.auth.R.string.auth_biometric_subtitle))
            .setNegativeButtonText(getString(com.mxvier.auth.R.string.auth_biometric_negative_button))
            .setAllowedAuthenticators(BIOMETRIC_STRONG)
            .build()
    }

    private fun checkBiometricCapability(): Boolean {
        val biometricManager = BiometricManager.from(requireContext())
        return biometricManager.canAuthenticate(BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
    }

    private fun showBiometricOfferDialog() {
        val isBiometricAvailable = checkBiometricCapability()
        val alreadyConfigured = viewModel.isBiometricEnabled.value

        if (isBiometricAvailable && !alreadyConfigured) {
            MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.MaterialAlertDialog_Material3)
                .setTitle(getString(com.mxvier.auth.R.string.auth_biometric_title))
                .setMessage(getString(com.mxvier.auth.R.string.auth_biometric_offer_message))
                .setPositiveButton(getString(com.mxvier.auth.R.string.auth_biometric_offer_positive)) { dialog, _ ->
                    viewModel.enableBiometricOption(true)
                    navigateToHome()
                    dialog.dismiss()
                }
                .setNegativeButton(getString(com.mxvier.auth.R.string.auth_biometric_offer_negative_btn)) { dialog, _ ->
                    viewModel.enableBiometricOption(false)
                    navigateToHome()
                    dialog.dismiss()
                }
                .setCancelable(false)
                .show()
        } else {
            navigateToHome()
        }
    }

    private fun navigateToHome() {
        val deepLinkUri = "app://movies/home".toUri()
        val navController = findNavController()

        val navOptions = NavOptions.Builder()
            .setPopUpTo(navController.currentDestination?.id ?: return, true)
            .setEnterAnim(android.R.anim.fade_in)
            .setExitAnim(android.R.anim.fade_out)
            .build()

        try {
            navController.navigate(deepLinkUri, navOptions)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), getString(com.mxvier.auth.R.string.auth_error_navigate_home), Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        handleUiState(state)
                    }
                }
                launch {
                    viewModel.savedUser.collect { savedUser ->
                        if (savedUser != null) {
                            binding.authEtUser.setText(savedUser)
                            binding.authSwitchRememberMe.isChecked = true
                        } else {
                            binding.authSwitchRememberMe.isChecked = false
                        }
                    }
                }
                launch {
                    viewModel.isBiometricEnabled.collect { isEnabled ->
                        if (isEnabled && checkBiometricCapability()) {
                            biometricPrompt.authenticate(promptInfo)
                        }
                    }
                }
            }
        }
    }

    private fun handleUiState(state: LoginUiState) {
        when (state) {
            is LoginUiState.Initial -> toggleLoading(isLoading = false)
            is LoginUiState.Loading -> toggleLoading(isLoading = true)
            is LoginUiState.Success -> {
                toggleLoading(isLoading = false)
                showBiometricOfferDialog()
            }
            is LoginUiState.Error -> {
                toggleLoading(isLoading = false)
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun toggleLoading(isLoading: Boolean) {
        binding.authProgressLoading.isVisible = isLoading
        binding.authContainerForm.isVisible = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}