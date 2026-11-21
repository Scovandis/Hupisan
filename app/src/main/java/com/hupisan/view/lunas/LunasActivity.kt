package com.hupisan.view.lunas

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.hupisan.R
import com.hupisan.database.entity.DataModel
import com.hupisan.databinding.ActivityLunasBinding
import com.hupisan.view.lunas.adapter.LunasAdapter
import com.hupisan.viewmodel.DataViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DecimalFormat

class LunasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLunasBinding
    private val viewModel: DataViewModel by viewModel()
    private lateinit var lunasAdapter: LunasAdapter

    private var allLunasList = ArrayList<DataModel>()
    private var activeFilter = FILTER_ALL
    private var searchQuery = ""

    companion object {
        const val FILTER_ALL = 0
        const val FILTER_RECEIVABLE = 1
        const val FILTER_DEBT = 2

        var STATUS_TYPE: String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityLunasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.hupisan_page_bg)
            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        }

        setupToolbar()
        setupFilterTabs()
        setupSearch()
        setupRecyclerView()
        observeData()
    }

    private fun setupToolbar() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupFilterTabs() {
        binding.tabFilterAll.setOnClickListener { updateFilter(FILTER_ALL) }
        binding.tabFilterReceivable.setOnClickListener { updateFilter(FILTER_RECEIVABLE) }
        binding.tabFilterDebt.setOnClickListener { updateFilter(FILTER_DEBT) }
    }

    private fun updateFilter(filterIndex: Int) {
        activeFilter = filterIndex
        val tabs = listOf(
            Pair(binding.tabFilterAll, FILTER_ALL),
            Pair(binding.tabFilterReceivable, FILTER_RECEIVABLE),
            Pair(binding.tabFilterDebt, FILTER_DEBT)
        )

        val activeBg = ContextCompat.getDrawable(this, R.drawable.bg_filter_pill_active)
        val inactiveBg = ContextCompat.getDrawable(this, R.drawable.bg_filter_pill_inactive)
        val whiteColor = ContextCompat.getColor(this, R.color.white)
        val inactiveTextColor = ContextCompat.getColor(this, R.color.hupisan_text_secondary)

        for ((textView, filterType) in tabs) {
            if (filterType == activeFilter) {
                textView.background = activeBg
                textView.setTextColor(whiteColor)
            } else {
                textView.background = inactiveBg
                textView.setTextColor(inactiveTextColor)
            }
        }
        applyFilterAndSearch()
    }

    private fun setupSearch() {
        binding.etSearchLunas.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s?.toString()?.trim() ?: ""
                applyFilterAndSearch()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupRecyclerView() {
        lunasAdapter = LunasAdapter(this)
        binding.rvLunas.apply {
            layoutManager = LinearLayoutManager(this@LunasActivity)
            adapter = lunasAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun observeData() {
        binding.shimmerLunas.startShimmer()
        binding.shimmerLunas.visibility = View.VISIBLE
        binding.rvLunas.visibility = View.GONE
        binding.layoutEmptyLunas.visibility = View.GONE

        viewModel.getAllDataWhereStatusLunas(true).observe(this, Observer { list ->
            binding.shimmerLunas.stopShimmer()
            binding.shimmerLunas.visibility = View.GONE

            allLunasList = ArrayList(list ?: emptyList())
            calculateSummary(allLunasList)
            applyFilterAndSearch()
        })
    }

    private fun calculateSummary(list: List<DataModel>) {
        val totalAmount = list.sumOf { 
            if (it.jumlah_dibayar > 0) it.jumlah_dibayar.toLong() else it.jumlah.toLong()
        }
        val decimalFormat = DecimalFormat("##,###,###")
        binding.tvTotalLunasAmount.text = if (totalAmount > 0) "Rp " + decimalFormat.format(totalAmount) else "Rp 0"
        binding.tvTotalLunasCount.text = getString(R.string.total_catatan_lunas_format, list.size)
    }

    private fun applyFilterAndSearch() {
        var filtered = allLunasList.filter { item ->
            if (searchQuery.isNotEmpty()) {
                item.username.contains(searchQuery, ignoreCase = true)
            } else true
        }

        filtered = when (activeFilter) {
            FILTER_RECEIVABLE -> filtered.filter { it.jenisPinjaman }
            FILTER_DEBT -> filtered.filter { !it.jenisPinjaman }
            else -> filtered
        }

        if (filtered.isEmpty()) {
            binding.rvLunas.visibility = View.GONE
            binding.layoutEmptyLunas.visibility = View.VISIBLE
        } else {
            binding.layoutEmptyLunas.visibility = View.GONE
            binding.rvLunas.visibility = View.VISIBLE
        }
        lunasAdapter.setData(ArrayList(filtered))
    }
}
