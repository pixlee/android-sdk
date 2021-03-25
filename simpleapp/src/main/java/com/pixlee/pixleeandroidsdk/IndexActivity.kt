package com.pixlee.pixleeandroidsdk

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_index.*

/**
 * Created by sungjun on 3/23/21.
 */
class IndexActivity : AppCompatActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)

        basic.setOnClickListener {
            startActivity(Intent(this, PhotosActivity::class.java))
        }

        dynamic.setOnClickListener {
            startActivity(Intent(this, DynamicPhotosActivity::class.java))
        }
    }
}
