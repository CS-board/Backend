package com.chip.board.me.application.service;

import com.chip.board.global.base.exception.ServiceException;
import com.chip.board.global.base.exception.ErrorCode;
import com.chip.board.me.presentation.dto.response.MyProfileResponse;
import com.chip.board.me.presentation.dto.response.UpdateDepartmentResponse;
import com.chip.board.me.presentation.dto.response.UpdateGradeResponse;
import com.chip.board.register.application.port.UserRepositoryPort;
import com.chip.board.register.domain.Department;
import com.chip.board.register.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProfileService {

    private final UserRepositoryPort userRepositoryPort;

    public MyProfileResponse getMyProfile(Long userId) {
        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));

        return new MyProfileResponse(
                user.getName(),
                user.getStudentId(),
                user.getDepartment(),
                user.getGrade(),
                user.getUsername(),
                user.getBojId(),
                user.getGoalPoints()
        );
    }

    @Transactional
    public UpdateDepartmentResponse updateDepartment(Long userId, String department) {
        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));

        user.changeDepartment(Department.fromDisplayName(department).getDisplayName());
        userRepositoryPort.save(user);

        return new UpdateDepartmentResponse(user.getDepartment());
    }

    @Transactional
    public UpdateGradeResponse updateGrade(Long userId, int grade) {
        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));

        if (grade < 1 || grade > 5) {
            throw new ServiceException(ErrorCode.INVALID_USER_GRADE);
        }

        user.changeGrade(grade);
        userRepositoryPort.save(user);

        return new UpdateGradeResponse(user.getGrade());
    }
}