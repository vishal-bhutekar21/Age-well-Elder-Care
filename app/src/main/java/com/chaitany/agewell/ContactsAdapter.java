package com.chaitany.agewell;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {
    private List<Contact> contacts;
    private ContactActionListener listener;

    public interface ContactActionListener {
        void onCallClick(Contact contact);
        void onEditClick(Contact contact);
        void onDeleteClick(Contact contact);
    }

    public ContactsAdapter(List<Contact> contacts, ContactActionListener listener) {
        this.contacts = contacts;
        this.listener = listener;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.bind(contact);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public void updateContacts(List<Contact> newContacts) {
        this.contacts = newContacts;
        notifyDataSetChanged();
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText;
        private TextView phoneText;
        private Chip categoryChip;
        private Chip priorityChip;
        private ImageButton callButton;
        private ImageButton editButton;
        private ImageButton deleteButton;

        public ContactViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.nameText);
            phoneText = itemView.findViewById(R.id.phoneText);
            categoryChip = itemView.findViewById(R.id.categoryChip);
            priorityChip = itemView.findViewById(R.id.priorityChip);
            callButton = itemView.findViewById(R.id.callButton);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(Contact contact) {
            nameText.setText(contact.getName());
            phoneText.setText(contact.getPhone());
            categoryChip.setText(contact.getCategory());

            // Set category chip colors
            int[] colors = getCategoryColors(contact.getCategory());
            categoryChip.setChipBackgroundColorResource(colors[0]);
            categoryChip.setTextColor(itemView.getContext().getColor(colors[1]));

            // Show priority chip for high priority contacts
            priorityChip.setVisibility(contact.getPriority() == 1 ? View.VISIBLE : View.GONE);

            callButton.setOnClickListener(v -> listener.onCallClick(contact));
            editButton.setOnClickListener(v -> listener.onEditClick(contact));
            deleteButton.setOnClickListener(v -> listener.onDeleteClick(contact));
        }

        private int[] getCategoryColors(String category) {
            switch (category.toLowerCase()) {
                case "family":
                    return new int[]{R.color.blue_light, R.color.blue};
                case "friend":
                    return new int[]{R.color.green_light, R.color.green};
                case "medical":
                    return new int[]{R.color.red_light, R.color.red};
                default:
                    return new int[]{R.color.gray_light, R.color.gray};
            }
        }
    }
}