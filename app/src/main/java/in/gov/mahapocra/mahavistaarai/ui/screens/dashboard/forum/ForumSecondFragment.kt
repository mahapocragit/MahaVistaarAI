package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.forum

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.gov.mahapocra.mahavistaarai.databinding.FragmentForumSecondBinding
import `in`.gov.mahapocra.mahavistaarai.ui.adapters.ForumAllAdapter


class ForumSecondFragment : Fragment() {

    private lateinit var binding: FragmentForumSecondBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForumSecondBinding.inflate(inflater, container, false) // FIXED
        val adapter = ForumAllAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        return binding.root
    }
}