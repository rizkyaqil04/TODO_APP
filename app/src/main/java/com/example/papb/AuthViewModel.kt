package com.example.papb

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {
   private val auth: FirebaseAuth = FirebaseAuth.getInstance()

   private val _authState = MutableLiveData<AuthState>(
      if (auth.currentUser != null) AuthState.Authenticated else AuthState.Unauthenticated
   )
   val authState: LiveData<AuthState> = _authState

   init {
      auth.addAuthStateListener { firebaseAuth ->
         if (firebaseAuth.currentUser != null) {
            _authState.value = AuthState.Authenticated
         } else {
            _authState.value = AuthState.Unauthenticated
         }
      }
   }

   fun signUp(email: String, password: String) {
      if (email.isEmpty() || password.isEmpty()) {
         _authState.value = AuthState.Error("Email atau Password tidak boleh kosong")
         return
      }

      _authState.value = AuthState.Loading
      auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
         if (task.isSuccessful) {
            _authState.value = AuthState.Authenticated
         } else {
            _authState.value =
                    AuthState.Error(task.exception?.message ?: "Terjadi kesalahan saat mendaftar")
         }
      }
   }

   fun login(email: String, password: String) {
      if (email.isEmpty() || password.isEmpty()) {
         _authState.value = AuthState.Error("Email atau Password tidak boleh kosong")
         return
      }

      _authState.value = AuthState.Loading
      auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
         if (task.isSuccessful) {
            _authState.value = AuthState.Authenticated
         } else {
            _authState.value =
                    AuthState.Error(task.exception?.message ?: "Terjadi kesalahan saat login")
         }
      }
   }

   fun logout() {
      auth.signOut()
      _authState.value = AuthState.Unauthenticated
   }

   fun isUserLoggedIn(): Boolean {
      return auth.currentUser != null
   }

   fun getCurrentUserEmail(): String? {
      return auth.currentUser?.email
   }
}

sealed class AuthState {
   object Initial : AuthState()
   object Loading : AuthState()
   object Authenticated : AuthState()
   object Unauthenticated : AuthState()
   data class Error(val message: String) : AuthState()
}
