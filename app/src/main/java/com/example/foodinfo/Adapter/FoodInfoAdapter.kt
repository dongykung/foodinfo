package com.example.foodinfo.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.foodinfo.Food
import com.example.foodinfo.databinding.ItemfoodinfoBinding
import com.example.foodinfo.toBitmap
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class FoodInfoAdapter(var list:MutableList<Food>,private val listener:fooditemclick): RecyclerView.Adapter<FoodInfoAdapter.FoodInfoViewHolder>() {

    inner class FoodInfoViewHolder(val binding : ItemfoodinfoBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodInfoViewHolder {
      val inflater =parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = ItemfoodinfoBinding.inflate(inflater,parent,false)
        return FoodInfoViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: FoodInfoViewHolder, position: Int) {
       holder.binding.apply {
            recyclerviewtypeMeal.text =list[position].typemeal
           recyclerviewfoodName.text=list[position].foodname
           recyclerviewKcal.text=list[position].cal.toString()+" Kcal"
           val test = list[position].imageuri.toBitmap()
           recyclerviewfoodImage.setImageBitmap(test)

           worditem.setOnClickListener{
               listener.onClick(list[position])
           }
       }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

interface fooditemclick{
    fun onClick(item:Food)
}