package com.example.projemanag.firebase

import android.app.Activity
import android.util.Log
import com.example.projemanag.activities.MainActivity
import com.example.projemanag.activities.MyProfileActivity
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

    fun loadUserData(activity: Activity){
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)
                if (loggedInUser != null) {
                    when(activity){
                        is SignInActivity -> {
                            activity.signInSuccess(loggedInUser)

                        }
                        is MainActivity -> {
                            activity.updateNavigationUserDetails(loggedInUser)
                        }
                        is MyProfileActivity -> {
                            activity.setUserDataInUI(loggedInUser)
                        }

                    }
                }
            }
            .addOnFailureListener {
                    e ->
                when(activity){
                    is SignInActivity -> {
                        activity.hideProgressDialog()

                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error")
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