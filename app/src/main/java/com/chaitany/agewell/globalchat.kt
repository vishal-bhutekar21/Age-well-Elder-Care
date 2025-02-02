package com.chaitany.agewell

import ChatAdapter
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chaitany.agewell.com.chaitany.agewell.DataStorer
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class globalchat : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private val chatList = mutableListOf<ChatMessage>()
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: ImageButton
    private var userMobile = "9322067937"  // Get dynamically from user login

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_globalchat)
         userMobile=getUserMobile()

        DataStorer.context=this




        // Initialize Firebase
        database = FirebaseDatabase.getInstance().getReference("chats")

        val backButton: ImageView = findViewById(R.id.btn_back)
        backButton.setOnClickListener {
            onBackPressed()  // Navigate back when clicked
        }
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
                val sdfDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val sdfTime = SimpleDateFormat("HH:mm", Locale.getDefault())
                val currentDate = sdfDate.format(Date())
                val currentTime = sdfTime.format(Date())

                // Retrieve user details from SharedPreferences
                val sharedPref = getSharedPreferences("UserLogin", MODE_PRIVATE)
                val name = sharedPref.getString("name", "User Name")
                val chatMessage =
                    name?.let { it1 -> ChatMessage(it1, message, currentTime, currentDate) }
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

            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private fun getUserMobile(): String {
        val sharedPreferences = getSharedPreferences("UserLogin", MODE_PRIVATE)
        return sharedPreferences.getString("mobile", "") ?: ""  // Default to empty string if not found
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}