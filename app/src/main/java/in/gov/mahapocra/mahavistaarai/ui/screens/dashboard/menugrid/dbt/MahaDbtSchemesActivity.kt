package `in`.gov.mahapocra.mahavistaarai.ui.screens.dashboard.menugrid.dbt

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.gov.mahapocra.mahavistaarai.databinding.ActivityMahaDbtSchemesBinding
import `in`.gov.mahapocra.mahavistaarai.ui.viewmodel.DbtSchemesViewModel

class MahaDbtSchemesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMahaDbtSchemesBinding
    private lateinit var dbtSchemesViewModel: DbtSchemesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMahaDbtSchemesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbtSchemesViewModel = ViewModelProvider(this)[DbtSchemesViewModel::class.java]
        dbtSchemesViewModel.getMahaDBTSchemes(this)
        dbtSchemesViewModel.responseUrlMahaDbtSchemes.observe(this) { schemeList ->
            if (schemeList == null) {
                Log.d("TAGGER", "onCreate: schemeList is null")
            } else {
                binding.recyclerView.layoutManager = LinearLayoutManager(this)
                binding.recyclerView.adapter = MahadbtSchemesAdapter(schemeList)
            }
        }
        dbtSchemesViewModel.error.observe(this){
            Log.d("TAGGER", "error: $it")
        }
    }
}