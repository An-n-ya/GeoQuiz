package host.ankh.geoquiz

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

private const val EXTRA_ANSWER_IS_TRUE = "host.ankh.android.geoquiz.answer_is_true"
private const val EXTRA_CHEAT_TIMES = "host.ankh.android.geoquiz.cheat_times"
const val EXTRA_ANSWER_SHOW = "host.ankh.android.geoquiz.answer_shown"
const val MAX_CHEAT_TIMES = 3
class CheatActivity : AppCompatActivity() {
    private lateinit var answerTextView: TextView
    private lateinit var APITextView: TextView
    private lateinit var cheatTimesTextView: TextView
    private lateinit var showAnswerButton: Button

    private var answerIsTrue = false
    private var cheatTimes = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)

        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)


        answerTextView = findViewById(R.id.answer_text_view)
        APITextView = findViewById(R.id.api_version_text_view)
        cheatTimesTextView = findViewById(R.id.cheat_times_text_view)
        showAnswerButton = findViewById(R.id.show_answer_button)
        showAnswerButton.setOnClickListener{
            if (cheatTimes >= MAX_CHEAT_TIMES) {
                Toast.makeText(this@CheatActivity, "Run out of Cheating Times!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val answerText = when {
                answerIsTrue -> R.string.true_button
                else -> R.string.false_button
            }
            answerTextView.setText(answerText)
            val text = "Remaing Cheating Times: ${MAX_CHEAT_TIMES - cheatTimes - 1}"
            cheatTimesTextView.setText(text)
            showAnswerButton.isEnabled = false
            setAnswerShowResult(true)
        }
        var text = "API Level ${Build.VERSION.SDK_INT}"
        APITextView.setText(text)
        cheatTimes = intent.getIntExtra(EXTRA_CHEAT_TIMES, 0)
        text = "Remaing Cheating Times: ${MAX_CHEAT_TIMES - cheatTimes}"
        cheatTimesTextView.setText(text)
    }


    private fun setAnswerShowResult(isAnswerShown: Boolean) {
        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOW, isAnswerShown)
        }
        setResult(Activity.RESULT_OK, data)
    }

    companion object {
        fun newIntent(packageContext: Context, answerIsTrue: Boolean, cheatTimes: Int) : Intent {
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
                putExtra(EXTRA_CHEAT_TIMES, cheatTimes)
            }
        }
    }
}