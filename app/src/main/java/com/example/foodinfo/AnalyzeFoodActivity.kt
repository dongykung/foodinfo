package com.example.foodinfo

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.foodinfo.databinding.ActivityAnalyzeFoodBinding
import java.text.SimpleDateFormat
import java.util.*

class AnalyzeFoodActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAnalyzeFoodBinding
    private var nowyear :Int = 2023
    private var nowmonth :Int = 12
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalyzeFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.prebox.setOnClickListener{
          previousmonth()
        }

        binding.nextbox.setOnClickListener{
            nextmonth()
        }

        initViews()


    }

    @SuppressLint("SetTextI18n")
    private fun initViews(){
        val nowdate = nowyear.toString().plus("년").plus(nowmonth.toString()).plus("월")
        val nowmill = MillisMonth(nowdate)
        Log.d(TAG,nowmill.toString())
        Thread{
            val updatefoodinfo =AppDataBase.getInstance(this)?.foodDao()?.getFoodByMonth(nowmill.toString())
            val breakfastList =  updatefoodinfo?.filter { it.typemeal == "조식" }
            val lunchList = updatefoodinfo?.filter { it.typemeal == "중식" }
            val dinnerList = updatefoodinfo?.filter { it.typemeal == "석식" }
            val drinkList = updatefoodinfo?.filter { it.typemeal == "음료" }

            runOnUiThread{
                binding.typemeal1count.text = "총 "+breakfastList?.size.toString().plus("회")
                binding.typemealprice.text = breakfastList?.sumOf { it.price }.toString().plus("원")
                binding.typemealcal.text = breakfastList?.sumOf { it.cal }.toString().plus("Kcal")

                binding.typemeal2count.text = "총 "+lunchList?.size.toString().plus("회")
                binding.typemealprice2.text = lunchList?.sumOf { it.price }.toString().plus("원")
                binding.typemealcal2.text = lunchList?.sumOf { it.cal }.toString().plus("Kcal")

                binding.typemeal3count.text = "총 "+dinnerList?.size.toString().plus("회")
                binding.typemealprice3.text = dinnerList?.sumOf { it.price }.toString().plus("원")
                binding.typemealcal3.text = dinnerList?.sumOf { it.cal }.toString().plus("Kcal")

                binding.typemeal4count.text = "총 "+drinkList?.size.toString().plus("회")
                binding.typemealprice4.text = drinkList?.sumOf { it.price }.toString().plus("원")
                binding.typemealcal4.text = drinkList?.sumOf { it.cal }.toString().plus("Kcal")
            }
        }.start()
        binding.nowdate.text=nowdate
    }

    @SuppressLint("SetTextI18n")
    private fun previousmonth(){
        nowmonth--
        if(nowmonth==0){
            nowyear--
            nowmonth=12
        }
        val nowdate = nowyear.toString().plus("년").plus(nowmonth.toString()).plus("월")
        val nowmill = MillisMonth(nowdate)
        binding.nowdate.text=nowdate
        Thread{
                val updatefoodinfo =AppDataBase.getInstance(this)?.foodDao()?.getFoodByMonth(nowmill.toString())
                val breakfastList =  updatefoodinfo?.filter { it.typemeal == "조식" }
                val lunchList = updatefoodinfo?.filter { it.typemeal == "중식" }
                val dinnerList = updatefoodinfo?.filter { it.typemeal == "석식" }
            val drinkList = updatefoodinfo?.filter { it.typemeal == "음료" }

                    runOnUiThread{
                        binding.typemeal1count.text = "총 "+breakfastList?.size.toString().plus("회")
                        binding.typemealprice.text = breakfastList?.sumOf { it.price }.toString().plus("원")
                        binding.typemealcal.text = breakfastList?.sumOf { it.cal }.toString().plus("Kcal")

                        binding.typemeal2count.text = "총 "+lunchList?.size.toString().plus("회")
                        binding.typemealprice2.text = lunchList?.sumOf { it.price }.toString().plus("원")
                        binding.typemealcal2.text = lunchList?.sumOf { it.cal }.toString().plus("Kcal")

                        binding.typemeal3count.text = "총 "+dinnerList?.size.toString().plus("회")
                        binding.typemealprice3.text = dinnerList?.sumOf { it.price }.toString().plus("원")
                        binding.typemealcal3.text = dinnerList?.sumOf { it.cal }.toString().plus("Kcal")

                        binding.typemeal4count.text = "총 "+drinkList?.size.toString().plus("회")
                        binding.typemealprice4.text = drinkList?.sumOf { it.price }.toString().plus("원")
                        binding.typemealcal4.text = drinkList?.sumOf { it.cal }.toString().plus("Kcal")
                    }
        }.start()

    }

    @SuppressLint("SetTextI18n")
    private fun nextmonth(){
        nowmonth++
        if(nowmonth>=13){
            nowyear++
            nowmonth=1
        }
        val nowdate = nowyear.toString().plus("년").plus(nowmonth.toString()).plus("월")
        val nowmill = MillisMonth(nowdate)
        binding.nowdate.text=nowdate
        Thread{
            val updatefoodinfo =AppDataBase.getInstance(this)?.foodDao()?.getFoodByMonth(nowmill.toString())
            val breakfastList =  updatefoodinfo?.filter { it.typemeal == "조식" }
            val lunchList = updatefoodinfo?.filter { it.typemeal == "중식" }
            val dinnerList = updatefoodinfo?.filter { it.typemeal == "석식" }
            val drinkList = updatefoodinfo?.filter { it.typemeal == "음료" }

            runOnUiThread{
                binding.typemeal1count.text = "총 "+breakfastList?.size.toString().plus("회")
                binding.typemealprice.text = breakfastList?.sumOf { it.price }.toString().plus("원")
                binding.typemealcal.text = breakfastList?.sumOf { it.cal }.toString().plus("Kcal")

                binding.typemeal2count.text = "총 "+lunchList?.size.toString().plus("회")
                binding.typemealprice2.text = lunchList?.sumOf { it.price }.toString().plus("원")
                binding.typemealcal2.text = lunchList?.sumOf { it.cal }.toString().plus("Kcal")

                binding.typemeal3count.text = "총 "+dinnerList?.size.toString().plus("회")
                binding.typemealprice3.text = dinnerList?.sumOf { it.price }.toString().plus("원")
                binding.typemealcal3.text = dinnerList?.sumOf { it.cal }.toString().plus("Kcal")

                binding.typemeal4count.text = "총 "+drinkList?.size.toString().plus("회")
                binding.typemealprice4.text = drinkList?.sumOf { it.price }.toString().plus("원")
                binding.typemealcal4.text = drinkList?.sumOf { it.cal }.toString().plus("Kcal")
            }
        }.start()
    }

    private fun MillisMonth(dateString: String): Long {
        try {
            val format = SimpleDateFormat("yyyy년MM월", Locale.KOREA)
            val date = format.parse(dateString)

            // date를 밀리초로 변환
            return date?.time ?: 0
        } catch (e: Exception) {
            e.printStackTrace()
            return 0
        }
    }
}