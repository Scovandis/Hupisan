package my.notes.HHHH.view.main

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.google.android.material.snackbar.Snackbar
import com.rd.animation.type.AnimationType
import java.text.SimpleDateFormat
import java.util.*
import android.app.AlarmManager
import android.app.Notification

import android.os.SystemClock

import android.app.PendingIntent
import android.content.res.Configuration
import android.net.Uri
import androidx.core.app.NotificationCompat
import android.text.format.DateUtils
import my.notes.HHHH.R
import my.notes.HHHH.database.entity.DataModel
import my.notes.HHHH.databinding.ActivityMainBinding
import my.notes.HHHH.notification.service.MyService
import my.notes.HHHH.notification.service.NotificationService
import my.notes.HHHH.util.AppsConfig
import my.notes.HHHH.view.analistic.StatisticActivity
import my.notes.HHHH.view.bantuan.BantuanActivity
import my.notes.HHHH.view.hutang.HutangActivity
import my.notes.HHHH.view.lunas.LunasActivity
import my.notes.HHHH.view.main.adapter.AutoSliderAdapter
import my.notes.HHHH.view.main.adapter.NewAdapter
import my.notes.HHHH.view.pengaturan.SettingsActivity


class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.simpleName

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: my.notes.HHHH.database.DataViewModel
    lateinit var newAdapter: NewAdapter
    private lateinit var groupVM : my.notes.HHHH.database.ArisanGroupViewModel

    lateinit var appsConfig: AppsConfig


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appsConfig = AppsConfig()
        appsConfig.AppsConfig(this)
        loadLanguage()
        val HUTANG: String = getString(R.string.hutang)
        val PIUTAG: String = getString(R.string.piutang)

        val parser =  SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val timeNow = SimpleDateFormat("dd/MM/yyyy HH:mm")
        val formattedDate = timeNow.format(parser.parse("2018-12-14T09:55:00"))
        Log.d(TAG, "FORMATEDDATE $formattedDate")

        val c = Calendar.getInstance()
        val ms = c.get(Calendar.MILLISECOND)
        Log.d(TAG, "MILLISECOND $ms")

        val rightNow = Calendar.getInstance()

        val myDate = SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Date()).toString()
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm")
        val date = sdf.parse(myDate)
        val millis = date.time
        Log.d(TAG, "MILLLISEEECOND $millis")
        val cal = Calendar.getInstance() //current date and time

        cal.add(Calendar.DAY_OF_MONTH, 1) //add a day

        cal[Calendar.HOUR_OF_DAY] = 23 //set hour to last hour

        cal[Calendar.MINUTE] = 59 //set minutes to last minute

        cal[Calendar.SECOND] = 59 //set seconds to last second

        cal[Calendar.MILLISECOND] = 999 //set milliseconds to last millisecond

        val millis1 = cal.timeInMillis
        println("MILLIESS $millis1")
        val currentTime = System.currentTimeMillis()
        val endOfTomorrow = (currentTime + DateUtils.DAY_IN_MILLIS
                + (DateUtils.DAY_IN_MILLIS - currentTime % DateUtils.DAY_IN_MILLIS))
        println("ENDOFTOMOROW $endOfTomorrow")


        val offset = (millis + rightNow[Calendar.DST_OFFSET]).toLong()
        val sinceMid: Long = (rightNow.timeInMillis + offset) % (24 * 60 * 60 * 1000)
        println("$offset offset")
        println("RIGHT NOW $rightNow")
        println("${rightNow[Calendar.ZONE_OFFSET]} righnow zoneoffset")
        println("${rightNow[Calendar.DST_OFFSET]} righnow zoneoffset")

        println("$sinceMid milliseconds since midnight")



        autoSlider()
        onPageChange()
        binding.viewPager.background = getRandomDrawbleColor()

        binding.cardViewAbout.setOnClickListener {
            startService(Intent(this, MyService::class.java))
            Snackbar.make(binding.constraint, getString(R.string.fitur_belum_di_tambahkan), Snackbar.LENGTH_SHORT).show()
        }
        binding.cardViewChart.setOnClickListener {
            startActivity(Intent(this, StatisticActivity::class.java))
        }
        binding.cardViewHutang.setOnClickListener {
            viewModel.getAllDataWhereStatusData(false,
                false).observe(this, Observer {
                ISNULL = if (it.isEmpty()){ 0 }else{ 1 }
            })
            val intent = Intent(this, HutangActivity::class.java)
            intent.putExtra(HutangActivity.EXTRA_TYPE_JENIS, HUTANG)
            startActivity(intent)
        }

        binding.cardViewPiutang.setOnClickListener {
            viewModel.getAllDataWhereStatusData(false,
                true).observe(this, Observer {
                ISNULL = if (it.isEmpty()){ 0 }else{ 1 }
            })
            val intent = Intent(this, HutangActivity::class.java)
            intent.putExtra(HutangActivity.EXTRA_TYPE_JENIS, PIUTAG)
            startActivity(intent)
        }

        binding.cardViewArisan.setOnClickListener {
            Snackbar.make(binding.constraint, getString(R.string.fitur_sedang_dikembangkan), Snackbar.LENGTH_SHORT).show()
//            startActivity(Intent(this, FitureArisanActivity::class.java))
        }
        binding.cardViewLunas.setOnClickListener {
            startActivity(Intent(this, LunasActivity::class.java))
        }
        binding.cardViewGrosir.setOnClickListener {
            Snackbar.make(binding.constraint, getString(R.string.fitur_belum_di_tambahkan), Snackbar.LENGTH_SHORT).show()
        }
        binding.cardViewTentang.setOnClickListener {
            Snackbar.make(binding.constraint, getString(R.string.fitur_belum_di_tambahkan), Snackbar.LENGTH_SHORT).show()
        }
        binding.cardViewCaraMenggunakan.setOnClickListener {
            startActivity(Intent(this, BantuanActivity::class.java))
//            Snackbar.make(binding.constraint, getString(R.string.fitur_belum_di_tambahkan), Snackbar.LENGTH_SHORT).show()
        }
        newAdapter = NewAdapter(this@MainActivity)
        viewModel = ViewModelProvider(this).get(my.notes.HHHH.database.DataViewModel::class.java)
        groupVM = ViewModelProvider(this).get(my.notes.HHHH.database.ArisanGroupViewModel::class.java)
        binding.rvNew.visibility = View.GONE

        Handler(Looper.getMainLooper()).postDelayed({
            setData()
        }, 700)
        bindRecycler()

