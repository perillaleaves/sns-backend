package project.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.advice.error.APIError;
import project.advice.exception.UserNotFoundException;
import project.common.EncryptUtils;
import project.common.GenerateToken;
import project.s3.S3Service;
import project.token.domain.UserToken;
import project.token.repository.TokenRepository;
import project.user.domain.User;
import project.user.domain.UserProfileImage;
import project.user.repository.UserProfileImageRepository;
import project.user.repository.UserRepository;
import project.user.request.LoginRequest;
import project.user.request.ProfileEditRequest;
import project.user.request.SignupRequest;
import project.user.response.UserLoginResponse;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

@Service
public class UserApiService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final UserProfileImageRepository userProfileImageRepository;
    private final S3Service s3Service;

    public UserApiService(UserRepository userRepository, TokenRepository tokenRepository, UserProfileImageRepository userProfileImageRepository, S3Service s3Service) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.userProfileImageRepository = userProfileImageRepository;
        this.s3Service = s3Service;
    }

    @Transactional
    public void signup(SignupRequest request) throws NoSuchAlgorithmException {
        validate(request);

        UserProfileImage userProfileImage = UserProfileImage.builder()
                .userProfileImageURL("logo/default_profile.png")
                .build();
        userProfileImageRepository.save(userProfileImage);

        User user = User.builder()
                .userProfileImage(userProfileImage)
                .email(request.getEmail())
                .name(request.getName())
                .nickName(request.getNickName())
                .password(EncryptUtils.encrypt(request.getPassword()))
                .content("")
                .postSize(0L)
                .followerSize(0L)
                .followingSize(0L)
                .build();
        userRepository.save(user);
    }

    @Transactional
    public UserLoginResponse login(LoginRequest request) throws NoSuchAlgorithmException {
        String s3Url = "https://s3.ap-northeast-2.amazonaws.com/mullaepro.com/";
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(UserNotFoundException::new);
        loginValidate(request, user);

        UserToken token = UserToken.builder()
                .user(user)
                .accessToken(GenerateToken.generatedToken(user, request.getEmail()))
                .build();
        tokenRepository.save(token);
        return new UserLoginResponse(user.getId(),
                s3Url + user.getUserProfileImage().getUserProfileImageURL(),
                user.getNickName(),
                token.getAccessToken());
    }

    @Transactional
    public void editProfile(Long userId, Long loginUserId, ProfileEditRequest request) {
        editInputValidate(userId, loginUserId, request);
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        editValidate(request, user);

        user.editProfile(request);
    }

    @Transactional
    public void editProfileImage(Long userId, Long loginUserId, MultipartFile file, String dirName) throws IOException {
        editProfileImageInputValidate(userId, loginUserId, file);
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        String imgPaths = s3Service.upload(file, dirName);
        user.getUserProfileImage().userProfileImageModify(imgPaths);
    }

    private void validate(SignupRequest request) {
        boolean email_validate = Pattern.matches("\\w+@\\w+\\.\\w+(\\.\\w+)?", request.getEmail());
        if (!email_validate) {
            throw new APIError("InvalidEmail", "???????????? ????????? ?????? ??????????????????.");
        }

        if (2 > request.getName().length() || request.getName().length() > 10) {
            throw new APIError("InvalidName", "????????? 2??? ?????? 10??? ????????? ??????????????????.");
        }
        if (2 > request.getNickName().length() || request.getNickName().length() > 10) {
            throw new APIError("InvalidNickName", "???????????? 2??? ?????? 10??? ????????? ??????????????????.");
        }
        if (5 > request.getPassword().length() || request.getPassword().length() > 10) {
            throw new APIError("InvalidPassword", "??????????????? 5??? ?????? 10??? ????????? ??????????????????.");
        }

        if (userRepository.existsUserByEmail(request.getEmail())) {
            throw new APIError("DuplicatedEmail", "????????? ??????????????????.");
        }
        if (userRepository.existsUserByNickName(request.getNickName())) {
            throw new APIError("DuplicatedNickName", "????????? ??????????????????.");
        }
    }

    private void loginValidate(LoginRequest request, User user) throws NoSuchAlgorithmException {
        if (!user.getPassword().equals(EncryptUtils.encrypt(request.getPassword()))) {
            throw new APIError("InconsistencyPassword", "??????????????? ???????????? ????????????.");
        }
    }

    private void editInputValidate(Long userId, Long loginUserId, ProfileEditRequest request) {
        if (2 > request.getUserName().length() || request.getUserName().length() > 10) {
            throw new APIError("InvalidName", "????????? 2??? ?????? 10??? ????????? ??????????????????.");
        }
        if (2 > request.getNickName().length() || request.getNickName().length() > 10) {
            throw new APIError("InvalidNickName", "???????????? 2??? ?????? 10??? ????????? ??????????????????.");
        }
        if (request.getContent().isEmpty() || request.getContent().length() > 150) {
            throw new APIError("InvalidContent", "????????? 150??? ????????? ??????????????????.");
        }
        if (!loginUserId.equals(userId)) {
            throw new APIError("NotRequest", "????????? ???????????????.");
        }
    }

    private void editValidate(ProfileEditRequest request, User user) {
        if (userRepository.existsUserByNickName(request.getNickName()) && !user.getNickName().equals(request.getNickName())) {
            throw new APIError("DuplicatedNickName", "????????? ??????????????????.");
        }
    }

    private void editProfileImageInputValidate(Long userId, Long loginUserId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new APIError("EmptyPostImage", "?????? 1??? ????????? ????????? ????????? ????????????.");
        }
        if (!loginUserId.equals(userId)) {
            throw new APIError("NotRequest", "????????? ???????????????.");
        }
    }

}