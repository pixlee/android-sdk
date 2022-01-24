package com.pixlee.pixleeandroidsdk

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pixlee.pixleeandroidsdk.databinding.ActivityIndexBinding

/**
 * Created by sungjun on 3/23/21.
 */
class IndexActivity : AppCompatActivity() {
    private var _binding: ActivityIndexBinding? = null
    private val binding get() = _binding!!

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityIndexBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.grid.setOnClickListener {
            startActivity(Intent(this, SimpleGridActivity::class.java))
        }

        binding.list.setOnClickListener {
            startActivity(Intent(this, SimpleListActivity::class.java))
        }

        binding.dynamic.setOnClickListener {
            startActivity(Intent(this, DynamicDemoActivity::class.java))
        }
    }
}
