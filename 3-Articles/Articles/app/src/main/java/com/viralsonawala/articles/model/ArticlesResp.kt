package com.viralsonawala.articles.model

import com.google.gson.annotations.SerializedName


data class ArticlesResp(
        @SerializedName("count") var count: Int,
        @SerializedName("total_count") var totalCount: Int,
        @SerializedName("page_size") var pageSize: Int,
        @SerializedName("total_pages") var totalPages: Double,
        @SerializedName("next") var next: String,
        @SerializedName("previous") var previous: Any,
        @SerializedName("current_page") var currentPage: Int,
        @SerializedName("metadata") var metadata: Metadata,
        @SerializedName("message") var message: String,
        @SerializedName("data") var data: List<Article>
) {

    class Metadata

    data class Article(
            @SerializedName("title") var title: String,
            @SerializedName("slug") var slug: String,
            @SerializedName("status") var status: Int,
            @SerializedName("content_code") var contentCode: Any,
            @SerializedName("authors") var authors: List<Author>,
            @SerializedName("approvers") var approvers: List<Any>,
            @SerializedName("description") var description: String,
            @SerializedName("video_content") var videoContent: Any,
            @SerializedName("article_type_fk") var articleTypeFk: String,
            @SerializedName("featured_article") var featuredArticle: Boolean,
            @SerializedName("featured_image") var featuredImage: List<FeaturedImage>,
            @SerializedName("publish_date") var publishDate: String,
            @SerializedName("publish_date_readable") var publishDateReadable: String,
            @SerializedName("created_readable") var createdReadable: String,
            @SerializedName("region_list") var regionList: List<Region>,
            @SerializedName("tag_list") var tagList: List<TagItem>,
            @SerializedName("report_slug") var reportSlug: Any,
            @SerializedName("get_article_type_display") var getArticleTypeDisplay: String,
            @SerializedName("get_status_display") var getStatusDisplay: String,
            @SerializedName("subscription_package") var subscriptionPackage: List<SubscriptionPackage>
    ) {

        data class Region(
                @SerializedName("name") var name: String,
                @SerializedName("slug") var slug: String
        )


        data class SubscriptionPackage(
                @SerializedName("name") var name: String,
                @SerializedName("id") var id: Int
        )


        data class Author(
                @SerializedName("picture") var picture: String,
                @SerializedName("full_name") var fullName: String,
                @SerializedName("profile_slug") var profileSlug: String
        )


        data class FeaturedImage(
                @SerializedName("image_file") var imageFile: String
        )

        data class TagItem(
                @SerializedName("slug") var slug: String,
                @SerializedName("title") var title: String
        )
    }
}