package com.example.restaurant.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.restaurant.R
import com.example.restaurant.api.Network
import com.example.restaurant.classes.DNASnackBar
import com.example.restaurant.db.MenuDAO
import com.example.restaurant.home.HomeActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.iid.FirebaseInstanceId
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.util.concurrent.TimeUnit
import javax.inject.Inject
@Suppress("DEPRECATION")
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    @Inject
    lateinit var api: Network
    @Inject
    lateinit var db: MenuDAO
    private var verificationId: String=""
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()


    lateinit var pref: SharedPreferences
    lateinit var edit: SharedPreferences.Editor

    var s:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.restaurant.R.layout.activity_login)
        buttonGetOTP.visibility= View.VISIBLE
        editTextPhone.visibility= View.VISIBLE
        imageButtonLogin.visibility= View.GONE
        editTextOtp.visibility= View.GONE

        pref=this.getSharedPreferences("appSharedPrefs", Context.MODE_PRIVATE)
        edit=pref.edit()
        if(pref.getBoolean("loggedIn",false)==true &&
            FirebaseInstanceId.getInstance().token.toString()==pref.getString("reg","")){
            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            startActivity(intent, null)
            finish()
        }
        imageButtonLogin.setOnClickListener {

            try{
                if(progressBar2.visibility==View.GONE) {
                    verifyCode(editTextOtp.text.toString())
                }
            }
            catch(err:Exception){
                Log.d("LoginERror",err.toString())
                DNASnackBar.show(this,"There seems to be a problem from our end")

            }

        }
        buttonGetOTP.setOnClickListener{
            try{
                s = editTextPhone.text.toString()
                if (s.length != 10) {
                    DNASnackBar.show(this, "Please Enter valid Number!")
                } else {

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+91$s",
                        60,
                        TimeUnit.SECONDS,
                        this,
                        mCallBack
                    )
                    buttonGetOTP.visibility = View.GONE
                    editTextPhone.visibility = View.GONE
                    imageButtonLogin.visibility = View.VISIBLE
                    editTextOtp.visibility = View.VISIBLE
                }
            }
            catch(err:Exception){
                DNASnackBar.show(this,"There seems to be a problem from our end")
                
            }

        }









    }
    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        progressBar2.visibility=View.VISIBLE
        signInWithCredential(credential)
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {

            mAuth.signInWithCredential(credential)
                .addOnCompleteListener(object : OnCompleteListener<AuthResult?> {
                    override fun onComplete(task: Task<AuthResult?>) {
                        if (task.isSuccessful) {


                            CoroutineScope(Dispatchers.IO).launch {
                                val refreshedToken = FirebaseInstanceId.getInstance().token

                                try{
                                    val p = api.login(
                                        editTextPhone.text.toString(),
                                        refreshedToken.toString()
                                    )
                                    Log.d("p", p.body().toString())
                                    if (p.body()?.message == "SUCCESS") {
                                        edit.putBoolean("loggedIn", true)
                                        edit.putString("reg", refreshedToken.toString())
                                        edit.putString("phone", editTextPhone.text.toString())
                                        edit.apply()
                                        edit.commit()
                                        val intent =
                                            Intent(this@LoginActivity, HomeActivity::class.java)
                                        startActivity(intent, null)
                                        finish()

                                    }
                                }
                                catch (err:java.lang.Exception){
                                DNASnackBar.show(this@LoginActivity,"There seems to be some problem from our end")
                                    Log.d("errrr", err.toString())
                            }


                            }


                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                task.exception.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                            Log.d("errrr", task.exception?.message.toString())

                        }
                    }
                })
        progressBar2.visibility=View.GONE

    }
    private val mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onCodeSent(s: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(s, forceResendingToken)
            verificationId = s
        }

        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
            val code = phoneAuthCredential.smsCode
            if (code != null) {
                editTextOtp.setText(code)


            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            e.message?.let { DNASnackBar.show(this@LoginActivity, it) }
        }
    }




}