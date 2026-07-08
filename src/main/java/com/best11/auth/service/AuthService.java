package com.best11.auth.service;

import com.best11.auth.dto.request.LoginRequestDto;
import com.best11.auth.dto.request.SignupRequestDto;
import com.best11.auth.dto.response.LoginResponseDto;
import com.best11.common.exception.CustomException;
import com.best11.common.exception.ErrorCode;
import com.best11.security.JwtTokenProvider;
import com.best11.user.entity.User;
import com.best11.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void signUp(SignupRequestDto request){
        if(userRepository.existsByEmail(request.email())){
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
        if(userRepository.existsByUsername(request.username())){
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
        }
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(User.Role.USER)
                .build();

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto request){
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if(!passwordEncoder.matches(request.password(), user.getPasswordHash())){
            throw new BadCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        String token = jwtTokenProvider.createToken(user.getId(),user.getRole().name());

        return new LoginResponseDto(token);
    }
}
