package com.example.quiz_app_2.data.repository

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class AuthRepository @Inject constructor() {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    fun getCurrentUser() = auth.currentUser

    fun signIn(email: String, pass: String) = auth.signInWithEmailAndPassword(email, pass)

    fun signUp(email: String, pass: String) = auth.createUserWithEmailAndPassword(email, pass)

    fun signOut() = auth.signOut()
}