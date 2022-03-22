package com.example.animation_to_video.templatemode

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.animation_to_video.MainActivity
import com.example.animation_to_video.R
import com.example.animation_to_video.databinding.ActivityTemplateBinding

class TemplateActivity : AppCompatActivity() , TemplateAdapter.OnItemClickListener {

    private lateinit var binding : ActivityTemplateBinding

    private lateinit var templateAdapter: TemplateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTemplateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        templateAdapter = TemplateAdapter()

        binding.templateRecyclerView.apply {
            adapter = templateAdapter
            setHasFixedSize(true)
        }
        templateAdapter.setListener(this)
        templateAdapter.setTemplate(getTemplateList())

    }

    private fun getTemplateList() : List<TemplateItem> {

        val templateArrayList = ArrayList<TemplateItem>()
        templateArrayList.add(TemplateItem(R.drawable.ic_video,"Minimal"))
        templateArrayList.add(TemplateItem(R.drawable.ic_video,"Nature"))
        templateArrayList.add(TemplateItem(R.drawable.ic_video,"Education"))
        templateArrayList.add(TemplateItem(R.drawable.ic_video,"name4"))
        templateArrayList.add(TemplateItem(R.drawable.ic_video,"name5"))
        templateArrayList.add(TemplateItem(R.drawable.ic_video,"name6"))

        return templateArrayList
    }

    override fun onItemClick(position: Int) {
        Toast.makeText(this,"$position clicked", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(SELECTED_TEMPLATE,position)
        startActivity(intent)
    }


    companion object {
        val SELECTED_TEMPLATE = "selected-template"
    }


}