package com.cookieeditor.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.webkit.CookieManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookieeditor.app.databinding.ActivityCookieEditorBinding
import com.cookieeditor.app.databinding.DialogEditCookieBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CookieEditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCookieEditorBinding
    private lateinit var cookieManager: CookieManager
    private lateinit var adapter: CookieAdapter
    private var currentUrl: String = ""
    private var currentDomain: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCookieEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Cookie Editor"

        cookieManager = CookieManager.getInstance()
        currentUrl = intent.getStringExtra("url") ?: ""
        currentDomain = extractDomain(currentUrl)

        binding.tvDomain.text = currentDomain.ifEmpty { currentUrl }

        adapter = CookieAdapter(
            onEdit = { cookie -> showEditDialog(cookie) },
            onDelete = { cookie -> deleteCookie(cookie) }
        )

        binding.rvCookies.layoutManager = LinearLayoutManager(this)
        binding.rvCookies.adapter = adapter

        binding.btnRefresh.setOnClickListener { loadCookies() }
        binding.btnAdd.setOnClickListener { showEditDialog(null) }

        loadCookies()
    }

    private fun extractDomain(url: String): String {
        return try {
            val uri = android.net.Uri.parse(url)
            uri.host ?: url
        } catch (e: Exception) {
            url
        }
    }

    private fun loadCookies() {
        val cookieString = cookieManager.getCookie(currentUrl)
        val cookies = mutableListOf<CookieItem>()

        if (!cookieString.isNullOrBlank()) {
            cookieString.split(";").forEach { part ->
                val trimmed = part.trim()
                if (trimmed.isNotEmpty()) {
                    val eqIndex = trimmed.indexOf('=')
                    if (eqIndex > 0) {
                        val name = trimmed.substring(0, eqIndex).trim()
                        val value = trimmed.substring(eqIndex + 1).trim()
                        cookies.add(
                            CookieItem(
                                name = name,
                                value = value,
                                domain = currentDomain,
                                path = "/"
                            )
                        )
                    }
                }
            }
        }

        adapter.submitList(cookies)

        if (cookies.isEmpty()) {
            binding.rvCookies.visibility = View.GONE
            binding.tvEmpty.visibility = View.VISIBLE
        } else {
            binding.rvCookies.visibility = View.VISIBLE
            binding.tvEmpty.visibility = View.GONE
        }
    }

    private fun showEditDialog(cookie: CookieItem?) {
        val dialogBinding = DialogEditCookieBinding.inflate(LayoutInflater.from(this))

        if (cookie != null) {
            dialogBinding.etName.setText(cookie.name)
            dialogBinding.etValue.setText(cookie.value)
            dialogBinding.etDomain.setText(cookie.domain)
            dialogBinding.etPath.setText(cookie.path)
            dialogBinding.etName.isEnabled = false // Name usually shouldn't change when editing
        } else {
            dialogBinding.etDomain.setText(currentDomain)
            dialogBinding.etPath.setText("/")
        }

        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(if (cookie == null) "Add Cookie" else "Edit Cookie")
            .setView(dialogBinding.root)
            .setPositiveButton(getString(R.string.save), null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val name = dialogBinding.etName.text.toString().trim()
                val value = dialogBinding.etValue.text.toString().trim()
                val domain = dialogBinding.etDomain.text.toString().trim()
                val path = dialogBinding.etPath.text.toString().trim().ifEmpty { "/" }

                if (name.isEmpty()) {
                    dialogBinding.etName.error = "Name required"
                    return@setOnClickListener
                }

                // Build cookie string
                // Format: name=value; Domain=domain; Path=path
                val cookieValue = buildString {
                    append("$name=$value")
                    if (domain.isNotEmpty()) append("; Domain=$domain")
                    append("; Path=$path")
                }

                cookieManager.setCookie(currentUrl, cookieValue)
                cookieManager.flush()

                Toast.makeText(this, getString(R.string.cookie_saved), Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                loadCookies()
            }
        }

        dialog.show()
    }

    private fun deleteCookie(cookie: CookieItem) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Delete Cookie")
            .setMessage("Delete cookie \"${cookie.name}\"?")
            .setPositiveButton("Delete") { _, _ ->
                // To delete a cookie, set it with Max-Age=0 or expires in the past
                val deleteValue = "${cookie.name}=; Domain=${cookie.domain}; Path=${cookie.path}; Max-Age=0"
                cookieManager.setCookie(currentUrl, deleteValue)
                cookieManager.flush()
                Toast.makeText(this, getString(R.string.cookie_deleted), Toast.LENGTH_SHORT).show()
                loadCookies()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
