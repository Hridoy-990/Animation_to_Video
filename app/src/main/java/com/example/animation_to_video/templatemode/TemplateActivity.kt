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
    private val templateData = TemplateData()

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
        templateArrayList.add(TemplateItem(R.drawable.tmpbg0,"Template 0"))
        templateArrayList.add(TemplateItem(R.drawable.tmpbg1,"Template 1"))
        templateArrayList.add(TemplateItem(R.drawable.tmpbg2,"Template 2"))
        templateArrayList.add(TemplateItem(R.drawable.tmpbg3,"Template 3"))
        templateArrayList.add(TemplateItem(R.drawable.tmpbg4,"Template 4"))
        templateArrayList.add(TemplateItem(R.drawable.ic_video,"Template 5"))

        return templateArrayList
    }

    override fun onItemClick(position: Int) {
        Toast.makeText(this,"$position clicked", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(TEMPLATE_DATA, templateData.data[position])
        startActivity(intent)
    }


    companion object {
        const val SELECTED_TEMPLATE = "selected-template"
        const val TEMPLATE_DATA = "template-data"
    }


}