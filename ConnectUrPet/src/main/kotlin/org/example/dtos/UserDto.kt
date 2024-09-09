package org.example.dtos

import java.util.*

/*data class PrivilegeDetails (
    var id: Long? = null,
    var name: String? = null
)

data class RoleDetails (
    var id: Long? = null,
    var name: String? = null,
    var privileges: List<PrivilegeDetails>? = null,
)*/
data class UserLoginInput(
    var username: String? = "",
    var password: String? = ""
)


data class UserRegisterInput(
    var name: String,
    var email: String,
    var password: String,
    var createDate: Date?=null,
    var description: String?="",
    var location:  String?="",
)


data class AdopterResult (
    var name: String,
    var email: String,
    var description: String,
    var location:  String,
)

data class GiverResult(
    var name: String,
    var email: String,
    var description: String,
    var location:  String,
)

data class AdopterEditRequest(
    var id: String,
    var name: String,
    var email: String,
    var description: String,
    var location:  String,
)

data class GiverEditRequest(
    var id: String,
    var name: String,
    var email: String,
    var description: String,
    var location:  String,
)

data class ReviewInput(
    var text: String,
    var puntuation: Int,
    var adopterId: String,
    var giverId: String,
)

data class ReviewResult(
    var text: String,
    var puntuation: Int,
)

data class ReactionDetails(
    //var id: Int? = null,
    var pet: Int,
    var adopter: String,
    var giver: String,
    var match: Boolean,
    var view: Boolean,
)

data class ReactionResult(
    var id: String? = null,
    var pet: String,
    var adopter: String,
    var giver: String,
    var match: Boolean,
    var view: Boolean,
)

data class ReactionResponse(
    var id: String? = null,
)

data class ReactionUpdateRequest(
    var id: String,
    var match: Boolean,
    var view: Boolean
)

data class NotisRequest(
    var id: String
)

data class Noti(
    var message: String,
    var view: Boolean
)

data class NotisResult(
    var notisList:List<Noti>
)