package com.eventhub.event_booking_backend.service;

import com.eventhub.event_booking_backend.exception.BusinessException;
import com.eventhub.event_booking_backend.model.Booking;
import com.eventhub.event_booking_backend.model.PaymentStatus;
import com.eventhub.event_booking_backend.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final BookingRepository bookingRepository;

    @Transactional
    public void processPayment(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BusinessException("Booking not found"));

        if (booking.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new BusinessException("Payment already processed for this booking");
        }

        booking.setPaymentStatus(PaymentStatus.PAID);
        bookingRepository.save(booking);
    }

    @Transactional
    public void completePayment(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BusinessException("Booking not found"));

        if (booking.getPaymentStatus() != PaymentStatus.PAID) {
            throw new BusinessException("Booking must be PAID before completing");
        }

        booking.setPaymentStatus(PaymentStatus.COMPLETED);
        bookingRepository.save(booking);
    }

    @Transactional
    public void cancelPayment(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BusinessException("Booking not found"));

        if (booking.getPaymentStatus() == PaymentStatus.COMPLETED) {
            throw new BusinessException("Cannot cancel a completed payment");
        }

        booking.setPaymentStatus(PaymentStatus.CANCELLED);
        bookingRepository.save(booking);
    }
}
