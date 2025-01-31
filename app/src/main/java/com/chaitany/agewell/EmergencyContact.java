package com.chaitany.agewell;



import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.Priority;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;


import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import android.widget.Toast;
import androidx.appcompat.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class EmergencyContact extends AppCompatActivity implements ContactsAdapter.ContactActionListener {
    private RecyclerView contactsRecyclerView;
    private ContactsAdapter adapter;
    private List<Contact> contacts = new ArrayList<>();
    private SearchView searchView;
    private MaterialButton sendLocationButton;
    private MaterialButton emergencyCallButton;
    private FloatingActionButton addContactFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact);

        initializeViews();
        setupRecyclerView();
        setupSearchView();
        setupButtons();
        loadContacts();
    }

    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        contactsRecyclerView = findViewById(R.id.contactsRecyclerView);
        searchView = findViewById(R.id.searchView);
        sendLocationButton = findViewById(R.id.sendLocationButton);
        emergencyCallButton = findViewById(R.id.emergencyCallButton);
        addContactFab = findViewById(R.id.addContactFab);
    }

    private void setupRecyclerView() {
        adapter = new ContactsAdapter(contacts, this);
        contactsRecyclerView.setAdapter(adapter);
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterContacts(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterContacts(newText);
                return true;
            }
        });
    }

    private void setupButtons() {
        addContactFab.setOnClickListener(v -> showContactDialog(null));

        sendLocationButton.setOnClickListener(v -> sendLiveLocationToContacts());

        emergencyCallButton.setOnClickListener(v -> {
            if (!contacts.isEmpty()) {
                //(contacts.get(0));
            } else {
                showToast("No emergency contacts available");
            }
        });
    }

    private void showContactDialog(Contact contact) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_contact_form, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Initialize form fields
        TextInputEditText nameInput = dialogView.findViewById(R.id.nameInput);
        TextInputEditText phoneInput = dialogView.findViewById(R.id.phoneInput);
        AutoCompleteTextView categoryInput = dialogView.findViewById(R.id.categoryInput);
        AutoCompleteTextView priorityInput = dialogView.findViewById(R.id.priorityInput);

        // Set up dropdowns
        String[] categories = {"Family", "Friend", "Medical", "Other"};
        String[] priorities = {"High Priority", "Medium Priority", "Low Priority"};

        categoryInput.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, categories));
        priorityInput.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, priorities));

        // Pre-fill if editing
        if (contact != null) {
            nameInput.setText(contact.getName());
            phoneInput.setText(contact.getPhone());
            categoryInput.setText(contact.getCategory());
            priorityInput.setText(getPriorityText(contact.getPriority()));
        }

        builder.setView(dialogView)
                .setTitle(contact == null ? "Add Contact" : "Edit Contact")
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = nameInput.getText().toString();
                    String phone = phoneInput.getText().toString();
                    String category = categoryInput.getText().toString();
                    int priority = getPriorityValue(priorityInput.getText().toString());

                    if (validateInput(name, phone)) {
                        if (contact == null) {
                            addContact(name, phone, category, priority);
                        } else {
                            updateContact(contact, name, phone, category, priority);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addContact(String name, String phone, String category, int priority) {
        Contact newContact = new Contact(
                UUID.randomUUID().toString(),
                name,
                phone,
                category,
                priority
        );
        contacts.add(newContact);
        saveContacts();
        adapter.updateContacts(contacts);
        showToast("Contact added successfully");

        addEmergencyContactToFirebase(this,name,phone,category,priority);
    }

    private void updateContact(Contact contact, String name, String phone,
                               String category, int priority) {
        contact.setName(name);
        contact.setPhone(phone);
        contact.setCategory(category);
        contact.setPriority(priority);
        saveContacts();
        adapter.updateContacts(contacts);
        showToast("Contact updated successfully");
    }

    private void filterContacts(String query) {
        List<Contact> filteredList = contacts.stream()
                .filter(contact ->
                        contact.getName().toLowerCase().contains(query.toLowerCase()) ||
                                contact.getPhone().contains(query)
                )
                .sorted((a, b) -> a.getPriority() - b.getPriority())
                .collect(Collectors.toList());

        adapter.updateContacts(filteredList);
    }

    public void sendLiveLocationToContacts() {

       requestPermissions(this);
        if (!checkLocationPermission()) {
            showToast("Location permission not granted");
            return;
        }

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        LocationRequest locationRequest = new LocationRequest.Builder(5000) // 5 seconds interval
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY) // High accuracy priority
                .setMinUpdateIntervalMillis(2000) // 2 seconds fastest update
                .setMinUpdateDistanceMeters(10) // Update only if moved 10 meters
                .build();

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;

                for (Location location : locationResult.getLocations()) {
                    String locationUrl = String.format(
                            "https://www.google.com/maps?q=%f,%f",
                            location.getLatitude(),
                            location.getLongitude()
                    );

                    // Get the first priority contact
                    Contact firstPriorityContact = getFirstPriorityContact();

                    if (firstPriorityContact != null) {
                        sendSMSLocationToContact(firstPriorityContact, locationUrl);
                        showToast("Live location sent to first priority contact");
                    } else {
                        showToast("No priority contacts available");
                    }
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private Contact getFirstPriorityContact() {
        // Sort contacts by priority (ascending) and get the first one
        List<Contact> sortedContacts = contacts.stream()
                .sorted((a, b) -> Integer.compare(a.getPriority(), b.getPriority()))
                .collect(Collectors.toList());

        // Return the first priority contact (lowest priority value means highest priority)
        return sortedContacts.isEmpty() ? null : sortedContacts.get(0);
    }

    private void sendSMSLocationToContact(Contact contact, String locationUrl) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(contact.getPhone(), null, "Emergency! My live location: " + locationUrl, null, null);
    }



    private void sendSMSLocationToContacts(String locationUrl) {
        List<String> emergencyContacts = getEmergencyContacts(); // Implement method to get contacts

        SmsManager smsManager = SmsManager.getDefault();
        for (String contact : emergencyContacts) {
            smsManager.sendTextMessage(contact, null, "Emergency! My live location: " + locationUrl, null, null);
        }
    }

    // Mock method to retrieve emergency contacts (Replace this with actual contact retrieval logic)
    private List<String> getEmergencyContacts() {
        return List.of("+919876543210", "+918765432109"); // Example contacts
    }

    private boolean checkLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }





    @Override
    public void onCallClick(Contact contact) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + contact.getPhone()));
        startActivity(intent);
    }

    @Override
    public void onEditClick(Contact contact) {
        showContactDialog(contact);
    }

    @Override
    public void onDeleteClick(Contact contact) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Contact")
                .setMessage("Are you sure you want to delete this contact?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    contacts.remove(contact);
                    removeEmergencyContactFromFirebase(this,contact.getPhone());
                    saveContacts();
                    adapter.updateContacts(contacts);
                    showToast("Contact deleted");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadContacts() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        String contactsJson = prefs.getString("contacts", "[]");
        try {
            JSONArray jsonArray = new JSONArray(contactsJson);
            contacts.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                contacts.add(new Contact(
                        jsonObject.getString("id"),
                        jsonObject.getString("name"),
                        jsonObject.getString("phone"),
                        jsonObject.getString("category"),
                        jsonObject.getInt("priority")
                ));
            }
            adapter.updateContacts(contacts);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveContacts() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        JSONArray jsonArray = new JSONArray();
        for (Contact contact : contacts) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", contact.getId());
                jsonObject.put("name", contact.getName());
                jsonObject.put("phone", contact.getPhone());
                jsonObject.put("category", contact.getCategory());
                jsonObject.put("priority", contact.getPriority());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        prefs.edit().putString("contacts", jsonArray.toString()).apply();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private String getPriorityText(int priority) {
        switch (priority) {
            case 1: return "High Priority";
            case 2: return "Medium Priority";
            case 3: return "Low Priority";
            default: return "Medium Priority";
        }
    }

    private int getPriorityValue(String priorityText) {
        switch (priorityText) {
            case "High Priority": return 1;
            case "Low Priority": return 3;
            default: return 2;
        }
    }

    private boolean validateInput(String name, String phone) {
        if (name.trim().isEmpty()) {
            showToast("Name is required");
            return false;
        }
        if (!phone.matches("\\d{10}")) {
            showToast("Please enter a valid 10-digit phone number");
            return false;
        }
        return true;
    }

    public void requestPermissions(Activity activity) {
        List<String> permissionsToRequest = new ArrayList<>();

        // Check and add location permissions
        if (ContextCompat.checkSelfPermission(activity,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(activity,android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        // Check and add SEND_SMS permission
        if (ContextCompat.checkSelfPermission(activity,android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(android.Manifest.permission.SEND_SMS);
        }

        // Request only the permissions that are not granted
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(activity, permissionsToRequest.toArray(new String[0]), 102);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 102) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Permissions", permissions[i] + " granted");
                } else {
                    Log.d("Permissions", permissions[i] + " denied");
                }
            }
        }
    }


    public void addEmergencyContactToFirebase(Context context, String contactName, String contactMobile, String category, Integer priority) {
        SharedPreferences prefs = context.getSharedPreferences("UserLogin", Context.MODE_PRIVATE);
        String userMobile = prefs.getString("mobile", null); // Fetch stored user mobile number

        if (userMobile == null) {
            Log.e("Firebase", "User mobile number not found in SharedPreferences.");
            return;
        }

        DatabaseReference database = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userMobile)
                .child("emergency_contacts")
                .child(contactMobile);  // Using contact's mobile number as key

        Map<String, Object> contactMap = new HashMap<>();
        contactMap.put("name", contactName);
        contactMap.put("mobile", contactMobile);
        contactMap.put("category", category);
        contactMap.put("priority", priority);

        database.setValue(contactMap)
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Contact added successfully!"))
                .addOnFailureListener(e -> Log.e("Firebase", "Failed to add contact", e));
    }

    public void removeEmergencyContactFromFirebase(Context context, String contactMobile) {
        SharedPreferences prefs = context.getSharedPreferences("UserLogin", Context.MODE_PRIVATE);
        String userMobile = prefs.getString("mobile", null);

        if (userMobile == null) {
            Log.e("Firebase", "User mobile number not found in SharedPreferences.");
            return;
        }

        DatabaseReference database = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userMobile)
                .child("emergency_contacts")
                .child(contactMobile);  // Removing using contact's mobile number

        database.removeValue()
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Contact removed successfully!"))
                .addOnFailureListener(e -> Log.e("Firebase", "Failed to remove contact", e));
    }




}