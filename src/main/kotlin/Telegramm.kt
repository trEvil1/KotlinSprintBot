import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0
    val urlGetMe = "https://api.telegram.org/bot$botToken/getMe"

    val client: HttpClient = HttpClient.newBuilder().build()
    val requestMe: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetMe)).build()
    val responseMe: HttpResponse<String> = client.send(requestMe, HttpResponse.BodyHandlers.ofString())
    println(responseMe.body())

    val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val matchResult:MatchResult? = messageTextRegex.find(getUpdates(botToken, updateId))
    val groups = matchResult?.groups
    val text = groups?.get(1)?.value
    println(text)

    while (true) {
        val updates: String = getUpdates(botToken, updateId++)
        Thread.sleep(2000)
        val lastUpdateId = "\"update_id\":(.+?),".toRegex()
        val idResult:MatchResult? = lastUpdateId.find(updates)
        val idGroup = idResult?.groups
        val id = idGroup?.get(1)?.value?.toInt()
        println(id)
    }
}

fun getUpdates(botToken: String, updateId: Int): String {
    val urlGetUpdate = "https://api.telegram.org/bot$botToken/getUpdates?offset=$updateId"
    val client: HttpClient = HttpClient.newBuilder().build()
    val requestUpdate = HttpRequest.newBuilder().uri(URI.create(urlGetUpdate)).build()
    val responseUpdate: HttpResponse<String> = client.send(requestUpdate, HttpResponse.BodyHandlers.ofString())

    return responseUpdate.body()
}