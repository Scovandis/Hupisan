package com.hupisan.view.hutang

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.Observer
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.hupisan.viewmodel.DataViewModel
import com.hupisan.viewmodel.PaymentViewModel
import androidx.recyclerview.widget.LinearLayoutManager

import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.appcompat.app.AlertDialog

import androidx.recyclerview.widget.RecyclerView

import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.snackbar.Snackbar
import com.hupisan.R

import com.hupisan.database.entity.DataModel
import com.hupisan.databinding.ActivityHutangBinding
import com.hupisan.databinding.AlertDeleteAllBinding
import com.hupisan.databinding.AlertDeleteBinding
import com.hupisan.util.SwipeToDeleteCallback
import com.hupisan.view.hutang.adapter.HutangAdapter
import com.hupisan.view.hutang.adapter.onButtonClicked
import com.hupisan.view.main.MainActivity


class HutangActivity : AppCompatActivity(), onButtonClicked {
    private val TAG = HutangActivity::class.java.simpleName

    private lateinit var binding: ActivityHutangBinding

    lateinit var hAdapter: HutangAdapter

    val viewModel: DataViewModel by viewModel()
    val paymentViewModel: PaymentViewModel by viewModel()
    var isRefresh: Boolean = true
    var typeJenis: String? = null
    private var activeFilter = 0 // 0: Semua, 1: Belum Lunas, 2: Sebagian, 3: Lunas
    private var searchQuery = ""

    private var listData: ArrayList<DataModel>?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHutangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lottieAnimation.visibility = View.GONE
        binding.rvHutang.visibility = View.GONE

