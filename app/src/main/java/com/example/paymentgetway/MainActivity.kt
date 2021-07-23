package com.example.paymentgetway

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject

class MainActivity : AppCompatActivity(), PaymentResultListener {

    var name: EditText? = null
    var email:EditText? = null
    var phNo:EditText? = null
    var amount:EditText? = null
    var submit: Button? = null

    private var chackout: Checkout? = null
    private var razorpayKey: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        name = findViewById(R.id.name)
        email = findViewById(R.id.email)
        phNo = findViewById(R.id.phNo)
        amount = findViewById(R.id.amount)
        submit = findViewById(R.id.submit)

        submit?.setOnClickListener(View.OnClickListener {
            if (name?.getText().toString() == null || name?.getText().toString() == "") {
                name?.setError("Please Fillup")
            } else if (email?.getText().toString() == null || email?.getText().toString() == "") {
                email?.setError("Please Fillup")
            } else if (phNo?.getText().toString() == null || phNo?.getText().toString() == "") {
                phNo?.setError("Please Fillup")
            } else if (phNo?.getText().toString().length != 10) {
                phNo?.setError("Please Enter 10 digit phone number")
            } else if (amount?.getText().toString() == null || amount?.getText().toString() == "") {
                amount?.setError("Please Fillup")
            } else if (amount?.getText().toString().toInt() == 0) {
                amount?.setError("Amount should be greater than 0") //Razorpay min amount is 1 Rs.
            } else {
                //you have to convert Rs. to Paisa using multiplication of 100
                val convertedAmount = (amount?.getText().toString().toInt() * 100).toString()
                rezorpayCall(
                    name?.getText().toString(),
                    email?.getText().toString(),
                    phNo?.getText().toString(),
                    convertedAmount
                )
            }
        })

    }

    fun rezorpayCall(name: String?, email: String?, phNo: String?, convertedAmount: String?) {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        razorpayKey =
            "rzp_test_BWCqEBMEOkZDc2" //Generate your razorpay key from Settings-> API Keys-> copy Key Id
        chackout = Checkout()
        chackout!!.setKeyID(razorpayKey)
        try {
            val options = JSONObject()
            options.put("name", name)
            options.put("description", "Razorpay Payment Test")
            options.put("currency", "INR")
            options.put("amount", convertedAmount)
            val preFill = JSONObject()
            preFill.put("email", email)
            preFill.put("contact", phNo)
            options.put("prefill", preFill)
            chackout!!.open(this@MainActivity, options)
        } catch (e: Exception) {
            Toast.makeText(this@MainActivity, "Error in payment: " + e.message, Toast.LENGTH_LONG)
                .show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(razorpayPaymentID: String) {
        // After successful payment Razorpay send back a unique id
        Toast.makeText(
            this@MainActivity,
            "Transaction Successful: $razorpayPaymentID",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onPaymentError(i: Int, error: String) {
        // Error message
        Toast.makeText(this@MainActivity, "Transaction unsuccessful: $error", Toast.LENGTH_LONG)
            .show()
    }
}
