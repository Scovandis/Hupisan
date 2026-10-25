package my.notes.HHHH.view.analistic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import my.notes.HHHH.R
import my.notes.HHHH.databinding.ActivityStatisticBinding
import my.notes.HHHH.view.main.MainActivity

class StatisticActivity : AppCompatActivity() {
    private val TAG = StatisticActivity::class.java.simpleName
    private lateinit var binding: ActivityStatisticBinding

    private val tabIcon = intArrayOf(
        R.drawable.ic_baseline_money_24,
        R.drawable.ic_baseline_payments_24
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewPager = binding.viewPager
        setUpViewPager(viewPager)

        val tabLayout = binding.tabLayout
        tabLayout.setupWithViewPager(viewPager)
        setUpTabIcon()

        supportActionBar?.title = getString(R.string.statistic)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setUpViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFrag(HutangFragment.newInstance())
        adapter.addFrag(PiutangFragment.newInstance())
        viewPager.adapter = adapter
    }

    private fun setUpTabIcon() {
        binding.tabLayout.getTabAt(0)?.setIcon(tabIcon[0])
        binding.tabLayout.getTabAt(1)?.setIcon(tabIcon[1])
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

internal class ViewPagerAdapter(manager: FragmentManager?) : FragmentPagerAdapter(manager!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val mFragmentList: MutableList<Fragment> = ArrayList()
    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    fun addFrag(fragment: Fragment) {
        mFragmentList.add(fragment)
    }

}