package com.example.animation_to_video.repo

import com.example.animation_to_video.templatemode.datamodel.HorizontalModel
import com.example.animation_to_video.templatemode.datamodel.VerticalModel
import kotlin.random.Random

/**
 * @author Md Jahirul Islam Hridoy
 * Created on 27,March,2022
 */
class TemplatePreviewRepo {
    private val templateRepository = TemplateRepository()
    private val categoryNames = ArrayList<String> ()

    init {
        getFakeCategoryName()
    }

    private fun getFakeCategoryName() {
        categoryNames.add("General")
        categoryNames.add("Education")
        categoryNames.add("Nature")
        categoryNames.add("Technology")
        categoryNames.add("Minimal")
        categoryNames.add("Youtube")
        categoryNames.add("Travel")
        categoryNames.add("Birthday")
        categoryNames.add("Rag-day")
        categoryNames.add("Random")
    }

    fun getCategoryList(): ArrayList<VerticalModel> {
        val retList = ArrayList<VerticalModel>()
        for(i in 0 until categoryNames.size) {
            retList.add(VerticalModel(categoryNames[i],getSingleCategoryTemplateList(i)))
        }
        for(i in 0 until categoryNames.size) {
            retList.add(VerticalModel(categoryNames[i],getSingleCategoryTemplateList(i)))
        }
        return retList
    }

    private fun getSingleCategoryTemplateList(listNumber: Int): ArrayList<HorizontalModel> {
        val retList = ArrayList<HorizontalModel>()
        //for(i in 0 until templateRepository.getTotalTemplateCount()) {
        for(i in 0 until 50) {
            //val singleTemplate = templateRepository.getTemplateData(i)
            val randomIndex = Random.nextInt(templateRepository.getTotalTemplateCount())
            val singleTemplate = templateRepository.getTemplateData(randomIndex)
            retList.add(HorizontalModel(singleTemplate.templateNumber,singleTemplate.templatePreviewImageId,singleTemplate.backgroundType))
        }
        return retList
    }

    fun getCategoryNameBySerial(serial: Int) : String {
        return categoryNames[serial%categoryNames.size]
    }
}