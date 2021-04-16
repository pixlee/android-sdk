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

        grid.setOnClickListener {
            startActivity(Intent(this, SimpleGridActivity::class.java))
        }

        list.setOnClickListener {
            startActivity(Intent(this, SimpleListActivity::class.java))
        }

        dynamic.setOnClickListener {
            startActivity(Intent(this, DynamicDemoActivity::class.java))
        }
    }
}
