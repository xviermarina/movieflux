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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.mxvier.core.ui.theme.MovieFluxTheme

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MovieFluxTheme {
                    LoginScreen(
                        viewModel = viewModel,
                        onLoginClick = { user, pass, remember ->
                            viewModel.login(user, pass, remember)
                        }
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        executor = ContextCompat.getMainExecutor(requireContext())
        setupBiometricPrompt()
        observeUiState()

        viewModel.loadSavedPreferences()
    }

    private fun setupBiometricPrompt() {
        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                viewModel.loginWithBiometric()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode != BiometricPrompt.ERROR_USER_CANCELED && errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    Toast.makeText(
                        requireContext(),
                        getString(com.mxvier.auth.R.string.auth_biometric_fallback_message),
                        Toast.LENGTH_LONG
                    ).show()
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

    private fun handleBiometricOffer() {
        val biometricManager = BiometricManager.from(requireContext())
        val canAuthenticate = biometricManager.canAuthenticate(BIOMETRIC_STRONG)

        when (canAuthenticate) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                if (!viewModel.isBiometricEnabled.value && !viewModel.isBiometricRefused.value) {
                    showOfferDialog(
                        title = getString(com.mxvier.auth.R.string.auth_biometric_title),
                        message = getString(com.mxvier.auth.R.string.auth_biometric_offer_message),
                        positiveAction = { viewModel.enableBiometricOption(true) }
                    )
                } else {
                    navigateToHome()
                }
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                if (!viewModel.isBiometricRefused.value) {
                    showOfferDialog(
                        title = "Biometria não cadastrada",
                        message = "Seu dispositivo suporta biometria. Deseja cadastrar agora para usar no app?",
                        positiveAction = {
                            val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                                putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, BIOMETRIC_STRONG)
                            }
                            startActivity(enrollIntent)
                        }
                    )
                } else {
                    navigateToHome()
                }
            }
            else -> navigateToHome()
        }
    }

    private fun showOfferDialog(title: String, message: String, positiveAction: () -> Unit) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Sim") { _, _ -> 
                positiveAction()
                navigateToHome()
            }
            .setNegativeButton("Agora não") { _, _ ->
                viewModel.enableBiometricOption(false)
                navigateToHome()
            }
            .setCancelable(false)
            .show()
    }

    private fun navigateToHome() {
        val deepLinkUri = "app://movies/home".toUri()
        val navOptions = NavOptions.Builder()
            .setPopUpTo(findNavController().graph.startDestinationId, true)
            .build()
        try {
            findNavController().navigate(deepLinkUri, navOptions)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Erro ao navegar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        if (state is LoginUiState.Success) {
                            handleBiometricOffer()
                        } else if (state is LoginUiState.Error) {
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
                launch {
                    viewModel.isBiometricEnabled.collect { enabled ->
                        if (enabled && BiometricManager.from(requireContext()).canAuthenticate(BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS) {
                            biometricPrompt.authenticate(promptInfo)
                        }
                    }
                }
                launch {
                    if (viewModel.isBiometricRefused.value && 
                        BiometricManager.from(requireContext()).canAuthenticate(BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS) {
                        viewModel.resetBiometricRefusal()
                    }
                }
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
