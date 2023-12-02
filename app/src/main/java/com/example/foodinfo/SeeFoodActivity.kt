package com.example.foodinfo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.foodinfo.databinding.ActivitySeeFoodBinding
import java.text.SimpleDateFormat
import java.util.*

class SeeFoodActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySeeFoodBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeeFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initview()
    }


    private fun initview(){
        val id = intent.getIntExtra("id",0)
        Thread{
            val updatefoodinfo =AppDataBase.getInstance(this)?.foodDao()?.getFoodid(id)
            runOnUiThread{
                binding.seeFoodImage.setImageBitmap(updatefoodinfo?.imageuri?.toBitmap())
                binding.seeFoodAddress.text = updatefoodinfo?.address
                binding.seeFoodKcal.text=updatefoodinfo?.cal.toString()+" Kcal"
                binding.seeFoodExplainText.text=updatefoodinfo?.review
                binding.seeFoodPrice.text = updatefoodinfo?.price.toString()+"원"
                binding.seeFoodTypeMeal.text=updatefoodinfo?.typemeal
                binding.seeFoodName.text=updatefoodinfo?.foodname
                binding.seedateText.text=updatefoodinfo?.date
            }
        }.start()




    }

   private fun convertMillisToString(millis: Long): String {
        val convert = SimpleDateFormat("yyyy년MM월dd일HH시mm분", Locale.KOREA)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis

        return convert.format(calendar.time)
    }
}