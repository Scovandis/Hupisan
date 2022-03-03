package my.notes.HHHH.view.hutang

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import my.notes.HHHH.R
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import my.notes.HHHH.database.entity.DataPembayaranModel
import my.notes.HHHH.databinding.ActivityDetailHutangBinding
import my.notes.HHHH.databinding.AlertDicicilBinding
import my.notes.HHHH.view.hutang.adapter.PembayaranAdapter
import my.notes.HHHH.view.lunas.LunasActivity
import my.notes.HHHH.view.lunas.WindowFragmentLunas
import my.notes.HHHH.view.main.MainActivity
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


class DetailHutangActivity : AppCompatActivity() {
    private val TAG = DetailHutangActivity::class.java.simpleName
    private lateinit var binding: ActivityDetailHutangBinding

    private var typeJenis: Boolean? = null

    private lateinit var viewModel: my.notes.HHHH.database.DataViewModel
    private lateinit var pembayaranViewModel: my.notes.HHHH.database.PembayaranViewModel
    private var jenis: String? = null
    private lateinit var pembayaranAdapter: PembayaranAdapter

    private lateinit var ivAvatar: ImageView
    private lateinit var ivAvatarGender: ImageView

    private var EXPAND_AVATAR_SIZE: Float = 0F
    private var COLLAPSE_IMAGE_SIZE: Float = 0F
    private var cashCollapseState: Pair<Int, Int>? = null
    private var margin: Float = 0F
    private lateinit var toolbar: Toolbar
    private lateinit var appBarLayout: AppBarLayout

    private lateinit var titleToolbarText: AppCompatTextView
    private lateinit var titleToolbarTextSingle: AppCompatTextView
    private lateinit var collapsingAvatarContainer: FrameLayout
    private lateinit var background: FrameLayout

    private var activityJumlah = 0
    private var activityDibayar = 0

