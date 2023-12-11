package host.ankh.geoquiz

import android.util.Log
import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"

class QuizViewModel : ViewModel() {
    public var set: HashSet<Int> = HashSet()

    private val questionBank = listOf(
        Question(R.string.question_beijing, true),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true),
        Question(R.string.question_africa, false),
        Question(R.string.question_oceans, true),
    )
    var currentIndex = 0
    var score = 0
    var isCheater = false
    var cheatTimes = 0

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer
    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId
    val isQuestionAnswered: Boolean
        get() = set.contains(currentIndex)
    val isFinished: Boolean
        get() = set.size == questionBank.size
    val grade: Float
        get() = score.toFloat() / questionBank.size.toFloat() * 100

    fun moveToNext(num: Int) {
        val size = questionBank.size
        currentIndex = (currentIndex + num + size) % size;
    }
    fun answered() {
        set.add(currentIndex)
    }
    fun addScore() {
        score += 1
    }



}