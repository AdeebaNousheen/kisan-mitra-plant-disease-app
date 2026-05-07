package com.adeeba.plantdiseaseapp

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class CommunityRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getPosts(isTrending: Boolean, onResult: (List<Post>) -> Unit) {

        val query = if (isTrending) {
            db.collection("community_posts")
                .orderBy("likes", Query.Direction.DESCENDING)
        } else {
            db.collection("community_posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
        }

        query.addSnapshotListener { snapshot, _ ->

            val posts = snapshot?.documents?.mapNotNull { doc ->
                Post(
                    id = doc.id,
                    userId = doc.getString("userId") ?: "",
                    username = doc.getString("username") ?: "",
                    message = doc.getString("message") ?: "",
                    imageUrl = doc.getString("imageUrl") ?: "",
                    likes = (doc.getLong("likes") ?: 0).toInt(),
                    likedBy = doc.get("likedBy") as? List<String> ?: emptyList(),
                    profileUrl = doc.getString("profileUrl") ?: "",
                    bio = doc.getString("bio") ?: "",
                    timestamp = doc.getTimestamp("timestamp")?.toDate()?.time ?: 0,
                    bestReplyId = doc.getString("bestReplyId") ?: "" // ✅ NEW
                )
            } ?: emptyList()

            onResult(posts)
        }
    }
}