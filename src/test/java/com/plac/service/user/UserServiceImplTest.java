package com.plac.service.user;

import com.plac.domain.User;
import com.plac.dto.request.user.UserReqDto;
import com.plac.dto.response.user.UserResDto;
import com.plac.repository.RefreshTokenRepository;
import com.plac.repository.UserRepository;
import com.plac.service.password_checker.PasswordChecker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordChecker passwordChecker;

    @MockBean
    private RefreshTokenRepository refreshTokenRepository;

    @MockBean
    private BCryptPasswordEncoder encoder;

    @Test
    @DisplayName("사용자 등록(회원가입)이 올바르게 동작하는지 테스트")
    void signUpTest() {
        UserReqDto.CreateUser reqDto = new UserReqDto.CreateUser();
        reqDto.setUsername("test1@email.com");
        reqDto.setPassword("password1234");

        User mockUser = User.builder()
                .username(reqDto.getUsername())
                .password(reqDto.getPassword())
                .provider("normal")
                .roles("ROLE_USER")
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(encoder.encode(anyString())).thenReturn("encodedPassword");
        when(passwordChecker.checkWeakPassword(anyString())).thenReturn(false);

        UserResDto result = userService.signUp(reqDto);

        assertNotNull(result);
        assertEquals(reqDto.getUsername(), result.getUsername());
    }

    @Test
    @DisplayName("유저 조회 테스트. 특정 사용자 ID로 사용자를 조회할 때, 예상대로 사용자가 반환되는지를 확인하기 위해 작성")
    void findUserTest() {
        Long userId = 1L;
        User mockUser = User.builder()
                .id(userId)
                .username("test2@email.com")
                .provider("google")
                .roles("ROLE_USER")
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        User result = userService.findUser(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
    }

    @Test
    @DisplayName("유저 삭제 테스트 : deleteUser() 호출 시 예외가 발생하지 않음을 확인")
    void deleteUserByIdTest() {
        Long userId = 1L;
        User mockUser = User.builder()
                .id(userId)
                .username("test1@email.com")
                .provider("naver")
                .roles("ROLE_USER")
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        doNothing().when(userRepository).delete(mockUser);

        assertDoesNotThrow(() -> userService.deleteUser(userId));
    }

    @Test
    @DisplayName("모든 유저 조회 테스트")
    void findAllUserTest() {
        List<User> users = Arrays.asList(
            User.builder()
                    .username("user1@example.com")
                    .provider("google")
                    .roles("ROLE_USER")
                    .createdAt(LocalDateTime.now())
                    .build(),
            User.builder()
                    .username("user2@example.com")
                    .provider("kakao")
                    .roles("ROLE_USER")
                    .createdAt(LocalDateTime.now())
                    .build()
        );

        when(userRepository.findAll()).thenReturn(users);

        List<UserResDto> result = userService.findAll();
        assertNotNull(result);
        assertEquals(2, result.size());
    }

}