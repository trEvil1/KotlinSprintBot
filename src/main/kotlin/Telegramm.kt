import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun main(args: Array<String>) {

    val botToken = args[0]
    val urlGetMe = "https://api.telegram.org/bot$botToken/getMe"
    val urlGetUpdate = "https://api.telegram.org/bot$botToken/getUpdates"

    val client:HttpClient = HttpClient.newBuilder().build()

    val requestUpdate = HttpRequest.newBuilder().uri(URI.create(urlGetUpdate)).build()
    val requestMe: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetMe)).build()

    val responseMe: HttpResponse<String> = client.send(requestMe, HttpResponse.BodyHandlers.ofString())
    val responseUpdate: HttpResponse<String> = client.send(requestUpdate, HttpResponse.BodyHandlers.ofString())

    println(responseUpdate.body())
    println(responseMe.body())
}