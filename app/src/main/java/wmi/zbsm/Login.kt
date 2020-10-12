package wmi.zbsm

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

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
            val passRaw = passET?.getText().toString()
            val pass = md5(passRaw);

            if( pass == sharedPassPref.getString(passSharedKey,"null") ){
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("pass", passRaw);
                startActivity(intent)
            } else {
                passET?.setError("Wrong password")
            }

        }
    }
    fun md5(s: String): String? {
        try {
            // Create MD5 Hash
            val digest = MessageDigest.getInstance("MD5")
            digest.update(s.toByteArray())
            val messageDigest = digest.digest()

            // Create Hex String
            val hexString = StringBuffer()
            for (i in messageDigest.indices) hexString.append(
                Integer.toHexString(
                    0xFF and messageDigest[i].toInt()
                )
            )
            return hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }
}