package com.adeeba.plantdiseaseapp

import com.google.firebase.firestore.FirebaseFirestore

class FirebaseRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getDiseaseData(
        diseaseName: String,
        onResult: (DiseaseData?) -> Unit
    ) {
        db.collection("diseases")
            .whereEqualTo("name", diseaseName)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val doc = result.documents[0]
                    val data = doc.toObject(DiseaseData::class.java)
                    onResult(data)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }
}