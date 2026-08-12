package com.internflow.api.internship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
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

    @Test
    void createShouldReturnCreatedInternship() {
        CreateInternshipRequest request = new CreateInternshipRequest("Java Internship", "BMW", 6);

        when(internshipRepository.save(any(Internship.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InternshipResponse internshipResponse = internshipService.create(request);
        assertEquals(request.title(), internshipResponse.title());
        assertEquals(request.company(), internshipResponse.company());
        assertEquals(request.durationInMonths(), internshipResponse.durationInMonths());
        assertEquals(InternshipStatus.OPEN, internshipResponse.status());

        verify(internshipRepository).save(any(Internship.class));
    }

    @Test
    void updateShouldReturnUpdatedInternshipWhenInternshipExists() {
        Internship existing = new Internship("Java Internship", "BMW", 6);
        CreateInternshipRequest request = new CreateInternshipRequest("Backend Internship", "Siemens", 8);

        when(internshipRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(internshipRepository.save(existing)).thenReturn(existing);
        Optional<InternshipResponse> result = internshipService.update(1L, request);
        assertTrue(result.isPresent());

        InternshipResponse response = result.get();
        assertEquals(request.title(), response.title());
        assertEquals(request.company(), response.company());
        assertEquals(request.durationInMonths(), response.durationInMonths());
        assertEquals(InternshipStatus.OPEN, response.status());

        verify(internshipRepository).save(existing);
    }

    @Test
    void updateShouldReturnEmptyWhenInternshipDoesNotExist() {
        CreateInternshipRequest request = new CreateInternshipRequest("Backend Internship", "Siemens", 8);

        when(internshipRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<InternshipResponse> result = internshipService.update(1L, request);
        assertTrue(result.isEmpty());

        verify(internshipRepository, never()).save(any(Internship.class));
    }

    @Test
    void findAllInternshipsShouldReturnAllInternships() {
        Internship internship1 = new Internship("Java Internship", "BMW", 6);
        Internship internship2 = new Internship("Backend Internship", "Siemens", 8);

        when(internshipRepository.findAll()).thenReturn(List.of(internship1, internship2));
        List<InternshipResponse> result = internshipService.findAllInternships(null, null);

        assertEquals(2, result.size());

        assertEquals(internship1.getTitle(), result.get(0).title());
        assertEquals(internship1.getCompany(), result.get(0).company());
        assertEquals(internship1.getDurationInMonths(), result.get(0).durationInMonths());
        assertEquals(internship1.getStatus(), result.get(0).status());

        assertEquals(internship2.getTitle(), result.get(1).title());
        assertEquals(internship2.getCompany(), result.get(1).company());
        assertEquals(internship2.getDurationInMonths(), result.get(1).durationInMonths());
        assertEquals(internship2.getStatus(), result.get(1).status());

        verify(internshipRepository).findAll();

    }

    @Test
    void findAllInternshipsShouldReturnInternshipsFilteredByStatus() {
        Internship internship1 = new Internship("Java Internship", "BMW", 6);
        when(internshipRepository.findByStatus(InternshipStatus.OPEN)).thenReturn(List.of(internship1));
        List<InternshipResponse> result = internshipService.findAllInternships(InternshipStatus.OPEN, null);

        assertEquals(1, result.size());
        assertEquals(InternshipStatus.OPEN, result.get(0).status());

        verify(internshipRepository, never()).findAll();
        verify(internshipRepository).findByStatus(InternshipStatus.OPEN);
    }

    @Test
    void findAllInternshipsShouldReturnInternshipsFilteredByCompany() {
        Internship internship1 = new Internship("Java Internship", "BMW", 6);
        when(internshipRepository.findByCompany(internship1.getCompany())).thenReturn(List.of(internship1));
        List<InternshipResponse> result = internshipService.findAllInternships(null, "BMW");

        assertEquals(1, result.size());
        assertEquals("BMW", result.get(0).company());

        verify(internshipRepository).findByCompany("BMW");
        verify(internshipRepository, never()).findByStatus(any());
        verify(internshipRepository, never()).findAll();
        verify(internshipRepository, never()).findByStatusAndCompany(any(), any());
    }

    @Test
    void findAllInternshipsShouldReturnInternshipsFilteredByStatusAndCompany() {
        Internship internship1 = new Internship("Java Internship", "BMW", 6);
        when(internshipRepository.findByStatusAndCompany(InternshipStatus.OPEN, "BMW")).thenReturn(List.of(internship1));
        List<InternshipResponse> result = internshipService.findAllInternships(InternshipStatus.OPEN, "BMW");

        assertEquals(1, result.size());
        assertEquals("BMW", result.get(0).company());
        assertEquals(InternshipStatus.OPEN, result.get(0).status());

        verify(internshipRepository).findByStatusAndCompany(InternshipStatus.OPEN, "BMW");
        verify(internshipRepository, never()).findByCompany(any());
        verify(internshipRepository, never()).findByStatus(any());
        verify(internshipRepository, never()).findAll();
    }

}