//        setServiceNotifcation()
    }
    private fun loadLanguage(){
        var lang = appsConfig.getRegionLanguage()

        if(lang.equals(getString(R.string.english))){
            lang ="en"
        }else if (lang.equals(getString(R.string.indonesia))){
            lang = "in"
        }else if(lang.equals(getString(R.string.thailand))){
            lang = "tpi"
        }else if(lang.equals(getString(R.string.vietnamese))){
            lang = "vun"
        }else if(lang.equals(getString(R.string.india))){
            lang = "kok"
        }

        val locale = Locale(lang)
        Locale.setDefault(locale)
        val configuration = Configuration()

        configuration.locale = locale
        baseContext.resources.updateConfiguration(configuration, baseContext.resources.displayMetrics)
    }

    private fun intentViewPlayStore(){
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/apps/details?id=com.example.hhhh")
                setPackage("com.android.vending")
        }
        startActivity(intent)
    }
    private fun loadTheme(){
        val themeId = appsConfig.getThemePreferences()
        Log.d(TAG, "THEMEID $themeId")
        when(themeId.toInt()){
            1 -> {
                setTheme(R.style.LightTheme)
            }
            2 -> {
                setTheme(R.style.DarkTheme)
            }
        }
    }

    private fun onPageChange() {
        binding.viewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                when(position){
                    0 -> {
                        binding.viewPager.background = getRandomDrawbleColor()
                    }
                    1 -> {
                        binding.viewPager.background = getRandomDrawbleColor()
                    }
                    2 -> {
                        binding.viewPager.background = getRandomDrawbleColor()
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.menu_setting -> {
//                Snackbar.make(binding.constraint, getString(R.string.fitur_belum_di_tambahkan), Snackbar.LENGTH_SHORT).show()
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
            R.id.menu_rate_up -> {
                intentViewPlayStore()
                return true
            }
            R.id.menu_export -> {
                Snackbar.make(binding.constraint, getString(R.string.fitur_belum_di_tambahkan), Snackbar.LENGTH_SHORT).show()
                return true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

    }

    private val vibrantLightColorList = arrayOf(
        ColorDrawable(Color.parseColor("#808080")), ColorDrawable(Color.parseColor("#839e2e")),
        ColorDrawable(Color.parseColor("#601561")), ColorDrawable(Color.parseColor("#601561")),
        ColorDrawable(Color.parseColor("#a96232")), ColorDrawable(Color.parseColor("#a96232")),
        ColorDrawable(Color.parseColor("#4d1a7f")), ColorDrawable(Color.parseColor("#4c2882")),
        ColorDrawable(Color.parseColor("#004040")), ColorDrawable(Color.parseColor("#004242")),
        ColorDrawable(Color.parseColor("#008000")), ColorDrawable(Color.parseColor("#38862a"))
    )

    fun getRandomDrawbleColor(): ColorDrawable? {
        val idx = Random().nextInt(vibrantLightColorList.size)
        return vibrantLightColorList[idx]
    }

    var currentPage = 0
    var timer: Timer? = null
    val DELAY_MS: Long = 500

    val PERIOD_MS: Long = 3000

    private fun getItem(i: Int): Int = binding.viewPager.currentItem + i
    private fun autoSlider(){
        binding.viewPager.adapter = AutoSliderAdapter(supportFragmentManager, this)
        binding.indicator.setAnimationType(AnimationType.WORM)

//        val handler = Handler()
//        val Update = Runnable {
//            if (currentPage == getItem(+1)) {
//                currentPage = 0
//            }
//            binding.viewPager.setCurrentItem(currentPage++, true)
//        }

//        timer = Timer()
//
//        timer!!.schedule(object : TimerTask() {
//            override fun run() {
//                handler.post(Update)
//            }
//        }, DELAY_MS, PERIOD_MS)

    }

    private fun setData() {
        viewModel.loadDataLimit(false).observe(this, Observer {
            binding.shimmer1.stopShimmer()
            binding.shimmer1.visibility = View.GONE
            if (it.isEmpty()) {
                binding.rvNew.visibility = View.GONE
                binding.cardViewTextNull.visibility = View.VISIBLE
            } else {
                binding.cardViewTextNull.visibility = View.GONE
                binding.rvNew.visibility = View.VISIBLE
                newAdapter.setData(it as ArrayList<DataModel>)
            }
        })
    }

    private fun bindRecycler() {
        binding.rvNew.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = newAdapter
        }
    }

    private fun scheduleNotification(notification: Notification, delay: Int) {
        val notificationIntent = Intent(this, NotificationService::class.java)
        notificationIntent.putExtra(NotificationService.NOTIFICATION_ID, 1)
        notificationIntent.putExtra(NotificationService.NOTIFICATION, notification)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            notificationIntent,
            flags
        )
        val futureInMillis = SystemClock.elapsedRealtime() + delay
        val alarmManager = (getSystemService(ALARM_SERVICE) as AlarmManager)
        alarmManager[AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis] = pendingIntent
    }

    private fun getNotification(content: String): Notification? {
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, default_notification_channel_id)
        builder.setContentTitle(getString(R.string.jatuh_tempo))
        builder.setContentText(content)
        builder.setSmallIcon(R.drawable.ic_launcher_foreground)
        builder.setAutoCancel(true)
        builder.setChannelId(NOTIFICATION_CHANNEL_ID)
        return builder.build()
    }

    companion object{
        var ISNULL = 0
        val NOTIFICATION_CHANNEL_ID = "10001"
        private val default_notification_channel_id = "default"
    }
}
