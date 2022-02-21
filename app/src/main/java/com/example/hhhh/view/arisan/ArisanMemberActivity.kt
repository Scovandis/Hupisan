package com.example.hhhh.view.arisan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.hhhh.R
import com.example.hhhh.database.ArisanViewModel
import com.example.hhhh.database.entity.ArisanModel
import com.example.hhhh.databinding.ActivityArisanMemberBinding
import com.example.hhhh.view.arisan.adapter.ArisanMemeberAdapter

class ArisanMemberActivity : AppCompatActivity() {
    private val TAG = ArisanMemberActivity::class.java.simpleName
    private lateinit var binding: ActivityArisanMemberBinding
    lateinit var memberVM: ArisanViewModel
    lateinit var memberAdapter: ArisanMemeberAdapter


    var isRefres: Boolean = true
    private var groupId: Int? = null
    private var groupName: String? = null
    private var groupAnggaran: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArisanMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar = binding.animToolbar
        setSupportActionBar(toolbar)
        val collapsingToolbar = binding.collapsingToolbar
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        memberVM = ViewModelProvider(this).get(ArisanViewModel::class.java)
        memberAdapter = ArisanMemeberAdapter(this)
        groupId = intent.extras!!.get(EXTRA_GROUP_ID).toString().toInt()
        groupName = intent.extras!!.get(EXTRA_GROUP_NAME).toString()
        groupAnggaran = intent.extras!!.get(EXTRA_GROUP_ANGGARAN).toString().toInt()
        Log.d(TAG, "EXTRASSS ::: $groupId")

        collapsingToolbar.title = groupName

        setFirstView()
        setActionView()
        setDataView(true)
    }
    private fun setDataView(isRefresh: Boolean){
        binding.lottieAnimation.visibility = View.GONE
        binding.rvMemberArisan.visibility = View.GONE
        binding.tvIsEmpty.visibility = View.GONE
        if (isRefresh){
//            binding.shimmer1.visibility = View.VISIBLE
//            binding.shimmer1.startShimmer()
            Handler(Looper.getMainLooper()).postDelayed({
//                binding.shimmer1.stopShimmer()
//                binding.shimmer1.visibility = View.GONE
                setData()
                binding.swipeRefreshLayout.isRefreshing = false
            }, 1000)

        }
    }

    private fun setData(){
        memberVM.getAllMemberFromGroupId(groupId!!.toInt()).observe(this, Observer {
            Log.d(TAG, "DATA ALL MEMBER FROM GROUP ID $it")
            if (it.isNullOrEmpty()){
                binding.lottieAnimation.visibility = View.VISIBLE
                binding.tvIsEmpty.visibility = View.VISIBLE
                binding.rvMemberArisan.visibility = View.GONE
            }else {
                binding.lottieAnimation.visibility = View.GONE
                binding.tvIsEmpty.visibility = View.GONE
                binding.rvMemberArisan.visibility = View.VISIBLE
                memberAdapter.setData(it as ArrayList<ArisanModel>)
            }
        })
        setRecyclerView()
    }
    private fun setRecyclerView(){
        binding.rvMemberArisan.apply {
            layoutManager = GridLayoutManager(this@ArisanMemberActivity, 3, GridLayoutManager.VERTICAL, false)
            adapter = memberAdapter
        }
    }

    override fun onPause() {
//        binding.shimmer1.stopShimmer()
        super.onPause()
    }
    override fun onResume() {
        super.onResume()
//        binding.shimmer1.startShimmer()
    }
    private fun setActionView(){
        binding.fabAddMember.setOnClickListener {
            val intent = Intent(this, AddMemberActivity::class.java)
            intent.putExtra(AddMemberActivity.EXTRA_GROUP_ID, groupId)
            intent.putExtra(AddMemberActivity.EXTRA_GROUP_NAME, groupName)
            intent.putExtra(AddMemberActivity.EXTRA_NOMINAL_PEMBAYARAN, groupAnggaran)
            startActivity(intent)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            setDataView(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                startActivity(Intent(this, ArisanActivity::class.java))
                return true
            }else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
    private fun setFirstView(){
//        binding.shimmer1.startShimmer()
//        binding.shimmer1.visibility = View.VISIBLE

        binding.tvIsEmpty.visibility = View.GONE
        binding.lottieAnimation.visibility = View.GONE
        binding.rvMemberArisan.visibility = View.GONE
    }
    companion object{
        val EXTRA_GROUP_ID = "extra_group_id"
        val EXTRA_GROUP_NAME = "extra_group_name"
        val EXTRA_GROUP_ANGGARAN = "extra_group_anggaran"
    }
}