package com.example.trackersiklusmenstruasi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.trackersiklusmenstruasi.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupClickListeners()
        setupMenuButton()
    }

    private fun setupMenuButton() {
        binding.btnMenu.setOnClickListener {
            val popup = androidx.appcompat.widget.PopupMenu(requireContext(), it)
            popup.menu.add("Bantuan")
            popup.setOnMenuItemClickListener { item ->
                if (item.title == "Bantuan") {
                    startActivity(Intent(requireContext(), HelpActivity::class.java))
                }
                true
            }
            popup.show()
        }
    }

    override fun onResume() {
        super.onResume()
        setupProfileInfo()
        loadProfileImage()
    }

    private fun loadProfileImage() {
        try {
            val file = java.io.File(requireContext().filesDir, "profile_pic_final.jpg")
            if (file.exists()) {
                val bitmap = android.graphics.BitmapFactory.decodeFile(file.absolutePath)
                binding.ivAvatar.setImageBitmap(bitmap)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupProfileInfo() {
        val dbHelper = DatabaseHelper.getInstance(requireContext())
        val profile = dbHelper.getUserProfile()
        
        profile?.let {
            binding.tvProfileTitle.text = it.name
            binding.tvName.text = it.name
            binding.tvEmail.text = "${it.name.lowercase().replace(" ", "")}@gmail.com"
        }
        
        // Setup Settings Items
        binding.itemSettings.tvTitle.text = "Pengaturan"
        binding.itemSettings.ivIcon.setImageResource(R.drawable.ic_settings)
        
        binding.itemReminder.tvTitle.text = "Peringatan Pengingat"
        binding.itemReminder.ivIcon.setImageResource(R.drawable.ic_volume_up)
        
        binding.itemAccountSecurity.tvTitle.text = "Akun & Keamanan"
        binding.itemAccountSecurity.ivIcon.setImageResource(R.drawable.ic_lock)
        
        binding.itemConnectedAccount.tvTitle.text = "Akun Terhubung"
        binding.itemConnectedAccount.ivIcon.setImageResource(R.drawable.ic_user)
        
        binding.itemPaymentMethod.tvTitle.text = "Metode Pembayaran"
        binding.itemPaymentMethod.ivIcon.setImageResource(R.drawable.ic_visa)
        
        binding.itemDataAnalysis.tvTitle.text = "Data & Analisis"
        binding.itemDataAnalysis.ivIcon.setImageResource(R.drawable.ic_bar_chart)
        
        binding.itemLogout.tvTitle.text = "Keluar"
        binding.itemLogout.ivIcon.setImageResource(R.drawable.ic_close)
        binding.itemLogout.tvTitle.setTextColor(android.graphics.Color.RED)
    }

    private fun setupClickListeners() {
        // Langsung ke Pengaturan Umum (Bahasa, Satuan, dll)
        binding.itemSettings.root.setOnClickListener {
            startActivity(Intent(requireContext(), GeneralSettingsActivity::class.java))
        }

        binding.itemReminder.root.setOnClickListener {
            startActivity(Intent(requireContext(), ReminderAlertActivity::class.java))
        }

        binding.itemAccountSecurity.root.setOnClickListener {
            startActivity(Intent(requireContext(), AccountSecurityActivity::class.java))
        }

        binding.itemConnectedAccount.root.setOnClickListener {
            startActivity(Intent(requireContext(), ConnectedAccountActivity::class.java))
        }

        binding.itemPaymentMethod.root.setOnClickListener {
            startActivity(Intent(requireContext(), BillingMethodsActivity::class.java))
        }

        binding.itemDataAnalysis.root.setOnClickListener {
            startActivity(Intent(requireContext(), CycleHistoryActivity::class.java))
        }

        binding.bannerPremium.setOnClickListener {
            startActivity(Intent(requireContext(), UnlockPremiumActivity::class.java))
        }

        binding.itemLogout.root.setOnClickListener {
            showLogoutDialog()
        }

        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(requireContext(), PersonalDataActivity::class.java))
        }
    }

    private fun showLogoutDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_logout, null)
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<android.view.View>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<android.view.View>(R.id.btnLogout).setOnClickListener {
            dialog.dismiss()
            SessionManager(requireContext()).logout()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
