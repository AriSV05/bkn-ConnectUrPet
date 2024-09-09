package org.example.mappers

import org.example.dtos.*
import org.example.entities.Reaction
import org.example.entities.Review
import org.example.entities.UserInformation
import org.mapstruct.*
import java.time.LocalDateTime

@Mapper(
    imports = [LocalDateTime::class],
    componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface LoginRegisMapper {


    @Mapping(target = "createDate", defaultExpression ="java(new java.util.Date())")
    @Mapping(target = "enabled", constant = "true")
    fun registerInputToUser(userInfo: UserRegisterInput): UserInformation

}

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface GiverMapper {
    fun userInformationToGiverResult(
        giver: UserInformation?,
    ): GiverResult
}

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface AdopterMapper {
    fun userInformationToAdopterResult(
        user: UserInformation?,
    ): AdopterResult
}

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface ReviewMapper {
    fun reviewToReviewResult(
        review: Review?,
    ): ReviewResult

    fun reviewListToReviewResultList(
        reviews: List<Review>
    ): List<ReviewResult>

    @Mapping(target = "giver", ignore = true) // Ignoramos para manejarlo manualmente
    @Mapping(target = "adopter", ignore = true) // Ignoramos para manejarlo manualmente
    fun reviewInputToReview(reviewInput: ReviewInput): Review
}

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface ReactionMapper {

    //TODO: Verificar que los mapper funcionan correctamente
    @Mapping(source = "pet.id", target = "pet")
    @Mapping(source = "adopter.id", target = "adopter")
    @Mapping(source = "giver.id", target = "giver")
    fun reactionToReactionResult(reaction: Reaction?): ReactionResult

    @Mapping(target = "giver", ignore = true) // Ignoramos para manejarlo manualmente
    @Mapping(target = "adopter", ignore = true) // Ignoramos para manejarlo manualmente
    @Mapping(target = "pet", ignore = true) // Ignoramos para manejarlo manualmente
    fun reactionDetailsToReaction(reactionDetails: ReactionDetails): Reaction

    fun reactionToReactResponse(
        reaction: Reaction
    ): ReactionResponse

}


