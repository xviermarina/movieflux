package com.mxvier.auth.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mxvier.auth.databinding.FragmentLoginBinding
import com.mxvier.auth.presentation.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding ?:
    throw IllegalStateException("Binding acessado fora do ciclo de vida da View")

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLoginButton()
    }

    private fun setupLoginButton() {
        binding.etUser.doOnTextChanged { _,_,_,_ -> binding.tilUser.error = null }
        binding.etPassword.doOnTextChanged { _,_,_,_ -> binding.tilPassword.error = null }

        binding.btnLogin.setOnClickListener {
            val user = binding.etUser.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            binding.tilUser.error = null
            binding.tilPassword.error = null

            var hasError = false
            if (user.isEmpty()){
                binding.tilUser.error = "Por favor, preencha o usuário"
                hasError = true
            }

            if (password.isEmpty()){
                binding.tilUser.error = "Por favor, preencha a senha"
                hasError = true
            }

            if (hasError) return@setOnClickListener
            viewModel.login(user, password)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}