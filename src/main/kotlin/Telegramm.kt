import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun main(args: Array<String>) {

    val botToken = args[0]
    val updateId = 1
    val urlGetMe = "https://api.telegram.org/bot$botToken/getMe"

    val client: HttpClient = HttpClient.newBuilder().build()
    val requestMe: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetMe)).build()
    val responseMe: HttpResponse<String> = client.send(requestMe, HttpResponse.BodyHandlers.ofString())

    println(responseMe.body())

    while (true) {
        Thread.sleep(2000)
        val updates: String = getUpdates(botToken, updateId)
        val startUpdateId = updates.lastIndexOf("update_id")
        val endUpdateId = updates.lastIndexOf(",\n\"message\"")
        if (startUpdateId == -1 || endUpdateId == -1) continue

        val lastUpdateId = "\"update_id\":(.+?),".toRegex()
        val idResult:MatchResult? = lastUpdateId.find(updates)
        val idGroup = idResult?.groups
        val id = idGroup?.get(1)?.value?.toInt()
        println(updates)
        println(id)

        val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
        val matchResult:MatchResult? = messageTextRegex.find(updates)
        val groups = matchResult?.groups
        val text = groups?.get(1)?.value
        println(text)
    }
}

fun getUpdates(botToken: String, updateId: Int): String {
    val urlGetUpdate = "https://api.telegram.org/bot$botToken/getUpdates?offset=$updateId"
    val client: HttpClient = HttpClient.newBuilder().build()
    val requestUpdate = HttpRequest.newBuilder().uri(URI.create(urlGetUpdate)).build()
    val responseUpdate: HttpResponse<String> = client.send(requestUpdate, HttpResponse.BodyHandlers.ofString())

    return responseUpdate.body()
}