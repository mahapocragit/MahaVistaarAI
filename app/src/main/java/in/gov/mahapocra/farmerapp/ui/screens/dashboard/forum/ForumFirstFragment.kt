package `in`.gov.mahapocra.farmerapp.ui.screens.dashboard.forum

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import `in`.gov.mahapocra.farmerapp.databinding.FragmentForumFirstBinding

class ForumFirstFragment : Fragment() {

    private lateinit var binding: FragmentForumFirstBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForumFirstBinding.inflate(layoutInflater, container, false)
        val adapter = ForumTrendingAdapter()
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter
        return binding.root
    }
}