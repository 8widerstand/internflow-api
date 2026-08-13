package com.internflow.api.internship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
    void findAllInternshipsShouldReturnPagedInternships() {
        Internship internship1 = new Internship("Java Internship", "BMW", 6);
        Internship internship2 = new Internship("Backend Internship", "Siemens", 8);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Internship> page = new PageImpl<>(List.of(internship1, internship2), pageable, 2);
        when(internshipRepository.findAll(pageable)).thenReturn(page);

        Page<InternshipResponse> result = internshipService.findAllInternships(null, null, pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(internship1.getTitle(), result.getContent().get(0).title());
        verify(internshipRepository).findAll(pageable);
    }

    @Test
    void findAllInternshipsShouldReturnPagedInternshipsFilteredByStatus() {
        Internship internship1 = new Internship("Java Internship", "BMW", 6);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Internship> page = new PageImpl<>(List.of(internship1), pageable, 1);
        when(internshipRepository.findByStatus(InternshipStatus.OPEN, pageable)).thenReturn(page);

        Page<InternshipResponse> result = internshipService.findAllInternships(InternshipStatus.OPEN, null, pageable);
        assertEquals(1, result.getContent().size());
        assertEquals(internship1.getTitle(), result.getContent().get(0).title());

        verify(internshipRepository).findByStatus(InternshipStatus.OPEN, pageable);
        verify(internshipRepository, never()).findAll(pageable);
    }

    @Test
    void findAllInternshipsShouldReturnPagedInternshipsFilteredByCompany() {
        Internship internship1 = new Internship("Java Internship", "BMW", 6);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Internship> page = new PageImpl<>(List.of(internship1), pageable, 1);
        when(internshipRepository.findByCompanyContainingIgnoreCase("bm", pageable)).thenReturn(page);

        Page<InternshipResponse> result = internshipService.findAllInternships(null, "bm", pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(internship1.getTitle(), result.getContent().get(0).title());

        verify(internshipRepository).findByCompanyContainingIgnoreCase("bm", pageable);
        verify(internshipRepository, never()).findAll(pageable);
    }

    @Test
    void findAllInternshipsShouldReturnPagedInternshipsFilteredByStatusAndCompany() {
        Internship internship1 = new Internship("Java Internship", "BMW", 6);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Internship> page = new PageImpl<>(List.of(internship1), pageable, 1);
        when(internshipRepository.findByStatusAndCompanyContainingIgnoreCase(InternshipStatus.OPEN, "bm", pageable))
                .thenReturn(page);

        Page<InternshipResponse> result = internshipService.findAllInternships(InternshipStatus.OPEN, "bm", pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(internship1.getTitle(), result.getContent().get(0).title());

        verify(internshipRepository).findByStatusAndCompanyContainingIgnoreCase(InternshipStatus.OPEN, "bm", pageable);
        verify(internshipRepository, never()).findAll(pageable);
    }
}
