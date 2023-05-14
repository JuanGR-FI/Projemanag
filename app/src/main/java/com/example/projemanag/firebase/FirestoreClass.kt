package com.example.projemanag.firebase

import android.util.Log
import com.example.projemanag.activities.SignInActivity
import com.example.projemanag.activities.SignUpActivity
import com.example.projemanag.models.User
import com.example.projemanag.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {
    private val mFirestore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: User){
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }
            .addOnFailureListener {
                e -> Log.e(activity.javaClass.simpleName, "Error")
            }
    }

    fun signInUser(activity: SignInActivity){
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)
                if (loggedInUser != null) {
                    activity.signInSuccess(loggedInUser)
                }
            }
            .addOnFailureListener {
                    e -> Log.e(activity.javaClass.simpleName, "Error")
            }
    }

    fun getCurrentUserId(): String {

        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""

        if(currentUser != null){
            currentUserID = currentUser.uid
        }

        return currentUserID

    }

}