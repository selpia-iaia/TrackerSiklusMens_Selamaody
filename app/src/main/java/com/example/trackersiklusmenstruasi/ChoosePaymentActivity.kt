package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trackersiklusmenstruasi.databinding.ActivityChoosePaymentBinding

class ChoosePaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChoosePaymentBinding
    private var selectedMethod: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChoosePaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        setupPaymentMethods()
        
        // Pilih Mastercard secara default sebagai contoh awal
        selectMethod("Mastercard")

        binding.btnLanjut.setOnClickListener {
            if (selectedMethod == null) {
                Toast.makeText(this, "Silakan pilih metode pembayaran", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val loading = LoadingDialogFragment()
            loading.show(supportFragmentManager, "loading")
            
            binding.root.postDelayed({
                loading.dismiss()
                startActivity(Intent(this, PaymentSuccessActivity::class.java))
            }, 2000)
        }
    }

    private fun setupPaymentMethods() {
        binding.payGoogle.apply {
            ivPaymentIcon.setImageResource(R.drawable.ic_google)
            tvPaymentName.text = "Google Pay"
            tvPaymentDetail.text = "sela123@gmail.com"
            root.setOnClickListener { selectMethod("Google Pay") }
        }
        binding.payPaypal.apply {
            ivPaymentIcon.setImageResource(R.drawable.ic_paypal)
            tvPaymentName.text = "PayPal"
            tvPaymentDetail.text = "sela123@gmail.com"
            root.setOnClickListener { selectMethod("PayPal") }
        }
        binding.payApple.apply {
            ivPaymentIcon.setImageResource(R.drawable.ic_apple)
            tvPaymentName.text = "Apple Pay"
            tvPaymentDetail.text = "sela123@gmail.com"
            root.setOnClickListener { selectMethod("Apple Pay") }
        }
        binding.payMastercard.apply {
            ivPaymentIcon.setImageResource(R.drawable.ic_mastercard)
            tvPaymentName.text = "Mastercard"
            tvPaymentDetail.text = ".... .... .... 2589"
            root.setOnClickListener { selectMethod("Mastercard") }
        }
        binding.payVisa.apply {
            ivPaymentIcon.setImageResource(R.drawable.ic_visa)
            tvPaymentName.text = "Visa"
            tvPaymentDetail.text = ".... .... .... 5742"
            root.setOnClickListener { selectMethod("Visa") }
        }
        binding.payAdd.apply {
            ivPaymentIcon.setImageResource(R.drawable.ic_plus)
            tvPaymentName.text = "Tambah Metode"
            tvPaymentDetail.text = "Pembayaran Baru"
            root.setOnClickListener { selectMethod("Add") }
        }
    }

    private fun selectMethod(method: String) {
        selectedMethod = method
        val methods = listOf(
            binding.payGoogle, binding.payPaypal, binding.payApple, 
            binding.payMastercard, binding.payVisa, binding.payAdd
        )

        // 1. Reset semua kotak ke tampilan tidak terpilih (Putih)
        methods.forEach { item ->
            item.paymentContainer.setBackgroundColor(getColor(R.color.white))
            item.tvPaymentName.setTextColor(getColor(R.color.black))
            item.tvPaymentDetail.setTextColor(getColor(R.color.gray))
        }

        // 2. Cari kotak mana yang diklik pengguna
        val selected = when(method) {
            "Google Pay" -> binding.payGoogle
            "PayPal" -> binding.payPaypal
            "Apple Pay" -> binding.payApple
            "Mastercard" -> binding.payMastercard
            "Visa" -> binding.payVisa
            else -> binding.payAdd
        }
        
        // 3. Ubah kotak yang dipilih menjadi "Aktif" (Berwarna + Teks Putih)
        // Jika Mastercard kita pakai ungu (premium), jika yang lain kita pakai pink agar konsisten
        val colorRes = if (method == "Mastercard") R.color.premium_center else R.color.pink_main
        
        selected.paymentContainer.setBackgroundColor(getColor(colorRes))
        selected.tvPaymentName.setTextColor(getColor(R.color.white))
        selected.tvPaymentDetail.setTextColor(getColor(R.color.white))
    }
}
