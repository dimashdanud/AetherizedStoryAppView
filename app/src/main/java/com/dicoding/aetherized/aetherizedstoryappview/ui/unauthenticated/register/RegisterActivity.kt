package com.dicoding.aetherized.aetherizedstoryappview.ui.unauthenticated.register

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.dicoding.aetherized.aetherizedstoryappview.data.model.User
import com.dicoding.aetherized.aetherizedstoryappview.util.network.ApiConfig
import com.dicoding.aetherized.aetherizedstoryappview.databinding.ActivityRegisterBinding
import com.dicoding.aetherized.aetherizedstoryappview.ui.authenticated.settings.SettingsViewModel
import com.dicoding.aetherized.aetherizedstoryappview.ui.unauthenticated.main.MainActivity
import com.dicoding.aetherized.aetherizedstoryappview.util.helper.CustomPreference
import com.dicoding.aetherized.aetherizedstoryappview.util.helper.ViewModelFactory
import java.util.Locale
import java.util.regex.Pattern

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModelFactory by lazy { ViewModelFactory(CustomPreference(this)) }
    private val viewModel by viewModels<RegisterViewModel> { viewModelFactory }
    private val settingsViewModel by viewModels<SettingsViewModel> { viewModelFactory }


    private val apiService = ApiConfig.getApiService()
    private var nameForm = false
    private var emailForm = false
    private var passForm = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupActions()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupActions() {
        binding.nameEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                nameForm = !s.isNullOrEmpty()
                setMyButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {

            }
        })
        binding.emailEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                emailForm = isValidEmail(s.toString())
                setMyButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {

            }
        })
        binding.passwordEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                passForm = !s.isNullOrEmpty()
                setMyButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {

            }
        })
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            when {
                name.isEmpty() -> {
                    binding.nameEditTextLayout.error = "Masukkan email"
                }
                email.isEmpty() -> {
                    binding.emailEditTextLayout.error = "Masukkan email"
                }
                password.isEmpty() -> {
                    binding.passwordEditTextLayout.error = "Masukkan password"
                }
                isValidPassword(password) -> {
                    binding.passwordEditTextLayout.error = "Masukkan minimal 8 karakter"
                }
                else -> {
                    val user = User(name, email.lowercase(), password, false)
                    viewModel.register(user) { response ->
                        if (!response.error) {
                            Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
    private fun setMyButtonEnable() {
        val formFilled = nameForm && emailForm && passForm
        binding.signupButton.isEnabled = formFilled
    }
    private fun isValidPassword(password: String): Boolean {
        return (password.length < 8)
    }
    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z]{2,6}$"
        val pattern = Pattern.compile(emailPattern, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }
}