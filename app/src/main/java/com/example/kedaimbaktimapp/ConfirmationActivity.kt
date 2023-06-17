package com.example.kedaimbaktimapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.kedaimbaktimapp.data.response.transactionResponse
import com.example.kedaimbaktimapp.data.retrofit.ApiConfig
import com.example.kedaimbaktimapp.databinding.ActivityConfirmationBinding
import com.example.kedaimbaktimapp.model.Food
import com.example.kedaimbaktimapp.model.Location
import com.example.kedaimbaktimapp.model.Transaction
import com.example.kedaimbaktimapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.BillingAddress
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.ShippingAddress
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


class ConfirmationActivity : AppCompatActivity(), TransactionFinishedCallback {

    private lateinit var binding: ActivityConfirmationBinding
    private lateinit var auth: FirebaseAuth
    private var foodPrice: Double = 0.0
    private lateinit var builderDialog: AlertDialog.Builder
    private lateinit var alertDialog: AlertDialog
    private lateinit var foodName: String
    private lateinit var userData: User
    private lateinit var locationData: Location
    private lateinit var transactionId: String
    private lateinit var userId: String
    private lateinit var foodId: String
    private var quantity: Int = 0
    private var totalPay: Int = 0
    private lateinit var sendingDate: String
    private lateinit var orderType: String
    private lateinit var note: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setTitle("Confirmation")


        //get foodId and userId
        foodId = intent.getStringExtra("foodId").toString()
        auth = Firebase.auth
        val firebaseUser = auth.currentUser
        userId = firebaseUser?.uid.toString()

        //get data from checkout
        quantity = intent.getIntExtra("quantity", 0)
        val date = intent.getStringExtra("date")
        val time = intent.getStringExtra("time")
        totalPay = intent.getIntExtra("total_pay", 0)
        orderType = intent.getStringExtra("orderType").toString()
        note = intent.getStringExtra("note").toString()
        sendingDate = date + " " + time

        val key = FirebaseDatabase.getInstance().getReference("Transaction").push().key
        transactionId = key.toString()

