package store.chikendev._2tm.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EntityFileType {
    USER_AVATAR(false, "files_dev/users/user-"),
    STORE_LOGO(false, "files_dev/stores/store-"),
    BILL_OF_LANDING(false, "files_dev/stores/bill-"),

    PRODUCT(true, "files_dev/products/product-"),
    CATEGORY(true, "files_dev/categories/category-"),
    BRAND(true, "files_dev/brands/brand-"),
    SLIDER(true, "files_dev/sliders/slider-"),
    BANNER(true, "files_dev/banners/banner-"),
    ORDER(true, "files_dev/orders/order-"),
    CONSIGNMENT_ORDER(true, "files_dev/consignment/order-"),
    REVIEW(true, "files_dev/reviews/review-"),
    COMMENT(true, "files_dev/comments/comment-");

    private boolean multiple;
    private String dir;
}
