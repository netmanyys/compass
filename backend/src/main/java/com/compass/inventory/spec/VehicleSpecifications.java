package com.compass.inventory.spec;

import com.compass.inventory.entity.PackageEntity;
import com.compass.inventory.entity.Vehicle;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class VehicleSpecifications {
    public static Specification<Vehicle> vinEquals(String vin) {
        return (root, query, cb) -> vin == null ? null : cb.equal(cb.lower(root.get("vin")), vin.toLowerCase(Locale.ROOT));
    }

    public static Specification<Vehicle> manufactureLike(String manufacture) {
        return (root, query, cb) -> manufacture == null ? null : cb.like(cb.lower(root.get("manufacture")), like(manufacture));
    }

    public static Specification<Vehicle> modelLike(String model) {
        return (root, query, cb) -> model == null ? null : cb.like(cb.lower(root.get("model")), like(model));
    }

    public static Specification<Vehicle> yearMin(Integer yearMin) {
        return (root, query, cb) -> yearMin == null ? null : cb.greaterThanOrEqualTo(root.get("year"), yearMin);
    }

    public static Specification<Vehicle> yearMax(Integer yearMax) {
        return (root, query, cb) -> yearMax == null ? null : cb.lessThanOrEqualTo(root.get("year"), yearMax);
    }

    public static Specification<Vehicle> priceMin(java.math.BigDecimal priceMin) {
        return (root, query, cb) -> priceMin == null ? null : cb.greaterThanOrEqualTo(root.get("listPrice"), priceMin);
    }

    public static Specification<Vehicle> priceMax(java.math.BigDecimal priceMax) {
        return (root, query, cb) -> priceMax == null ? null : cb.lessThanOrEqualTo(root.get("listPrice"), priceMax);
    }

    public static Specification<Vehicle> mileageMin(Integer mileageMin) {
        return (root, query, cb) -> mileageMin == null ? null : cb.greaterThanOrEqualTo(root.get("mileage"), mileageMin);
    }

    public static Specification<Vehicle> mileageMax(Integer mileageMax) {
        return (root, query, cb) -> mileageMax == null ? null : cb.lessThanOrEqualTo(root.get("mileage"), mileageMax);
    }

    public static Specification<Vehicle> colorEquals(String color) {
        return (root, query, cb) -> color == null ? null : cb.equal(cb.lower(root.get("color")), color.toLowerCase(Locale.ROOT));
    }

    public static Specification<Vehicle> statusEquals(String status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Vehicle> conditionEquals(String conditionGrade) {
        return (root, query, cb) -> conditionGrade == null ? null : cb.equal(root.get("conditionGrade"), conditionGrade);
    }

    public static Specification<Vehicle> packageIdsIn(List<UUID> packageIds) {
        return (root, query, cb) -> {
            if (packageIds == null || packageIds.isEmpty()) {
                return null;
            }
            query.distinct(true);
            Join<Vehicle, PackageEntity> join = root.join("packages", JoinType.LEFT);
            return join.get("id").in(packageIds);
        };
    }

    public static Specification<Vehicle> keywordLike(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return null;
            }
            String pattern = like(keyword);
            return cb.or(
                cb.like(cb.lower(root.get("vin")), pattern),
                cb.like(cb.lower(root.get("manufacture")), pattern),
                cb.like(cb.lower(root.get("model")), pattern),
                cb.like(cb.lower(root.get("color")), pattern),
                cb.like(cb.lower(root.get("trim")), pattern),
                cb.like(cb.lower(root.get("bodyType")), pattern),
                cb.like(cb.lower(root.get("location")), pattern)
            );
        };
    }

    private static String like(String value) {
        return "%" + value.toLowerCase(Locale.ROOT) + "%";
    }
}
