package project.comment.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import project.comment.domain.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Boolean existsCommentByIdAndUserId(Long id, Long userId);

    @EntityGraph(attributePaths = {"user"})
    List<Comment> findAllByPostId(Long postId);

    Optional<Comment> findByParentId(Long parentId);

}