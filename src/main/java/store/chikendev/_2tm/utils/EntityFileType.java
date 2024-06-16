package store.chikendev._2tm.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EntityFileType {
    PRODUCT("files_dev/products/product-"),
    PROFILE("files_dev/profiles/profile-"),
    CATEGORY("files_dev/categories/category-"),
    BRAND("files_dev/brands/brand-"),
    SLIDER("files_dev/sliders/slider-"),
    BANNER("files_dev/banners/banner-"),
    ORDER("files_dev/orders/order-"),
    REVIEW("files_dev/reviews/review-"),
    COMMENT("files_dev/comments/comment-");
    private String dir;
}
