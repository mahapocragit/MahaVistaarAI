package `in`.gov.mahapocra.farmerapppks.activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.adapter.ViewPagerAdapter
import `in`.gov.mahapocra.farmerapppks.databinding.ActivityWeatherHomeTempBinding
import `in`.gov.mahapocra.farmerapppks.ui.weather.Item
import `in`.gov.mahapocra.farmerapppks.ui.weather.WindAdapter

class WeatherTempActivity : AppCompatActivity() {

    lateinit var binding: ActivityWeatherHomeTempBinding
    private lateinit var recyclerAdapter: WindAdapter
    private lateinit var itemList: List<Item>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherHomeTempBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewPagerAdapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = viewPagerAdapter

        binding.relativeLayoutTopBar.relativeLayoutToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.gradient_top_figma))
        binding.relativeLayoutTopBar.imgBackArrow.visibility = View.VISIBLE
        binding.relativeLayoutTopBar.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.previousSevenDayTV.setOnClickListener {
            binding.previousSevenDayTV.apply {
                background =
                    ContextCompat.getDrawable(
                        this@WeatherTempActivity,
                        R.drawable.shape_right_green
                    )
                setTextColor(Color.WHITE)
            }
            binding.nextSevenDayTV.apply {
                background =
                    ContextCompat.getDrawable(this@WeatherTempActivity, R.drawable.shape_left_white)
                setTextColor(Color.BLACK)
            }
        }

        binding.nextSevenDayTV.setOnClickListener {
            binding.nextSevenDayTV.apply {
                background =
                    ContextCompat.getDrawable(
                        this@WeatherTempActivity,
                        R.drawable.shape_left
                    )
                setTextColor(Color.WHITE)
            }
            binding.previousSevenDayTV.apply {
                background =
                    ContextCompat.getDrawable(this@WeatherTempActivity, R.drawable.shape_right)
                setTextColor(Color.BLACK)
            }
        }

        // Connect TabLayout and ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Temp"
                1 -> tab.text = "Rain"
                2 -> tab.text = "Humidity"
                3 -> tab.text = "Wind"
            }
        }.attach()


        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Initialize data list
        itemList = listOf(
            Item("Tue"),
            Item("Wed"),
            Item("Thur"),
            Item("Fri"),
            Item("Sat"),
            Item("Sun"),
            Item("Mon")
        )

        // Set adapter
        recyclerAdapter = WindAdapter(itemList, "temp")
        binding.recyclerView.adapter = recyclerAdapter
    }
}