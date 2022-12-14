package com.zhen.backend.respository;

import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@AllArgsConstructor
public class SpecificationImpl<T> implements Specification<T> {
    private String key;
    private MatchType matchType;
    private String value;

    private Enum<?>[] getEnumerables(Class<?> enumerable, String[] strings) {
        Enum<?>[] enums = new Enum[strings.length];
        for (int i = 0; i < strings.length; i++) {
            enums[i] = Enum.valueOf((Class<? extends Enum>) enumerable, strings[i].toUpperCase());
        }
        return enums;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        String[] values;
        Class<?> type = root.get(key).getJavaType();
        if(type.isEnum()) {
            Enum<?> enumValue = null;
            Enum<?>[] enumValues;
            switch (matchType) {
                case EQUAL:
                    enumValue = Enum.valueOf((Class<? extends Enum>) type, value.toUpperCase());
                    return criteriaBuilder.equal(root.get(key), enumValue);
                case NOT_EQUAL:
                    enumValue = Enum.valueOf((Class<? extends Enum>) type, value.toUpperCase());
                    return criteriaBuilder.notEqual(root.get(key), value);
                case IN:
                    enumValues = getEnumerables(type, value.split(","));
                    return root.get(key).in(enumValues);
                case NOT_IN:
                    enumValues = getEnumerables(type, value.split(","));
                    return root.get(key).in(enumValues).not();
            }
            return null;
        }
        switch (matchType) {
            case NOT_EQUAL:
                return criteriaBuilder.notEqual(root.get(key), value);
            case LIKE:
                return criteriaBuilder.like(root.get(key), "%" + value + "%");
            case NOT_LIKE:
                return criteriaBuilder.notLike(root.get(key), "%" + value + "%");
            case LESS_THAN:
                return criteriaBuilder.lessThan(root.get(key), value);
            case LESS_THAN_OR_EQUAL:
                return criteriaBuilder.lessThanOrEqualTo(root.get(key), value);
            case GREATER_THAN:
                return criteriaBuilder.greaterThan(root.get(key), value);
            case GREATER_THAN_OR_EQUAL:
                return criteriaBuilder.greaterThanOrEqualTo(root.get(key), value);
            case START_WITH:
                return criteriaBuilder.like(root.get(key), value+"%");
            case NOT_STARTING_WITH:
                return criteriaBuilder.notLike(root.get(key), value+"%");
            case ENDS_WITH:
                return criteriaBuilder.like(root.get(key),"%"+value);
            case NOT_ENDING_WITH:
                return criteriaBuilder.notLike(root.get(key), "%"+value);
            case BETWEEN:
                values = value.split(",");
                if (values.length != 2)
                    return null;
                return criteriaBuilder.between(root.get(key), values[0], values[1]);
            case IN:
                values = value.split(",");
                return root.get(key).in(values);
            case NOT_IN:
                values = value.split(",");
                return root.get(key).in(values).not();
        }
        return criteriaBuilder.equal(root.get(key), value);
    }
}
