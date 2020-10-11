package wmi.zbsm

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class Login : AppCompatActivity() {

    private val PRIVATEMODE = Context.MODE_PRIVATE
    private val passSharedKey = "PasswordSetting"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginBtn = findViewById<Button>(R.id.loginBtn) as Button
        val sharedPassPref = getSharedPreferences(passSharedKey, PRIVATEMODE)

        loginBtn.setOnClickListener{
            val passET = findViewById<EditText>(R.id.loginPass) as? EditText
            val pass = passET?.getText().toString()

            if( pass == sharedPassPref.getString(passSharedKey,"null") ){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                passET?.setError("Wrong password")
            }

        }
    }
}