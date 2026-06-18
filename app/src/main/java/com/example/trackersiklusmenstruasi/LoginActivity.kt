package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.trackersiklusmenstruasi.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var isPasswordVisible = false
    private lateinit var googleSignInClient: GoogleSignInClient

    private val googleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleGoogleSignInResult(task)
        } else {
            val errorMessage = if (result.resultCode == 0) {
                "Login Gagal (Code: 0). \n\nHal ini biasanya terjadi karena SHA-1 PC/Laptop Anda belum didaftarkan di Google Cloud Console atau Firebase.\n\nApakah Anda ingin melanjutkan dengan akun simulasi (Mode Tamu)?"
            } else {
                "Login dibatalkan (Code: ${result.resultCode})"
            }
            
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Masalah Koneksi Google")
                .setMessage(errorMessage)
                .setPositiveButton("Gunakan Akun Tamu") { _, _ -> 
                    val sessionManager = SessionManager(this)
                    sessionManager.setLoggedIn(true)
                    sessionManager.setUserId(1)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleLauncher.launch(signInIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }

        binding.tvForgotPassword.setOnClickListener {
            // Membuka halaman Lupa Kata Sandi
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        binding.btnTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                binding.etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            binding.etPassword.setSelection(binding.etPassword.text.length)
        }

        binding.btnLogin.setOnClickListener {
            val sessionManager = SessionManager(this)
            sessionManager.setLoggedIn(true)
            sessionManager.setUserId(1) // Default user ID untuk simulasi
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        setupGoogleSignIn()
        setupSocialLogin()
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun setupSocialLogin() {
        binding.btnGoogleLogin.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                val email = account.email ?: ""
                
                Toast.makeText(this, "Masuk: $email", Toast.LENGTH_SHORT).show()

                lifecycleScope.launch {
                    try {
                        val apiService = ApiService.create()
                        val response = apiService.saveConnectedAccount(
                            ConnectedAccountModel(
                                user_id = 1,
                                provider = "Google",
                                email = email
                            )
                        )

                        val sessionManager = SessionManager(this@LoginActivity)
                        sessionManager.setLoggedIn(true)
                        sessionManager.setUserId(response.user_id ?: 1)

                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } catch (e: Exception) {
                        Toast.makeText(this@LoginActivity, "Offline: Masuk sebagai tamu", Toast.LENGTH_SHORT).show()
                        val sessionManager = SessionManager(this@LoginActivity)
                        sessionManager.setLoggedIn(true)
                        sessionManager.setUserId(1)
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                }
            }
        } catch (e: ApiException) {
            Toast.makeText(this, "ApiException (${e.statusCode}): ${e.message}", Toast.LENGTH_LONG).show()
            android.util.Log.e("LoginActivity", "Google login failed", e)
        }
    }
}
