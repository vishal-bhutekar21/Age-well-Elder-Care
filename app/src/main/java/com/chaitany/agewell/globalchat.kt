package com.chaitany.agewell

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class globalchat : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private val chatList = mutableListOf<ChatMessage>()
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: ImageButton
    private val userMobile = "9322067937"  // Get this dynamically from user login

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_globalchat)

        // Initialize Firebase
        database = FirebaseDatabase.getInstance().getReference("chats")

        // Initialize UI
        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatAdapter = ChatAdapter(chatList)
        chatRecyclerView.adapter = chatAdapter

        // Load messages from Firebase
        loadMessages()

        // Send message on button click
        sendButton.setOnClickListener {
            val message = messageEditText.text.toString().trim()
            if (message.isNotEmpty()) {
                val chatMessage = ChatMessage(userMobile, message)
                database.push().setValue(chatMessage)
                messageEditText.text.clear()
            }
        }
    }

    private fun loadMessages() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (data in snapshot.children) {
                    val chat = data.getValue(ChatMessage::class.java)
                    if (chat != null) chatList.add(chat)
                }
                chatAdapter.notifyDataSetChanged()
                chatRecyclerView.scrollToPosition(chatList.size - 1)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}
