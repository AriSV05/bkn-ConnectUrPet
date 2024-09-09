package org.example.webservices

import org.example.dtos.*
import org.example.services.RegisterService
import org.example.services.UserService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("\${url.singUp}")
class SinUpController(private val loginRegisterService: RegisterService) {

    @PostMapping("/adopter", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun registerAdopter(@RequestBody userInfo: UserRegisterInput) = loginRegisterService.registerAdopter(userInfo)

    @PostMapping("/giver", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun registerGiver(@RequestBody userInfo: UserRegisterInput) = loginRegisterService.registerGiver(userInfo)
}



@RestController
@RequestMapping("\${url.adopters}")
class AdopterController(private val adopterService: UserService) {

    @Throws(NoSuchElementException::class)
    @GetMapping("{id}")
    @ResponseBody
    fun findById(@PathVariable id:Int) = adopterService.findAdopterById(id)

    @PutMapping("{id}",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun adopterEdit(@RequestBody adopter: AdopterEditRequest) = adopterService.updateAdopter(adopter)
}

@RestController
@RequestMapping("\${url.givers}")
class GiverController(private val giverService: UserService) {

    @Throws(NoSuchElementException::class)
    @GetMapping("{id}")
    @ResponseBody
    fun findById(@PathVariable id:Int) = giverService.findGiverById(id)

    @PutMapping("{id}",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun giverEdit(@RequestBody giver: GiverEditRequest) = giverService.updateGiver(giver)
}

@RestController
@RequestMapping("\${url.reviews}")
class ReviewController(private val userService: UserService) {

    @Throws(NoSuchElementException::class)
    @GetMapping("/giver/{giverId}")
    @ResponseBody
    fun findReviewsByGiverId(@PathVariable giverId: Int) = userService.findReviewsByGiverId(giverId)

    @PostMapping("", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun addReview(@RequestBody review: ReviewInput) = userService.addReview(review)

}

@RestController
@RequestMapping("\${url.reactions}")
class ReactionController(private val userService: UserService) {

    @Throws(NoSuchElementException::class)
    @GetMapping("/{giverId}")
    @ResponseBody
    fun findReactionsByGiverId(@PathVariable giverId: Int) = userService.findReactionsByGiverId(giverId)

    @PostMapping("", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun addReaction(@RequestBody reaction: ReactionDetails) = userService.addReaction(reaction)

    @DeleteMapping("/{id}")
    fun deleteReaction(@PathVariable id: Int) = userService.deleteReactionById(id)

    @PutMapping("{id}",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun giverEdit(@RequestBody reaction: ReactionUpdateRequest) = userService.updateReaction(reaction)
}

@RestController
@RequestMapping("\${url.notifications}")
class NotificationController(private val userService: UserService) {

    @PostMapping(
        "/readNotis",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun readNotifications(@RequestBody notisRequest: NotisRequest) = userService.readNotification(notisRequest)

    @PostMapping(
        "/deleteNoti",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun deleteNotification(@RequestBody notisRequest: NotisRequest) = userService.deleteNotification(notisRequest)
}