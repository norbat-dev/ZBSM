package wmi.zbsm

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


class PasswordSetting : AppCompatActivity() {
    private val PRIVATE_MODE = 0
    private val SHARED_NAME = "zbsm-v2"
    private val TAG = "ZBSM"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref: SharedPreferences = getSharedPreferences(SHARED_NAME, PRIVATE_MODE)

        if (sharedPref.getBoolean(SHARED_NAME, false)) {
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
            finish()
        } else {
//            val editor = sharedPref.edit()
//            editor.putBoolean(SHARED_NAME, true)
//            editor.commit()

            val savePassBtn = findViewById(R.id.savePass) as? Button
            savePassBtn?.setOnClickListener{
                val editTextPassword = findViewById(R.id.newPass) as? EditText
                val passwordString = editTextPassword.toString()

                val random = SecureRandom()
                val salt = ByteArray(256)
                random.nextBytes(salt)

                val passwordChar: CharArray =
                    passwordString.toCharArray() //Turn password into char[] array
                val pbKeySpec =
                    PBEKeySpec(passwordChar, salt, 1324, 256) //1324 iterations
                val secretKeyFactory: SecretKeyFactory =
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
                val keyBytes: ByteArray =
                    secretKeyFactory.generateSecret(pbKeySpec).getEncoded()
                val keySpec = SecretKeySpec(keyBytes, "AES")




                Log.e(TAG, passwordString);
            }
        }

        setContentView(R.layout.activity_password_setting)
    }
}