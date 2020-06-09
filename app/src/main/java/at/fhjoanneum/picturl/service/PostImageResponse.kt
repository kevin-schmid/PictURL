package at.fhjoanneum.picturl.service

class PostImageResponse {
    var data: PostImageResponseData? = null
    var success: Boolean = false
}

class PostImageResponseData {
    var id: String = ""
    var deletehash: String = ""
    var link: String = ""
    var title: String = ""
}