package com.internflow.api.internship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InternshipServiceTest {
    @Mock
    private InternshipRepository internshipRepository;

    @InjectMocks
    private InternshipService internshipService;

    @Test
    void deleteShouldReturnFalseWhenInternshipDoesNotExist() {
        when(internshipRepository.existsById(1L)).thenReturn(false);

        boolean result = internshipService.delete(1L);

        assertFalse(result);
        verify(internshipRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteShouldReturnTrueWhenInternshipDoesExist() {
        when(internshipRepository.existsById(1L)).thenReturn(true);

        boolean result = internshipService.delete(1L);

        assertTrue(result);
        verify(internshipRepository).deleteById(1L);
    }

    @Test
    void findInternshipByIdShouldReturnEmptyWhenInternshipDoesNotExist() {
        when(internshipRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<InternshipResponse> result = internshipService.findInternshipById(1L);
        assertTrue(result.isEmpty());
    }

    @Test
    void findInternshipByIdShouldReturnInternshipWhenInternshipExists() {
        Internship internship = new Internship("Java Internship", "BMW", 6);
        when(internshipRepository.findById(1L)).thenReturn(Optional.of(internship));

        Optional<InternshipResponse> result = internshipService.findInternshipById(1L);
        assertTrue(result.isPresent());

        InternshipResponse response = result.get();
        assertEquals(internship.getTitle(), response.title());
        assertEquals(internship.getCompany(), response.company());
        assertEquals(internship.getDurationInMonths(), response.durationInMonths());
        assertEquals(internship.getStatus(), response.status());
    }

    @Test
    void updateStatusShouldReturnUpdatedInternshipWhenInternshipExists() {
        Internship internship = new Internship("Java Internship", "BMW", 6);
        when(internshipRepository.findById(1L)).thenReturn(Optional.of(internship));
        when(internshipRepository.save(internship)).thenReturn(internship);

        Optional<InternshipResponse> result = internshipService.updateStatus(1L, InternshipStatus.COMPLETED);
        assertTrue(result.isPresent());
        InternshipResponse response = result.get();
        assertEquals(InternshipStatus.COMPLETED, response.status());

        verify(internshipRepository).save(internship);
    }

    @Test
    void updateStatusShouldReturnEmptyWhenInternshipDoesNotExist() {
        when(internshipRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<InternshipResponse> result = internshipService.updateStatus(1L, InternshipStatus.COMPLETED);
        assertTrue(result.isEmpty());

        verify(internshipRepository, never()).save(any(Internship.class));
    }
}
