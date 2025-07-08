package com.grepp.spring.app.model.library.service;

import com.grepp.spring.app.model.library.domain.Library;
import com.grepp.spring.app.model.library.model.LibraryDTO;
import com.grepp.spring.app.model.library.repos.LibraryRepository;
import com.grepp.spring.app.model.place_bookmark.domain.PlaceBookmark;
import com.grepp.spring.app.model.place_bookmark.repos.PlaceBookmarkRepository;
import com.grepp.spring.util.NotFoundException;
import com.grepp.spring.util.ReferencedWarning;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class LibraryService {

    private final LibraryRepository libraryRepository;
    private final PlaceBookmarkRepository placeBookmarkRepository;

    public LibraryService(final LibraryRepository libraryRepository,
            final PlaceBookmarkRepository placeBookmarkRepository) {
        this.libraryRepository = libraryRepository;
        this.placeBookmarkRepository = placeBookmarkRepository;
    }

    public List<LibraryDTO> findAll() {
        final List<Library> libraries = libraryRepository.findAll(Sort.by("libraryId"));
        return libraries.stream()
                .map(library -> mapToDTO(library, new LibraryDTO()))
                .toList();
    }

    public LibraryDTO get(final Long libraryId) {
        return libraryRepository.findById(libraryId)
                .map(library -> mapToDTO(library, new LibraryDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final LibraryDTO libraryDTO) {
        final Library library = new Library();
        mapToEntity(libraryDTO, library);
        return libraryRepository.save(library).getLibraryId();
    }

    public void update(final Long libraryId, final LibraryDTO libraryDTO) {
        final Library library = libraryRepository.findById(libraryId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(libraryDTO, library);
        libraryRepository.save(library);
    }

    public void delete(final Long libraryId) {
        libraryRepository.deleteById(libraryId);
    }

    private LibraryDTO mapToDTO(final Library library, final LibraryDTO libraryDTO) {
        libraryDTO.setLibraryId(library.getLibraryId());
        libraryDTO.setName(library.getName());
        libraryDTO.setAddress(library.getAddress());
        libraryDTO.setLongitude(library.getLongitude());
        libraryDTO.setLatitude(library.getLatitude());
        libraryDTO.setUrl(library.getUrl());
        libraryDTO.setActivated(library.getActivated());
        libraryDTO.setCreatedAt(library.getCreatedAt());
        libraryDTO.setModifiedAt(library.getModifiedAt());
        return libraryDTO;
    }

    private Library mapToEntity(final LibraryDTO libraryDTO, final Library library) {
        library.setName(libraryDTO.getName());
        library.setAddress(libraryDTO.getAddress());
        library.setLongitude(libraryDTO.getLongitude());
        library.setLatitude(libraryDTO.getLatitude());
        library.setUrl(libraryDTO.getUrl());
        library.setActivated(libraryDTO.getActivated());
        library.setCreatedAt(libraryDTO.getCreatedAt());
        library.setModifiedAt(libraryDTO.getModifiedAt());
        return library;
    }

    public ReferencedWarning getReferencedWarning(final Long libraryId) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Library library = libraryRepository.findById(libraryId)
                .orElseThrow(NotFoundException::new);
        final PlaceBookmark libraryPlaceBookmark = placeBookmarkRepository.findFirstByLibrary(library);
        if (libraryPlaceBookmark != null) {
            referencedWarning.setKey("library.placeBookmark.library.referenced");
            referencedWarning.addParam(libraryPlaceBookmark.getPBookmarkId());
            return referencedWarning;
        }
        return null;
    }

}
