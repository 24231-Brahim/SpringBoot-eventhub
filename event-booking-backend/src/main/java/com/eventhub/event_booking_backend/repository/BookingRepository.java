package com.eventhub.event_booking_backend.repository;

import com.eventhub.event_booking_backend.model.Booking;
import com.eventhub.event_booking_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);
    boolean existsByEventIdAndUserId(Long eventId, Long userId);
}