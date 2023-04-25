package com.example.chillisauce.spaces.service;

import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.spaces.dto.BoxRequestDto;
import com.example.chillisauce.spaces.dto.LocationDto;
import com.example.chillisauce.spaces.entity.*;
import com.example.chillisauce.spaces.repository.LocationRepository;
import com.example.chillisauce.spaces.repository.UserLocationRepository;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.entity.UserRoleEnum;
import com.example.chillisauce.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class LocationServiceTest {
    @InjectMocks
    private LocationService locationService;

    @Mock
    private LocationRepository locationRepository;
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserLocationRepository userLocationRepository;

    private UserDetailsImpl details;

    private UserLocation userLocation;



    @BeforeEach
    void setup() {
        User user = User.builder()
                .role(UserRoleEnum.ADMIN)
                .build();
        details = new UserDetailsImpl(user, null);
        Location location = new Location("테스트", "200", "300");
        userLocation = new UserLocation();
        userLocation.setLocation(location);
    }

    @Nested
    @DisplayName("성공케이스")
    class SuccessCase {
        @Test
        void UserLocation_isPresent_true_유저_등록_이동() {
            // given
            Long locationId = 2L;
            Location differentLocation = new Location("다른 테스트", "100", "100");
            details = new UserDetailsImpl(User.builder().role(UserRoleEnum.USER).build(), "test");

            when(userRepository.findById(details.getUser().getId())).thenReturn(Optional.of(details.getUser()));
            when(locationRepository.findById(locationId)).thenReturn(Optional.of(differentLocation));
            when(userLocationRepository.findByUserId(details.getUser().getId())).thenReturn(Optional.of(userLocation));
            // when
            LocationDto locationDto = locationService.moveWithUser(locationId,details);

            // then
            assertEquals(locationDto.getLocationName(), differentLocation.getLocationName());
            assertEquals(locationDto.getX(), differentLocation.getX());
            assertEquals(locationDto.getY(), differentLocation.getY());
        }
    }
}