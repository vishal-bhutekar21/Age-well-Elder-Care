import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class FirebaseManager {
    private static final String APPOINTMENTS_REF = "appointments";
    private static final String USERS_REF = "users";

    private final DatabaseReference database;
    private final FirebaseAuth auth;
    private ValueEventListener appointmentsListener;

    public interface DataCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }

    public FirebaseManager() {
        database = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
    }

    public String getCurrentUserId() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    public void addAppointment(Appointment appointment, DataCallback<String> callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onError("User not authenticated");
            return;
        }

        String appointmentId = database.child(APPOINTMENTS_REF).push().getKey();
        appointment.setId(appointmentId);
        appointment.setUserId(userId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("/" + APPOINTMENTS_REF + "/" + appointmentId, appointment.toMap());
        updates.put("/" + USERS_REF + "/" + userId + "/appointments/" + appointmentId, true);

        database.updateChildren(updates)
                .addOnSuccessListener(aVoid -> callback.onSuccess(appointmentId))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void listenToAppointments(DataCallback<List<Appointment>> callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onError("User not authenticated");
            return;
        }

        if (appointmentsListener != null) {
            database.child(APPOINTMENTS_REF).removeEventListener(appointmentsListener);
        }

        appointmentsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Appointment> appointments = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Appointment appointment = snapshot.getValue(Appointment.class);
                    if (appointment != null && appointment.getUserId().equals(userId)) {
                        appointments.add(appointment);
                    }
                }
                callback.onSuccess(appointments);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        };

        database.child(APPOINTMENTS_REF)
                .orderByChild("timestamp")
                .addValueEventListener(appointmentsListener);
    }

    public void removeAppointment(String appointmentId, DataCallback<Void> callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onError("User not authenticated");
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("/" + APPOINTMENTS_REF + "/" + appointmentId, null);
        updates.put("/" + USERS_REF + "/" + userId + "/appointments/" + appointmentId, null);

        database.updateChildren(updates)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void cleanup() {
        if (appointmentsListener != null) {
            database.child(APPOINTMENTS_REF).removeEventListener(appointmentsListener);
        }
    }
}