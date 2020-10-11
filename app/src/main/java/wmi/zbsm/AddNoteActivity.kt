package wmi.zbsm

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Base64
import android.util.Base64.*
import android.util.Base64.NO_WRAP
import android.util.Base64.encodeToString

import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_note.*


import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


class AddNoteActivity : AppCompatActivity() {

    val dbTable = "Notes"
    var id = 0
    private val PRIVATEMODE = Context.MODE_PRIVATE
    private val passSharedKey = "PasswordSetting"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        try {

            val bundle:Bundle = intent.extras
            id = bundle.getInt("ID",0)
            if(id!=0){
                titleEdit.setText(bundle.getString("name"))
                descEdit.setText(bundle.getString("des"))
            }
        }catch (ex:Exception){

        }
    }

    fun addFunc(view: View) {
        val dbManager = DbManager(this)
        val values = ContentValues()

        val random = SecureRandom()
        val salt = ByteArray(256)
        random.nextBytes(salt)


        val sharedPassPref = getSharedPreferences(passSharedKey, PRIVATEMODE)
        val passwordString = sharedPassPref.getString(passSharedKey,"null")

        val passwordChar: CharArray =
            passwordString.toCharArray() //Turn password into char[] array
        val pbKeySpec =
            PBEKeySpec(passwordChar, salt, 1324, 256) //1324 iterations
        val secretKeyFactory: SecretKeyFactory =
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val keyBytes: ByteArray =
            secretKeyFactory.generateSecret(pbKeySpec).getEncoded()
        val keySpec = SecretKeySpec(keyBytes, "AES")

        val ivRandom =
            SecureRandom()
        val iv = ByteArray(16)
        ivRandom.nextBytes(iv)
        val ivSpec = IvParameterSpec(iv)

        val desc = descEdit.text.toString()
        val descBytes = desc.toByteArray()
        val title = titleEdit.text.toString()
        val titleBytes = title.toByteArray()

        val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
        val encryptedDesc: ByteArray = cipher.doFinal(descBytes)
        val encryptedTitle: ByteArray = cipher.doFinal(titleBytes)


        values.put("Title", Base64.encodeToString(encryptedTitle, Base64.NO_WRAP))
        values.put("Description",Base64.encodeToString(encryptedDesc, Base64.NO_WRAP))
        values.put("salt",Base64.encodeToString(salt, Base64.NO_WRAP))
        values.put("iv",Base64.encodeToString(iv, Base64.NO_WRAP))

        if( id == 0 ){
            val ID = dbManager.insert(values)
            if(ID > 0){
                Toast.makeText(this, "Notatka zapisana", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Błąd...", Toast.LENGTH_SHORT).show()
            }

        } else {
            val selectionArgs = arrayOf(id.toString())
            val ID = dbManager.update(values,"ID=?", selectionArgs)
            if(ID>0){
                Toast.makeText(this, "Notatka zapisana", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Błąd...", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
