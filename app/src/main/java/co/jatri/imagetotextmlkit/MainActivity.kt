package co.jatri.imagetotextmlkit

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import co.jatri.imagetotextmlkit.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding


    private lateinit var scannerActivityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        handleActivityResult()

        binding.scanBtn.setOnClickListener {
           val intent = Intent(this, ScannerActivity::class.java)
            scannerActivityResultLauncher.launch(intent)
        }
    }

    private fun handleActivityResult() {
        scannerActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    val text = it.data?.getStringExtra(Util.SCANNER_RESULT)
                    binding.recognizeTextTv.text = text
                }
            }
    }
}