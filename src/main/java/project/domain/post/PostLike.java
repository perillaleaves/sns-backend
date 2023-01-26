package project.domain.post;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.common.CreatedAtEntity;
import project.domain.user.User;

import javax.persistence.*;
import javax.servlet.http.HttpServletRequest;

@Table(name = "postLikes")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLike extends CreatedAtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "postLikeId")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @Builder
    public PostLike(Post post, User user) {
        this.post = post;
        this.user = user;
    }

    public static PostLike addLike(Post post, HttpServletRequest httpServletRequest) {
        User userId = (User) httpServletRequest.getAttribute("userId");
        post.addPostLikeSize(post.getPostLikeSize());
        return PostLike.builder()
                .post(post)
                .user(userId)
                .build();
    }

    public boolean isLikeOf(User user) {
        return user.hasId(user.getId());
    }

}