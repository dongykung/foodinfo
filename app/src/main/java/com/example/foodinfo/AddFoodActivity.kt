package com.example.foodinfo

import android.Manifest
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.foodinfo.databinding.ActivityAddFoodBinding
import com.google.android.material.chip.Chip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class AddFoodActivity : AppCompatActivity() {
    companion object{  //자바로 치면 static
        private const val REQUEST_STORAGE_CODE = 100
    }

    private lateinit var binding:ActivityAddFoodBinding

    private var photouri:Uri?=null
    private var date:String?=null
    private var time:String?=null
    private var loadmonth:String?=null
    private lateinit var bitmap:Bitmap
    private  var bytemap : ByteArray ?=null

    private val filterActivityLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == AppCompatActivity.RESULT_OK && it.data !=null) {
                photouri = it.data?.data
                try {
                    photouri?.let {
                         bitmap = if (Build.VERSION.SDK_INT < 28) {
                            MediaStore.Images.Media.getBitmap(this.contentResolver, it)
                        } else {
                            val source = ImageDecoder.createSource(this.contentResolver, it)
                             val iamgebitmap = ImageDecoder.decodeBitmap(source)
                             binding.foodImageView.setImageBitmap(iamgebitmap)
                            ImageDecoder.decodeBitmap(source)
                        }

                        bytemap = bitmap.toByteArray()!!
                    }
                }catch(e:Exception) {
                    e.printStackTrace()
                }
            } else if(it.resultCode == AppCompatActivity.RESULT_CANCELED){
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }else{
                Log.d("ActivityResult","something wrong")
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val toolbar=binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "음식 추가"

        binding.addressSpinner.adapter = ArrayAdapter.createFromResource(
            this,
            R.array.address,
            android.R.layout.simple_list_item_1
        )

        binding.datebox.setOnClickListener{
            showDateDialog()
        }

        binding.timebox.setOnClickListener{
            if(date==null){
                Toast.makeText(this,"날짜 먼저 입력해 주세요",Toast.LENGTH_SHORT).show()
            }
            else{
                showTimeDialog()
            }
        }

        binding.foodImageView.setOnClickListener{
            ImagecheckPermission()
        }

        binding.UploadButton.setOnClickListener{
            if(bytemap==null){
                Toast.makeText(this,"음식 사진을 업로드 해주세요",Toast.LENGTH_SHORT).show()
            }
            else if(date==null){
                Toast.makeText(this,"날짜를 입력해주세요",Toast.LENGTH_SHORT).show()
            }
            else if(time==null){
                Toast.makeText(this,"시간을 입력해 주세요",Toast.LENGTH_SHORT).show()
            }
            else if(binding.typeMeal.checkedChipId==-1){
                Toast.makeText(this,"조식,중식,석식 중 한가지를 고르세요",Toast.LENGTH_SHORT).show()
            }
            else if(binding.textFoodInputEditText.text.toString()==""){
                Toast.makeText(this,"음식을 입력해주세요",Toast.LENGTH_SHORT).show()
            }
            else if(binding.foodExplainInputEditText.text.toString()==""){
                Toast.makeText(this,"음식 설명을 입력해주세요",Toast.LENGTH_SHORT).show()
            }
            else if(binding.priceInputEditText.text.toString()==""){
                Toast.makeText(this,"가격을 입력해주세요",Toast.LENGTH_SHORT).show()
            }
            else{
                uploadFood()
            }
        }


        initView()

    }

    private fun initView(){
        val types = listOf("조식","중식","석식","음료")
        binding.typeMeal.apply {
            types.forEach{  it->
                addView(createChip(it))
            }
        }
    }


    private fun ImagecheckPermission(){
        when {
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
               loadImage()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                showPermissionRationalDialog()
            }
            else -> {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    100
                )
            }
        }
    }
    private fun showPermissionRationalDialog(){
        AlertDialog.Builder(this).apply {
            setMessage("사진을 불러오기 위해서 이미지 접근 권한이 필요합니다")
            setPositiveButton("허용"){_,_->
                ActivityCompat.requestPermissions(
                    this@AddFoodActivity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_STORAGE_CODE
                )
            }
            setNegativeButton("거부",null)
        }.show()
    }

    private fun showPermissionDisallowDialog(){
        AlertDialog.Builder(this).apply {
            setMessage("사진을 불러오기 위해서 이미지 접근 권한이 필요합니다")
            setPositiveButton("권한 변경하러 가기"){_,_->
                navigagteToAppsetting()
            }
            setNegativeButton("거부",null)
        }.show()
    }

    private fun navigagteToAppsetting(){
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package",packageName,null)
        }
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val StoragePermissionGranted = requestCode == REQUEST_STORAGE_CODE && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED
        if(StoragePermissionGranted){
            loadImage()
        }else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_EXTERNAL_STORAGE))  {
                showPermissionRationalDialog()
            }else{
                showPermissionDisallowDialog()
            }
        }
    }

    private fun loadImage(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        filterActivityLauncher.launch(intent)
    }




    private fun createChip(text:String):Chip{
        return Chip(this).apply {
            setText(text)
            isCheckable=true
            isClickable=true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                finish()
                Log.d(TAG,binding.addressSpinner.selectedItem.toString())
                Log.d(TAG,binding.textFoodInputEditText.text.toString())
                Log.d(TAG,binding.typeMeal.checkedChipId.toString())
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }



    private fun showDateDialog() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val DatePickerDialog = Dialog(this)
        DatePickerDialog.setContentView(R.layout.datedialog)
        DatePickerDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val datePicker = DatePickerDialog.findViewById<DatePicker>(R.id.fooddatepicker)
        val dateokButton = DatePickerDialog.findViewById<Button>(R.id.dateokButton)
        datePicker.init(currentYear, currentMonth, currentDay, null)
        dateokButton.setOnClickListener{
            val year= datePicker.year.toString()
            val month=if(datePicker.month<9)"0"+(datePicker.month+1).toString() else (datePicker.month+1).toString()
            val day=if(datePicker.dayOfMonth<10) "0"+datePicker.dayOfMonth.toString() else datePicker.dayOfMonth.toString()
            date=year.plus("년").plus(month+"월").plus(day+"일")
            loadmonth=year.plus("년").plus(month+"월")
            binding.dateText.setTextColor(Color.BLACK)
           binding.dateText.text=date
            DatePickerDialog.dismiss()
        }
        DatePickerDialog.show()
    }

    private fun showTimeDialog(){
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val TimePickerDialog = Dialog(this)
        TimePickerDialog.setContentView(R.layout.timedialog)
        TimePickerDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val timePicker = TimePickerDialog.findViewById<TimePicker>(R.id.foodtimepicker)
        val timeokButton = TimePickerDialog.findViewById<Button>(R.id.timeokButton)
        timePicker.setIs24HourView(true)
        timePicker.hour = currentHour
        timePicker.minute = currentMinute
        timeokButton.setOnClickListener{
            val hour=if(timePicker.hour<10)"0"+(timePicker.hour).toString()else (timePicker.hour).toString()
            val min=if(timePicker.minute<10)"0"+(timePicker.minute).toString() else timePicker.minute.toString()
            time=hour.plus("시").plus(min+"분")
            binding.timeText.setTextColor(Color.BLACK)
            binding.timeText.text=time
            TimePickerDialog.dismiss()
        }
        TimePickerDialog.show()
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
    private fun convertStringToMillisMonth(dateString: String): Long {
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

    private fun convertStringToMillis2(dateString: String): Long {
        try {
            val format = SimpleDateFormat("yyyy년MM월dd일HH시mm분", Locale.KOREA)
            val date = format.parse(dateString)

            // date를 밀리초로 변환
            return date?.time ?: 0
        } catch (e: Exception) {
            e.printStackTrace()
            return 0
        }
    }

    private fun uploadFood(){
        val address = binding.addressSpinner.selectedItem.toString()
        val typemeal = findViewById<Chip>(binding.typeMeal.checkedChipId).text.toString()
        val uploaddate = convertStringToMillis(date!!).toString()
        val uploaddatetime = convertStringToMillis2(date?.plus(time)!!).toString()
        val foodname = binding.textFoodInputEditText.text.toString()
        val month = convertStringToMillisMonth(loadmonth!!).toString()
        val review = binding.foodExplainInputEditText.text.toString()
        val price = binding.priceInputEditText.text.toString().toInt()
        val cal = (50..150).random()

        val food = Food(address=address,
                        typemeal=typemeal,
                        date=uploaddate,
                        datetime=uploaddatetime,
                        datemonth = month,
                        foodname=foodname,
                        imageuri=bytemap!!,
                        review = review,
                        price = price,
                        cal = cal)

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                // IO 스레드에서 데이터베이스 작업 수행
                AppDataBase.getInstance(this@AddFoodActivity)?.foodDao()?.insert(food)
            }
            runOnUiThread{ Toast.makeText(applicationContext,"저장 완료",Toast.LENGTH_SHORT).show()}
            val intent = Intent().putExtra("Updated",true)
            Log.d(TAG,month.toString())
            setResult(RESULT_OK,intent)
            finish()
        }


    }


}