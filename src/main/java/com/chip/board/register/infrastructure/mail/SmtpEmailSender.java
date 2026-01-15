package com.chip.board.register.infrastructure.mail;

import com.chip.board.global.base.exception.ErrorCode;
import com.chip.board.global.base.exception.ServiceException;
import com.chip.board.register.application.port.EmailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SmtpEmailSender implements EmailSender {
    private final JavaMailSender mailSender;

    @Value("${email.sender}")
    private String senderEmail;

    private static final String PREFIX = "[CHIP_SAT] ";
    private static final String AUTH_SUBJECT = PREFIX + "인증번호 발송";
    private static final String AUTH_BODY = """
        <h3>요청하신 인증 번호입니다.</h3>
        <h1>%s</h1>
        <h3>감사합니다.</h3>
        """;
    private static final String TEMP_SUBJECT = PREFIX + "임시 비밀번호 안내";
    private static final String TEMP_BODY = """
        <h3>요청하신 임시 비밀번호입니다.</h3>
        <h1>%s</h1>
        <h3>보안을 위해 로그인 후 반드시 비밀번호를 변경해 주세요!</h3>
        <h3>감사합니다.</h3>
        """;

    @Override
    public void sendAuthCode(String email, int code) {
        sendHtml(email, AUTH_SUBJECT, String.format(AUTH_BODY, code));
    }

    @Override
    public void sendTempPassword(String email, String tempPassword) {
        sendHtml(email, TEMP_SUBJECT, String.format(TEMP_BODY, tempPassword));
    }

    private void sendHtml(String to, String subject, String html) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            msg.setFrom(senderEmail);
            msg.setRecipients(MimeMessage.RecipientType.TO, to);
            msg.setSubject(subject);
            msg.setText(html, "UTF-8", "html");
            mailSender.send(msg);
        } catch (MessagingException e) {
            throw new ServiceException(ErrorCode.EMAIL_SEND_ERROR);
        }
    }
}