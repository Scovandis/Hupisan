package my.notes.HHHH.view.arisan

import android.content.Context
import android.content.Intent
import android.graphics.Color
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
import my.notes.HHHH.viewmodel.ArisanViewModel
import my.notes.HHHH.viewmodel.ArisanGroupViewModel
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import my.notes.HHHH.R
import com.google.android.material.snackbar.Snackbar
import my.notes.HHHH.database.entity.GrupArisanModel
import my.notes.HHHH.databinding.ActivityArisanBinding
import my.notes.HHHH.databinding.AlertDeleteAllBinding
import my.notes.HHHH.databinding.AlertDeleteBinding
import my.notes.HHHH.util.SwipeToDeleteCallback
import my.notes.HHHH.view.arisan.adapter.ArisanGroupAdapter
import my.notes.HHHH.view.main.MainActivity
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
        if (FitureArisanActivity.statusDimulai){
            binding.bottomAppBar.title=getString(R.string.dimulai)
            binding.fabAddGroup.visibility = View.GONE
        }else {
            binding.bottomAppBar.title=getString(R.string.belum_dimulai)
            swipeToDelete()
        }
        setData(true)
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
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val alertBinding : AlertDeleteBinding = AlertDeleteBinding.inflate(inflater)
        dialogBuilder.setView(alertBinding.root)

        alertBinding.tvJudulCatatanAlert.text = item.grup_name
        rulers(alertBinding, item)
        dialogBuilder.setPositiveButton(getString(R.string.hapus)){ dialogInterface, int ->
            val userJudul = item.grup_name
            val etDelete = alertBinding.etHapusAlert.editText!!.text

            if (etDelete.toString() != userJudul){
                Snackbar.make(binding.swipeRefreshLayout, getString(R.string.item_tidak_terhapus), Snackbar.LENGTH_SHORT).show()
            }else{
                alertBinding.etHapusAlert.helperText = getString(R.string.masukkan_nama_yang_sesuai_untuk_menghapus)
                groupArisanVM.deleteGroup(item)
                val snackbar = Snackbar
                    .make(
                        binding.swipeRefreshLayout,
                        getString(R.string.item_telah_di_hapus_dari_daftar),
                        Snackbar.LENGTH_LONG
                    )
                Log.d(TAG, "data $item ")

                userArisanVM.deleteMemberWhereGroupId(item.uid)
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
    private fun setData(isRefresh: Boolean){
        binding.lottieAnimation.visibility = View.GONE
        binding.rvGroupArisan.visibility = View.GONE
        binding.tvIsEmpty.visibility = View.GONE

        if (isRefresh){
            binding.shimmer1.startShimmer()
            binding.shimmer1.visibility = View.VISIBLE

            Handler(Looper.getMainLooper()).postDelayed({
                dataGroup()
            },1000)
        }
    }

    private fun dataGroup(){
        binding.shimmer1.stopShimmer()
        binding.shimmer1.visibility = View.GONE
        Log.d(TAG, "FITUR ARISAN DIMULAI ${FitureArisanActivity.statusDimulai}")
        groupArisanVM.getAllGroupWhereStatus(FitureArisanActivity.statusDimulai).observe(this, Observer {
            Log.d(TAG, "DATA WHERE STATUS FROM GROUP ID $it")
            if (it.isNullOrEmpty()){
                binding.lottieAnimation.visibility = View.VISIBLE
                binding.rvGroupArisan.visibility = View.GONE
                binding.tvIsEmpty.visibility = View.VISIBLE
            }else {
                binding.lottieAnimation.visibility = View.GONE
                binding.rvGroupArisan.visibility = View.VISIBLE
                binding.tvIsEmpty.visibility = View.GONE
                groupAdapter.setData(it as ArrayList<GrupArisanModel>)
            }
        })
        binding.swipeRefreshLayout.isRefreshing = false
        bindRecycler()
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
            startActivity(Intent(this, MainActivity::class.java))
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            setData(isRefresh = true)
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

    private fun alertDialogDeleteAll(){
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val alertBindingDeleteAll : AlertDeleteAllBinding = AlertDeleteAllBinding.inflate(inflater)
        dialogBuilder.setView(alertBindingDeleteAll.root)

        alertBindingDeleteAll.tvTypeCatatan.text = getString(R.string.semua_data)
        alertBindingDeleteAll.tvJudulCatatanAlert.text = getString(R.string.hapus_semua_data)
        alertBindingDeleteAll.tvView1.text = getString(R.string.semua_data)

        rulersDeleteAll(alertBindingDeleteAll)

        val judul = getString(R.string.hapus_semua_data)
        val etEdit = alertBindingDeleteAll.etHapusAlert.editText!!.text

        dialogBuilder.setPositiveButton(getString(R.string.hapus)){dialog, i ->
            Handler(Looper.getMainLooper()).postDelayed({
                if (etEdit.toString() != judul){
                    Snackbar.make(binding.swipeRefreshLayout, getString(R.string.item_tidak_terhapus), Snackbar.LENGTH_SHORT).show()
                }else {
                    groupArisanVM.deleteAllGroup(false)
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
        alertBindingDeleteAll.etHapusAlert.editText!!.addTextChangedListener(object: TextWatcher {
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