    private val rotateOpen : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim) }
    private val rotateClose : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim) }
    private val fromBottom : Animation by lazy{ AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim)}
    private val toBottom : Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim)}
    private var closed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailHutangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pembayaranViewModel = ViewModelProvider(this).get(my.notes.HHHH.database.PembayaranViewModel::class.java)
        viewModel = ViewModelProvider(this).get(my.notes.HHHH.database.DataViewModel::class.java)

        //bindview
        EXPAND_AVATAR_SIZE = resources.getDimension(R.dimen.default_expanded_image_size)
        COLLAPSE_IMAGE_SIZE = resources.getDimension(R.dimen.default_collapsed_image_size)
        margin = resources.getDimension(R.dimen.avatar_margin)
        collapsingAvatarContainer = binding.stuffContainer
        appBarLayout = binding.appBarLayout
        toolbar = binding.animToolbar
        toolbar.visibility = View.INVISIBLE
        ivAvatar = binding.imgbAvatarWrap
        ivAvatarGender = binding.imgbAvatarGender

        //color verivication
        val option = RequestOptions()
            .centerCrop()
            .placeholder(ColorDrawable(Color.BLUE))
        Glide.with(this).load("").apply(option).into(binding.content.ivVerification)
        binding.content.ivVerificationIcon.setColorFilter(Color.WHITE)
        //verification visible GONE
        binding.content.ivVerification.visibility = View.GONE
        binding.content.ivVerificationIcon.visibility = View.GONE

        //Fab action setting color
        titleToolbarText = binding.tvProfileName
        titleToolbarTextSingle = binding.tvProfileNameSingle
        background = binding.flBackground
        binding.fabTambahkan.setColorFilter(Color.WHITE)
        binding.fabLunas.setColorFilter(Color.WHITE)
        binding.fabCicil.setColorFilter(Color.WHITE)
        binding.optionF.setColorFilter(Color.parseColor("#FF6200EE"))

        //get id && typejenis data
        val uid = intent.extras?.get(EXTRA_UID)
        val jenis = intent.extras?.get(EXTRA_TYPE_JENIS).toString()
        typeJenis = jenis.toBoolean()
        binding.content.tvJumlahStatus.text = getString(R.string.jumlah) + " "+ typeJenis
        setData(uid.toString())


        //appbar animation
        appBarLayout.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { appBarLayout, i ->
                if (isCalculated.not()) {
                    startAvatarAnimatePointY =
                        Math.abs((appBarLayout.height - EXPAND_AVATAR_SIZE - toolbar.height / 2) / appBarLayout.totalScrollRange)
                    animateWeigt = 1 / (1 - startAvatarAnimatePointY)
                    isCalculated = true
                }

                val offset = Math.abs(i / appBarLayout.totalScrollRange.toFloat())
                updateViews(offset)
            })

        onActionListener()
        pembayaranAdapter = PembayaranAdapter(this)
        binding.content.rvDataPembayaranFromId.visibility = View.INVISIBLE
        binding.content.rlTextNull.visibility = View.INVISIBLE
        Handler(Looper.getMainLooper()).postDelayed(
            {
                binding.content.shimmer1.stopShimmer()
                binding.content.shimmer1.visibility = View.GONE

                setListPembayaran(uid.toString().toInt())
                bindRv()
            }, 900
        )

    }

    override fun onPause() {
        super.onPause()
        binding.content.shimmer1.stopShimmer()
    }

    override fun onResume() {
        binding.content.shimmer1.startShimmer()
        super.onResume()
    }

    private fun setListPembayaran(uid: Int) {
        pembayaranViewModel.getDataPembayaranFromUid(uid).observe(this, Observer {
            if (it != null){
                binding.content.rlTextNull.visibility = View.GONE
                binding.content.rvDataPembayaranFromId.visibility = View.VISIBLE
                pembayaranAdapter.setData(it as ArrayList<DataPembayaranModel>)
            }else {
                binding.content.rlTextNull.visibility = View.VISIBLE
                binding.content.rvDataPembayaranFromId.visibility = View.GONE
            }
        })
    }

    private fun bindRv() {
        binding.content.rvDataPembayaranFromId.apply {
            layoutManager = LinearLayoutManager(this@DetailHutangActivity, LinearLayoutManager.VERTICAL, false)
            adapter = pembayaranAdapter
        }
    }

    private fun setAlert(statusOption: Int){
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val alertBinding : AlertDicicilBinding = AlertDicicilBinding.inflate(inflater)
        dialogBuilder.setView(alertBinding.root)

        pembayaranViewModel = ViewModelProvider(this).get(my.notes.HHHH.database.PembayaranViewModel::class.java)


        alertBinding.tvTotalHutang.text = getString(R.string.dolar) +"." + decimalFormat.format(activityJumlah)
        alertBinding.tvUsername.text = binding.tvProfileName.text


        val dibuatPada = alertBinding.tvAlertDibuatPada
        dibuatPada.text = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Date())

        when(statusOption){
            0 -> {
                alertBinding.etJumlahAlert.editText!!.isEnabled = false
                alertBinding.etJumlahAlert.editText!!.setText(activityJumlah.toString())
            }
            1 -> {

            }
            2 -> {

            }
        }
        val type = if (!typeJenis!!) getString(R.string.hutang) else getString(R.string.piutang)
        onRules(alertBinding, statusOption)
        if (statusOption == 1){
            alertBinding.tvStatusType.text = getString(R.string.mengurangi) + " " +type
            //dikurangi
            dialogBuilder.setPositiveButton(getString(R.string.bayar)
            ) { dialogInterface, p1 ->
                roomDbSaved(alertBinding, statusOption)
                dialogInterface.dismiss()
            }
            dialogBuilder.setNegativeButton(getString(R.string.kembali)
            ) { dialogInterface, p1 -> dialogInterface!!.dismiss() }
        }else if(statusOption == 0){
            alertBinding.tvStatusType.text = getString(R.string.melunasi) + " "+type

            //lunas
            dialogBuilder.setPositiveButton(getString(R.string.lunas)){ dialogInterface, p1 ->
                roomDbSaved(alertBinding, statusOption)
                dialogInterface.dismiss()
            }
            dialogBuilder.setNegativeButton(getString(R.string.kembali)){dialogInterface, p1 ->
                dialogInterface.dismiss()
            }
        }
        else if(statusOption == 2){
            alertBinding.tvStatusType.text = getString(R.string.menambah) + " "+type
            //ditambah
            dialogBuilder.setPositiveButton(getString(R.string.ditambah)){
                dialogInterface, p1 ->
                roomDbSaved(alertBinding, statusOption)
                dialogInterface.dismiss()
            }
            dialogBuilder.setNegativeButton(getString(R.string.kembali)){ dialogInterface, p1 ->
                dialogInterface.dismiss()
            }
        }
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    private fun roomDbSaved(alertBinding: AlertDicicilBinding, statusOption: Int) {
        val uid = intent.extras?.get(EXTRA_UID)
        //alert
        val fromUid = uid.toString().toInt()
        Log.d(TAG, " roomDbSave uid : $fromUid")
        val dibuat = alertBinding.tvAlertDibuatPada.text.toString()
        val keterangan = alertBinding.etKeteranganAlert.editText!!.text.toString()
        val aJumlah = alertBinding.etJumlahAlert.editText!!.text
        var alertJumlah = aJumlah.toString().replace(" ", "").replace(".", "").toInt()
        //activity

        Log.d(TAG , "ACTIVITY JUMLAH ::::::::::::::::: $activityJumlah")
        Log.d(TAG, "ALERT JUMLAH :::::::  $alertJumlah")
        Log.d(TAG, "ALERT SATUS $statusOption")
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm a").format(Date())
        if (aJumlah.length > 9) {
            alertBinding.etJumlahAlert.helperText =
                getString(R.string.jumlah_tidak_boleh_lebih_dari_9)
        }else {
            if(aJumlah.isNullOrEmpty() || aJumlah.length == null){
                alertBinding.etJumlahAlert.error = getString(R.string.nominal_tidak_boleh_kosong)
            }else{
                if (keterangan.length > 210){
                    alertBinding.etKeteranganAlert.error = getString(R.string.keterangan_tidak_boleh_lebih_dari_210_huruf)
                }else{
                    if(alertJumlah == 0 || aJumlah.isEmpty() || aJumlah.isNullOrEmpty()){
                        alertBinding.etJumlahAlert.error = getString(R.string.nominal_tidak_boleh_kosong)
                    }else{
                        val dateFormat = DecimalFormat("##,###,###")
                        val dataBayar = DataPembayaranModel(
                            0,
                            fromUid,
                            dibuat,
                            alertJumlah,
                            keterangan,
                            statusOption,
                            typeJenis!!
                        )
                        when(statusOption){
                            1 -> {
                                var total = activityJumlah - alertJumlah
                                Log.d(TAG, "TOTAL DIKURANGI $total")
                                if(total < 0){
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        Snackbar.make(binding.clRootContent, getString(R.string.nominal_tidak_boleh_melebihi)+ " $typeJenis", Snackbar.LENGTH_SHORT).show()
                                    },700)
                                }
                                else if (total == 0){
                                    var bayar = activityDibayar + alertJumlah
                                    Log.d(TAG, "BAYAR $bayar")
                                    pembayaranViewModel.insertData(dataBayar)
                                    viewModel.updateDataJumlah(total, fromUid, true, simpleDateFormat.toString(), bayar)
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        Snackbar.make(binding.clRootContent, getString(R.string.berhasil_dilunasi)+" "+ getString(R.string.dolar) +" "+ dateFormat.format(alertJumlah), Snackbar.LENGTH_SHORT).show()
                                    },700)
                                }else if(alertJumlah > activityJumlah){
                                    Toast.makeText(this, getString(R.string.cek_kembali_jumlah_yang_anda_masukkan), Toast.LENGTH_SHORT).show()
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        Snackbar.make(binding.clRootContent, getString(R.string.nominal_tidak_boleh_melebihi)+ " $typeJenis", Snackbar.LENGTH_SHORT).show()
                                    },700)
                                }else {
                                    var bayar = activityDibayar + alertJumlah
                                    pembayaranViewModel.insertData(dataBayar)
                                    viewModel.updateDataJumlah(total, fromUid, false, "-", bayar)
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        Snackbar.make(
                                            binding.clRootContent,
                                            getString(R.string.berhasil_dicicil)+" "+ getString(R.string.dolar) +" "+ dateFormat.format(alertJumlah),
                                            Snackbar.LENGTH_SHORT
                                        ).show() },700)
                                }
                            }
                            2 -> {
                                var total = activityJumlah + alertJumlah
                                Log.d(TAG, "TOTAL DITAMBAH $total")
                                pembayaranViewModel.insertData(dataBayar)
                                viewModel.updateDataJumlah(total, fromUid, false, "-", activityDibayar)
                                Handler(Looper.getMainLooper()).postDelayed({
                                    Snackbar.make(
                                        binding.clRootContent,
                                        getString(R.string.berhasil_ditambah) +" "+ getString(R.string.dolar) +" "+ dateFormat.format(alertJumlah),
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                },700)
                            }
                            0 -> {
                                var total = activityJumlah - alertJumlah
                                Log.d(TAG, "TOTAL LUNAS $total")
                                if(total == 0){
                                    var bayar = activityDibayar + alertJumlah
                                    pembayaranViewModel.insertData(dataBayar)
                                    viewModel.updateDataJumlah(total, fromUid, true, simpleDateFormat.toString(), bayar)
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        Snackbar.make(
                                            binding.clRootContent,
                                            getString(R.string.berhasil_dilunasi)+" "+ getString(R.string.dolar) +" "+ dateFormat.format(total),
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                    },900)

                                }else {
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        Snackbar.make(binding.clRootContent, getString(R.string.nampaknya_mengalami_bug), Snackbar.LENGTH_SHORT).show()
                                    },700)
                                }
                            }
                        }
                    }
            }
            }
        }
    }

    private fun onRules(alertBinding: AlertDicicilBinding, optionsString: Int) {
        alertBinding.etKeteranganAlert.editText!!.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(p0: Editable?) {
                if (p0!!.length >= 210){
                    alertBinding.etKeteranganAlert.error = getString(R.string.keterangan_tidak_boleh_lebih_dari_210_huruf)
                } else if(p0.startsWith(" ")){
                    alertBinding.etKeteranganAlert.helperText = getString(R.string.keterangan_tidak_boleh_diawali_dengan_huruf_kosong)
                    p0.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(0))
                }else {
                    p0.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(210))
                        alertBinding.etKeteranganAlert.helperText = getString(R.string.bila_ada_keterangan_yang_di_hutang)
                }
            }
        })
        alertBinding.etJumlahAlert.editText!!.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                when(optionsString){

                }
            }
            override fun afterTextChanged(p0: Editable?) {
                if(p0!!.length >= 9){
                    alertBinding.etJumlahAlert.helperText = getString(R.string.jumlah_tidak_boleh_lebih_dari_9)
                    p0.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(9))
                }else if (p0.startsWith("-")){
                    alertBinding.etJumlahAlert.error = getString(R.string.nominal_tidak_boleh_diawali_nilai_negative)
                    p0.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(0))
                }else if(p0.startsWith("0")){
                    alertBinding.etJumlahAlert.helperText = getString(R.string.tidak_boleh_diawali_dengan_0)
                    p0.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(1))
                }else if(p0.startsWith(" ")){
                    alertBinding.etJumlahAlert.error = getString(R.string.tidak_boleh_diawali_dengan_nilai_kosong)
                    p0.filters = arrayOf(InputFilter.LengthFilter(0))
                }else if(p0.startsWith(".")){
                    alertBinding.etJumlahAlert.error = getString(R.string.masukkan_nilai_dengan_benar)
                    p0.filters = arrayOf(InputFilter.LengthFilter(0))
                }else if(p0.startsWith(",")){
                    alertBinding.etJumlahAlert.error = getString(R.string.masukkan_nilai_dengan_benar)
                    p0.filters = arrayOf(InputFilter.LengthFilter(0))
                } else {
                    p0.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(9))
                    alertBinding.etJumlahAlert.helperText = getString(R.string.masukkan_jumlah_nominal)
                }
            }
        })
    }

    private fun onActionListener(){
        binding.tvBackToHome.setOnClickListener {
            if (WindowFragmentLunas.STAY_LUNAS == 1){
                WindowFragmentLunas.STAY_LUNAS = 0
                startActivity(Intent(this, LunasActivity::class.java))
            }else if(STAY_MAIN_MENU == 101){
                STAY_MAIN_MENU = 100
                startActivity(Intent(this, MainActivity::class.java))
            } else{
                val intent = Intent(this, HutangActivity::class.java)
                intent.putExtra(HutangActivity.EXTRA_TYPE_JENIS, jenis)
                startActivity(intent)
            }
        }
        binding.optionF.setOnClickListener {
            onAddButtonClick()
        }

        binding.fabCicil.setOnClickListener {
            val dikurangi: Int = 1
            setAlert(dikurangi)
        }

        binding.fabLunas.setOnClickListener {
            val lunas: Int = 0
            setAlert(lunas)
        }

        binding.fabTambahkan.setOnClickListener {
            val ditambah: Int = 2
            setAlert(ditambah)
        }
    }
    private fun onAddButtonClick(){
        setVisibility(closed)
        setAnimation(closed)
        closed = !closed
    }

    private fun setVisibility(closed: Boolean){
        if (!closed){
            binding.fabTambahkan.visibility = View.VISIBLE
            binding.fabLunas.visibility = View.VISIBLE
            binding.fabCicil.visibility = View.VISIBLE
            binding.tvLunas.visibility = View.VISIBLE
            binding.tvCicil.visibility = View.VISIBLE
            binding.tvTabmahkan.visibility = View.VISIBLE
        }else{
            binding.fabTambahkan.visibility = View.INVISIBLE
            binding.fabLunas.visibility = View.INVISIBLE
            binding.fabCicil.visibility = View.INVISIBLE
            binding.tvLunas.visibility = View.INVISIBLE
            binding.tvCicil.visibility = View.INVISIBLE
            binding.tvTabmahkan.visibility = View.INVISIBLE
        }
    }

    private fun setAnimation(closed: Boolean){
        if (!closed){
            binding.optionF.startAnimation(rotateOpen)
            binding.fabTambahkan.startAnimation(fromBottom)
            binding.fabCicil.startAnimation(fromBottom)
            binding.fabLunas.startAnimation(fromBottom)
            binding.tvTabmahkan.startAnimation(fromBottom)
            binding.tvCicil.startAnimation(fromBottom)
            binding.tvLunas.startAnimation(fromBottom)
        }else{
            binding.optionF.startAnimation(rotateClose)
            binding.fabCicil.startAnimation(toBottom)
            binding.fabLunas.startAnimation(toBottom)
            binding.fabTambahkan.startAnimation(toBottom)
            binding.tvTabmahkan.startAnimation(toBottom)
            binding.tvCicil.startAnimation(toBottom)
            binding.tvLunas.startAnimation(toBottom)
        }
    }

    private var startAvatarAnimatePointY: Float = 0F
    private var animateWeigt: Float = 0F
    private var isCalculated = false

    private fun updateViews(percentOffset: Float) {
        /* Collapsing avatar transparent*/
        when {
            percentOffset > mUpperLimitTransparently -> {
                //avatarContainerView.alpha = 0.0f
                titleToolbarText.alpha = 0.0F
            }

            percentOffset < mUpperLimitTransparently -> {
                //  avatarContainerView.alpha = 1 - percentOffset
                titleToolbarText.alpha = 1f
            }
        }

        /*Collapsed/expended sizes for views*/
        val result: Pair<Int, Int> = when {
            percentOffset < ABROAD -> {
                Pair(
                    TO_EXPANDED_STATE, cashCollapseState?.second
                        ?: WAIT_FOR_SWITCH
                )
            }
            else -> {
                Pair(TO_COLLAPSED_STATE, cashCollapseState?.second ?: WAIT_FOR_SWITCH)
            }
        }
        result.apply {
            var translationY = 0f
            var headContainerHeight = 0f
            val translationX: Float
            var currentImageSize = 0
            when {
                cashCollapseState != null && cashCollapseState != this -> {
                    when (first) {
                        TO_EXPANDED_STATE -> {
                            translationY = toolbar.height.toFloat()
                            headContainerHeight = appBarLayout.totalScrollRange.toFloat()
                            currentImageSize = EXPAND_AVATAR_SIZE.toInt()
                            /**/
                            titleToolbarText.visibility = View.VISIBLE
                            titleToolbarTextSingle.visibility = View.INVISIBLE
                            background.setBackgroundColor(
                                ContextCompat.getColor(
                                    this@DetailHutangActivity,
                                    R.color.color_transparent
                                )
                            )
                            /**/
                            ivAvatar.translationX = 0f
                            ivAvatarGender.translationX = 0f
                        }

                        TO_COLLAPSED_STATE -> {
                            background.setBackgroundColor(
                                ContextCompat.getColor(
                                    this@DetailHutangActivity,
                                    R.color.purple_500
                                )
                            )
                            currentImageSize = COLLAPSE_IMAGE_SIZE.toInt()
                            translationY =
                                appBarLayout.totalScrollRange.toFloat() - (toolbar.height - COLLAPSE_IMAGE_SIZE) / 2
                            headContainerHeight = toolbar.height.toFloat()
                            translationX =
                                appBarLayout.width / 2f - COLLAPSE_IMAGE_SIZE / 2 - margin * 2
                            /**/
                            ValueAnimator.ofFloat(ivAvatar.translationX, translationX).apply {
                                addUpdateListener {
                                    if (cashCollapseState!!.first == TO_COLLAPSED_STATE) {
                                        ivAvatar.translationX = it.animatedValue as Float
                                    }
                                }
                                interpolator = AnticipateOvershootInterpolator()
                                startDelay = 69
                                duration = 350
                                start()
                            }
                            ValueAnimator.ofFloat(ivAvatarGender.translationX, translationX).apply {
                                addUpdateListener {
                                    if (cashCollapseState!!.first == TO_COLLAPSED_STATE){
                                        ivAvatarGender.translationX = it.animatedValue as Float
                                    }
                                }
                                interpolator = AnticipateOvershootInterpolator()
                                startDelay = 69
                                duration = 350
                                start()
                            }

                            titleToolbarText.visibility = View.INVISIBLE
                            titleToolbarTextSingle.apply {
                                visibility = View.VISIBLE
                                alpha = 0.2f
                                this.translationX = width.toFloat() / 2
                                animate().translationX(0f)
                                    .setInterpolator(AnticipateOvershootInterpolator())
                                    .alpha(1.0f)
                                    .setStartDelay(69)
                                    .setDuration(450)
                                    .setListener(null)
                            }
                        }
                    }

                    ivAvatar.apply {
                        layoutParams.height = currentImageSize
                        layoutParams.width = currentImageSize
                    }
                    ivAvatarGender.apply {
                        layoutParams.height = currentImageSize
                        layoutParams.width = currentImageSize
                    }

                    collapsingAvatarContainer.apply {
                        layoutParams.height = headContainerHeight.toInt()
                        this.translationY = translationY
                        requestLayout()
                    }
                    /**/
                    cashCollapseState = Pair(first, SWITCHED)
                }
                else -> {
                    /* Collapse avatar img*/
                    ivAvatar.apply {
                        if (percentOffset > startAvatarAnimatePointY) {

                            val animateOffset =
                                (percentOffset - startAvatarAnimatePointY) * animateWeigt
                            val avatarSize =
                                EXPAND_AVATAR_SIZE - (EXPAND_AVATAR_SIZE - COLLAPSE_IMAGE_SIZE) * animateOffset

                            this.layoutParams.also {
                                if (it.height != Math.round(avatarSize)) {
                                    it.height = Math.round(avatarSize)
                                    it.width = Math.round(avatarSize)
                                    this.requestLayout()
                                }
                            }

                            this.translationY = (COLLAPSE_IMAGE_SIZE - avatarSize) * animateOffset

                        } else {
                            this.layoutParams.also {
                                if (it.height != EXPAND_AVATAR_SIZE.toInt()) {
                                    it.height = EXPAND_AVATAR_SIZE.toInt()
                                    it.width = EXPAND_AVATAR_SIZE.toInt()
                                    this.layoutParams = it
                                }
                            }
                        }
                    }
                    ivAvatarGender.apply {
                        if (percentOffset > startAvatarAnimatePointY) {

                            val animateOffset =
                                (percentOffset - startAvatarAnimatePointY) * animateWeigt
                            val avatarSize =
                                EXPAND_AVATAR_SIZE - (EXPAND_AVATAR_SIZE - COLLAPSE_IMAGE_SIZE) * animateOffset

                            this.layoutParams.also {
                                if (it.height != Math.round(avatarSize)) {
                                    it.height = Math.round(avatarSize)
                                    it.width = Math.round(avatarSize)
                                    this.requestLayout()
                                }
                            }

                            this.translationY = (COLLAPSE_IMAGE_SIZE - avatarSize) * animateOffset

                        } else {
                            this.layoutParams.also {
                                if (it.height != EXPAND_AVATAR_SIZE.toInt()) {
                                    it.height = EXPAND_AVATAR_SIZE.toInt()
                                    it.width = EXPAND_AVATAR_SIZE.toInt()
                                    this.layoutParams = it
                                }
                            }
                        }
                    }
                    cashCollapseState = Pair(first, WAIT_FOR_SWITCH)
                }
            }
        }
    }

    private val vibrantLightColorList = arrayOf(
        ColorDrawable(Color.parseColor("#9ACCCD")), ColorDrawable(Color.parseColor("#8FD8A0")),
        ColorDrawable(Color.parseColor("#CBD890")), ColorDrawable(Color.parseColor("#DACC8F")),
        ColorDrawable(Color.parseColor("#D9A790")), ColorDrawable(Color.parseColor("#D18FD9")),
        ColorDrawable(Color.parseColor("#FF6772")), ColorDrawable(Color.parseColor("#DDFB5C"))
    )

    fun getRandomDrawbleColor(): ColorDrawable? {
        val idx = Random().nextInt(vibrantLightColorList.size)
        return vibrantLightColorList[idx]
    }
    private fun setData(
        uid: String
        ) {
        Log.d(TAG, "UID FROM ADAPTER $uid")
        val id = uid.toInt()
        viewModel.getDataWhereId(id).observe(this, Observer{
//            val option = RequestOptions()
//                .centerCrop()
//                .placeholder(getRandomDrawbleColor())
            with(binding){
                if (it != null){
                    val jenisPinjaman = if (!it.jenisPinjaman) getString(R.string.hutang) else getString(R.string.piutang)
                    jenis = jenisPinjaman
//                    Glide.with(this@DetailHutangActivity).load("").apply(option).into(ivAvatar)
                    activityJumlah = it.jumlah
                    activityDibayar = it.jumlah_dibayar
                    ivAvatar.background = getRandomDrawbleColor()

                    titleToolbarText.text = it.username
                    titleToolbarTextSingle.text = it.username

                    val decimalFormat = DecimalFormat("##,###,###")
                    content.tvNama.text = it.username
                    content.tvTanggalDibuat.text = it.diBuat
                    content.tvDescription.text = it.deskripsi
                    content.tvJumlah.text = getString(R.string.dolar)+ ". " + decimalFormat.format(it.jumlah)
                    content.tvJumlahStatus.text = getString(R.string.jumlah) + " " +jenisPinjaman

                    imgbAvatarGender.setColorFilter(ContextCompat.getColor(this@DetailHutangActivity, R.color.white))
                    when(it.genderUser){
                        1 -> {
                            content.tvGender.text = getString(R.string.laki_laki)
                            imgbAvatarGender.setImageDrawable(resources.getDrawable(R.drawable.ic_user_male))
                        }
                        2 -> {
                            content.tvGender.text = getString(R.string.perempuan)
                            imgbAvatarGender.setImageDrawable(resources.getDrawable(R.drawable.ic_user_female))
                        }
                        3 -> {
                            content.tvGender.text = getString(R.string.lainnya)
                            imgbAvatarGender.setImageDrawable(resources.getDrawable(R.drawable.ic_user_kosong))
                        }
                        else -> {
                            content.tvGender.text = "-"
                            imgbAvatarGender.setImageDrawable(resources.getDrawable(R.drawable.ic_user_alert))
                        }
                    }
                    if (it.jumlah.toInt() == 0){
                        binding.content.ivVerification.visibility = View.VISIBLE
                        binding.content.ivVerificationIcon.visibility = View.VISIBLE
                        binding.optionF.visibility = View.GONE
                        binding.fabTambahkan.visibility = View.GONE
                        binding.fabLunas.visibility = View.GONE
                        binding.fabCicil.visibility = View.GONE

                    }else {
                        binding.content.ivVerificationIcon.visibility = View.GONE
                        binding.content.ivVerification.visibility = View.GONE
                    }
                }
            }
        })
    }
    companion object {
        const val EXTRA_UID = "extra_uid"
        var EXTRA_TYPE_JENIS = "extra_type_jenis"

        const val ABROAD = 0.99f
        const val TO_EXPANDED_STATE = 0
        const val TO_COLLAPSED_STATE = 1
        const val WAIT_FOR_SWITCH = 0
        const val SWITCHED = 1

        private val mLowerLimitTransparently = ABROAD * 0.45
        private val mUpperLimitTransparently = ABROAD * 0.65
        var STAY_MAIN_MENU = 100

        val decimalFormat = DecimalFormat("##,###,###")
    }
}