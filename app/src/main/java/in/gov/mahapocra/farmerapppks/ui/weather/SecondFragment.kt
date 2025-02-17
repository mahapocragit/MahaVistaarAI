package `in`.gov.mahapocra.farmerapppks.ui.weather

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `in`.gov.mahapocra.farmerapppks.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SecondFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SecondFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WindAdapter
    private lateinit var itemList: List<Item>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_second, container, false)

        // Initialize RecyclerView and set LayoutManager
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        // Initialize data list
        itemList = listOf(
            Item("Now"),
            Item("1 pm"),
            Item("2 pm"),
            Item("3 pm"),
            Item("4 pm"),
            Item("5 pm"),
            Item("6 pm"),
            Item("7 pm"),
            Item("8 pm"),
        )

        // Set adapter
        adapter = WindAdapter(itemList, "rain")
        recyclerView.adapter = adapter

        return view
    }
}