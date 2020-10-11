package wmi.zbsm

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import javax.crypto.spec.IvParameterSpec
import kotlinx.android.synthetic.main.activity_password_setting.*




class PasswordSetting : AppCompatActivity() {
    private val PRIVATEMODE = Context.MODE_PRIVATE
    private val firstSharedKey = "FirstRun"
    private val passSharedKey = "PasswordSetting"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_setting)

        val savePassBtn = findViewById<Button>(R.id.savePass) as Button
        val sharedFirstPref = getSharedPreferences(firstSharedKey, PRIVATEMODE)

        if (sharedFirstPref.getBoolean(firstSharedKey, false)) {

            Log.d("APP", "not first run")

            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
            finish()

        } else {

            Log.d("APP", "first run")

            val sharedPassPref = getSharedPreferences(passSharedKey, PRIVATEMODE)




            if( sharedPassPref.contains(passSharedKey) ){

                Log.d("APP-shared", sharedPassPref.getString(passSharedKey,"null"))
                Log.d("APP", "passworShared exist")
                val mainIntent = Intent(this, Login::class.java)
                startActivity(mainIntent)
                finish()
            } else {

                Log.d("APP-shared", sharedPassPref.getString(passSharedKey,"null"))
                Log.d("APP", "passworShared not exist")
                savePassBtn?.setOnClickListener{

                    val editTextPassword = findViewById(R.id.newPass) as? EditText
                    val passwordString = editTextPassword?.getText().toString()

                    if( passwordString.isNullOrEmpty() ){

                        editTextPassword?.setError("Enter password!")

                    } else {

                        if( passwordString.length < 8 ){
                            editTextPassword?.setError("Enter minimum 8 characters!")
                        } else {

                            Log.d("APP-shared",sharedPassPref.getString(passSharedKey,"no ni ma"))
                            val sharedPassEdit: SharedPreferences =
                                this@PasswordSetting.getPreferences(PRIVATEMODE)
                            val editor = sharedPassEdit.edit()
                            editor.clear()
                            editor.putString(passSharedKey, passwordString)
                            val zapisano = editor.commit()
                            Log.d("APP-shared",passwordString)
                            Log.d("APP-shared",zapisano.toString())
                            Log.d("APP-shared",sharedPassPref.getString(passSharedKey,"no ni ma"))
                            Log.d("APP-shared",sharedPassPref.contains(passSharedKey).toString())


                            val intent = Intent(this, MainActivity::class.java)
                            // start your next activity
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }
}