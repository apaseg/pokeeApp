package com.example.pokeeapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import com.example.pokeeapp.R
import com.example.pokeeapp.databinding.ActivitySetInfoBinding
//import com.example.healthmonitor.databinding.ActivityUserInformationBinding a
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class setInfo : AppCompatActivity() {

    private lateinit var binding: ActivitySetInfoBinding
    private lateinit var dbRegRef: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var ImageUri: Uri
    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.personAdd.setOnClickListener {
            selectImage()
        }

        dbRegRef = FirebaseDatabase.getInstance().getReference("UserDetail")

        binding.ibtnRegister.setOnClickListener {

            val etFirstName = binding.ietFirstName.text.toString().trim()
            val etLastName = binding.ietLastName.text.toString().trim()
            val etUserName = binding.ietUserName.text.toString().trim()
            val etPhoneNumber = binding.ietPhoneNumber.text.toString().trim()
            val userId = dbRegRef.push().key!!
            val user = UserData(userId,etFirstName,etLastName,etUserName,etPhoneNumber)
            if(userId != null) {
                dbRegRef.child(userId).setValue(user).addOnCompleteListener {
                    if(it.isSuccessful){
                        uploadProfilePic()
//                        Toast.makeText(this,"successful to upload Profile pic",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(this,"failed to upload Profile pic",Toast.LENGTH_SHORT).show()
                    }
                }
            }

            val intent = Intent(applicationContext, NavigationActivity::class.java)
            startActivity(intent)
//                Log.d("checkagain","checked")
//                if(ind){
//                    Log.d("check","checked")
//                    val intent = Intent(applicationContext, MainActivity::class.java)
//                    startActivity(intent)
//                }
//            }
        }

    }

    private fun selectImage() {
        val GALLERY_INTENT_CALLED = 1
        val GALLERY_KITKAT_INTENT_CALLED = 2

        if (Build.VERSION.SDK_INT < 19) {
            val intent = Intent()
            intent.type = "*/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, GALLERY_INTENT_CALLED)
        } else {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "*/*"
            startActivityForResult(intent, GALLERY_KITKAT_INTENT_CALLED)
        }
    }

    @SuppressLint("WrongConstant")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (Build.VERSION.SDK_INT < 19) {
            ImageUri = data?.data!!
        } else {
            ImageUri = data?.data!!
            val takeFlags: Int = (data.flags
                    and (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION))

            try {
                this.contentResolver.takePersistableUriPermission(ImageUri,takeFlags)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }

        binding.personAdd.setImageURI(ImageUri)
    }

    private fun uploadProfilePic() {
//        imageUri = Uri.parse("android.resources://$packageName/${R.drawable.myimage}")
//        imageUri = Uri.parse("$ImageUri")

        firebaseAuth = FirebaseAuth.getInstance()

        val usId = firebaseAuth.currentUser?.uid
        storageReference = FirebaseStorage.getInstance().getReference("UserDetail/" + firebaseAuth.currentUser?.uid +".jpg")
        storageReference.putFile(ImageUri).addOnSuccessListener {
            Toast.makeText(this,"Profile Successfully uploaded",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this,"Failed to upload the image",Toast.LENGTH_SHORT).show()
        }
    }
}