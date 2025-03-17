package `in`.gov.mahapocra.farmerapppks.ui.screens.dashboard.chc

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.gov.mahapocra.farmerapppks.R
import `in`.gov.mahapocra.farmerapppks.databinding.ActivityChcenterBinding

class CHCenterActivity : AppCompatActivity() {

    private lateinit var binding:ActivityChcenterBinding
    private lateinit var adapter: CHCenterRecyclerAdapter
    private var tempStrArr = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChcenterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.imgBackArrow.visibility = View.VISIBLE
        binding.toolbar.imgBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.toolbar.textViewHeaderTitle.text = "CHC Center"

        for(i in 1..10){
            tempStrArr.add("Hello $i")
        }
        binding.previousSevenDayTV.apply {
            background =
                ContextCompat.getDrawable(
                    this@CHCenterActivity,
                    R.drawable.shape_right_green
                )
            setTextColor(Color.WHITE)
        }

        binding.nextSevenDayTV.apply {
            background =
                ContextCompat.getDrawable(this@CHCenterActivity, R.drawable.shape_left_white)
            setTextColor(Color.BLACK)
        }

        adapter = CHCenterRecyclerAdapter(tempStrArr)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

    }
}