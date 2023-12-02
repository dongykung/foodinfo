package com.example.foodinfo

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodinfo.Adapter.FoodInfoAdapter
import com.example.foodinfo.Adapter.fooditemclick
import com.example.foodinfo.databinding.ActivityMyMeelBinding
import com.google.android.material.datepicker.DayViewDecorator
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewFacade
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class MyMeelActivity : AppCompatActivity(),fooditemclick {
    private lateinit var binding : ActivityMyMeelBinding
    private lateinit var foodAdapter : FoodInfoAdapter
    private var loadtime : Long ?=null
    private val selectedDates: MutableSet<CalendarDay> = mutableSetOf()
    private val UpdatedFood = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        val isUpdated = result.data?.getBooleanExtra("Updated",false) ?:false
        if(result.resultCode == RESULT_OK && isUpdated){
            Thread{
                updateFoodInfo()
                val food =AppDataBase.getInstance(this)?.foodDao()?.getAll()
                Log.d(TAG,food.toString())
            }.start()
        }



    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyMeelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addFoodButton.setOnClickListener{
            UpdatedFood.launch(Intent(applicationContext,AddFoodActivity::class.java))
        }

        val customCalendar = binding.calendar


        customCalendar.setOnDateChangedListener{ widget,date,seleted->
            clearCustomSelectorForSelectedDates()
            if(seleted){
                selectedDates.add(date)
                updateDecorators()

                val formattedDate = SimpleDateFormat("yyyy년MM월dd일", Locale.KOREA).format(date.date.time)
                Log.d(TAG,formattedDate)
                val loaddata = convertStringToMillis(formattedDate)
                loadtime=loaddata
                Log.d(TAG,loaddata.toString())

                Thread{
                    val foodinfo =AppDataBase.getInstance(this)?.foodDao()?.getFoodByName(loaddata.toString())
                    Log.d(TAG,foodinfo.toString())
                    if (foodinfo != null) {
                        foodAdapter.list = foodinfo
                        runOnUiThread{foodAdapter.notifyDataSetChanged()}
                    }
                }.start()
            }
        }
        initView()

    }





    private fun initView(){

        foodAdapter = FoodInfoAdapter(mutableListOf(),this)
        binding.myMealRecyclerView.apply {
            adapter = foodAdapter
            layoutManager=LinearLayoutManager(applicationContext)

        }
    }

    private fun updateFoodInfo(){
        Thread{
            if (loadtime != null) {
                val updatefoodinfo =AppDataBase.getInstance(this)?.foodDao()?.getFoodByName(loadtime.toString())
                if (updatefoodinfo != null) {
                    foodAdapter.list = updatefoodinfo
                    runOnUiThread{foodAdapter.notifyDataSetChanged()}
                }
            }
        }.start()
    }

    private fun clearCustomSelectorForSelectedDates() {
        for (selectedDate in selectedDates) {
            // 선택한 날짜의 custom_selector 제거
            binding.calendar.removeDecorators()
        }
        // 선택한 날짜 목록 초기화
        selectedDates.clear()
    }
    private fun updateDecorators() {

        // 선택된 날짜에만 decorate를 설정합니다.
        val selectedDecorator = SelectedDecorator(selectedDates,this)
        binding.calendar.addDecorator(selectedDecorator)
    }

    class SelectedDecorator(private val dates : Set<CalendarDay>,private val context:Context):com.prolificinteractive.materialcalendarview.DayViewDecorator{
        override fun shouldDecorate(day: CalendarDay?): Boolean {
            return dates.contains(day)
        }
        override fun decorate(view: DayViewFacade?) {
            view?.setSelectionDrawable(ContextCompat.getDrawable(context, R.drawable.customday)!!)
            view?.addSpan(RelativeSizeSpan(1.4f))
            view?.addSpan(StyleSpan(Typeface.BOLD))
            view?.addSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.startColor)))
        }
    }



    private fun convertStringToMillis(dateString: String): Long {
        try {
            val format = SimpleDateFormat("yyyy년MM월dd일", Locale.KOREA)
            val date = format.parse(dateString)

            // date를 밀리초로 변환
            return date?.time ?: 0
        } catch (e: Exception) {
            e.printStackTrace()
            return 0
        }
    }

    override fun onClick(item: Food) {
        val intent=Intent(this,SeeFoodActivity::class.java)
        intent.putExtra("id",item.id)
        startActivity(intent)
        Log.d(TAG,item.toString())
    }


}