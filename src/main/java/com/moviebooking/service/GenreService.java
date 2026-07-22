package com.moviebooking.service;

import com.moviebooking.dto.GenreRequest;
import com.moviebooking.dto.GenreResponse;
import com.moviebooking.entity.Genre;
import com.moviebooking.repository.GenreRepository;
import com.moviebooking.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GenreService {

    private final GenreRepository genreRepository;
    private final MovieRepository movieRepository;

    @Autowired
    public GenreService(GenreRepository genreRepository, MovieRepository movieRepository) {
        this.genreRepository = genreRepository;
        this.movieRepository = movieRepository;
    }

    @Transactional(readOnly = true)
    public Page<GenreResponse> getAllGenres(String keyword, Pageable pageable) {
        Page<Genre> genrePage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            genrePage = genreRepository.findByNameContainingIgnoreCase(keyword.trim(), pageable);
        } else {
            genrePage = genreRepository.findAll(pageable);
        }
        return genrePage.map(GenreResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public GenreResponse getGenreById(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thể loại phim với ID: " + id));
        return GenreResponse.fromEntity(genre);
    }

    @Transactional
    public GenreResponse createGenre(GenreRequest request) {
        String name = request.getName().trim();

        // BR-MOVIE-31: Tên thể loại phải là duy nhất
        if (genreRepository.existsByName(name)) {
            throw new IllegalArgumentException("Tên thể loại phim đã tồn tại trong hệ thống");
        }

        Genre genre = Genre.builder()
                .name(name)
                .description(request.getDescription() != null ? request.getDescription().trim() : null)
                .build();

        Genre savedGenre = genreRepository.save(genre);
        return GenreResponse.fromEntity(savedGenre);
    }

    @Transactional
    public GenreResponse updateGenre(Long id, GenreRequest request) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thể loại phim với ID: " + id));

        String name = request.getName().trim();

        // BR-MOVIE-31: Kiểm tra trùng tên đối với các thể loại khác
        if (genreRepository.existsByNameAndIdNot(name, id)) {
            throw new IllegalArgumentException("Tên thể loại phim đã tồn tại trong hệ thống");
        }

        genre.setName(name);
        genre.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);

        Genre updatedGenre = genreRepository.save(genre);
        return GenreResponse.fromEntity(updatedGenre);
    }

    @Transactional
    public void deleteGenre(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thể loại phim với ID: " + id));

        // BR-MOVIE-32: Không được xóa thể loại đang được gán cho ít nhất một phim
        boolean isUsed = movieRepository.existsByGenresId(id);
        if (isUsed) {
            throw new IllegalArgumentException("Không thể xóa thể loại đang được gán cho ít nhất một bộ phim");
        }

        genreRepository.delete(genre);
    }
}
