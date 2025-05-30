package com.example.meettify.dto.search;

import com.example.meettify.dto.meet.category.Category;
import lombok.*;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class SearchLog {
    private String name;
    private String createdAt;
    private List<Category> category;

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof SearchLog)) return false;
        SearchLog that = (SearchLog) obj;
        return Objects.equals(name, that.name);
    }
}
