package com.example.animation_to_video.splashscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.animation_to_video.R
import com.example.animation_to_video.databinding.ActivitySplashScreenBinding
import com.example.animation_to_video.templatemode.TemplateActivity
import java.io.FileOutputStream
import java.io.InputStream

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.splashScreenIv.alpha = 0f
        binding.splashScreenIv.animate().setDuration(2000).alpha(1f).withEndAction{
            val intent = Intent(this,TemplateActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            finish()
        }
        writeToCache()
    }

    private fun writeToCache() {
        var input: InputStream = resources.openRawResource(R.raw.test)
        var out = FileOutputStream(cacheDir.absolutePath + "/out.mp4")
        val buff = ByteArray(1024)
        var read = 0

        for(i in 0..4) {
            when(i) {
                0 -> {
                    input = resources.openRawResource(R.raw.tmpbg0)
                    out = FileOutputStream(cacheDir.absolutePath + "/tmpbg0.mp4")
                }
                1 -> {
                    input = resources.openRawResource(R.raw.tmpbg1)
                    out = FileOutputStream(cacheDir.absolutePath + "/tmpbg1.mp4")
                }
                2 -> {
                    input = resources.openRawResource(R.raw.tmpbg2)
                    out = FileOutputStream(cacheDir.absolutePath + "/tmpbg2.mp4")
                }
                3 -> {
                    input = resources.openRawResource(R.raw.tmpbg3)
                    out = FileOutputStream(cacheDir.absolutePath + "/tmpbg3.mp4")
                }
                4 -> {
                    input = resources.openRawResource(R.raw.tmpbg4)
                    out = FileOutputStream(cacheDir.absolutePath + "/tmpbg4.mp4")
                }
            }
            try {
                while (input.read(buff).also { read = it } > 0) {
                    out.write(buff, 0, read)
                }
            } finally {
                input.close()
                out.close()
            }
        }
    }
}