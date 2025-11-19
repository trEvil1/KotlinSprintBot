import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Update(
    @SerialName("update_id")
    val updateId: Long,
    @SerialName("message")
    val message: Message? = null,
    @SerialName("callback_query")
    val callbackQuery: CallbackQuery? = null,
)

@Serializable
data class Response(
    @SerialName("result")
    val result: List<Update>
)

@Serializable
data class Message(
    @SerialName("text")
    val text: String
)
@Serializable
data class CallbackQuery(
    @SerialName("data")
    val data: String
)

fun main() {
    val json = Json{
        ignoreUnknownKeys = true
    }

    val responseString =
        """{
    "ok": true,
    "result": [
        {
            "update_id": 131543288,
            "message": {
                "message_id": 704,
                "from": {
                    "id": 1736822073,
                    "is_bot": false,
                    "first_name": "Kirill",
                    "username": "truevi11",
                    "language_code": "ru"
                },
                "chat": {
                    "id": 1736822073,
                    "first_name": "Kirill",
                    "username": "truevi11",
                    "type": "private"
                },
                "date": 1763546119,
                "text": "/start",
                "entities": [
                    {
                        "offset": 0,
                        "length": 6,
                        "type": "bot_command"
                    }
                ]
            }
        }
    ]
}"""
//    val word = Json.encodeToString(
//        Word(
//            original = "Hello",
//            translate = "Привет",
//            correctAnswerCount = 0
//        )
//    )
//    println(word)
//
//    val wordObject = Json.decodeFromString<Word>(
//        """{"original":"Hello","translate":"Привет","correctAnswerCount":0}"""
//    )
//    println(wordObject)
    val response = json.decodeFromString<Response>(responseString)
    println(response)
}