package com.example.kedaimbaktimapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.kedaimbaktimapp.databinding.ActivityAddFoodBinding
import java.text.NumberFormat
import java.util.*

class AddFoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddFoodBinding
    private lateinit var sList: ArrayList<String>
    private var current= ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val format: NumberFormat = NumberFormat.getCurrencyInstance()
//        format.setMaximumFractionDigits(0)
//        format.setCurrency(Currency.getInstance("IDR"))
//            format.format(binding.foodPrice)

        sList = arrayListOf()
        sList.add("Nasi Kotak")
        sList.add("Tumpeng")
        sList.add("Nasi Bento")
        val dataAdapter = ArrayAdapter(this, R.layout.selected_item_spinner, sList)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = dataAdapter
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(p0!=null){
//                    (p0.getChildAt(0) as TextView?)?.setTextColor(Color.GRAY)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
        binding.uploadPhoto.setOnClickListener{
            openFileChooser()
        }

        binding.cardView.setOnClickListener {
            openFileChooser()

        }
        binding.foodPrice.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() != current) {
//                    binding.foodPrice.removeTextChangedListener(this)
//
//                    val cleanString: String = s!!.replace("""[$,.]""".toRegex(), "")
//
//                    val parsed = cleanString.toDouble()
//                    val formatted = NumberFormat.getCurrencyInstance().format((parsed / 100))
//
//                    current = formatted
//                    binding.foodPrice.setText(formatted)
//                    binding.foodPrice.setSelection(formatted.length)
//                    binding.foodPrice.addTextChangedListener(this)
                }
            }
        })
    }

    private fun openFileChooser() {
        val intent = Intent()
            .setType("*/*")
            .setAction(Intent.ACTION_GET_CONTENT)

        startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == RESULT_OK) {
            val selectedFile = data?.data // The URI with the location of the file
            binding.foodPicture.setImageURI(selectedFile)
            binding.cardView.visibility = View.VISIBLE
            binding.uploadPhoto.visibility = View.GONE
        }
    }
}