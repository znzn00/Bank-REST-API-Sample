package com.zhen.backend.util;

import com.zhen.backend.respository.MatchType;
import com.zhen.backend.respository.SpecificationImpl;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;

@Getter
public class QueryToJpa<T> {
    private Specification<T> specification;
    private Sort sort;
    private Pageable pageable;

    public QueryToJpa(Map<String, String> queryParams) {
        Integer page = null;
        Integer limit = null;
        if (queryParams.containsKey("page")) {
            page = Integer.valueOf(queryParams.get("page"));
            queryParams.remove("page");
        }
        if (queryParams.containsKey("limit")) {
            limit = Integer.valueOf(queryParams.get("limit"));
            if (page == null) {
                page = 0;
            }
            queryParams.remove("limit");
        }

        if (queryParams.containsKey("sortBy")) {
            String[] sorting = queryParams.get("sortBy").split(",");

            for (String sortString :
                    sorting) {
                String[] splitted = sortString.split(".");
                Sort.Direction direction = Sort.Direction.valueOf(splitted[1].toUpperCase());
                if (this.sort == null)
                    this.sort = Sort.by(direction, splitted[0]);
                else
                    this.sort = this.sort.and(Sort.by(direction, splitted[0]));

            }
        }
        if (limit != null) {
            if (sort == null)
                this.pageable = PageRequest.of(page, limit);
            else
                this.pageable = PageRequest.of(page, limit, sort);
        }

        for (Map.Entry<String, String> e :
                queryParams.entrySet()) {
            String queryKey = e.getKey();
            Specification<T> temporal;
            if (queryKey.contains(":")) {
                String[] queryKeyNames = queryKey.split(":");
                temporal = new SpecificationImpl<T>(queryKeyNames[0], MatchType.valueOfEtiqueta(queryKeyNames[1]), e.getValue());
            } else {
                temporal = new SpecificationImpl<T>(queryKey, MatchType.EQUAL, e.getValue());
            }
            if (this.specification == null)
                this.specification = Specification.where(temporal);
            else
                this.specification = this.specification.and(temporal);
        }
    }

}
