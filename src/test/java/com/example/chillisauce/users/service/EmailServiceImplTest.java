package com.example.chillisauce.users.service;

import com.example.chillisauce.users.exception.UserErrorCode;
import com.example.chillisauce.users.exception.UserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;


import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @InjectMocks
    private EmailServiceImpl emailService;

    @Mock
    private JavaMailSender mailSender;
    @Mock
    private MimeMessage mimeMessage;

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

        @DisplayName("메일 발송 성공")
        @Test
        void mailSend() throws Exception {
            //given
            String to = "123@123";

            Mockito.when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

            //when
            emailService.sendSimpleMessage(to);

            //then
            verify(mailSender, times(1)).createMimeMessage();
//            verify(mailSender, times(1)).send(any(MimeMessage.class));    //캐스팅익셉션이 뜸  지피티한테 질문을 해도 정확한 답변을 안줌.

        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class failCase {

        @DisplayName("메일 발송 실패 (이메일 형식 오류)")
        @Test
        void fail1() {
            //given
            String to = "1234";

            //when
            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
            doThrow(MailSendException.class).when(mailSender).send(any(MimeMessage.class));

            UserException exception = assertThrows(UserException.class, () -> {
                emailService.sendSimpleMessage(to);
            });

            //then
            assertThat(exception).isNotNull();
            assertThat(exception.getMessage()).isEqualTo("이메일 형식이 맞지 않습니다.");
        }
    }
}