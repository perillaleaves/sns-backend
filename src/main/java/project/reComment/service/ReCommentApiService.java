package project.reComment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.advice.error.APIError;
import project.advice.exception.CommentNotFoundException;
import project.advice.exception.ReCommentNotFoundException;
import project.comment.domain.Comment;
import project.comment.repository.CommentRepository;
import project.reComment.domain.ReComment;
import project.reComment.repository.ReCommentRepository;
import project.reComment.request.ReCommentRequest;
import project.user.domain.User;

@Service
@Transactional
public class ReCommentApiService {

    private final CommentRepository commentRepository;
    private final ReCommentRepository reCommentRepository;

    public ReCommentApiService(CommentRepository commentRepository, ReCommentRepository reCommentRepository) {
        this.commentRepository = commentRepository;
        this.reCommentRepository = reCommentRepository;
    }

    public void create(Long commentId, ReCommentRequest request, User loginUser) {
        validation(request);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        ReComment reComment = ReComment.builder()
                .content(request.getContent())
                .reCommentLikeSize(0L)
                .user(loginUser)
                .comment(comment)
                .build();

        comment.increaseReCommentSize(comment.getReCommentSize());
        comment.getPost().increaseCommentSize(comment.getPost().getCommentSize());
        reCommentRepository.save(reComment);
    }

    public void update(Long reCommentId, ReCommentRequest request, Long loginUserId) {
        validation(request);
        ReComment reComment = reCommentRepository.findById(reCommentId)
                .orElseThrow(ReCommentNotFoundException::new);
        loginValidate(loginUserId, reComment);

        reComment.update(request.getContent());
    }

    public void delete(Long reCommentId, Long loginUserId) {
        ReComment reComment = reCommentRepository.findById(reCommentId)
                .orElseThrow(ReCommentNotFoundException::new);
        loginValidate(loginUserId, reComment);

        reComment.getComment().decreaseReCommentSize(reComment.getComment().getReCommentSize());
        reComment.getComment().getPost().decreaseCommentSize(reComment.getComment().getPost().getCommentSize());
        reCommentRepository.delete(reComment);
    }


    private static void validation(ReCommentRequest request) {
        if (request.getContent().isEmpty() || request.getContent().length() > 300) {
            throw new APIError("InvalidContent", "문구를 1자이상 300자이하로 입력해주세요.");
        }
    }

    private static void loginValidate(Long loginUserId, ReComment reComment) {
        if (!reComment.getUser().getId().equals(loginUserId)) {
            throw new APIError("NotLogin", "로그인 권한이 있는 유저의 요청이 아닙니다.");
        }
    }

}