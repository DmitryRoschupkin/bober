package me.dmitriy.bober.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PostCommentNode {

    private final PostComment postComment;
    private final List<PostCommentNode> replies = new ArrayList<>();
    public PostCommentNode(PostComment postComment) {
        this.postComment = postComment;
    }
}
