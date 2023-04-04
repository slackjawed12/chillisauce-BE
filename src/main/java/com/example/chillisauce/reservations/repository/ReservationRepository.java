package com.example.chillisauce.reservations.repository;

import com.example.chillisauce.reservations.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("select r from Reservation r " +
            "where r.meetingRoom.id=:meetingRoomId and r.startTime< :endTime and r.endTime > :startTime")
    Optional<Reservation> findFirstByMeetingRoomAndTime(
            @Param("meetingRoomId") Long meetingRoomId,
            @Param("startTime") LocalDateTime start,
            @Param("endTime") LocalDateTime end);

    @Query("select r from Reservation r " +
            "where r.meetingRoom.id=:meetingRoomId and r.startTime between :startTime and :endTime")
    List<Reservation> findAllByMeetingRoomIdAndStartTimeBetween(
            @Param("meetingRoomId") Long meetingRoomId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    List<Reservation> findAllByMeetingRoomId(Long meetingRoomId);
}
