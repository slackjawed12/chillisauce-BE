package com.example.chillisauce.spaces.service;


import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.*;
import com.example.chillisauce.spaces.entity.Box;
import com.example.chillisauce.spaces.entity.Mr;
import com.example.chillisauce.spaces.entity.Space;
import com.example.chillisauce.spaces.exception.SpaceErrorCode;
import com.example.chillisauce.spaces.exception.SpaceException;
import com.example.chillisauce.spaces.repository.BoxRepository;
import com.example.chillisauce.spaces.repository.MrRepository;
import com.example.chillisauce.spaces.repository.SpaceRepository;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.UserRoleEnum;
import com.example.chillisauce.users.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpaceService {
    private final SpaceRepository spaceRepository;
    private final CompanyRepository companyRepository;
    private final BoxRepository boxRepository;
    private final MrRepository mrRepository;


    //공간 생성
    @Transactional
    public SpaceResponseDto createSpace(String companyName, SpaceRequestDto spaceRequestDto, UserDetailsImpl details) {
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }
        Companies companies = companyRepository.findByCompanyName(companyName).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.COMPANIES_NOT_FOUND)
        );
        Space space = spaceRepository.save(new Space(spaceRequestDto, companies));
        return new SpaceResponseDto(space);
    }

    //공간 조회
    @Transactional
    public List<SpaceResponseDto> getSpacelist(String companyName, Long spaceId) {
        Space space = findCompanyNameAndSpaceId(companyName,spaceId);
        List<SpaceResponseDto> spaceResponseDto = new ArrayList<>();
        spaceResponseDto.add(new SpaceResponseDto(space));

        return spaceResponseDto;
    }
    //공간 전체 수정
    @Transactional
    public SpaceResponseDto updateSpace(String companyName, Long spaceId, SpaceRequestDto spaceRequestDto, UserDetailsImpl details) {
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }
        Space space = findCompanyNameAndSpaceId(companyName,spaceId);
        space.updateSpaceName(spaceRequestDto.getSpaceName());

        // 완전히 삭제하고 교체, 삭제를 안하게 될 경우 목록이 중복되거나 오류가 발생할 수 있음
        space.getBoxes().clear();
        space.getMrs().clear();

        // boxes 업데이트 및 추가
        for (BoxRequestDto boxRequestDto : spaceRequestDto.getBoxlist()) {
            Box box = boxRepository.findById(boxRequestDto.getId()).orElseThrow(
                    () -> new SpaceException(SpaceErrorCode.BOX_NOT_FOUND)
            );
            box.updateBox(boxRequestDto);
            space.addBox(box);
        }//Hibernate 에서 발생하는 "A collection with cascade='all-delete-orphan' was no longer referenced by the owning entity instance"
        // 오류를 방지하려고 set 대신 addbox 메서드를 만들어서 사용

        // spaceRequestDto 의 박스 목록을 순회하며 각 Mr를 처리
        for (MrRequestDto mrRequestDto : spaceRequestDto.getMrlist()) {
            // boxRequestDto 의 ID를 사용하여 기존 Mr를 찾음
            Mr mr = mrRepository.findById(mrRequestDto.getId()).orElseThrow(
                    () -> new SpaceException(SpaceErrorCode.MR_NOT_FOUND)
            );
            // mrRequestDto 의 정보로 기존 mr를 업데이트
            mr.updateMr(mrRequestDto);
            // 업데이트된 mr를 공간에 추가
            space.addMr(mr);
        }

        return new SpaceResponseDto(spaceRepository.save(space));
    }
    //사무실 삭제
    @Transactional
    public SpaceResponseDto deleteSpace(String companyName, Long spaceId, UserDetailsImpl details) {
        if (!details.getUser().getRole().equals(UserRoleEnum.ADMIN)) {
            throw new SpaceException(SpaceErrorCode.NOT_HAVE_PERMISSION);
        }
        Space space = findCompanyNameAndSpaceId(companyName,spaceId);
        spaceRepository.deleteById(spaceId);
        return new SpaceResponseDto(space);
    }

    public Space findCompanyNameAndSpaceId(String companyName, Long spaceId) {
        Companies company = companyRepository.findByCompanyName(companyName).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.COMPANIES_NOT_FOUND)
        );
        return spaceRepository.findByIdAndCompanies(spaceId, company).orElseThrow(
                () -> new SpaceException(SpaceErrorCode.SPACE_NOT_FOUND)
        );
    }



}