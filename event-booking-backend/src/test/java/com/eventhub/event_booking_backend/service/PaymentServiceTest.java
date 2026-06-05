package com.eventhub.event_booking_backend.service;

import com.eventhub.event_booking_backend.exception.BusinessException;
import com.eventhub.event_booking_backend.model.Booking;
import com.eventhub.event_booking_backend.model.PaymentStatus;
import com.eventhub.event_booking_backend.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Booking testBooking;

    @BeforeEach
    void setUp() {
        testBooking = Booking.builder()
                .id(1L)
                .paymentStatus(PaymentStatus.PENDING)
                .build();
    }

    @Test
    void processPayment_Success() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        paymentService.processPayment(1L);

        assertEquals(PaymentStatus.PAID, testBooking.getPaymentStatus());
        verify(bookingRepository).save(testBooking);
    }

    @Test
    void processPayment_BookingNotFound_ThrowsException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> paymentService.processPayment(1L));
    }

    @Test
    void processPayment_AlreadyPaid_ThrowsException() {
        testBooking.setPaymentStatus(PaymentStatus.PAID);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));

        assertThrows(BusinessException.class, () -> paymentService.processPayment(1L));
    }

    @Test
    void completePayment_Success() {
        testBooking.setPaymentStatus(PaymentStatus.PAID);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        paymentService.completePayment(1L);

        assertEquals(PaymentStatus.COMPLETED, testBooking.getPaymentStatus());
        verify(bookingRepository).save(testBooking);
    }

    @Test
    void completePayment_BookingNotFound_ThrowsException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> paymentService.completePayment(1L));
    }

    @Test
    void completePayment_NotPaid_ThrowsException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));

        assertThrows(BusinessException.class, () -> paymentService.completePayment(1L));
    }

    @Test
    void cancelPayment_Success() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        paymentService.cancelPayment(1L);

        assertEquals(PaymentStatus.CANCELLED, testBooking.getPaymentStatus());
        verify(bookingRepository).save(testBooking);
    }

    @Test
    void cancelPayment_BookingNotFound_ThrowsException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> paymentService.cancelPayment(1L));
    }

    @Test
    void cancelPayment_AlreadyCompleted_ThrowsException() {
        testBooking.setPaymentStatus(PaymentStatus.COMPLETED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));

        assertThrows(BusinessException.class, () -> paymentService.cancelPayment(1L));
    }
}
