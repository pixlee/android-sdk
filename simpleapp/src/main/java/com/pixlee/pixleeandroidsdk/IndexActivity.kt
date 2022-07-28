package com.pixlee.pixleeandroidsdk

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pixlee.pixleeandroidsdk.databinding.ActivityIndexBinding
import com.pixlee.pixleeandroidsdk.pxlwidgetview.*

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
        binding.widget.setOnClickListener {
            startActivity(Intent(this, WidgetActivity::class.java))
        }

        binding.mosaic.setOnClickListener {
            startActivity(Intent(this, MosaicActivity::class.java))
        }

        binding.horizontal.setOnClickListener {
            startActivity(Intent(this, HorizontalActivity::class.java))
        }

        binding.grid.setOnClickListener {
            startActivity(Intent(this, GridActivity::class.java))
        }

        binding.list.setOnClickListener {
            startActivity(Intent(this, ListActivity::class.java))
        }

        binding.dynamic.setOnClickListener {
            startActivity(Intent(this, DynamicDemoActivity::class.java))
        }
    }
}
