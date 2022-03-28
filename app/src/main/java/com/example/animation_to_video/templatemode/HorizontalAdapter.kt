package com.example.animation_to_video.templatemode

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.animation_to_video.AppConstants
import com.example.animation_to_video.R
import com.example.animation_to_video.templatemode.datamodel.HorizontalModel
import java.util.ArrayList

/**
 * @author Md Jahirul Islam Hridoy
 * Created on 27,March,2022
 */
class HorizontalAdapter (val context: Context, val templateList: ArrayList<HorizontalModel>,
                         val listener: OnItemClickListener) :
    RecyclerView.Adapter<HorizontalAdapter.HorizontalViewHolder>()

{
    inner class HorizontalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val previewImageView: ImageView = itemView.findViewById(R.id.iv_template_preview)
        val typeTextView: TextView = itemView.findViewById(R.id.tv_template_background_type)
        val makeFavourite: ImageView = itemView.findViewById(R.id.iv_make_favourite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalAdapter.HorizontalViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.template_item_horizontal,parent,false)
        return HorizontalViewHolder(v)
    }

    override fun onBindViewHolder(holder: HorizontalAdapter.HorizontalViewHolder, position: Int) {
        val horizontalModel = templateList[position]
        //holder.imageView.setImageResource(horizontalModel.imageId)
        Glide.with(context).load(horizontalModel.imageId).into(holder.previewImageView)
        holder.typeTextView.text = horizontalModel.backgroundType
        holder.itemView.setOnClickListener {
            listener.onItemClick(AppConstants.TEMPLATE,horizontalModel.templateNumber)
        }
        holder.makeFavourite.setOnClickListener {
            Toast.makeText(context,"Coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return templateList.size
    }

    interface OnItemClickListener {
        fun onItemClick(type: String,position: Int)
    }
}
