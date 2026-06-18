package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.trackersiklusmenstruasi.databinding.ActivitySignupBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    private val googleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleGoogleSignInResult(task)
        } else {
            // Jika Code: 0, biasanya karena SHA-1 belum terdaftar di Google Cloud/Firebase
            val errorMessage = if (result.resultCode == 0) {
                "Login Gagal (Code: 0). \n\nTips: Pastikan SHA-1 PC Anda sudah terdaftar di Google Cloud Console/Firebase."
            } else {
                "Login dibatalkan (Code: ${result.resultCode})"
            }
            
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Info Google Sign-In")
                .setMessage(errorMessage)
                .setPositiveButton("Coba Lagi") { _, _ -> signInWithGoogle() }
                .setNeutralButton("Lewati (Simulasi)") { _, _ -> proceedLocally() }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupGoogleSignIn()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnGoToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnGoogle.setOnClickListener {
            signInWithGoogle()
        }

        binding.btnGoToRegister.setOnClickListener {
            // Pindah ke form pendaftaran (Screen 6)
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.tvPrivacy.setOnClickListener {
            Toast.makeText(this, "Membuka Kebijakan Privasi...", Toast.LENGTH_SHORT).show()
        }

        binding.tvTerms.setOnClickListener {
            Toast.makeText(this, "Membuka Syarat & Ketentuan...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signInWithGoogle() {
        googleLauncher.launch(googleSignInClient.signInIntent)
    }

    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                val email = account.email ?: ""
                val name = account.displayName ?: "Google User"
                
                Toast.makeText(this, "Menghubungkan: $email", Toast.LENGTH_SHORT).show()

                lifecycleScope.launch {
                    try {
                        val apiService = ApiService.create()
                        // Daftarkan atau hubungkan akun Google di database
                        val response = apiService.saveConnectedAccount(
                            ConnectedAccountModel(
                                user_id = 1, // Placeholder
                                provider = "Google",
                                email = email
                            )
                        )

                        if (response.success) {
                            val sessionManager = SessionManager(this@SignupActivity)
                            sessionManager.setLoggedIn(true)
                            sessionManager.setUserId(response.user_id ?: 1)

                            startActivity(Intent(this@SignupActivity, ProfileSetupActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@SignupActivity, "Server: ${response.message}", Toast.LENGTH_SHORT).show()
                            // Tetap lanjut jika gagal sync (opsional, sesuaikan kebutuhan)
                            proceedLocally()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@SignupActivity, "Error API: ${e.message}", Toast.LENGTH_SHORT).show()
                        proceedLocally()
                    }
                }
            }
        } catch (e: ApiException) {
            Toast.makeText(this, "ApiException (${e.statusCode}): ${e.message}", Toast.LENGTH_LONG).show()
            android.util.Log.e("SignupActivity", "Google Sign-In failed", e)
        }
    }

    private fun proceedLocally() {
        val sessionManager = SessionManager(this)
        sessionManager.setLoggedIn(true)
        sessionManager.setUserId(1)
        startActivity(Intent(this, ProfileSetupActivity::class.java))
        finish()
    }
}
