package com.grepp.spring.app.model.festival.service;

import com.grepp.spring.app.model.festival.domain.Festival;
import com.grepp.spring.app.model.festival.model.FestivalDTO;
import com.grepp.spring.app.model.festival.repos.FestivalRepository;
import com.grepp.spring.app.model.place_bookmark.domain.PlaceBookmark;
import com.grepp.spring.app.model.place_bookmark.repos.PlaceBookmarkRepository;
import com.grepp.spring.util.NotFoundException;
import com.grepp.spring.util.ReferencedWarning;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class FestivalService {

    private final FestivalRepository festivalRepository;
    private final PlaceBookmarkRepository placeBookmarkRepository;

    public FestivalService(final FestivalRepository festivalRepository,
            final PlaceBookmarkRepository placeBookmarkRepository) {
        this.festivalRepository = festivalRepository;
        this.placeBookmarkRepository = placeBookmarkRepository;
    }

    public List<FestivalDTO> findAll() {
        final List<Festival> festivals = festivalRepository.findAll(Sort.by("festivalId"));
        return festivals.stream()
                .map(festival -> mapToDTO(festival, new FestivalDTO()))
                .toList();
    }

    public FestivalDTO get(final Long festivalId) {
        return festivalRepository.findById(festivalId)
                .map(festival -> mapToDTO(festival, new FestivalDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final FestivalDTO festivalDTO) {
        final Festival festival = new Festival();
        mapToEntity(festivalDTO, festival);
        return festivalRepository.save(festival).getFestivalId();
    }

    public void update(final Long festivalId, final FestivalDTO festivalDTO) {
        final Festival festival = festivalRepository.findById(festivalId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(festivalDTO, festival);
        festivalRepository.save(festival);
    }

    public void delete(final Long festivalId) {
        festivalRepository.deleteById(festivalId);
    }

    private FestivalDTO mapToDTO(final Festival festival, final FestivalDTO festivalDTO) {
        festivalDTO.setFestivalId(festival.getFestivalId());
        festivalDTO.setName(festival.getName());
        festivalDTO.setLocation(festival.getLocation());
        festivalDTO.setCategory(festival.getCategory());
        festivalDTO.setAddress(festival.getAddress());
        festivalDTO.setStartAt(festival.getStartAt());
        festivalDTO.setTarget(festival.getTarget());
        festivalDTO.setCreatedAt(festival.getCreatedAt());
        festivalDTO.setModifiedAt(festival.getModifiedAt());
        festivalDTO.setActivated(festival.getActivated());
        festivalDTO.setUrl(festival.getUrl());
        festivalDTO.setLongitude(festival.getLongitude());
        festivalDTO.setLatitude(festival.getLatitude());
        festivalDTO.setEndAt(festival.getEndAt());
        return festivalDTO;
    }

    private Festival mapToEntity(final FestivalDTO festivalDTO, final Festival festival) {
        festival.setName(festivalDTO.getName());
        festival.setLocation(festivalDTO.getLocation());
        festival.setCategory(festivalDTO.getCategory());
        festival.setAddress(festivalDTO.getAddress());
        festival.setStartAt(festivalDTO.getStartAt());
        festival.setTarget(festivalDTO.getTarget());
        festival.setCreatedAt(festivalDTO.getCreatedAt());
        festival.setModifiedAt(festivalDTO.getModifiedAt());
        festival.setActivated(festivalDTO.getActivated());
        festival.setUrl(festivalDTO.getUrl());
        festival.setLongitude(festivalDTO.getLongitude());
        festival.setLatitude(festivalDTO.getLatitude());
        festival.setEndAt(festivalDTO.getEndAt());
        return festival;
    }

    public ReferencedWarning getReferencedWarning(final Long festivalId) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Festival festival = festivalRepository.findById(festivalId)
                .orElseThrow(NotFoundException::new);
        final PlaceBookmark festivalPlaceBookmark = placeBookmarkRepository.findFirstByFestival(festival);
        if (festivalPlaceBookmark != null) {
            referencedWarning.setKey("festival.placeBookmark.festival.referenced");
            referencedWarning.addParam(festivalPlaceBookmark.getPBookmarkId());
            return referencedWarning;
        }
        return null;
    }

}
