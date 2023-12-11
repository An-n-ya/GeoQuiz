package host.ankh.geoquiz

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Gravity
import android.view.View

import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val KEY_SCORE = "score"
private const val KEY_CHEATER = "cheater"
private const val REQUEST_CODE_CHEAT = 0

class MainActivity : AppCompatActivity() {
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: Button
    private lateinit var prevButton: Button
    private lateinit var cheatButton: Button
    private lateinit var questionTextView: TextView


    private val quizViewModel: QuizViewModel by lazy {
        // ViewModelProviders is deprecated
        ViewModelProvider(this).get(QuizViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        setContentView(R.layout.activity_main)
        setTitle(R.string.app_name)

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        val score = savedInstanceState?.getInt(KEY_SCORE, 0) ?: 0
        val is_cheater = savedInstanceState?.getBoolean(KEY_CHEATER, false) ?: false
        quizViewModel.currentIndex = currentIndex
        quizViewModel.score = score
        quizViewModel.isCheater = is_cheater
        val question_id_arr = savedInstanceState?.getIntegerArrayList(KEY_INDEX) ?: ArrayList<Int>()
        question_id_arr.forEach { id ->
            quizViewModel.set.add(id)
        }

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        prevButton = findViewById(R.id.prev_button)
        cheatButton = findViewById(R.id.cheat_button)
        questionTextView = findViewById(R.id.question_text_view)

        trueButton.setOnClickListener{ view: View ->
            checkAnswer(true)
        }

        falseButton.setOnClickListener{ view: View ->
            checkAnswer(false)
        }

        nextButton.setOnClickListener{view: View ->
            nextQuestion(1)
        }
        prevButton.setOnClickListener{view: View ->
            nextQuestion(-1)
        }
        cheatButton.setOnClickListener{view: View ->
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
//            startActivity(intent)
            startActivityForResult(intent, REQUEST_CODE_CHEAT)
        }

        questionTextView.setOnClickListener { view: View ->
            nextQuestion(1)
        }
        updateQuestion()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            quizViewModel.isCheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOW,false)  ?: false;
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "onSaveInstanceState")
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.currentIndex)
        savedInstanceState.putInt(KEY_SCORE, quizViewModel.score)
        savedInstanceState.putBoolean(KEY_CHEATER, quizViewModel.isCheater)
        // DONE: how to persist hash set?
        var question_id_arr = ArrayList<Int>(quizViewModel.set)
        savedInstanceState.putIntegerArrayList(KEY_INDEX, question_id_arr)
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }
    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }


    private fun nextQuestion(num: Int) {
        quizViewModel.moveToNext(num)
        quizViewModel.isCheater = false
        updateQuestion()
    }
    private fun enableButtons() {
        trueButton.isEnabled = true
        falseButton.isEnabled = true
    }
    private fun disableButtons() {
        trueButton.isEnabled = false
        falseButton.isEnabled = false
    }

    private fun updateQuestion() {
        questionTextView.setText(quizViewModel.currentQuestionText)
        if (quizViewModel.isQuestionAnswered) {
            disableButtons()
        } else {
            enableButtons()
        }
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer
//        val messageResId = if (userAnswer == correctAnswer) {
//            quizViewModel.addScore()
//            R.string.correct_toast
//        } else {
//            R.string.incorrect_toast
//        }
        val messageResId = when {
            quizViewModel.isCheater -> {
                quizViewModel.addScore()
                R.string.judgment_toast
            }
            userAnswer == correctAnswer -> {
                quizViewModel.addScore()
                R.string.correct_toast
            }
            else -> R.string.incorrect_toast
        }
        // disable this question once it is answered
        quizViewModel.answered()
        var toast = Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
        // doesn't work on 30 or higher API version
        // toast.setGravity(Gravity.TOP, 0, 0)
        toast.show()
        disableButtons()

        if (quizViewModel.isFinished) {
            // we're done here, give the user score
            var res: Float = quizViewModel.grade
            var text = "Complete! Your score is: $res.";
            toast = Toast.makeText(this, text, Toast.LENGTH_LONG)
            toast.show()
        }
    }
}