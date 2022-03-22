package com.example.animation_to_video.templatemode


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.animation_to_video.databinding.TemplateItemLayoutBinding

/**
 * @author Md Jahirul Islam Hridoy
 * Created on 22,March,2022
 */
class TemplateAdapter:  RecyclerView.Adapter<TemplateAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: TemplateItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)
    private var templateList: List<TemplateItem> = arrayListOf()
    private var listener: OnItemClickListener?= null

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setListener(mListener: OnItemClickListener) {
        listener = mListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = TemplateItemLayoutBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        with(holder) {
            binding.templateImageView.setImageResource(templateList[position].image)
            binding.templateName.text = templateList[position].name

            binding.layoutRoot.setOnClickListener {
                if(position != RecyclerView.NO_POSITION) {
                    listener?.onItemClick(position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return templateList.size
    }

    fun setTemplate(templateList: List<TemplateItem>) {
        this.templateList = templateList
    }
    
    companion object {
        private const val TAG = "TemplateAdapter"
    }

}