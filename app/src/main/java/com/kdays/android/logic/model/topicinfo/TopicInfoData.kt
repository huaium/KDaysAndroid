package com.kdays.android.logic.model.topicinfo

data class TopicInfoData(
    val forum: Forum,
    val pager: Pager,
    val parentForums: Any,
    val permission: Permission,
    val posts: List<Post>,
    val topic: Topic
)