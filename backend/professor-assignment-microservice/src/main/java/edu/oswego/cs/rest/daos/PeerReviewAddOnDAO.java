package edu.oswego.cs.rest.daos;

import lombok.NonNull;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public class PeerReviewAddOnDAO {
    @JsonbProperty("peer_review_instructions") public String peerReviewInstructions;
    @JsonbProperty("peer_review_due_date") public String peerReviewDueDate;
    @JsonbProperty("peer_review_points") public int peerReviewPoints;

    @JsonbCreator
    public PeerReviewAddOnDAO(
            @NonNull @JsonbProperty("peer_review_instructions") String peerReviewInstructions,
            @NonNull @JsonbProperty("peer_review_due_date") String peerReviewDueDate,
            @NonNull @JsonbProperty("peer_review_points") Integer peerReviewPoints
    )
    {
        this.peerReviewInstructions = peerReviewInstructions;
        this.peerReviewDueDate = peerReviewDueDate;
        this.peerReviewPoints = peerReviewPoints;
    }
}
