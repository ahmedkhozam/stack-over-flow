package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VoteRequest {
    @NotNull
    private Integer value; // +1 for upvote, -1 for downvote
}
