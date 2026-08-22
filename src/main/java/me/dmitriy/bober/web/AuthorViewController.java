package me.dmitriy.bober.web;

import me.dmitriy.bober.data.AuthorRepository;
import me.dmitriy.bober.data.PostMarkRepository;
import me.dmitriy.bober.data.PostRepository;
import me.dmitriy.bober.models.*;
import me.dmitriy.bober.service.PostCommentService;
import me.dmitriy.bober.service.PostService;
import me.dmitriy.bober.service.SubscriptionService;
import me.dmitriy.bober.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/authors")
public class AuthorViewController {

    @Autowired
    private AuthorRepository authorRepository;

    private final SubscriptionService subscriptionService;
    private final UserService userService;
    private final PostService postService;
    @Autowired
    private PostMarkRepository postMarkRepository;
    @Autowired
    private PostCommentService postCommentService;
    @Autowired
    private PostRepository postRepository;

    public AuthorViewController(SubscriptionService subscriptionService, UserService userService, PostService postService) {
        this.subscriptionService = subscriptionService;
        this.userService = userService;
        this.postService = postService;
    }

    @GetMapping("/{id}")
    public String getAuthor(Model model, @PathVariable int id, Principal principal) {
        Author author = authorRepository
                .findByIdWithBooks(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Author not found"));
        int subscribersCount = author.getSubscriptions().size();
        int booksAmount = author.getBooksAmount();
        boolean isSubscribed = false;
        boolean isOwner = false;
        Map<Integer, Boolean> userPostMarks = new HashMap<>();
        List<Post> posts = postRepository.findAllByAuthorIdWithComments(id);
        Map<Integer, List<PostCommentNode>> postCommentTrees = new HashMap<>();
        for(Post post : posts) {
            if(post.getComments() != null) {
                postCommentTrees.put(post.getId(), postCommentService.buildTree(post.getComments()));
            }
        }
        User currentUser = null;
        if(principal != null) {
            currentUser = userService.getCurrentUser();
            isSubscribed = subscriptionService.isSubscribed(currentUser, author);
            isOwner = author.getUser() != null && currentUser.getId()== author.getUser().getId();

            if (!author.getPosts().isEmpty()) {
                List<PostMark> marks = postMarkRepository.findAllByUserIdAndPosts(currentUser.getId(), author.getPosts());
                for (PostMark mark : marks) {
                    userPostMarks.put(mark.getPost().getId(), mark.isLike());
                }
            }
        }
        model.addAttribute("author", author);
        model.addAttribute("subscribersCount", subscribersCount);
        model.addAttribute("isSubscribed", isSubscribed);
        model.addAttribute("booksAmount", booksAmount);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userPostMarks", userPostMarks);
        model.addAttribute("posts", posts);
        model.addAttribute("postCommentTrees", postCommentTrees);
        return "author-page";
    }

    @PostMapping("/{id}/subscribe")
    @PreAuthorize("isAuthenticated()")
    public String subscribe(@PathVariable int id) {
        Author author = authorRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found"));
        User currentUser = userService.getCurrentUser();
        boolean isOwner = currentUser.getId() == author.getUser().getId();
        if (isOwner) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Нельзя подписаться на самого себя");
        } else {
            subscriptionService.toggleSubscription(currentUser, author);
        }
        return "redirect:/authors/" + id;
    }

    @PostMapping("{id}/post")
    @PreAuthorize("isAuthenticated()")
    public String post(@PathVariable int id,
                       @RequestParam(required = false) String title,
                       @RequestParam String text) {
        Author author = authorRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found"));
        User currentUser = userService.getCurrentUser();
        boolean isOwner = currentUser.getId() == author.getUser().getId();
        if (!isOwner) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Нельзя публиковать записи от чужого имени!");
        } else {
            postService.postMessage(author, title, text);
        }
        return "redirect:/authors/" + id;
    }

    @PostMapping("{id}/post/mark")
    @PreAuthorize("isAuthenticated()")
    public String markPost(@PathVariable("id") int authorId, @RequestParam int postId, @RequestParam boolean isLike) {
        User currentUser = userService.getCurrentUser();
        postService.toggleMark(postId, currentUser.getId(), isLike);
        return "redirect:/authors/" + authorId + "#post-" + postId;
    }

    @PostMapping("/posts/{postId}/delete")
    @PreAuthorize("isAuthenticated()")
    public String deletePost(@PathVariable int postId, @RequestParam int authorId) {
        postService.deletePost(postId);
        return "redirect:/authors/" + authorId;
    }

    @PostMapping("/posts/{postId}/edit")
    @PreAuthorize("isAuthenticated()")
    public String editPost(@PathVariable int postId,
                           @RequestParam int authorId,
                           @RequestParam String title,
                           @RequestParam String text) {
        postService.editPost(postId, title, text);
        return "redirect:/authors/" + authorId + "#post-" + postId;
    }

    @PostMapping("/posts/{postId}/comments")
    @PreAuthorize("isAuthenticated()")
    public String commentPost(@PathVariable int postId,
                              @RequestParam int authorId,
                              @RequestParam String text,
                              @RequestParam(required = false) Integer parentId) {
        postCommentService.addComment(postId, parentId, text);
        return "redirect:/authors/" + authorId + "#post-" + postId;
    }


    @PostMapping("/posts/comment/{commentId}/delete")
    @PreAuthorize("isAuthenticated()")
    public String deleteComment(@PathVariable int commentId,
                                @RequestParam(required = false) Integer authorId) {
        postCommentService.softDelete(commentId);
        return "redirect:/authors/" + authorId;
    }

    @PostMapping("/posts/comments/{commentId}/edit")
    @PreAuthorize("isAuthenticated()")
    public String editComment(@PathVariable int commentId,
                              @RequestParam int authorId,
                              @RequestParam String text) {
        postCommentService.editComment(commentId, text);
        return "redirect:/authors/" + authorId;
    }

}
