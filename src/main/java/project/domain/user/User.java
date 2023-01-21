package project.domain.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.common.BaseEntity;
import project.config.EncryptUtils;
import project.request.SignupRequest;

import javax.persistence.*;
import java.security.NoSuchAlgorithmException;
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

    private String profileImage;

    private String email;
    private String name;
    private String nickName;
    private String password;

    private String content;

    private Long postSize;
    private Long followSize;
    private Long followingSize;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserToken> userTokens = new ArrayList<>();

    @Builder
    public User(String profileImage, String email, String name, String nickName, String password) {
        this.profileImage = profileImage;
        this.email = email;
        this.name = name;
        this.nickName = nickName;
        this.password = password;
    }

    public static User create(SignupRequest request) {
        try {
            return User.builder()
                    .profileImage(request.getProfileImage())
                    .email(request.getEmail())
                    .name(request.getName())
                    .nickName(request.getNickName())
                    .password(EncryptUtils.encrypt(request.getPassword()))
                    .build();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}