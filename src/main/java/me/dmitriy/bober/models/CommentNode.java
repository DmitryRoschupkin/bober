package me.dmitriy.bober.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CommentNode {

    private final Comment comment;
    private final List<CommentNode> replies = new ArrayList<>();
    public CommentNode(Comment comment) {
        this.comment = comment;
    }
}
