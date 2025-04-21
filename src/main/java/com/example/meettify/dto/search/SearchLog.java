package com.example.meettify.dto.search;

import com.example.meettify.dto.meet.category.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class SearchLog {
    private String name;
    private String createdAt;

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
