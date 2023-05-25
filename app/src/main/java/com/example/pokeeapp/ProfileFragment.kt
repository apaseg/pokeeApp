package com.example.pokeeapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.content.Intent
import android.graphics.BitmapFactory
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
//import com.google.firebase.database.R
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private lateinit var dbRef: DatabaseReference
private lateinit var firebaseAuth: FirebaseAuth
private lateinit var uid: String
private lateinit var name: TextView
private lateinit var userName: TextView
private lateinit var user: UserData
private lateinit var signOut: ImageView
private lateinit var storageReference: StorageReference
private lateinit var profileImage: ImageView

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile,container,false)

        name = view.findViewById(R.id.Name)
        userName = view.findViewById(R.id.userName)
        signOut = view.findViewById(R.id.signOut)
        profileImage = view.findViewById(R.id.profileImage)

        setProfileData()

        signOut.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(context, SignIn::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun setProfileData(){

        firebaseAuth = FirebaseAuth.getInstance()
        uid = firebaseAuth.currentUser?.uid.toString()

        dbRef = FirebaseDatabase.getInstance().getReference("UserDetail")

        if(uid.isNotEmpty()){
            getUserData()
        }
    }

    private fun getUserData() {
        dbRef.child(uid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
//                user = snapshot.getValue(UserData::class.java)!!
//                name.text = user.etFirstName
//                userName.text = user.etUserName
                getUserProfile()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Failed to retrive profile info",Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getUserProfile() {
        storageReference = FirebaseStorage.getInstance().reference.child("UserDetail/$uid.jpg")
        val localFile = File.createTempFile("tempImage","jpg")
        storageReference.getFile(localFile).addOnSuccessListener {

            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            profileImage.setImageBitmap(bitmap)
        }.addOnFailureListener {
            Toast.makeText(context,"Failed to retrieve user Image",Toast.LENGTH_SHORT).show()
        }
    }
}