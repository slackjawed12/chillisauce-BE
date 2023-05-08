package com.example.chillisauce.users.dto.response;

import com.example.chillisauce.users.dto.response.UserDetailResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserListResponseDto {
    List<UserDetailResponseDto> userList;
}