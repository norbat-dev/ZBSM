package wmi.zbsm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Base64.*
import android.util.Base64.NO_WRAP
import android.util.Base64.decode
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.row.view.*
import java.nio.charset.Charset
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    val listNotes = ArrayList<Note>()
    private val PRIVATEMODE = Context.MODE_PRIVATE
    private val passSharedKey = "PasswordSetting"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        LoadQuery("%")

        val mFab = findViewById<FloatingActionButton>(R.id.fab)
        mFab.setOnClickListener {
            startActivity(Intent(this, AddNoteActivity::class.java))
        }
    }

    override fun onResume(){
        super.onResume()
        LoadQuery("%")
    }
    private fun LoadQuery(title:String){
        val dbManager = DbManager(this)
        val projections = arrayOf("ID", "Title", "Description", "salt", "iv")
        val selectionArgs = arrayOf(title)
        val cursor = dbManager.Query(projections, "Title like ?", selectionArgs, "Title")
        listNotes.clear()
        if(cursor.moveToFirst()){

            do{
//                val salt = cursor.getString(cursor.getColumnIndex("salt")).toByteArray()
//                val ivDB = cursor.getString(cursor.getColumnIndex("iv")).toByteArray()

                val salt = Base64.decode(cursor.getString(cursor.getColumnIndex("salt")), Base64.NO_WRAP)
                val iv = Base64.decode(cursor.getString(cursor.getColumnIndex("iv")), Base64.NO_WRAP)


                val ID = cursor.getInt(cursor.getColumnIndex("ID"))
                val titleEncrypted = Base64.decode(cursor.getString(cursor.getColumnIndex("Title")), Base64.NO_WRAP)
                val descriptionEncrypted = Base64.decode(cursor.getString(cursor.getColumnIndex("Description")), Base64.NO_WRAP)

                val sharedPassPref = getSharedPreferences(passSharedKey, PRIVATEMODE)
                val passwordString = sharedPassPref.getString(passSharedKey,"null")

                val passwordChar: CharArray = passwordString.toCharArray()
                val pbKeySpec = PBEKeySpec(passwordChar, salt, 1324, 256)
                val secretKeyFactory: SecretKeyFactory =
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
                val keyBytes: ByteArray =
                    secretKeyFactory.generateSecret(pbKeySpec).getEncoded()
                val keySpec = SecretKeySpec(keyBytes, "AES")
//
                val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
                val ivSpec = IvParameterSpec(iv)
                cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
                val titleDecrypted = cipher.doFinal(titleEncrypted)
                val descriptionDecrypted = cipher.doFinal(descriptionEncrypted)



                listNotes.add(Note(ID, titleDecrypted.toString(Charset.defaultCharset()), descriptionDecrypted.toString(Charset.defaultCharset())))
            }while(cursor.moveToNext())


        }

        //adapter
        var myNotesAdapter = MyNotesAdapter(this, listNotes)
        //
        notesLV.adapter = myNotesAdapter

    }



    inner class MyNotesAdapter : BaseAdapter {
        var listNotesAdapter = ArrayList<Note>()
        var context:Context?=null

        constructor(context: Context, listNotesAdapter: ArrayList<Note>) : super(){
            this.listNotesAdapter = listNotesAdapter
            this.context = context
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            //infalte layout row.xml
            val myView = layoutInflater.inflate(R.layout.row, null)
            val myNote = listNotesAdapter[p0]
            myView.titleTv.text = myNote.nodeName
            myView.descTv.text = myNote.nodeDes
            //delete button click
            myView.deleteBtn.setOnClickListener{
                var dbManager = DbManager(this.context!!)
                val selectionArgs = arrayOf(myNote.nodeID.toString())
                dbManager.delete("ID=?", selectionArgs)
                LoadQuery("%")
            }
            //edit
            myView.editBtn.setOnClickListener {
                UpdateFun(myNote)
            }
            return myView
        }

        override fun getItem(p0: Int): Any {
            return listNotesAdapter[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getCount(): Int {
            return listNotesAdapter.size
        }


    }

    private fun UpdateFun(myNote: Note) {
        val intent = Intent(this, AddNoteActivity::class.java)
        intent.putExtra("ID", myNote.nodeID)
        intent.putExtra("name", myNote.nodeName)
        intent.putExtra("des", myNote.nodeDes)
        intent.putExtra("buttontxt","Zapisz")
        startActivity(intent)
    }
}