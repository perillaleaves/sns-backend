package project.domain.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.common.CreatedAtEntity;
import project.exception.APIError;

import javax.persistence.*;
import javax.servlet.http.HttpServletRequest;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow extends CreatedAtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "followId")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private User fromUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private User toUser;

    @Builder
    public Follow(User fromUser, User toUser) {
        this.fromUser = fromUser;
        this.toUser = toUser;
    }

    public static Follow follow(User fromUser, User toUser) {
        return Follow.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .build();
    }

}