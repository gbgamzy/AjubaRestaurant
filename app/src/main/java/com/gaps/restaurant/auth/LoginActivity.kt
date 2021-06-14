package com.gaps.restaurant.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gaps.restaurant.api.Network
import com.gaps.restaurant.classes.DNASnackBar
import com.gaps.restaurant.classes.Food
import com.gaps.restaurant.classes.FoodMenu
import com.gaps.restaurant.classes.Image
import com.gaps.restaurant.db.MenuDAO
import com.gaps.restaurant.home.HomeActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken

import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.TimeUnit
import javax.inject.Inject



@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    @Inject
    lateinit var api: Network

    @Inject
    lateinit var db: MenuDAO
    var i = 0
    private var verificationId: String? = null
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val KEY_VERIFICATION_ID = "key_verification_id"


    lateinit var pref: SharedPreferences
    lateinit var edit: SharedPreferences.Editor
    var name = ""
    var logging: Boolean = false
    var state: Int = 1

    var s: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.gaps.restaurant.R.layout.activity_login)
        buttonGetOTP.visibility = View.VISIBLE
        editTextPhone.visibility = View.VISIBLE
        imageButtonLogin.visibility = View.GONE
        editTextOtp.visibility = View.GONE

        pref = this.getSharedPreferences("appSharedPrefs", Context.MODE_PRIVATE)
        edit = pref.edit()

        if (pref.getBoolean("loggedIn", false)) {
            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            startActivity(intent, null)
            finish()
        }
        else{
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    reloadMenu()
                }

            }
            catch(err:Exception){
                Log.w("errorreload",err.toString())

            }
        }
        imageButtonLogin.setOnClickListener {
            if (!logging) {
                logging = true
                progressBar2.visibility = View.VISIBLE
                try {


                    if ((verificationId == "" || verificationId == null) && savedInstanceState != null) {
                        onRestoreInstanceState(savedInstanceState)


                        verificationId?.let { it1 -> Log.d("NullCheck1", it1) }

                    }
                    verifyCode(editTextOtp.text.toString())

                } catch (err: Exception) {
                    Log.d("LoginERror", err.toString())
                    DNASnackBar.show(applicationContext, "There seems to be a problem from our end")
                    logging = false

                }
            }

        }
        buttonGetOTP.setOnClickListener {
            try {
                s = editTextPhone.text.toString()
                if (etName.text.toString().length < 4)
                    DNASnackBar.show(applicationContext, "Enter a proper name")
                else {
                    if (s.length != 10) {
                        DNASnackBar.show(applicationContext, "Please Enter valid Number!")
                    } else {

                        name = etName.text.toString()
                        edit.putString("name", name)


                        val options = PhoneAuthOptions.newBuilder(mAuth)
                            .setPhoneNumber("+91$s") // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(this) // Activity (for callback binding)
                            .setCallbacks(mCallBack) // OnVerificationStateChangedCallbacks
                            .build()
                        try {
                            Log.d("otpE", PhoneAuthProvider.verifyPhoneNumber(options).toString())
                            if (savedInstanceState != null) {
                                onSaveInstanceState(savedInstanceState)
                            }
                        } catch (err: Exception) {
                            Log.d("OTP ERROR", err.toString())
                        }
                        buttonGetOTP.visibility = View.GONE
                        editTextPhone.visibility = View.GONE
                        etName.visibility = View.GONE
                        imageButtonLogin.visibility = View.VISIBLE
                        editTextOtp.visibility = View.VISIBLE
                    }
                }
            } catch (err: Exception) {
                progressBar2.visibility = View.GONE
                Toast.makeText(
                    this@LoginActivity,
                    "There seems to be a problem from our end",
                    Toast.LENGTH_LONG
                ).show()
                logging = false


            }

        }


    }

    private fun verifyCode(code: String) {

        verificationId?.let { Log.d("vmverificationCode", it) }
        val credential = verificationId?.let { PhoneAuthProvider.getCredential(it, code) }



        progressBar2.visibility = View.VISIBLE
        if (credential != null) {
            signInWithCredential(credential)
        }
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {

        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(object : OnCompleteListener<AuthResult?> {
                override fun onComplete(task: Task<AuthResult?>) {
                    if (task.isSuccessful) {
                        var refreshedToken = ""



                        try {
                            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                                if (it.isSuccessful) {
                                    refreshedToken = it.result.toString()
                                    Log.d("REGTOKEN", "SUCCESS" + refreshedToken)
                                    CoroutineScope(Dispatchers.IO).launch {
                                        val p = api.login(
                                            editTextPhone.text.toString(),
                                            refreshedToken,
                                            name
                                        )

                                        Log.d("p", p.body().toString())
                                        if (p.body()?.message == "SUCCESS") {

                                            edit.putBoolean("loggedIn", true)
                                            edit.putString("reg", refreshedToken)
                                            edit.putString(
                                                "phone",
                                                editTextPhone.text.toString()
                                            )
                                            edit.apply()
                                            edit.commit()
                                            progressBar2.visibility = View.GONE
                                            val intent =
                                                Intent(
                                                    this@LoginActivity,
                                                    HomeActivity::class.java
                                                )

                                            if (state == 1) {
                                                state = 0
                                                startActivity(intent, null)
                                                finish()
                                                state = 1
                                            }
                                        } else {
                                            DNASnackBar.show(
                                                applicationContext,
                                                "There is some error"
                                            )
                                            progressBar2.visibility = View.GONE

                                        }


                                    }

                                }
                                if (it.isCanceled) {
                                    Log.d("REGTOKENCANCELLED", it.result.toString())
                                }
                            }

                        } catch (err: java.lang.Exception) {
                            Log.d("REGTOKENERROR", err.toString())
                            progressBar2.visibility = View.GONE
                            DNASnackBar.show(
                                applicationContext,
                                "There seems to be some problem from our end"
                            )

                            logging = false
                        }


                    } else {
                        progressBar2.visibility = View.GONE
                        Toast.makeText(
                            this@LoginActivity,
                            task.exception.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                        logging = false
                        Log.d("errrr", task.exception?.message.toString())
                        DNASnackBar.show(applicationContext, "Otp might be wrong")

                    }
                }
            })
        progressBar2.visibility = View.GONE

    }

    private val mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(
                s: String,
                forceResendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(s, forceResendingToken)
                Log.d("code", "onCodeSent:" + s)
                verificationId = s


                val mResendToken: ForceResendingToken = forceResendingToken


            }

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                progressBar2.visibility = View.GONE
                signInWithCredential(phoneAuthCredential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.d("OTP ERROR", e.toString())
                DNASnackBar.show(applicationContext, "OTP seems to be wrong.")

            }
        }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_VERIFICATION_ID, verificationId)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        verificationId = savedInstanceState.getString(KEY_VERIFICATION_ID)!!
    }

    suspend fun reloadMenu(): Boolean {
        try {
            db.clearCart()
            db.clearMenu()
            val foodList: ArrayList<Food> = ArrayList()
            val list: List<FoodMenu> = api.getMenu().body()!!
            Log.d("vmvm", list.toString())

            val list1: ArrayList<FoodMenu> = ArrayList()


            val foods = api.getFood().body()
            if (foods != null) {
                foodList.addAll(foods)
            }
            val arrayList: ArrayList<Food> = ArrayList()
            arrayList.addAll(foodList.sortedWith(compareBy { it.name }))

            var allFood = FoodMenu(
                category = "All",
                list = arrayList
            )
            list1.add(allFood)
            list.forEach { menu ->
                var fl: ArrayList<Food> = ArrayList()
                foodList.forEach {


                    if (it.category == menu.category) {
                        fl.add(it)


                    }
                }
                Log.d("FoodMenuList", fl.toString())
                menu.list = fl

            }

            list.forEach {


                db.addMenu(it)
                it.list.forEach { t ->

                    db.addToCart(t)

                }


            }



            reloadImages(foodList)

            return true

        } catch (err: Exception) {
            Log.d("vmErrorReloadMenu", err.toString())
            return false

        }


    }

    private suspend fun reloadImages(foodList: ArrayList<Food>) {
        try {
            var images = ArrayList<Image>()
            foodList.forEach {


                var body = api.getImage(it.image).body()
                val futureStudioIconFile: File =
                    File(this.getExternalFilesDir(null), File.separator + it.name + ".jpg")

                var inputStream: InputStream? = null
                var outputStream: OutputStream? = null

                val fileReader = ByteArray(4096)
                val fileSize = body!!.contentLength()
                var fileSizeDownloaded: Long = 0
                inputStream = body.byteStream()
                outputStream = FileOutputStream(futureStudioIconFile)

                while (true) {
                    var read = inputStream.read(fileReader)


                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read

                }
                outputStream.flush()
                inputStream.close()
                outputStream.close()


            }

        } catch (err: Exception) {
            Log.d("vmErrorReloadImages", err.toString())

        }


    }

}