        typeJenis = intent.getStringExtra(EXTRA_TYPE_JENIS) ?: getString(R.string.hutang)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.hupisan_page_bg)
        }
        binding.swipeRefreshLayout.setColorSchemeColors(
            ContextCompat.getColor(this, R.color.hupisan_primary_dark),
            ContextCompat.getColor(this, R.color.hupisan_accent_amber)
        )

        supportActionBar?.hide()

        binding.tvToolbarTitle.text = typeJenis
        binding.tvSummaryLabel.text = "Total $typeJenis"

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnFilterAction.setOnClickListener {
            alertDeleteAll()
        }

        setupFilterTabs()
        setupSearch()

        binding.fabAddAction.setOnClickListener {
            val intent = Intent(this, AddHutangActivity::class.java)
            intent.putExtra(AddHutangActivity.EXTRA_STRING_JENIS, typeJenis)
            startActivity(intent)
        }
        
        hAdapter = HutangAdapter(this@HutangActivity, this)
        bindview()
        observeData()

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setupFilterTabs() {
        binding.tabFilterAll.setOnClickListener { updateFilter(0) }
        binding.tabFilterUnpaid.setOnClickListener { updateFilter(1) }
        binding.tabFilterPartial.setOnClickListener { updateFilter(2) }
        binding.tabFilterPaid.setOnClickListener { updateFilter(3) }
    }

    private fun updateFilter(filterIndex: Int) {
        activeFilter = filterIndex
        val tabs = listOf(
            binding.tabFilterAll,
            binding.tabFilterUnpaid,
            binding.tabFilterPartial,
            binding.tabFilterPaid
        )
        tabs.forEachIndexed { index, textView ->
            if (index == activeFilter) {
                textView.setBackgroundResource(R.drawable.bg_filter_pill_active)
                textView.setTextColor(Color.WHITE)
            } else {
                textView.setBackgroundResource(R.drawable.bg_filter_pill_inactive)
                textView.setTextColor(ContextCompat.getColor(this, R.color.hupisan_text_secondary))
            }
        }
        applyFilterAndSearch()
    }

    private fun setupSearch() {
        binding.etSearchDebt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s?.toString()?.trim() ?: ""
                applyFilterAndSearch()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun applyFilterAndSearch() {
        val originalList = listData ?: return
        var filtered = originalList.filter { item ->
            if (searchQuery.isNotEmpty()) {
                item.username.contains(searchQuery, ignoreCase = true)
            } else true
        }

        filtered = when (activeFilter) {
            1 -> filtered.filter { !it.statusDataSelesai }
            2 -> filtered.filter { !it.statusDataSelesai } // Sebagian / ongoing
            3 -> filtered.filter { it.statusDataSelesai }
            else -> filtered
        }

        if (filtered.isEmpty()) {
            binding.rvHutang.visibility = View.GONE
            binding.lottieAnimation.visibility = View.VISIBLE
            binding.tvIsEmpty.visibility = View.VISIBLE
        } else {
            binding.lottieAnimation.visibility = View.GONE
            binding.tvIsEmpty.visibility = View.GONE
            binding.rvHutang.visibility = View.VISIBLE
        }
        hAdapter.setData(ArrayList(filtered))
    }

    private fun observeData() {
        binding.shimmer1.startShimmer()
        binding.shimmer1.visibility = View.VISIBLE
        binding.rvHutang.visibility = View.GONE
        binding.lottieAnimation.visibility = View.GONE
        binding.tvIsEmpty.visibility = View.GONE

        val isPiutang = typeJenis == getString(R.string.piutang)
        viewModel.getAllDataWhereStatusData(statusData = false, jenis = isPiutang).observe(this, Observer {
            binding.shimmer1.stopShimmer()
            binding.shimmer1.visibility = View.GONE
            binding.swipeRefreshLayout.isRefreshing = false

            val decimalFormat = java.text.DecimalFormat("##,###,###")
            val totalSum = it.sumOf { item -> item.jumlah }
            binding.tvTotalSummaryAmount.text = if (totalSum > 0) "Rp " + decimalFormat.format(totalSum) else "Rp 0"

            if (it.isEmpty()) {
                binding.rvHutang.visibility = View.GONE
                binding.lottieAnimation.visibility = View.VISIBLE
                binding.tvIsEmpty.visibility = View.VISIBLE
                binding.tvIsEmpty.text = getString(R.string.oops_sepertinya_anda_belum_memiliki_rekap_data)
                listData = ArrayList()
                hAdapter.setData(listData!!)
            } else {
                binding.lottieAnimation.visibility = View.GONE
                binding.tvIsEmpty.visibility = View.GONE
                binding.rvHutang.visibility = View.VISIBLE
                listData = ArrayList(it)
                hAdapter.setData(listData!!)
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.delete_all -> {
                alertDeleteAll()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun alertDeleteAll(){
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val alertBindingDeleteAll : AlertDeleteAllBinding = AlertDeleteAllBinding.inflate(inflater)
        dialogBuilder.setView(alertBindingDeleteAll.root)

        alertBindingDeleteAll.tvTypeCatatan.text = getString(R.string.semua_data)+ " " + typeJenis
        alertBindingDeleteAll.tvJudulCatatanAlert.text = getString(R.string.hapus_semua_data)
        alertBindingDeleteAll.tvView1.text = getString(R.string.semua_data)+ " " + typeJenis

        rulersDeleteAll(alertBindingDeleteAll)

        val judul = getString(R.string.hapus_semua_data)
        val etEdit = alertBindingDeleteAll.etHapusAlert.editText!!.text

        dialogBuilder.setPositiveButton(getString(R.string.hapus)){dialog, i ->
            Handler(Looper.getMainLooper()).postDelayed({
                if (etEdit.toString() != judul){
                    Snackbar.make(binding.swipeRefreshLayout, getString(R.string.item_tidak_terhapus), Snackbar.LENGTH_SHORT).show()
                }else {
                    val jenis: Boolean = !typeJenis.equals(getString(R.string.hutang))
                    viewModel.deleteAllDataWhere(jenis, false)
                    Snackbar.make(binding.swipeRefreshLayout, getString(R.string.data_berhasil_dihapus), Snackbar.LENGTH_SHORT).show()
                }
            },700)
            dialog.cancel()
        }
        dialogBuilder.setNegativeButton(getString(R.string.kembali)){dialog, i->
            dialog.dismiss()

        }
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }
    private fun rulersDeleteAll(alertBindingDeleteAll: AlertDeleteAllBinding){
        alertBindingDeleteAll.etHapusAlert.editText!!.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val etDelete = alertBindingDeleteAll.etHapusAlert.editText!!.text

                if (p0!!.length >= 20){
                    alertBindingDeleteAll.etHapusAlert.error = getString(R.string.nama_tidak_boleh_lebih_dari_20_huruf)
                }else if(p0.length > 2 && etDelete.toString() != getString(R.string.hapus_semua_data)){
                    alertBindingDeleteAll.etHapusAlert.error = getString(R.string.sepertinya_nama_yang_anda_masukkan_berbeda_dari_daftar_item_ini)
                }else if(etDelete.toString() == getString(R.string.hapus_semua_data)){
                    alertBindingDeleteAll.etHapusAlert.helperText = getString(R.string.benar)
                }
                else if(p0.isBlank() or p0.isEmpty()){
                    alertBindingDeleteAll.etHapusAlert.error = getString(R.string.nama_tidak_boleh_kosong)
                }
                else {
                    alertBindingDeleteAll.etHapusAlert.helperText = getString(R.string.masukkan_nama_yang_sesuai_untuk_menghapus)
                }
            }
        })
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_delete, menu)
        val item = menu!!.findItem(R.id.delete_all)
        if (MainActivity.ISNULL == 1){
            item.isVisible = true
        }
        return super.onCreateOptionsMenu(menu)
    }

    fun bindview(){
        binding.rvHutang.apply {
            layoutManager = LinearLayoutManager(this@HutangActivity, LinearLayoutManager.VERTICAL, false)
            adapter = hAdapter
        }
        swipeToDelete()
    }
    private fun swipeToDelete(){
        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val item = hAdapter.getData().get(position)
                alertDelete(position, item)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.rvHutang)
    }
    override fun onResume() {
        super.onResume()
        binding.shimmer1.startShimmer()
    }
    override fun onPause() {
        binding.shimmer1.stopShimmer()
        super.onPause()
    }
    private fun alertDelete(position: Int, item: DataModel) {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val alertBinding : AlertDeleteBinding = AlertDeleteBinding.inflate(inflater)
        dialogBuilder.setView(alertBinding.root)

        alertBinding.tvJudulCatatanAlert.text = item.username
        rulers(alertBinding, item)
        dialogBuilder.setPositiveButton(getString(R.string.hapus)){ dialogInterface, int ->
            val userJudul = item.username
            val etDelete = alertBinding.etHapusAlert.editText!!.text

            if (etDelete.toString() != userJudul){
                Snackbar.make(binding.swipeRefreshLayout, getString(R.string.item_tidak_terhapus), Snackbar.LENGTH_SHORT).show()
            }else{
                alertBinding.etHapusAlert.helperText = getString(R.string.masukkan_nama_yang_sesuai_untuk_menghapus)
                viewModel.deleteData(item)
                val snackbar = Snackbar
                    .make(
                        binding.swipeRefreshLayout,
                        getString(R.string.item_telah_di_hapus_dari_daftar),
                        Snackbar.LENGTH_LONG
                    )
                paymentViewModel.deleteDataFormUid(item.id)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    snackbar.setActionTextColor(getColor(R.color.purple_500))
                }else{
                    snackbar.setActionTextColor(Color.YELLOW)
                }
                snackbar.show()
            }
        }
        dialogBuilder.setNegativeButton(getString(R.string.kembali)){ dialogInterface, int -> dialogInterface.dismiss()}

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    private fun rulers(alertBinding: AlertDeleteBinding, item: DataModel) {
        alertBinding.etHapusAlert.editText!!.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val userJudul = item.username
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
    override fun onClick(data: DataModel) {
        viewModel.updateData(data)
    }

    companion object {
        const val EXTRA_TYPE_JENIS = "extra_type_jenis"
    }
}