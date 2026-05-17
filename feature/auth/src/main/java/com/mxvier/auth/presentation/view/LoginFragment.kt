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
        binding.etUser.doOnTextChanged { _, _, _, _ -> binding.tilUser.error = null }
        binding.etPassword.doOnTextChanged { _, _, _, _ -> binding.tilPassword.error = null }

        binding.btnLogin.setOnClickListener {
            val username = binding.etUser.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val isRememberMeChecked = binding.switchRememberMe.isChecked

            binding.tilUser.error = null
            binding.tilPassword.error = null

            var hasError = false

            if (username.isEmpty()) {
                binding.tilUser.error = "Por favor, preencha o usuário"
                hasError = true
            }

            if (password.isEmpty()) {
                binding.tilPassword.error = "Por favor, preencha a sua senha"
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
                    Toast.makeText(requireContext(), "Utilize a senha para entrar", Toast.LENGTH_SHORT).show()
                }
            }
        })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Login Biométrico")
            .setSubtitle("Autentique-se usando sua digital ou face")
            .setNegativeButtonText("Inserir senha manualmente")
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
                .setTitle("Login Biométrico")
                .setMessage("Deseja ativar o acesso por biometria para os próximos logins?")
                .setPositiveButton("Sim, ativar") { dialog, _ ->
                    viewModel.enableBiometricOption(true)
                    navigateToHome()
                    dialog.dismiss()
                }
                .setNegativeButton("Agora não") { dialog, _ ->
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
        Toast.makeText(context,"Navegar pra home", Toast.LENGTH_SHORT).show()
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
                    viewModel.savedCredentials.collect { credentials ->
                        if (credentials != null) {
                            val (user, pass) = credentials
                            binding.etUser.setText(user)
                            binding.etPassword.setText(pass)
                            binding.switchRememberMe.isChecked = true
                        } else {
                            binding.switchRememberMe.isChecked = false
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
        binding.progressLoading.isVisible = isLoading
        binding.containerForm.isVisible = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}