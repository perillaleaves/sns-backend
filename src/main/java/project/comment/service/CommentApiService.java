package project.comment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.advice.error.APIError;
import project.advice.exception.CommentNotFoundException;
import project.advice.exception.PostNotFoundException;
import project.comment.domain.Comment;
import project.comment.repository.CommentRepository;
import project.comment.request.CommentRequest;
import project.post.domain.Post;
import project.post.repository.PostRepository;
import project.user.domain.User;

@Service
@Transactional
public class CommentApiService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentApiService(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    public void create(Long postId, CommentRequest request, User loginUser) {
        validation(request);
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        Comment comment = Comment.builder()
                .content(request.getContent())
                .commentLikeSize(0L)
                .reCommentSize(0L)
                .user(loginUser)
                .post(post)
                .build();

        post.increaseCommentSize(post.getCommentSize());
        commentRepository.save(comment);
    }

    public void update(Long commentId, CommentRequest request, Long loginUserId) {
        validation(request);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);
        loginValidate(loginUserId, comment);

        comment.update(request.getContent());
    }

    public void delete(Long commentId, Long loginUserId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);
        loginValidate(loginUserId, comment);

        comment.getPost().decreaseCommentSize(comment.getPost().getCommentSize());
        commentRepository.delete(comment);
    }

    private static void validation(CommentRequest request) {
        if (request.getContent().isEmpty() || request.getContent().length() > 300) {
            throw new APIError("InvalidContent", "????????? 1????????? 300???????????? ??????????????????.");
        }
    }

    private static void loginValidate(Long loginUserId, Comment comment) {
        if (!comment.getUser().getId().equals(loginUserId)) {
            throw new APIError("NotLogin", "????????? ????????? ?????? ????????? ????????? ????????????.");
        }
    }

}