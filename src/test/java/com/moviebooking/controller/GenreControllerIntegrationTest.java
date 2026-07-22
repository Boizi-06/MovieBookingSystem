package com.moviebooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviebooking.dto.GenreRequest;
import com.moviebooking.entity.Genre;
import com.moviebooking.entity.Movie;
import com.moviebooking.entity.User;
import com.moviebooking.repository.GenreRepository;
import com.moviebooking.repository.MovieRepository;
import com.moviebooking.repository.UserRepository;
import com.moviebooking.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class GenreControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private User adminUser;
    private User customerUser;
    private String adminToken;
    private String customerToken;

    @BeforeEach
    public void setUp() {
        adminUser = User.builder()
                .fullname("Genre Test Admin")
                .email("genreadmin@moviebooking.com")
                .password("Password123")
                .role("ADMIN")
                .status("ACTIVE")
                .build();
        adminUser = userRepository.save(adminUser);
        adminToken = "Bearer " + jwtTokenProvider.generateToken(adminUser);

        customerUser = User.builder()
                .fullname("Genre Test Customer")
                .email("genrecustomer@moviebooking.com")
                .password("Password123")
                .role("CUSTOMER")
                .status("ACTIVE")
                .build();
        customerUser = userRepository.save(customerUser);
        customerToken = "Bearer " + jwtTokenProvider.generateToken(customerUser);
    }

    @Test
    public void testGetAllGenres_Success() throws Exception {
        Genre genre1 = Genre.builder().name("Chiến tranh Test").description("Phim chiến tranh").build();
        genreRepository.save(genre1);

        mockMvc.perform(get("/api/v1/genres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content[*].name", hasItem("Chiến tranh Test")));
    }

    @Test
    public void testCreateGenre_Success() throws Exception {
        GenreRequest request = GenreRequest.builder()
                .name("Tâm lý Xã hội")
                .description("Thể loại phim tâm lý kịch tính")
                .build();

        mockMvc.perform(post("/api/v1/genres")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("Thêm thể loại phim mới thành công")))
                .andExpect(jsonPath("$.data.name", is("Tâm lý Xã hội")));
    }

    @Test
    public void testCreateGenre_DuplicateName() throws Exception {
        Genre genre = Genre.builder().name("Kinh dị Độc quyền").description("Phim ma rùng rợn").build();
        genreRepository.save(genre);

        GenreRequest duplicateRequest = GenreRequest.builder()
                .name("Kinh dị Độc quyền")
                .description("Thử trùng tên")
                .build();

        mockMvc.perform(post("/api/v1/genres")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Tên thể loại phim đã tồn tại trong hệ thống")));
    }

    @Test
    public void testCreateGenre_ForbiddenForCustomer() throws Exception {
        GenreRequest request = GenreRequest.builder()
                .name("Thể loại Customer")
                .build();

        mockMvc.perform(post("/api/v1/genres")
                        .header("Authorization", customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testUpdateGenre_Success() throws Exception {
        Genre genre = Genre.builder().name("Tên Cũ").description("Mô tả cũ").build();
        genre = genreRepository.save(genre);

        GenreRequest updateRequest = GenreRequest.builder()
                .name("Tên Mới Cập Nhật")
                .description("Mô tả mới cập nhật")
                .build();

        mockMvc.perform(put("/api/v1/genres/" + genre.getId())
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.name", is("Tên Mới Cập Nhật")))
                .andExpect(jsonPath("$.data.description", is("Mô tả mới cập nhật")));
    }

    @Test
    public void testDeleteGenre_Success() throws Exception {
        Genre unusedGenre = Genre.builder().name("Thể loại Rảnh Rỗi").description("Chưa gán phim nào").build();
        unusedGenre = genreRepository.save(unusedGenre);

        mockMvc.perform(delete("/api/v1/genres/" + unusedGenre.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("Xóa thể loại phim thành công")));

        assertFalse(genreRepository.existsById(unusedGenre.getId()));
    }

    @Test
    public void testDeleteGenre_InUseFailure() throws Exception {
        Genre usedGenre = Genre.builder().name("Thể loại Đang Dùng").description("Đã gán cho 1 bộ phim").build();
        usedGenre = genreRepository.save(usedGenre);

        Movie movie = Movie.builder()
                .title("Phim Thử Xóa Thể Loại")
                .duration(100)
                .releaseDate(LocalDate.now())
                .ageRating("P")
                .language("Tiếng Việt")
                .status("NOW_SHOWING")
                .genres(Set.of(usedGenre))
                .build();
        movieRepository.save(movie);

        mockMvc.perform(delete("/api/v1/genres/" + usedGenre.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Không thể xóa thể loại đang được gán cho ít nhất một bộ phim")));
    }
}
