package com.chaitany.agewell;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {

    private List<Feedback> feedbackList;

    public FeedbackAdapter(List<Feedback> feedbackList) {
        this.feedbackList = feedbackList;
    }

    @Override
    public FeedbackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for each feedback item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedback_list_item, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FeedbackViewHolder holder, int position) {
        // Get the feedback item at the current position
        Feedback feedback = feedbackList.get(position);

        // Set the user's name and comment
        holder.userNameText.setText(feedback.getUserName()); // Display user's name
        holder.commentText.setText(feedback.getComment()); // Display comment

        // Set the star rating
        holder.starRatingView.setRating(feedback.getRating()); // Set the star rating
    }

    @Override
    public int getItemCount() {
        return feedbackList.size(); // Return the total number of feedback items
    }

    public static class FeedbackViewHolder extends RecyclerView.ViewHolder {

        TextView commentText; // TextView for comment
        TextView userNameText; // TextView for user's name
        StarRatingView starRatingView; // Custom view for star rating

        public FeedbackViewHolder(View itemView) {
            super(itemView);
            // Initialize the views
            commentText = itemView.findViewById(R.id.feedback_comment); // TextView for comment
            userNameText = itemView.findViewById(R.id.feedback_user_name); // TextView for user's name
            starRatingView = itemView.findViewById(R.id.feedback_star_rating); // Initialize the StarRatingView
        }
    }
}