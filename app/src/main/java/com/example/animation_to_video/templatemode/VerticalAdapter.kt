package com.example.animation_to_video.templatemode

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.animation_to_video.AppConstants
import com.example.animation_to_video.R
import com.example.animation_to_video.templatemode.datamodel.VerticalModel

/**
 * @author Md Jahirul Islam Hridoy
 * Created on 27,March,2022
 */
class VerticalAdapter (
    var context: Context, val arrayList: ArrayList<VerticalModel>,
    val listener: HorizontalAdapter.OnItemClickListener
) : RecyclerView.Adapter<VerticalAdapter.VerticalViewHolder>() {

    inner class VerticalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var horizontalRecyclerView: RecyclerView = itemView.findViewById(R.id.rv_horizontal)
        var categoryName: TextView = itemView.findViewById(R.id.tv_catagory_name)
        var expandCategoryButton: TextView = itemView.findViewById(R.id.bt_expand_catagory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerticalViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.template_item_vertical,parent,false)
        return VerticalViewHolder(v)
    }

    override fun onBindViewHolder(holder: VerticalViewHolder, position: Int) {
        val verticalModel = arrayList[position]
        val categoryName = verticalModel.catagoryName
        val singleCategoryTemplates = verticalModel.arrayList
        holder.categoryName.text = categoryName

        val horizontalRecyclerViewAdapter = HorizontalAdapter(context,singleCategoryTemplates,listener)

        holder.horizontalRecyclerView.setHasFixedSize(true)
        holder.horizontalRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
        holder.horizontalRecyclerView.adapter = horizontalRecyclerViewAdapter

        holder.expandCategoryButton.setOnClickListener {
            listener.onItemClick(AppConstants.EXPAND,position)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

}