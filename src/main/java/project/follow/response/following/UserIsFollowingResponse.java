package project.follow.response.following;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserIsFollowingResponse {

    private Long userId;
    private String userProfileImageUrl;
    private String userName;
    private String nickName;
    private Boolean isFollowing;

    public UserIsFollowingResponse(Long userId, String userProfileImageUrl, String userName, String nickName, Boolean isFollowing) {
        this.userId = userId;
        this.userProfileImageUrl = userProfileImageUrl;
        this.userName = userName;
        this.nickName = nickName;
        this.isFollowing = isFollowing;
    }

}