        if (foodId != null && userId != null) {
            showDataFood(foodId)
            showDataFromCheckout(
                quantity, date.toString(),
                time.toString(), totalPay, orderType, note
            )
            showDataUser(userId)
            showDataLocation(userId)
            getAdminId()

            SdkUIFlowBuilder.init()
                .setClientKey("SB-Mid-client-dNdDT3kZjkK5dKLz") // client_key is mandatory
                .setContext(applicationContext) // context is mandatory
                .setTransactionFinishedCallback(this)
                .setMerchantBaseUrl("https://catechumenical-rece.000webhostapp.com/index.php/") //set merchant url (required)
                .enableLog(true) // enable sdk log (optional)
                .setColorTheme(
                    CustomColorTheme(
                        "#424242",
                        "#424242",
                        "#424242"
                    )
                ) // set theme. it will replace theme on snap theme on MAP ( optional)
                .setLanguage("id") //`en` for English and `id` for Bahasa
                .buildSDK()

            binding.payButton.setOnClickListener {
                paymentMidtrans(transactionId, userId, foodId, totalPay, quantity)

            }
        }
    }

    private fun paymentMidtrans(
        tid: String,
        userId: String,
        foodId: String,
        totalPay: Int,
        quantity: Int
    ) {

        val transactionRequest = TransactionRequest(tid, totalPay.toDouble())
        val detail =
            com.midtrans.sdk.corekit.models.ItemDetails(foodId, foodPrice, quantity, foodName)
        val itemDetails = ArrayList<com.midtrans.sdk.corekit.models.ItemDetails>()
        itemDetails.add(detail)
        uiKitDetails(transactionRequest, userId)
        transactionRequest.itemDetails = itemDetails
        MidtransSDK.getInstance().transactionRequest = transactionRequest
        MidtransSDK.getInstance().startPaymentUiFlow(this)
    }

    private fun uiKitDetails(transactionRequest: TransactionRequest, userId: String) {
        val customerDetails = CustomerDetails()
        customerDetails.customerIdentifier = userId
        customerDetails.phone = userData.number
        customerDetails.firstName = userData.name
        customerDetails.email = userData.email

        val shippingAddress = ShippingAddress()
        shippingAddress.address = locationData.address
        shippingAddress.city = locationData.regency
        shippingAddress.postalCode = locationData.postalcode
        customerDetails.shippingAddress = shippingAddress

        val billingAddress = BillingAddress()
        billingAddress.address = locationData.address
        billingAddress.city = locationData.regency
        billingAddress.postalCode = locationData.postalcode
        customerDetails.billingAddress = billingAddress

        transactionRequest.customerDetails = customerDetails
    }

    private fun openMaps(lat: String, lng: String) {
        val geoUri =
            "http://maps.google.com/maps?q=loc:" + lat + "," + lng
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
        startActivity(intent)
    }

    private fun showDataUser(userId: String) {
        FirebaseDatabase.getInstance().getReference("Registered Users").child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user =
                        snapshot.getValue(com.example.kedaimbaktimapp.model.User::class.java)
                    if (user != null) {
                        binding.userName.text = user.name
                        binding.userNumber.text = user.number
                        userData = user
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun createTransaction(
        transactionId: String,
        userId: String,
        foodId: String,
        quantity: Int,
        totalPay: Int,
        dateOrder: String,
        dateSend: String,
        orderType: String,
        transactionStatus: String,
        note: String
    ) {

        val transaction = Transaction(
            transactionId, userId, foodId, quantity, totalPay,
            dateOrder, dateSend, orderType, transactionStatus, note
        )

        //create transaction
        FirebaseDatabase.getInstance().getReference("Transaction").child(transactionId)
            .setValue(transaction).addOnCompleteListener { task ->
            }

        //create transaction user
        FirebaseDatabase.getInstance().getReference("Registered Users").child(userId)
            .child("transaction").child(transactionId).setValue(transaction)
            .addOnCompleteListener { task ->
            }

        createLocationforTransaction(transactionId)
    }

    private fun createLocationforTransaction(key: String) {
        FirebaseDatabase.getInstance().getReference("Location").child(key)
            .setValue(locationData)
    }

    private fun showDataFromCheckout(
        quantity: Int,
        date: String,
        time: String,
        totalPay: Int,
        orderType: String,
        note: String
    ) {
        binding.quantity.text = quantity.toString() + " Item"
        binding.orderDate.text = getCurrentTime()
        binding.sendingDate.text = date + " " + time
        binding.totalBayarInfo.text = getCurrencyFormat(totalPay)
        binding.totalBayar.text = getCurrencyFormat(totalPay)
        binding.orderType.text = orderType
        binding.note.text = note
    }

    private fun showDataFood(foodId: String) {
        FirebaseDatabase.getInstance().getReference("Food").child(foodId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val food = snapshot.getValue(Food::class.java)
                    if (food != null) {
                        Glide.with(applicationContext)
                            .load(food.photo)
                            .centerCrop()
                            .into(binding.foodImg)
                        binding.foodName.text = food.name
                        binding.foodPrice.text = getCurrencyFormat(food.price)
                        foodPrice = food.price.toDouble()
                        foodName = food.name.toString()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun showDataLocation(uid: String) {
        FirebaseDatabase.getInstance().getReference("Location").child(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val location =
                        snapshot.getValue(Location::class.java)
                    if (location != null) {
                        binding.customerLoc.text =
                            location.province + ", " + location.regency + ", " + location.subdistrict + ", " + location.area + ", " + location.address + " (" + location.postalcode + ")"
                        binding.maps.setOnClickListener {
                            openMaps(location.lat, location.lng)
                        }
                        locationData = location
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    private fun getAdminId() {
        FirebaseDatabase.getInstance().getReference("Registered Users").orderByChild("admin")
            .equalTo(true).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (itemSnapshot in snapshot.children) {
                        val adminId = itemSnapshot.key.toString()
                        cateringLocaction(adminId)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    private fun cateringLocaction(adminId: String) {
        FirebaseDatabase.getInstance().getReference("Location").child(adminId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val location =
                        snapshot.getValue(Location::class.java)
                    if (location != null) {
                        binding.cateringLoc.text =
                            location.province + ", " + location.regency + ", " + location.subdistrict + ", " + location.area + ", " + location.address + " (" + location.postalcode + ")"
                        binding.maps2.setOnClickListener {
                            openMaps(location.lat, location.lng)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun getCurrencyFormat(price: Int): String? {
        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.setMaximumFractionDigits(0)
        format.setCurrency(Currency.getInstance("IDR"))
        val priceIdr = format.format(price)
        return priceIdr
    }

    private fun getCurrentTime(): String {
        val formatter = SimpleDateFormat("EEEE, dd-MM-yyyy HH:mm")
        val curentDate = Date()
        val current = formatter.format(curentDate)
        return current
    }

    override fun onTransactionFinished(result: TransactionResult?) {
        if (result != null) {
            if (result.response != null) {
                when (result.status) {
                    TransactionResult.STATUS_SUCCESS -> showSettingDialog(R.layout.transaction_success, 1)
                    TransactionResult.STATUS_PENDING -> showSettingDialog(R.layout.transaction_success, 2)
                    TransactionResult.STATUS_FAILED -> showSettingDialog(R.layout.transaction_fail, 3)
                }
            } else if (result.isTransactionCanceled) {
                Toast.makeText(this, "Transaction Canceled", Toast.LENGTH_LONG).show()
            } else {
                if (result.status.equals(TransactionResult.STATUS_INVALID, ignoreCase = true)) {
                    Toast.makeText(
                        this,
                        "Transaction Invalid" + result.response.transactionId,
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(this, "Something Wrong", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showSettingDialog(adminSettings: Int, code: Int) {
        builderDialog = AlertDialog.Builder(this)
        val layout: View = layoutInflater.inflate(adminSettings, null)
        val button: Button = layout.findViewById(R.id.back)

        builderDialog.setView(layout)
        alertDialog = builderDialog.create()
        alertDialog.show()

        button.setOnClickListener {
            if(code == 1) {
                createTransaction(transactionId, userId, foodId, quantity, totalPay, sendingDate, getCurrentTime(), orderType, "Belum diproses", note)
            } else if (code == 2){
                createTransaction(transactionId, userId, foodId, quantity, totalPay, sendingDate, getCurrentTime(), orderType, "Belum dibayar", note)
            }
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


}
