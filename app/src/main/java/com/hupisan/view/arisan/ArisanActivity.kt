package com.hupisan.view.arisan

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.hupisan.viewmodel.ArisanViewModel
import com.hupisan.viewmodel.ArisanGroupViewModel
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hupisan.R
import com.google.android.material.snackbar.Snackbar
import com.hupisan.database.entity.GrupArisanModel
import com.hupisan.databinding.ActivityArisanBinding
import com.hupisan.databinding.AlertDeleteAllBinding
import com.hupisan.databinding.AlertDeleteBinding
import com.hupisan.util.SwipeToDeleteCallback
import com.hupisan.view.arisan.adapter.ArisanGroupAdapter
import com.hupisan.view.main.MainActivity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ArisanActivity : AppCompatActivity() {
    lateinit var binding: ActivityArisanBinding
    private val TAG = ArisanActivity::class.java.simpleName
    val groupArisanVM: ArisanGroupViewModel by viewModel()
    lateinit var groupAdapter: ArisanGroupAdapter
    val userArisanVM: ArisanViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArisanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        groupAdapter = ArisanGroupAdapter(this)


        firstView()
        setSupportActionBar(binding.bottomAppBar)
        bindRecycler()
        if (FitureArisanActivity.statusDimulai){
            binding.bottomAppBar.title=getString(R.string.dimulai)
            binding.fabAddGroup.visibility = View.GONE
        }else {
            binding.bottomAppBar.title=getString(R.string.belum_dimulai)
            swipeToDelete()
        }
        observeData()
        setActionView()
    }
    private fun bindRecycler(){
        binding.rvGroupArisan.apply {
            layoutManager = LinearLayoutManager(this@ArisanActivity, LinearLayoutManager.VERTICAL, false)
            adapter = groupAdapter
        }
    }
    private fun swipeToDelete(){
        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val item = groupAdapter.getData().get(position)
                alertDelete(position, item)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.rvGroupArisan)
    }
    private fun rulers(alertBinding: AlertDeleteBinding, item: GrupArisanModel) {
        alertBinding.etHapusAlert.editText!!.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val userJudul = item.grup_name
                val etDelete = alertBinding.etHapusAlert.editText!!.text

                if (p0!!.length >= 20){
                    alertBinding.etHapusAlert.error = getString(R.string.nama_tidak_boleh_lebih_dari_20_huruf)
                }else if(p0.length > 2 && etDelete.toString() != userJudul){
                    alertBinding.etHapusAlert.error = getString(R.string.sepertinya_nama_yang_anda_masukkan_berbeda_dari_daftar_item_ini)
                }else if(etDelete.toString() == userJudul){
                    alertBinding.etHapusAlert.helperText = getString(R.string.benar)
                }
                else if(p0.isBlank() or p0.isEmpty()){
                    alertBinding.etHapusAlert.error = getString(R.string.nama_tidak_boleh_kosong)
                }
                else {
                    alertBinding.etHapusAlert.helperText = getString(R.string.masukkan_nama_yang_sesuai_untuk_menghapus)
                }
            }
        })
    }
    private fun alertDelete(position: Int, item: GrupArisanModel) {
        val alertBinding = AlertDeleteBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(this)
            .setView(alertBinding.root)
            .create()

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val userJudul = item.grup_name
        alertBinding.tvStatusType.text = "${getString(R.string.nama_group)}:"
        alertBinding.tvJudulCatatanAlert.text = userJudul
        rulers(alertBinding, item)

        alertBinding.btnBatal.setOnClickListener {
            alertDialog.dismiss()
        }

        alertBinding.btnHapus.setOnClickListener {
            val etDelete = alertBinding.etHapusAlert.editText?.text?.toString() ?: ""

            if (etDelete != userJudul) {
                Snackbar.make(binding.swipeRefreshLayout, getString(R.string.item_tidak_terhapus), Snackbar.LENGTH_SHORT).show()
            } else {
                alertBinding.etHapusAlert.helperText = getString(R.string.masukkan_nama_yang_sesuai_untuk_menghapus)
                groupArisanVM.deleteGroup(item)
                val snackbar = Snackbar.make(
                    binding.swipeRefreshLayout,
                    getString(R.string.item_telah_di_hapus_dari_daftar),
                    Snackbar.LENGTH_LONG
                )
                Log.d(TAG, "data $item ")

                userArisanVM.deleteMemberWhereGroupId(item.uid)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    snackbar.setActionTextColor(getColor(R.color.purple_500))
                } else {
                    snackbar.setActionTextColor(Color.YELLOW)
                }
                snackbar.show()
                alertDialog.dismiss()
            }
        }

        alertDialog.show()
    }
    private fun observeData() {
        firstView()
        groupArisanVM.getAllGroupWhereStatus(FitureArisanActivity.statusDimulai).observe(this, Observer {
            binding.shimmer1.stopShimmer()
            binding.shimmer1.visibility = View.GONE
            binding.swipeRefreshLayout.isRefreshing = false

            if (it.isNullOrEmpty()){
                binding.lottieAnimation.visibility = View.VISIBLE
                binding.rvGroupArisan.visibility = View.GONE
                binding.tvIsEmpty.visibility = View.VISIBLE
                groupAdapter.setData(ArrayList())
            }else {
                binding.lottieAnimation.visibility = View.GONE
                binding.rvGroupArisan.visibility = View.VISIBLE
                binding.tvIsEmpty.visibility = View.GONE
                groupAdapter.setData(ArrayList(it))
            }
        })
    }
    private fun firstView(){
        //visible & start shimmer
        binding.shimmer1.startShimmer()
        binding.shimmer1.visibility = View.VISIBLE

        //invisible recyclerveiw and more
        binding.lottieAnimation.visibility = View.GONE
        binding.rvGroupArisan.visibility = View.GONE
        binding.tvIsEmpty.visibility = View.GONE
    }

    private fun setActionView(){
        binding.fabAddGroup.setColorFilter(Color.parseColor("#FF6200EE"))

        binding.fabAddGroup.setOnClickListener {
            val intent = Intent(this, AddArisanActivity::class.java)
            intent.putExtra(AddArisanActivity.EXTRA_VALUE, 0)
            startActivity(intent)
        }

        binding.bottomAppBar.setOnClickListener {
            finish()
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.menu_home -> {
                FitureArisanActivity.DATA_ISNULL = 0
                startActivity(Intent(this, FitureArisanActivity::class.java))
                return true
            }
            R.id.menu_delete_all -> {
                alertDialogDeleteAll()
                return true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun alertDialogDeleteAll() {
        val alertBinding = AlertDeleteAllBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(this)
            .setView(alertBinding.root)
            .create()

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val confirmKeyword = getString(R.string.hapus_semua_data)
        alertBinding.tvSubtitle.text = getString(R.string.ketik_konfirmasi_hapus_semua_format, confirmKeyword)
        alertBinding.tvCounter.text = "0/${confirmKeyword.length}"

        alertBinding.etConfirmDelete.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s?.toString() ?: ""
                alertBinding.tvCounter.text = "${input.length}/${confirmKeyword.length}"
                if (input.equals(confirmKeyword, ignoreCase = true)) {
                    alertBinding.tvErrorHelper.visibility = View.GONE
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        alertBinding.btnBatal.setOnClickListener {
            alertDialog.dismiss()
        }

        alertBinding.btnHapus.setOnClickListener {
            val input = alertBinding.etConfirmDelete.text?.toString()?.trim() ?: ""
            if (!input.equals(confirmKeyword, ignoreCase = true)) {
                alertBinding.tvErrorHelper.visibility = View.VISIBLE
                alertBinding.tvErrorHelper.text = getString(R.string.sepertinya_nama_yang_anda_masukkan_berbeda_dari_daftar_item_ini)
                Snackbar.make(binding.swipeRefreshLayout, getString(R.string.item_tidak_terhapus), Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            alertDialog.dismiss()
            groupArisanVM.deleteAllGroup(false)
            Snackbar.make(binding.swipeRefreshLayout, getString(R.string.data_berhasil_dihapus), Snackbar.LENGTH_SHORT).show()
        }

        alertDialog.show()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.bottom_menu, menu)
        val itemDeleteAll = menu!!.findItem(R.id.menu_delete_all)
        if (FitureArisanActivity.DATA_ISNULL == 1){
            itemDeleteAll.isVisible = true
        }

        if (FitureArisanActivity.statusDimulai){
            itemDeleteAll.isVisible = true
        }

        return true
    }
}