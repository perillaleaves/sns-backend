package project.user.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.common.BaseEntity;
import project.token.domain.UserToken;
import project.user.request.ProfileEditRequest;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userProfileImageId")
    private UserProfileImage userProfileImage;

    private String email;
    private String name;
    private String nickName;
    private String password;

    private String content;

    private Long postSize;
    private Long followingSize;
    private Long followerSize;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserToken> userTokens = new ArrayList<>();

    @Builder
    public User(Long id, UserProfileImage userProfileImage, String email, String name, String nickName, String password, String content, Long postSize, Long followingSize, Long followerSize, List<UserToken> userTokens) {
        this.id = id;
        this.userProfileImage = userProfileImage;
        this.email = email;
        this.name = name;
        this.nickName = nickName;
        this.password = password;
        this.content = content;
        this.postSize = postSize;
        this.followingSize = followingSize;
        this.followerSize = followerSize;
        this.userTokens = userTokens;
    }

    public void increasePostSize(Long postSize) {
        this.postSize = ++postSize;
    }

    public void decreasePostSize(Long postSize) {
        this.postSize = --postSize;
    }

    public void increaseFollowingSize(Long followingSize) {
        this.followingSize = ++followingSize;
    }

    public void increaseFollowerSize(Long followerSize) {
        this.followerSize = ++followerSize;
    }

    public void decreaseFollowingSize(Long followingSize) {
        this.followingSize = --followingSize;
    }

    public void decreaseFollowerSize(Long followerSize) {
        this.followerSize = --followerSize;
    }

    public void editProfile(ProfileEditRequest request) {
        this.name = request.getUserName();
        this.nickName = request.getNickName();
        this.content = request.getContent();
    }



}